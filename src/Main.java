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

        

    }
}