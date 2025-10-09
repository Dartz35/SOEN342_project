import java.io.*;
import java.time.*;
import java.util.*;

public final class RouteCsvLoader {

    private static final String[] EXPECTED_HEADERS = {
            "Route ID",
            "Departure City",
            "Arrival City",
            "Departure Time",
            "Arrival Time",
            "Train Type",
            "Days of Operation",
            "First Class ticket rate (in euro)",
            "Second Class ticket rate (in euro)"
    };

    public static List<Route> load(String path) throws IOException {
        List<Route> routes = new ArrayList<>();
        int autoId = 1, skipped = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new IOException("Empty CSV: " + path);

            String[] headerCells = splitCSV(headerLine); // <-- uses splitCSV

            if (headerCells.length != EXPECTED_HEADERS.length) {
                throw new IOException("Invalid number of columns. Expected "
                        + EXPECTED_HEADERS.length + " headers but found " + headerCells.length);
            }
            for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
                if (!headerCells[i].equals(EXPECTED_HEADERS[i])) {
                    throw new IOException("Invalid header at column " + (i + 1)
                            + ": expected \"" + EXPECTED_HEADERS[i]
                            + "\" but found \"" + headerCells[i] + "\"");
                }
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] row = splitCSV(line);

                if (row.length != EXPECTED_HEADERS.length) {
                    skipped++;
                    continue;
                }

                String id = row[0].trim();
                if (id.isEmpty()) id = "R" + (autoId++);

                String from = row[1].trim();
                String to = row[2].trim();
                String depS = row[3].trim();
                String arrS = row[4].trim();
                String trainType = row[5].trim();
                String days = row[6].trim();
                String firstRateS = row[7].trim();
                String secondRateS = row[8].trim();

                if (from.isEmpty() || to.isEmpty() || depS.isEmpty() || arrS.isEmpty()) {
                    skipped++;
                    continue;
                }

                try {
                    LocalTime dep = LocalTime.parse(depS);

                    boolean nextDay = arrS.contains("+1d");
                    arrS = arrS.replace(" (+1d)", "")
                            .replace("(+1d)", "")
                            .trim();

                    LocalTime arr = LocalTime.parse(arrS);

                    Duration duration = Duration.between(dep, arr);
                    if (nextDay) duration = duration.plusHours(24);

                    double firstRate = Double.parseDouble(firstRateS);
                    double secondRate = Double.parseDouble(secondRateS);

                    List<String> daysOfOp = new ArrayList<>();
                    if (!days.isEmpty()) {
                        for (String d : days.split("[,;\\s]+")) {
                            if (!d.isBlank()) daysOfOp.add(d.trim());
                        }
                    }
                    //Construct the route objects
                    routes.add(new Route(
                            id,
                            from,
                            to,
                            trainType,
                            dep,
                            arr,
                            daysOfOp,
                            firstRate,
                            secondRate,
                            duration
                    ));

                } catch (Exception badRow) {
                    skipped++;
                }
            }
        }

        if (routes.isEmpty()) {
            throw new IOException("No valid rows parsed (skipped=" + skipped + ").");
        }

        System.out.println("[CSV] Parsed " + routes.size() + " routes; skipped " + skipped + " malformed rows.");
        return routes;
    }

    private static String[] splitCSV(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());

        for (int i = 0; i < result.size(); i++) {
            String cell = result.get(i);
            if (cell.startsWith("\"") && cell.endsWith("\"") && cell.length() >= 2) {
                cell = cell.substring(1, cell.length() - 1);
            }
            result.set(i, cell);
        }

        return result.toArray(new String[0]);
    }
}

