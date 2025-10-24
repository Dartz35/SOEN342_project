import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Main {

    private static final String CSV_PATH = "eu_rail_network.csv";
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("*********Trip Finder & Booking*********");
        System.out.println("CSV path: " + CSV_PATH);

        List<Route> routes;
        try {
            routes = RouteCsvLoader.load(CSV_PATH);
        } catch (IOException e) {
            System.err.println("Failed to load CSV: " + e.getMessage());
            System.err.println("Make sure the file exists exactly here:\n" + CSV_PATH);
            return;
        }
        System.out.println("Loaded " + routes.size() + " routes.");

        // initial search
        List<Trip> trips = performSearch(routes);
        if (trips.isEmpty()) {
            System.out.println("No trips found.");
            return;
        }

        showTrips(trips);
        Trip selectedTrip = null; // holds the currently selected trip

        // Main menu
        while (true) {
            System.out.println("\nOptions: Search, Sort, Select, Book, ViewTrips, or Quit");
            System.out.print("> ");
            String choice = in.nextLine().trim().toUpperCase(Locale.ROOT);

            switch (choice) {
                case "SEARCH":
                    trips = performSearch(routes);
                    showTrips(trips);
                    break;

                case "SORT":
                    SortBy key = askSortKey();
                    SortOrder order = askSortOrder();
                    trips = TripSort.sort(trips, key, order);
                    showTrips(trips);
                    break;

                case "SELECT":
                    selectedTrip = selectTrip(trips);
                    break;

                case "BOOK":
                    if (selectedTrip == null) {
                        System.out.println(" No trip selected. Please select a trip first.");
                    } else {
                        selectedTrip = bookTrip(selectedTrip); // link selected trip
                    }
                    break;

                case "VIEWTRIPS":
                    viewTripsMenu();
                    break;

                case "QUIT":
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Unknown option.");
            }
        }
    }

    // ======================= SEARCH =======================

    private static List<Trip> performSearch(List<Route> routes) {
        System.out.println("\nFilter by one attribute:");
        System.out.println(" - from       (origin city)");
        System.out.println(" - to         (destination city)");
        System.out.println(" - departure  (HH:mm)");
        System.out.println(" - arrival    (HH:mm)");
        System.out.println(" - duration   (minutes)");
        System.out.println(" - firstRate  (<= value)");
        System.out.println(" - secondRate (<= value)");
        System.out.println(" - trainType");
        System.out.println(" - dayOfOp");
        System.out.print("Attribute: ");
        String attr = in.nextLine().trim();
        System.out.print("Value: ");
        String val = in.nextLine().trim();

        SearchCriteria crit = new SearchCriteria(attr, val, LocalDate.now());
        return TripFinder.findIndirectIfNoDirect(routes, crit);
    }

    private static void showTrips(List<Trip> trips) {
        System.out.println("\nResults (" + trips.size() + "):");
        int i = 1;
        for (Trip t : trips) {
            // If you prefer the formatted single-line output, uncomment and fix as one line:
            // System.out.printf(Locale.ROOT,
            //         "%2d) %s => %s | legs=%d | dep=%s | arr=%s | dur=%dm | First=%.2f | Second=%.2f%n",
            //         i++, t.getOrigin(), t.getDestination(), t.getLegs().size(),
            //         t.getFirstDepartureTime(), t.getFinalArrivalTime(),
            //         t.getTotalTravelTime().toMinutes(),
            //         t.getTotalFirstRate(), t.getTotalSecondRate());
            System.out.printf("%2d) %s%n", i++, t);
        }
    }

    // ======================= SORTING =======================

    private static SortBy askSortKey() {
        System.out.println("Sort by: ");
        System.out.println(" 1) Departure time");
        System.out.println(" 2) Arrival time");
        System.out.println(" 3) Origin city");
        System.out.println(" 4) Destination city");
        System.out.println(" 5) Duration");
        System.out.println(" 6) First Rate");
        System.out.println(" 7) Second Rate");
        System.out.print("Choose 1-7: ");
        String s = in.nextLine().trim();
        switch (s) {
            case "1": return SortBy.DEPARTURE_TIME;
            case "2": return SortBy.ARRIVAL_TIME;
            case "3": return SortBy.ORIGIN_CITY;
            case "4": return SortBy.DESTINATION_CITY;
            case "5": return SortBy.DURATION;
            case "6": return SortBy.FIRST_RATE;
            case "7": return SortBy.SECOND_RATE;
            default: return SortBy.ARRIVAL_TIME;
        }
    }

    private static SortOrder askSortOrder() {
        System.out.print("Order [A]sc / [D]esc (default A): ");
        String s = in.nextLine().trim().toUpperCase(Locale.ROOT);
        return s.equals("D") ? SortOrder.DESC : SortOrder.ASC;
    }

    // ======================= SELECTION =======================

    private static Trip selectTrip(List<Trip> trips) {
        System.out.print("Enter Trip ID (e.g., R02058): ");
        String tripId = in.nextLine().trim().toUpperCase(Locale.ROOT);
        for (Trip t : trips) {
            if (t.getId().equalsIgnoreCase(tripId) || t.getId().endsWith(tripId)) {
                System.out.println("\nSelected Trip Details:");
                System.out.println(t);
                System.out.println(" Trip " + t.getId() + " selected.");
                return t;
            }
        }
        System.out.println("No trip found with ID: " + tripId);
        return null;
    }

    // ======================= BOOKING =======================

    private static Trip bookTrip(Trip selectedTrip) {
        System.out.println("\nBooking Trip: " + selectedTrip.getId() +
                " (" + selectedTrip.getOrigin() + " => " + selectedTrip.getDestination() + ")");

        System.out.print("How many travellers? ");
        int numTravellers = Integer.parseInt(in.nextLine().trim());
        List<Client> clients = new ArrayList<>();

        for (int i = 1; i <= numTravellers; i++) {
            System.out.println("\nTraveller " + i + ":");
            System.out.print("First name: ");
            String first = in.nextLine().trim();
            System.out.print("Last name: ");
            String last = in.nextLine().trim();
            System.out.print("ID (passport or similar): ");
            String id = in.nextLine().trim();
            System.out.print("Age: ");
            int age = Integer.parseInt(in.nextLine().trim());
            clients.add(new Client(first, last, id, age));
        }

        BookedTrip booked = TripManager.bookTrip(clients, selectedTrip);
        if (booked != null) {
            System.out.println("\n Trip booked successfully and linked to selected trip!");
            System.out.println("Linked Trip: " + selectedTrip.getId());
            for (Reservation r : booked.getReservations())
                System.out.println(" - " + r);
        }
        return selectedTrip;
    }

    private static void viewTripsMenu() {
        System.out.print("Enter last name: ");
        String last = in.nextLine().trim();
        System.out.print("Enter ID: ");
        String id = in.nextLine().trim();

        List<BookedTrip> found = TripManager.viewTrips(last, id);
        if (found.isEmpty()) {
            System.out.println("No trips found for " + last + " (" + id + ")");
            return;
        }

        System.out.println("\nTrips for " + last + ":");
        for (BookedTrip bt : found) {
            System.out.print(bt);
            System.out.println(" | Linked Trip: " + bt.getTrip().getId() + " (" +
                    bt.getTrip().getOrigin() + " => " + bt.getTrip().getDestination() + ")");
        }
    }
}