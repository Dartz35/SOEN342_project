
public class Ticket {
    private static long counter = 10000; 
    private long ticketNumber;

    public Ticket() {
        this.ticketNumber = counter++;
    }

    public long getTicketNumber() {
        return ticketNumber;
    }

    @Override
    public String toString() {
        return "Ticket #" + ticketNumber;
    }
}


