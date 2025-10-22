import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookedTrip {
    private String tripId;
    private Trip trip;
    private List<Reservation> reservations;

    public BookedTrip(Trip trip, List<Client> clients) {
        this.tripId = generateTripId();
        this.trip = trip;
        this.reservations = new ArrayList<>();
        for (Client c : clients) {
            this.reservations.add(new Reservation(c));
        }
    }

    public Trip getTrip() {
        return trip;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<Client> getTravellers() {
        List<Client> travellers = new ArrayList<>();
        for (Reservation r : reservations) {
            travellers.add(r.getClient());
        }
        return travellers;
    }

    private String generateTripId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder("TRIP-");
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "BookedTrip " + tripId + " | " + reservations.size() + " traveller(s)";
    }
}
