import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Main {

    private static final String CSV_PATH = "eu_rail_network.csv";
    private static final Scanner in = new Scanner(System.in);
    private static final Database db = new Database();
    private static final LayoverPolicy policy = new LayoverPolicy(1, java.time.LocalTime.of(6, 0),
            java.time.LocalTime.of(22, 0),
            java.time.Duration.ofHours(2),
            java.time.Duration.ofMinutes(30));

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
        List<Trip> trips = null;

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
                    selectedTrip = selectTrip(routes);
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
        return TripFinder.findDirect(routes, crit);
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

    private static Trip selectTrip(List<Route> routes) {
        System.out.print("\nEnter departure city: ");
        String fromCity = in.nextLine().trim();
        System.out.print("Enter arrival city: ");
        String toCity = in.nextLine().trim();


        LayoverPolicy lp = new LayoverPolicy(
                12,                       // 1. policyId (int)
                LocalTime.of(6, 0),       // 2. Daytime start (LocalTime)
                LocalTime.of(19, 0),      // 3. Daytime end (LocalTime)
                Duration.ofHours(4),      // 4. Max Daytime Layover (Duration)
                Duration.ofHours(2)       // 5. Max Night Layover (Duration)
        );

        // Step 1: Find all possible trips (direct or indirect)
        List<Trip> foundTrips = TripFinder.findIndirectFromTo(routes, fromCity, toCity,lp);


        if (foundTrips.isEmpty()) {
            System.out.println("\n No direct or indirect trips found from " +
                    fromCity + " to " + toCity + ".");
            return null;
        }

        // Step 2: Separate direct and indirect trips for clarity
        List<Trip> directTrips = new ArrayList<>();
        List<Trip> indirectTrips = new ArrayList<>();

        for (Trip t : foundTrips) {
            if (t instanceof IndirectTrip) indirectTrips.add(t);
            else directTrips.add(t);
        }

        // Step 3: Display results nicely
        System.out.println("\n Trips found from " + fromCity + " to " + toCity + ":");

        if (!directTrips.isEmpty()) {
            System.out.println("\n--- Direct Trips ---");
            System.out.println();
            int i = 1;
            for (Trip t : directTrips) {
                System.out.printf("%2d) %s -> %s | %d legs | Trip ID: %s%n",
                        i++, t.getOrigin(), t.getDestination(), t.getLegs().size(), t.getId());
                System.out.println("   Route chain:");
                List<Route> legs = t.getLegs();

                // Use a loop with an index

                Route r = legs.get(0);

                // Print the route details
                System.out.printf("     %s → %s | RouteID=%s |dep=%s | arr=%s | firstRate=%s%n",
                        r.getFrom(), r.getTo(), r.getId() ,r.getDepartureTime(), r.getArrivalTime(), r.getFirstRate());


                System.out.println();
            }
        }
        if (!indirectTrips.isEmpty()) {
            System.out.println("\n--- Indirect Trips (connections) ---");
            System.out.println();
            int i = 1;
            System.out.println(lp);
            System.out.println();
            for (Trip t : indirectTrips) {
                System.out.printf("%2d) %s -> %s | %d legs | Trip ID: %s%n",
                        i++, t.getOrigin(), t.getDestination(), t.getLegs().size(), t.getId());
                System.out.println("   Route chain:");
                List<Route> legs = t.getLegs();

                // Use a loop with an index
                for (int i2 = 0; i2 < legs.size(); i2++) {
                    Route r = legs.get(i2);

                    // Print the route details
                    System.out.printf("     %s → %s | RouteID=%s |dep=%s | arr=%s | firstRate=%s%n",
                            r.getFrom(), r.getTo(), r.getId() ,r.getDepartureTime(), r.getArrivalTime(), r.getFirstRate());

                    // Only print "Change Connection" if it is NOT the last leg
                    if (i2 < legs.size() - 1) {
                        System.out.println("            -- Change Connection --");
                    }
                }
                System.out.println();
            }
        }

        // Step 4: Let user select a trip
        System.out.print("Enter Trip ID to select: ");
        String id = in.nextLine().trim().toUpperCase(Locale.ROOT);

        for (Trip t : foundTrips) {
            if (t.getId().equalsIgnoreCase(id) || t.getId().endsWith(id)) {
                System.out.println("\nSelected Trip Details:");
                System.out.println("---------------------------------");
                System.out.println("Trip ID: " + t.getId());
                System.out.println("From: " + t.getOrigin());
                System.out.println("To: " + t.getDestination());
                System.out.println("Legs: " + t.getLegs().size());
                for (Route r : t.getLegs()) {
                    System.out.printf("  %s → %s | dep=%s | arr=%s%n",
                            r.getFrom(), r.getTo(), r.getDepartureTime(), r.getArrivalTime());
                }
                System.out.println("---------------------------------");
                return t;
            }
        }

        System.out.println("No trip found with ID: " + id);
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
        db.connect();
        db.saveTrip(selectedTrip);
        for (Client c : clients) {
            db.saveClient(c);
            String randomSuffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            db.saveBookedTrip(booked,c,randomSuffix);
        }






        System.out.println("\nTrip booked successfully and saved to database!");

        for (Reservation r : booked.getReservations()) {
            // 1. Save the ticket (parent)
            db.saveTicket(r.getTicket());

            // 2. Save the reservation (parent)
            db.saveReservation(r);

        }

        db.close();

        return selectedTrip;
    }


    private static void viewTripsMenu() {
        System.out.print("Enter last name: ");
        String last = in.nextLine().trim();
        System.out.print("Enter ID: ");
        String id = in.nextLine().trim();

        List<BookedTrip> found = TripManager.viewTrips(last, id);




        db.connect();
        System.out.println("\n" + last + " Trip History: ");
        db.readAllBookings(id);
        db.close();
    }
}
