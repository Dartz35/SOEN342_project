
import java.util.List;

public class TripManager {
    private static TripHistory tripHistory = new TripHistory();

    public static BookedTrip bookTrip(List<Client> clients, Trip trip) {
        BookedTrip bookedTrip = new BookedTrip(trip, clients);
        tripHistory.addTrip(bookedTrip);
        System.out.println("Trip booked successfully: " + bookedTrip);
        return bookedTrip;
    }

    public static List<BookedTrip> viewTrips(String lastName, String id) {
        return tripHistory.getTripsByClient(lastName, id);
    }
}

