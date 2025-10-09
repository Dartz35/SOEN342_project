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

