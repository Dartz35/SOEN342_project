import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Main {

    private static final String CSV_PATH = "eu_rail_network.csv";

    private static final Scanner in = new Scanner(System.in);
    public static void main(String[] args) {

        System.out.println("*********Trip Finder********");
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

        System.out.println("\nFilter by one attribute:");
        System.out.println(" - from       (origin city)");
        System.out.println(" - to         (destination city)");
        System.out.println(" - departure  (HH:mm)");
        System.out.println(" - arrival    (HH:mm)");
        System.out.println(" - duration   (minutes)");
        System.out.println(" - First Rate      (<= value)");
        System.out.println(" - Second Rate      (<= value)");
        System.out.println(" - Train type");
        System.out.println(" - Day of operation ");

        System.out.print("Attribute: ");
        String attr = in.nextLine().trim();
        System.out.print("Value: ");
        String val = in.nextLine().trim();

        SearchCriteria crit = new SearchCriteria(attr, val, LocalDate.now());

        List<Trip> trips = TripFinder.findIndirectIfNoDirect(routes, crit);

        if (trips.isEmpty()) {
            System.out.println("No trips found for: " + crit);
            return;
        }

        showTrips(trips);

        
        while (true) {
            System.out.println("\nOptions: Sort or Quit");
            System.out.print("> ");
            String choice = in.nextLine().trim().toUpperCase(Locale.ROOT);
            if (choice.equalsIgnoreCase("Quit")) break;

            if (choice.equalsIgnoreCase("Sort")) {
                SortBy key = askSortKey();
                SortOrder order = askSortOrder();
                trips = TripSort.sort(trips, key, order);
                showTrips(trips);
            }
        }

        System.out.println("Goodbye!");
    }

    }
}

 private static void showTrips(List<Trip> trips) {
        System.out.println("\nResults (" + trips.size() + "):");
        int i = 1;
        for (Trip t : trips) {
            /*System.out.printf(Locale.ROOT,
                    "%2d) %s → %s | legs=%d | dep=%s | arr=%s | dur=%dm | $%.2f%n | $%.2f%n",
                    i++,
                    t.getOrigin(), t.getDestination(), t.getLegs().size(),
                    t.getFirstDepartureTime(), t.getFinalArrivalTime(),
                    t.getTotalTravelTime().toMinutes(),
                    t.getTotalFirstRate(),
                    t.getTotalSecondRate());*/
            System.out.println(t);
        }
    }

    private static SortBy askSortKey() {
        System.out.println("Sort by: ");
        System.out.println(" 1) Departure time");
        System.out.println(" 2) Arrival time");
        System.out.println(" 3) Origin city (A→Z)");
        System.out.println(" 4) Destination city (A→Z)");
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
            default:
                System.out.println("Unknown, defaulting to Arrival time.");
                return SortBy.ARRIVAL_TIME;
        }
    }

    private static SortOrder askSortOrder() {
        System.out.print("Order [A]sc / [D]esc (default A): ");
        String s = in.nextLine().trim().toUpperCase(Locale.ROOT);
        return s.equals("D") ? SortOrder.DESC : SortOrder.ASC;
    }
}
