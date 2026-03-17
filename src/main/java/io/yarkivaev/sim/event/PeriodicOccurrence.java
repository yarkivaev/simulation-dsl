package io.yarkivaev.sim.event;

import io.yarkivaev.sim.distribution.Distribution;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Event occurrence at fixed intervals with random jitter.
 *
 * <p>Example usage:
 * <pre>
 *   Occurrence hourly = new PeriodicOccurrence(
 *       Duration.ofHours(1), new Uniform(-300, 300, rng)
 *   );
 *   Optional<Instant> next = hourly.next(Instant.now());
 * </pre>
 *
 * @param interval base interval between occurrences
 * @param jitter distribution of jitter in seconds added to interval
 */
public record PeriodicOccurrence(Duration interval, Distribution jitter)
    implements Occurrence {

    /**
     * Computes the next occurrence by adding interval plus jitter.
     *
     * @param after reference instant
     * @return the next occurrence
     */
    @Override
    public Optional<Instant> next(final Instant after) {
        long seconds = this.interval.toSeconds() + (long) this.jitter.sample();
        return Optional.of(after.plusSeconds(Math.max(1L, seconds)));
    }
}
