import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TripHistory {

    private List<BookedTrip> trips;

    public TripHistory() {
        this.trips = new ArrayList<>();
    }

    public void addTrip(BookedTrip trip) {
        trips.add(trip);
    }

    public List<BookedTrip> getTripsByClient(String lastName, String id) {
        List<BookedTrip> result = new ArrayList<>();
        for (BookedTrip bt : trips) {
            for (Client c : bt.getTravellers()) {
                if (c.getLastName().equalsIgnoreCase(lastName)
                        && c.getId().equalsIgnoreCase(id)) {
                    result.add(bt);
                    break;
                }
            }
        }
        return result;
    }

    public List<BookedTrip> getCurrentTrips(LocalDate currentDate) {
        return trips;
    }

    public List<BookedTrip> getPastTrips(LocalDate currentDate) {
        return new ArrayList<>();
    }


}
