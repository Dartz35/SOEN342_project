import java.time.Duration;
import java.time.LocalTime;

public class LayoverPolicy {
    // Defines the "daytime" period
    private final int policyId;
    private final LocalTime daytimeStart;
    private final LocalTime daytimeEnd;
    private final Duration maxDaytimeLayover;
    private final Duration maxNightLayover;

    /**
     * Creates a new, specific layover policy.
     *
     * @param policyId   The ID for this policy.
     * @param dayStart   The time "day" begins (e.g., 06:00).
     * @param dayEnd     The time "day" ends (e.g., 22:00).
     * @param maxDay     Maximum layover allowed during the day (e.g., 2 hours).
     * @param maxNight   Maximum layover allowed at night (e.g., 30 minutes).
     */
    public LayoverPolicy(int policyId, LocalTime dayStart, LocalTime dayEnd,
                           Duration maxDay, Duration maxNight) {
        this.policyId = policyId;
        this.daytimeStart = dayStart;
        this.daytimeEnd = dayEnd;
        this.maxDaytimeLayover = maxDay;
        this.maxNightLayover = maxNight;
    }

    /**
     * Checks if a specific connection (layover) is allowed by this policy.
     *
     * @param arrivalTime   The arrival time of the first leg.
     * @param departureTime The departure time of the next leg.
     * @return true if the layover is within the allowed bounds, false otherwise.
     */
    public boolean isLayoverAllowed(LocalTime arrivalTime, LocalTime departureTime) {
        // --- This logic now correctly handles overnight layovers ---
        long gapInSeconds = departureTime.toSecondOfDay() - arrivalTime.toSecondOfDay();
        if (gapInSeconds < 0) {
            gapInSeconds += 24 * 3600L; // Add 24 hours in seconds
        }
        Duration layover = Duration.ofSeconds(gapInSeconds);

        // Check if the layover *starts* during the day.
        // A layover starting at 21:00 is a "day" layover.
        boolean isDaytime = !arrivalTime.isBefore(daytimeStart) && arrivalTime.isBefore(daytimeEnd);

        if (isDaytime) {
            // Check if layover is within maxDay
            return layover.compareTo(maxDaytimeLayover) <= 0;
        } else {
            // Check if layover is within maxNight
            return layover.compareTo(maxNightLayover) <= 0;
        }
    }

    @Override
    public String toString() {
        return String.format(
        "[LayoverPolicy #%d] Daytime: %s -> %s | Max Day: %d hours | Max Night: %d hours",
        policyId,
        daytimeStart,
        daytimeEnd,
        maxDaytimeLayover.toHours(),
        maxNightLayover.toHours()
    );
    }
}

