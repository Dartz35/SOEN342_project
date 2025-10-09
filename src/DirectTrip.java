import java.util.List;
import java.util.Objects;

public final class DirectTrip extends Trip {
    public DirectTrip(String id, Route leg) {
        super(id, List.of(Objects.requireNonNull(leg, "leg")));
    }
    public DirectTrip(String id, List<Route> legs) {
        super(id, legs);
        if (legs.size() != 1) throw new IllegalArgumentException("DirectTrip must have exactly 1 leg");
    }
}