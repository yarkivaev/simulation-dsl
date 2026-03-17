package io.yarkivaev.sim.event;

import java.time.Instant;
import java.util.Optional;

/**
 * Strategy for determining when the next event should occur.
 *
 * <p>Example usage:
 * <pre>
 *   Occurrence sched = new PeriodicOccurrence(
 *       Duration.ofHours(4), new Constant(0)
 *   );
 *   Optional<Instant> next = sched.next(Instant.now());
 * </pre>
 */
public interface Occurrence {

    /**
     * Calculates the next occurrence after the given instant.
     *
     * @param after reference instant
     * @return the next occurrence instant, or empty if none
     */
    Optional<Instant> next(Instant after);
}
