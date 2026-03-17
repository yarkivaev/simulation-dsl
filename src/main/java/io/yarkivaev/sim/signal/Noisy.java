package io.yarkivaev.sim.signal;

import io.yarkivaev.sim.distribution.Distribution;
import java.time.Instant;

/**
 * Decorator that adds noise from a distribution to a base signal.
 *
 * <p>Example usage:
 * <pre>
 *   Signal noisy = new Noisy(baseSignal, new Uniform(-2, 2, rng));
 *   double value = noisy.value(Instant.now());
 * </pre>
 *
 * @param base the underlying signal
 * @param noise distribution of additive noise
 */
public record Noisy(Signal base, Distribution noise) implements Signal {

    /**
     * Produces the base signal value plus a noise sample.
     *
     * @param instant the point in time
     * @return base value plus noise
     */
    @Override
    public double value(final Instant instant) {
        return this.base.value(instant) + this.noise.sample();
    }
}
