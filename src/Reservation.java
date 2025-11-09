

import java.util.Random;

public class Reservation {
    private String reservationId;
    private Client client;
    private Ticket ticket;

    public Reservation(Client client) {
        this.reservationId = getReservationId();
        this.client = client;
        this.ticket = new Ticket();
    }

    public Client getClient() {
        return client;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public String getReservationId() {
        return generateReservationId();
    }

    // To create short alphanumeric ID
    private String generateReservationId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder("RSV-");
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    
}


