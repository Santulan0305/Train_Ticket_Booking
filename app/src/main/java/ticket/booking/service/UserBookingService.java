package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserBookingService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<User> userList;
    private User user;

    private final String USER_FILE_PATH =
            "app/src/main/java/ticket/booking/localDb/users.json";

    // ---------- CONSTRUCTOR FOR LOGIN ----------
    public UserBookingService(User loginUser) throws IOException {
        loadUserListFromFile();

        this.user = userList.stream()
                .filter(u -> u.getName().equals(loginUser.getName())
                        && UserServiceUtil.checkPassword(
                        loginUser.getPassword(), u.getHashedPassword()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid login"));
    }

    // ---------- DEFAULT CONSTRUCTOR ----------
    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        userList = objectMapper.readValue(
                new File(USER_FILE_PATH),
                new TypeReference<List<User>>() {});
    }

    private void saveUserListToFile() throws IOException {
        objectMapper.writeValue(new File(USER_FILE_PATH), userList);
    }

    // ---------- SIGN UP ----------
    public Boolean signUp(User newUser) {
        try {
            userList.add(newUser);
            saveUserListToFile();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ---------- FETCH BOOKINGS ----------
    public void fetchBookings() {
        if (user.getTicketsBooked().isEmpty()) {
            System.out.println("No bookings found");
            return;
        }
        user.printTickets();
    }

    // ---------- SEARCH TRAINS ----------
    public List<Train> getTrains(String source, String destination) {
        try {
            return new TrainService().searchTrains(source, destination);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // ---------- FETCH SEATS ----------
    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    // ---------- BOOK SEAT (FIXED) ----------
    public Boolean bookTrainSeat(Train train, int row, int col) {
        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();

            if (row >= 0 && row < seats.size() && col >= 0 && col < seats.get(row).size()) {
                if (seats.get(row).get(col) == 0) {
                    // Mark the seat as booked
                    seats.get(row).set(col, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train); // Update train data in file

                    // Create the Ticket using the booked seat info
                    Ticket ticket = new Ticket(
                            UUID.randomUUID().toString(),           // ticketId
                            user.getUserId(),                        // userId
                            train.getStations().get(0),             // source station
                            train.getStations().get(train.getStations().size() - 1), // destination station
                            java.time.LocalDate.now().toString(),   // travel date
                            train,                                   // train object
                            row,                                     // seat row
                            col                                      // seat column
                    );

                    // Add ticket to the user's booked tickets
                    user.getTicketsBooked().add(ticket);

                    // Save users to file
                    saveUserListToFile();

                    return true; // Booking successful
                } else {
                    return false; // Seat already booked
                }
            } else {
                return false; // Invalid seat
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    // ---------- CANCEL BOOKING (FIXED) ----------
    public Boolean cancelBooking(String ticketId) {
        Optional<Ticket> ticketOpt = user.getTicketsBooked()
                .stream()
                .filter(t -> t.getTicketId().equals(ticketId))
                .findFirst();

        if (!ticketOpt.isPresent()) {
            System.out.println("Ticket not found");
        }


        Ticket ticket = ticketOpt.get();

        try {
            // Free seat
            TrainService trainService = new TrainService();
            List<Train> trains = trainService.searchTrains("", "");

            for (Train t : trains) {
                if (t.getTrainId().equals(ticket.getTrainId())) {
                    t.getSeats()
                            .get(ticket.getRow())
                            .set(ticket.getCol(), 0);
                    trainService.addTrain(t);
                    break;
                }
            }

            // Remove ticket
            user.getTicketsBooked().remove(ticket);
            saveUserListToFile();

            System.out.println("Ticket cancelled successfully");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
