package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.service.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;

public class App {

    public static void main(String[] args) {

        System.out.println("Running Train Booking System");

        Scanner scanner = new Scanner(System.in);
        int option = 0;

        UserBookingService userBookingService;
        User currentUser = null;
        Train selectedTrain = null;
        Train trainSelectedForBooking = null;


        try {
            userBookingService = new UserBookingService();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while (option != 7) {

            System.out.println("\nChoose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");

            option = scanner.nextInt();

            switch (option) {

                // ---------------- SIGN UP ----------------
                case 1:
                    System.out.println("Enter username:");
                    String signUpName = scanner.next();

                    System.out.println("Enter password:");
                    String signUpPassword = scanner.next();

                    User newUser = new User(
                            signUpName,
                            signUpPassword,
                            UserServiceUtil.hashPassword(signUpPassword),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    userBookingService.signUp(newUser);
                    System.out.println("✅ Signup successful");
                    break;

                // ---------------- LOGIN ----------------
                case 2:
                    System.out.println("Enter username:");
                    String loginName = scanner.next();

                    System.out.println("Enter password:");
                    String loginPassword = scanner.next();

                    User loginUser = new User(
                            loginName,
                            loginPassword,
                            UserServiceUtil.hashPassword(loginPassword),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    try {
                        userBookingService = new UserBookingService(loginUser);
                        currentUser = loginUser;
                        System.out.println("✅ Login successful");
                    } catch (IOException e) {
                        System.out.println(" Login failed");
                    }
                    break;

                // ---------------- FETCH BOOKINGS ----------------
                case 3:
                    if (currentUser == null) {
                        System.out.println(" Please login first");
                        break;
                    }
                    userBookingService.fetchBookings();
                    break;

                // ---------------- SEARCH TRAINS ----------------
                case 4:
                    System.out.println("Enter source station:");
                    String source = scanner.next().toLowerCase();

                    System.out.println("Enter destination station:");
                    String destination = scanner.next().toLowerCase();

                    List<Train> trains = userBookingService.getTrains(source, destination);

                    if (trains.isEmpty()) {
                        System.out.println("No trains found");
                        break;
                    }

                    System.out.println("Available trains:");
                    for (int i = 0; i < trains.size(); i++) {
                        Train t = trains.get(i);
                        System.out.println((i + 1) + ". Train ID: " + t.getTrainId()
                                + " | Train No: " + t.getTrainNo());
                    }

                    System.out.println("Select train (Enter option number):");
                    int choice = scanner.nextInt();

                    if (choice < 1 || choice > trains.size()) {
                        System.out.println("Invalid choice");
                        break;
                    }

                    trainSelectedForBooking = trains.get(choice - 1);
                    System.out.println("Train selected successfully");

                    break;


                // ---------------- BOOK SEAT ----------------
                case 5:
                    if (currentUser == null) {
                        System.out.println(" Please login first");
                        break;
                    }

                    if (trainSelectedForBooking == null) {
                        System.out.println(" Please search and select a train first");
                        break;
                    }

                    List<List<Integer>> seats =
                            userBookingService.fetchSeats(trainSelectedForBooking);

                    for (List<Integer> rowList : seats) {
                        for (Integer seat : rowList) {
                            System.out.print(seat + " ");
                        }
                        System.out.println();
                    }

                    System.out.println("Enter row:");
                    int row = scanner.nextInt();

                    System.out.println("Enter column:");
                    int col = scanner.nextInt();

                    boolean booked =
                            userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);

                    if (booked) {
                        System.out.println(" Seat booked successfully");
                    } else {
                        System.out.println(" Seat not available");
                    }
                    break;

                // ---------------- CANCEL BOOKING ----------------
                case 6:
                    if (currentUser == null) {
                        System.out.println(" Please login first");
                        break;
                    }

                    System.out.println("Cancel booking feature coming soon 🚧");
                    break;

                // ---------------- EXIT ----------------
                case 7:
                    System.out.println(" Exiting application");
                    break;

                default:
                    System.out.println(" Invalid option");
            }
        }

        scanner.close();
    }
}
