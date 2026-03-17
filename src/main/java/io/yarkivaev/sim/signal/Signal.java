package io.yarkivaev.sim.signal;

import java.time.Instant;

/**
 * Time-varying value source that produces readings at given instants.
 *
 * <p>Example usage:
 * <pre>
 *   Signal hr = new Periodic(new Normal(75, 5, rng));
 *   double bpm = hr.value(Instant.now());
 * </pre>
 */
public interface Signal {

    /**
     * Produces a value at the given instant.
     *
     * @param instant the point in time
     * @return the signal value
     */
    double value(Instant instant);
}
