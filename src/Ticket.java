import java.util.Random;

public class Ticket {
    private static final Random random = new Random();
    private final int ticketNumber;

    public Ticket() {
        // Generate a random number between 10000 and 99999 (inclusive)
        this.ticketNumber = 10_000 + random.nextInt(90_000);
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    @Override
    public String toString() {
        return "Ticket #" + ticketNumber;
    }
}



