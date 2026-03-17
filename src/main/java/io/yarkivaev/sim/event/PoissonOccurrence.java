package io.yarkivaev.sim.event;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

/**
 * Event occurrence with Poisson-distributed inter-arrival times.
 *
 * <p>Example usage:
 * <pre>
 *   Occurrence arrival = new PoissonOccurrence(
 *       Duration.ofMinutes(30), new Random()
 *   );
 *   Optional&lt;Instant&gt; next = arrival.next(Instant.now());
 * </pre>
 *
 * @param rate mean inter-arrival duration
 * @param random source of randomness
 */
public record PoissonOccurrence(Duration rate, Random random) implements Occurrence {

    /**
     * Computes next occurrence using exponential inter-arrival time.
     *
     * @param after reference instant
     * @return the next occurrence
     */
    @Override
    public Optional<Instant> next(final Instant after) {
        double seconds = -this.rate.toSeconds()
            * Math.log(1.0 - this.random.nextDouble());
        return Optional.of(after.plusSeconds(Math.max(1L, (long) seconds)));
    }
}
