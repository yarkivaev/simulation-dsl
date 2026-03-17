package io.yarkivaev.sim.signal;

import io.yarkivaev.sim.distribution.Distribution;
import java.time.Instant;

/**
 * Signal that samples a distribution on each tick, ignoring time.
 *
 * <p>Example usage:
 * <pre>
 *   Signal hr = new Periodic(new Normal(75, 5, rng));
 *   double bpm = hr.value(Instant.now());
 * </pre>
 *
 * @param distribution source of random values
 */
public record Periodic(Distribution distribution) implements Signal {

    /**
     * Samples the distribution at the given instant.
     *
     * @param instant the point in time (unused, each call samples fresh)
     * @return a freshly sampled value
     */
    @Override
    public double value(final Instant instant) {
        return this.distribution.sample();
    }
}
