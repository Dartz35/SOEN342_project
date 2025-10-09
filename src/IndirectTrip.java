import java.util.List;

public final class IndirectTrip extends Trip {
    public IndirectTrip(String id, List<Route> legs) {
        super(id, legs);
        int n = legs.size();
        if (n < 2 || n > 3) { // <=— enforce: 2..3 legs (max 2 connections)
            throw new IllegalArgumentException("IndirectTrip must have 2 or 3 legs (max 2 connections)");
        }
    }
}
