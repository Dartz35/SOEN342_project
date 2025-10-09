import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public abstract class Trip {
    private final String id;
    private final List<Route> legs;
    private final String from;
    private final String to;
    private final LocalTime departureTime;
    private final LocalTime arrivalTime;
    private final Duration totalDuration;

    private final Duration timeToChangeConnection;

    private final double totalFirstRate;

    private final double totalSecondRate;

    private List<String> daysOfOp;

    private List<String> trainTypes;

    protected Trip(String id, List<Route> legs) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id must not be null/blank");
        if (legs == null || legs.isEmpty()) throw new IllegalArgumentException("legs must not be null/empty");
        this.id = id;
        this.legs = Collections.unmodifiableList(new ArrayList<>(legs));
        this.from = this.legs.get(0).getFrom();
        this.to = this.legs.get(legs.size() - 1).getTo();
        this.departureTime = this.legs.get(0).getDepartureTime();
        this.arrivalTime = this.legs.get(legs.size() - 1).getArrivalTime();
    }
}
