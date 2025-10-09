import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/** Single rail connection (one leg). */
public final class Route {
    private final String id;
    private final String from;
    private final String to;
    private final List<String> daysOfOp;
    private final String trainType;
    private final LocalTime departureTime;
    private final LocalTime arrivalTime;
    private final Duration scheduledDuration;

    private  final double firstRate;

    private final double secondRate;
    //private final double price; // NEW

    public Route(String id, String from, String to,String trainType, LocalTime departureTime, LocalTime arrivalTime,List<String> daysOfOp, double firstRate,double secondRate,Duration scheduledDuration) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id must not be null/blank");
        if (from == null || from.isBlank()) throw new IllegalArgumentException("from must not be null/blank");
        if (to == null || to.isBlank()) throw new IllegalArgumentException("to must not be null/blank");
        if (departureTime == null) throw new IllegalArgumentException("departureTime must not be null");
        if (arrivalTime == null) throw new IllegalArgumentException("arrivalTime must not be null");

        this.id = id;
        this.from = from;
        this.to = to;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.scheduledDuration = scheduledDuration;
        this.daysOfOp = daysOfOp;
        this.firstRate = firstRate;
        this.secondRate = secondRate;
        this.trainType = trainType;
    }

    
    private static Duration computeDuration(LocalTime dep, LocalTime arr) {
        long depSec = dep.toSecondOfDay();
        long arrSec = arr.toSecondOfDay();
        long diff = arrSec - depSec;
        if (diff < 0) diff += 24 * 3600L; // overnight
        return Duration.ofSeconds(diff);
    }

    public String getId() { return id; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public LocalTime getDepartureTime() { return departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public Duration getScheduledDuration() { return scheduledDuration; }
    public double getFirstRate() { return firstRate; }

    public double getSecondRate() { return secondRate; }

    public List<String> getDaysOfOp() {return daysOfOp;}

    public String getTrainType() {return trainType;}


    @Override public String toString() {
        return "Route{" + id + " " + from + "→" + to + " dep=" + departureTime + " arr=" + arrivalTime +
                " dur=" + scheduledDuration.toMinutes() + "first rate =" + firstRate + "second rate ="+secondRate+" }";
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return Double.compare(route.firstRate, firstRate) == 0 &&
                Double.compare(route.secondRate, secondRate) == 0 &&
                id.equals(route.id) &&
                from.equals(route.from) &&
                to.equals(route.to) &&
                departureTime.equals(route.departureTime) &&
                arrivalTime.equals(route.arrivalTime);
    }

    @Override public int hashCode() {
        return Objects.hash(id, from, to, departureTime, arrivalTime, firstRate,secondRate);
    }
}
