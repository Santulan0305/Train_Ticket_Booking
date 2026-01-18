package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Ticket {

    private String ticketId;
    private String TicketInfo;
    private String userId;
    private String source;
    private String destination;
    private String dateOfTravel;
    private Train train;
    private int row;    // seat row
    private int col;    // seat column

    public Ticket() {}

    public String getTicketInfo() {
        return String.format(
                "Ticket ID: %s | User ID: %s | Train ID: %s | Train No: %s | From: %s | To: %s | Date: %s | Seat: Row %d, Col %d",
                ticketId,
                userId,
                train.getTrainId(),
                train.getTrainNo(),
                source,
                destination,
                dateOfTravel,
                row,
                col
        );
    }

    public Ticket(String ticketId, String userId, String source, String destination,
                  String dateOfTravel, Train train, int row, int col) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
        this.row = row;
        this.col = col;
    }

    // Existing getters/setters
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDateOfTravel() { return dateOfTravel; }
    public void setDateOfTravel(String dateOfTravel) { this.dateOfTravel = dateOfTravel; }

    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }

    // NEW getters/setters for seat and train info
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public String getTrainId() { return train.getTrainId(); }
    public String getTrainNo() { return train.getTrainNo(); }

}
