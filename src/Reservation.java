

import java.util.Random;

public class Reservation {
    private String reservationId;
    private Client client;
    private Ticket ticket;

    public Reservation(Client client) {
        this.reservationId = generateReservationId();
        this.client = client;
        this.ticket = new Ticket();
    }

    public Client getClient() {
        return client;
    }

    public Ticket getTicket() {
        return ticket;
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

    @Override
    public String toString() {
        return reservationId + " | " + client + " | " + ticket;
    }
}

