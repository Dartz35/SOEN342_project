import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class BookedTrip {
    private String tripId;
    private Trip trip;
    private List<Reservation> reservations;
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public BookedTrip(Trip trip, List<Client> clients) {
        this.tripId = generateTripId();
        this.trip = trip;
        this.reservations = new ArrayList<>();
        for (Client c : clients) {
            this.reservations.add(new Reservation(c));
        }
    }

    public String getTripID(){
        return tripId;
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {  // 10-character random ID
            sb.append(ALPHANUM.charAt(random.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "BookedTrip " + tripId + " | " + reservations.size() + " traveller(s)";
    }
}
