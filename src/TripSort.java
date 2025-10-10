import java.util.*;

public final class TripSort {
    private TripSort() {}

    public static List<Trip> sort(List<Trip> trips, SortBy key, SortOrder order) {
        List<Trip> out = new ArrayList<>(trips);
        Comparator<Trip> cmp;

        switch (key) {
            case DEPARTURE_TIME:
                cmp = Comparator.comparing(Trip::getFirstDepartureTime);
                break;
            case ARRIVAL_TIME:
                cmp = Comparator.comparing(Trip::getFinalArrivalTime);
                break;
            case ORIGIN_CITY:
                cmp = Comparator.comparing(Trip::getOrigin, String.CASE_INSENSITIVE_ORDER);
                break;
            case DESTINATION_CITY:
                cmp = Comparator.comparing(Trip::getDestination, String.CASE_INSENSITIVE_ORDER);
                break;
            case DURATION:
                cmp = Comparator.comparingLong(t -> t.getTotalTravelTime().toMinutes());
                break;
            case FIRST_RATE:
                cmp = Comparator.comparingDouble(Trip::getTotalFirstRate);
                break;
            case SECOND_RATE:
                cmp = Comparator.comparingDouble(Trip::getTotalSecondRate);
                break;
            case TRAIN_TYPE:
                cmp = Comparator.comparing(
                        t -> String.join(",", normalizeList(t.getTrainType())),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;
            case DAY_OF_OP:
                cmp = Comparator.comparing(
                        t -> String.join(",", normalizeList(t.getDaysOfOp())),
                        String.CASE_INSENSITIVE_ORDER
                );
                break;
            default:
                cmp = Comparator.comparing(Trip::getFinalArrivalTime);
        }

        if (order == SortOrder.DESC)
            cmp = cmp.reversed();

        out.sort(cmp.thenComparing(Trip::getConnectionsCount));
        return out;
    }


    private static List<String> normalizeList(List<String> list) {
        if (list == null) return Collections.emptyList();
        List<String> copy = new ArrayList<>(list);
        copy.replaceAll(String::toLowerCase);
        Collections.sort(copy);
        return copy;
    }
}
