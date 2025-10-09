import java.io.IOException;
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


    }
}