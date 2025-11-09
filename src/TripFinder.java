import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class TripFinder {

    

    public static List<Trip> findDirect(List<Route> routes, SearchCriteria crit) {
        return routes.stream()
                .filter(r -> matches(r, crit))
                .map(r -> new DirectTrip("D:" + r.getId(), r))
                .collect(Collectors.toList());
    }


    public static List<Trip> findIndirectFromTo(List<Route> routes, String fromCity, String toCity, LayoverPolicy lp) {
        List<Trip> foundTrips = new ArrayList<>();


        for (Route a : routes) {
            if (a.getFrom().equalsIgnoreCase(fromCity) && a.getTo().equalsIgnoreCase(toCity)) {
                foundTrips.add(new DirectTrip("D:" + a.getId(), a)); 
            }
        }

 
        for (Route a : routes) {
           
            if (a.getFrom().equalsIgnoreCase(fromCity)) {

           
                if (a.getTo().equalsIgnoreCase(toCity)) {
                    continue; 
                }

                if (a.getTo().equalsIgnoreCase(fromCity)) {
                    continue; 
                }

                for (Route b : routes) {
              
                    if (isValidConnection(a, b)) {
                        
               
                        if (b.getTo().equalsIgnoreCase(toCity)) {
                            try {
                                foundTrips.add(new IndirectTrip( List.of(a, b), lp));
                            } catch (IllegalArgumentException e) {
                            }
                        }
    
                        else {
                            
                            if (b.getTo().equalsIgnoreCase(fromCity)) {
                                continue; 
                            }

                            for (Route c : routes) {
                                if (isValidConnection(b, c) && c.getTo().equalsIgnoreCase(toCity)) {
                                    try {
                                        foundTrips.add(new IndirectTrip(List.of(a, b, c), lp));
                                    } catch (IllegalArgumentException e) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return foundTrips;
    }


    private static boolean isValidConnection(Route first, Route next) {
        if (!first.getTo().equals(next.getFrom())) {
            return false;
        }

        long gapInSeconds = next.getDepartureTime().toSecondOfDay() - first.getArrivalTime().toSecondOfDay();
        if (gapInSeconds < 0) {
            gapInSeconds += 24 * 3600L; 
        }

        return gapInSeconds >= 0; 
    }

   
    public static List<Trip> findIndirectIfNoDirect(List<Route> routes, SearchCriteria crit,LayoverPolicy lp) {
        
        List<Trip> direct = findDirect(routes, crit);
        if (!direct.isEmpty()) return direct;

        List<Trip> indirect = new ArrayList<>();
        for (Route a : routes) {
            for (Route b : routes) {
                if (isValidConnection(a, b)) {
                    if (matches(a, crit) || matches(b, crit)) {
                        try {
                            indirect.add(new IndirectTrip(List.of(a, b), lp));
                        } catch (IllegalArgumentException e) { }
                    }
                }
            }
        }
        return indirect;
    }


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
                } catch (NumberFormatException e) {
                    return false;
                }

            case "first rate":
                try {
                    double limit = Double.parseDouble(c.value);
                    return r.getFirstRate() <= limit;
                } catch (NumberFormatException e) {
                    return false;
                }

            case "second rate":
                try {
                    double limit = Double.parseDouble(c.value);
                    return r.getSecondRate() <= limit;
                } catch (NumberFormatException e) {
                    return false;
                }

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
        
        try {

            return t.equals(LocalTime.parse(v));

        } catch (Exception e) {

            return false;

        }
    }
}
