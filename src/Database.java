import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Database {

    // 🔧 Update these settings according to your local MySQL configuration
    private static final String URL = "jdbc:mysql://localhost:3306/trip_booking_db";
    private static final String USER = "root";           // your MySQL username
    private static final String PASSWORD = "don_t_give_up_because_of_soen342";   // your MySQL password

    private Connection conn;

    // ========================= CONNECTION =========================
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ensures driver is loaded
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[Database] Connected to MySQL.");
        } catch (ClassNotFoundException e) {
            System.err.println("[Database] JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[Database] Connection failed: " + e.getMessage());
        }
    }


    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("[Database] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[Database] Error closing connection: " + e.getMessage());
        }
    }

    // ========================= SAVE METHODS =========================

    public void saveTrip(Trip trip) {
        String sql = "INSERT INTO Trip (trip_id, from_city, to_city, departure_time, arrival_time, total_duration, total_first_rate, total_second_rate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Duration d = Duration.parse(trip.getTotalTravelTime().toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trip.getId());
            stmt.setString(2, trip.getOrigin());
            stmt.setString(3, trip.getDestination());
            stmt.setString(4, trip.getFirstDepartureTime().toString());
            stmt.setString(5, trip.getFinalArrivalTime().toString());
            stmt.setString(6, String.format("%02d:%02d", d.toHours(), d.toMinutesPart()));
            stmt.setDouble(7, trip.getTotalFirstRate());
            stmt.setDouble(8, trip.getTotalSecondRate());

            stmt.executeUpdate();
            System.out.println("[Database] Trip saved: " + trip.getId());
        } catch (SQLException e) {
            System.err.println("[Database] Error saving trip: " + e.getMessage());
        }
    }

    public void saveClient(Client client) {
        String sql = """
        INSERT INTO Client (client_id, first_name, last_name, age)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE 
            first_name = VALUES(first_name),
            last_name = VALUES(last_name),
            age = VALUES(age)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Assuming getFullName() returns "First Last"
            String[] nameParts = client.getFullName().split(" ", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            stmt.setString(1, client.getId());
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setInt(4, 30); // Replace with real age if available

            stmt.executeUpdate();
            System.out.println("[Database] Client saved or updated: " + client.getFullName());
        } catch (SQLException e) {
            System.err.println("[Database] Error saving client: " + e.getMessage());
        }
    }



    public void saveTicket(Ticket ticket) {
        String sql = "INSERT INTO Ticket (ticket_number) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticket.getTicketNumber());

            stmt.executeUpdate();
            System.out.println("[Database] Ticket saved: " + ticket.getTicketNumber());
        } catch (SQLException e) {
            System.err.println("[Database] Error saving client: " + e.getMessage());
        }
    }

    public void saveReservation(Reservation res) {
        String sql = "INSERT INTO Reservation (reservation_id, client_id, ticket_number) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, res.getReservationId());
            stmt.setString(2, res.getClient().getId());
            stmt.setLong(3, res.getTicket().getTicketNumber());
            stmt.executeUpdate();
            System.out.println("[Database] Reservation saved for " + res.getClient().getFullName()+" | (ID: "+res.getClient().getId()+", Age:"+res.getClient().getAge()+" | Ticket: "+res.getTicket().getTicketNumber());

        } catch (SQLException e) {
            System.err.println("[Database] Error saving reservation: " + e.getMessage());
        }
    }

    public void saveBookedTrip(BookedTrip bookedTrip, Client c, String s) {
        String sql = "INSERT INTO BookedTrip (booked_trip_id, trip_id, client_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "BOOKING-"+bookedTrip.getTripID()+s);
            stmt.setString(2, bookedTrip.getTrip().getId());
            stmt.setString(3, c.getId());
            stmt.executeUpdate();
            System.out.println("[Database] BookedTrip saved for trip: " + bookedTrip.getTrip().getId());
        } catch (SQLException e) {
            System.err.println("[Database] Error saving booked trip: " + e.getMessage());
        }
    }




    // ========================= LOADING ROUTES (OPTIONAL) =========================
    public void loadRoutes() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Route LIMIT 5")) {
            while (rs.next()) {
                System.out.println("Route " + rs.getString("route_id") + ": "
                        + rs.getString("origin") + " -> " + rs.getString("destination"));
            }
        } catch (SQLException e) {
            System.err.println("[Database] Error loading routes: " + e.getMessage());
        }
    }


    public void readAllBookings(String clientId) {
        // First, get all booked trips for this client
        String sqlTrips = """
        SELECT DISTINCT 
            b.booked_trip_id,
            b.trip_id
        FROM BookedTrip b
        WHERE b.client_id = ?
        ORDER BY b.trip_id
        """;

        // Then, get all reservations for this client
        String sqlReservations = """
        SELECT reservation_id, ticket_number
        FROM Reservation
        WHERE client_id = ?
        ORDER BY reservation_id
        """;

        try {
            // === Load reservations ===
            List<String[]> reservations = new ArrayList<>();
            try (PreparedStatement stmtRes = conn.prepareStatement(sqlReservations)) {
                stmtRes.setString(1, clientId);
                try (ResultSet rsRes = stmtRes.executeQuery()) {
                    while (rsRes.next()) {
                        reservations.add(new String[]{
                                rsRes.getString("reservation_id"),
                                String.valueOf(rsRes.getInt("ticket_number"))
                        });
                    }
                }
            }

            // === Load trips and print ===
            try (PreparedStatement stmtTrips = conn.prepareStatement(sqlTrips)) {
                stmtTrips.setString(1, clientId);
                try (ResultSet rsTrips = stmtTrips.executeQuery()) {

                    System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-20s %-10s %-15s %-10s %-10s %-12s %-12s %-12s %-12s%n",
                            "BookedTrip ID", "Trip ID", "Reservation ID", "Client ID", "Ticket #",
                            "From City", "To City", "Departure", "Arrival");
                    System.out.println("-------------------------------------------------------------------------------------------------------------------------------");

                    boolean found = false;
                    int resIndex = 0; // to pair trips with reservations sequentially

                    while (rsTrips.next()) {
                        found = true;
                        String bookedTripId = rsTrips.getString("booked_trip_id");
                        String tripId = rsTrips.getString("trip_id");

                        // Pair each trip with a unique reservation if available
                        String reservationId = "N/A";
                        int ticketNumber = 0;
                        if (resIndex < reservations.size()) {
                            reservationId = reservations.get(resIndex)[0];
                            ticketNumber = Integer.parseInt(reservations.get(resIndex)[1]);
                            resIndex++;
                        }

                        String[] tripDetails = getTripDetails(tripId);
                        if (tripDetails != null) {
                            System.out.printf("%-20s %-10s %-15s %-10s %-10d %-12s %-12s %-12s %-12s%n",
                                    bookedTripId, tripId, reservationId, clientId, ticketNumber,
                                    tripDetails[0], tripDetails[1], tripDetails[2], tripDetails[3]);
                        }
                    }

                    if (!found) {
                        System.out.println("No bookings found for client: " + clientId);
                    }

                    System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
                }
            }

        } catch (SQLException e) {
            System.err.println("[Database] Error reading bookings: " + e.getMessage());
        }
    }

    public String[] getTripDetails(String tripId) {
        String sql = """
        SELECT 
            from_city,
            to_city,
            departure_time,
            arrival_time
        FROM Trip
        WHERE trip_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tripId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String fromCity = rs.getString("from_city");
                    String toCity = rs.getString("to_city");
                    String departure = rs.getTime("departure_time").toString();
                    String arrival = rs.getTime("arrival_time").toString();


                    return new String[] { fromCity, toCity, departure, arrival};
                }
            }

        } catch (SQLException e) {
            System.err.println("[Database] Error reading trip details: " + e.getMessage());
        }

        return null;
    }


}

