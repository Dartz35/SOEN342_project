import java.time.LocalTime;

public final class TripFinder {


    private static boolean matches(Route r, SearchCriteria c) {
        switch (c.attribute.toLowerCase()) {
            case "from":
            case "origin":
            case "departure city":
                return r.getFrom().equalsIgnoreCase(c.value);

            case "to":
            case "destination":
            case "arrival city":
                return r.getTo().equalsIgnoreCase(c.value);

            case "departure":
            case "departure time":
                return timeEq(r.getDepartureTime(), c.value);

            case "arrival":
            case "arrival time":
                return timeEq(r.getArrivalTime(), c.value);

            case "duration":
            case "duration minutes":
                try {
                    long want = Long.parseLong(c.value);
                    return r.getScheduledDuration().toMinutes() == want;
                } catch (NumberFormatException e) { return false; }

            case "first rate":
                try {
                    double limit = Double.parseDouble(c.value);
                    return r.getFirstRate() <= limit; 
                } catch (NumberFormatException e) { return false; }

            case "second rate":
                try {
                    double limit = Double.parseDouble(c.value);
                    return r.getSecondRate() <= limit; 
                } catch (NumberFormatException e) { return false; }

            case "train type":
                return r.getTrainType() != null && r.getTrainType().equalsIgnoreCase(c.value);

            case "day of operation":
                return r.getDaysOfOp() != null &&
                        r.getDaysOfOp().stream()
                                .anyMatch(d -> d.equalsIgnoreCase(c.value));

            default:
    
                return r.getId().equalsIgnoreCase(c.value);
        }
    }

    private static boolean timeEq(LocalTime t, String v) {
        try { return t.equals(LocalTime.parse(v)); }
        catch (Exception e) { return false; }
    }


}