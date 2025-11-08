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


    private static final Random random = new Random();

    protected Trip(String id, List<Route> legs) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id must not be null/blank");
        if (legs == null || legs.isEmpty()) throw new IllegalArgumentException("legs must not be null/empty");
        this.id = generateTripId();
        this.legs = Collections.unmodifiableList(new ArrayList<>(legs));
        this.from = this.legs.get(0).getFrom();
        this.to = this.legs.get(legs.size() - 1).getTo();
        this.departureTime = this.legs.get(0).getDepartureTime();
        this.arrivalTime = this.legs.get(legs.size() - 1).getArrivalTime();
        this.timeToChangeConnection = getTotalTransferTime();
        this.totalDuration = getTotalTravelTime();
        this.totalFirstRate = getTotalFirstRate();
        this.totalSecondRate = getTotalSecondRate();
        this.daysOfOp = getAllDaysOfOp();
        this.trainTypes = getAllTrainTypes();


        if (!isChained()) throw new IllegalArgumentException("Legs must chain from->to");
    }

    public String getId() { return id; }
    public List<Route> getLegs() { return legs; }

    public String getOrigin() { return this.from; }
    public String getDestination() { return this.to; }

    public List<String> getTrainType(){return this.trainTypes;}
    public int getConnectionsCount() { return legs.size() - 1; }
    public LocalTime getFirstDepartureTime() { return departureTime; }
    public LocalTime getFinalArrivalTime()   { return arrivalTime; }

    public List<String> getDaysOfOp(){return this.daysOfOp;}

    public Duration getTotalInTrainTime() {
        Duration sum = Duration.ZERO;
        for (Route r : legs) sum = sum.plus(r.getScheduledDuration());
        return sum;
    }
    private String generateTripId() {
    // Generate an 8-digit random number between 10000000 and 99999999
        int id = 10_000 + random.nextInt(90_000); // 10000–99999
         return String.valueOf(id);
    }
    public Duration getTotalTransferTime() {
        if (legs.size() <= 1) return Duration.ZERO;
        Duration total = Duration.ZERO;
        for (int i = 0; i < legs.size() - 1; i++) {
            long gap = legs.get(i + 1).getDepartureTime().toSecondOfDay()
                    - legs.get(i).getArrivalTime().toSecondOfDay();
            if (gap < 0) gap += 24 * 3600L;
            total = total.plusSeconds(gap);
        }
        return total;
    }

    public Duration getTotalTravelTime() { return getTotalInTrainTime().plus(getTotalTransferTime()); }

    public double getTotalFirstRate() {
        double sum = 0;
        for (Route r : legs) sum += r.getFirstRate();
        return sum;
    }

    public double getTotalSecondRate() {
        double sum = 0;
        for (Route r : legs) sum += r.getSecondRate();
        return sum;
    }

    public List<String> getAllTrainTypes() {

        List<String> trainTypes = new ArrayList<>();
        for (Route r : legs) trainTypes.add(r.getTrainType());

        List<String> finalTrainTypes = new ArrayList<>(new LinkedHashSet<>(trainTypes));

        return finalTrainTypes;
    }

    public List<String> getAllDaysOfOp() {
        if (legs == null || legs.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> common = new HashSet<>(legs.get(0).getDaysOfOp());

        for (int i = 1; i < legs.size(); i++) {
            common.retainAll(legs.get(i).getDaysOfOp());
        }

        List<String> result = new ArrayList<>(common);
        Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    private boolean isChained() {
        for (int i = 0; i < legs.size() - 1; i++) {
            if (!Objects.equals(legs.get(i).getTo(), legs.get(i + 1).getFrom())) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", " + getOrigin() + "→" + getDestination() +
                ", legs=" + legs.size() +
                ", dep=" + getFirstDepartureTime() +
                ", arr=" + getFinalArrivalTime() +
                ", dur=" + getTotalTravelTime().toMinutes() + "min" +
                ", changeTime=" + formatDuration(timeToChangeConnection) +
                ", First Rate=" + getTotalFirstRate() + "€" +
                ", Second Rate=" + getTotalSecondRate() + "€" +
                ", TrainTypes=" + formatList(getTrainType()) +
                ", DaysOfOp=" + formatList(getDaysOfOp()) +
                '}';
    }

    private static String formatList(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        return "[" + String.join(", ", list) + "]";
    }

    private static String formatDuration(java.time.Duration d) {
        if (d == null || d.isZero()) return "0min";
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        if (hours > 0)
            return hours + "h " + minutes + "m";
        else
            return minutes + "min";
    }


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;
        Trip trip = (Trip) o;
        return id.equals(trip.id) && legs.equals(trip.legs);
    }

    @Override public int hashCode() { return Objects.hash(id, legs); }
}
