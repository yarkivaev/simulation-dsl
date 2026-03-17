package io.yarkivaev.sim.distribution;

import java.util.Random;

/**
 * Uniform distribution sampler over a continuous range.
 *
 * <p>Example usage:
 * <pre>
 *   Distribution noise = new Uniform(-2.0, 2.0, new Random());
 *   double offset = noise.sample();
 * </pre>
 *
 * @param low lower bound (inclusive)
 * @param high upper bound (exclusive)
 * @param random source of randomness
 */
public record Uniform(double low, double high, Random random) implements Distribution {

    /**
     * Draws a value uniformly between low and high.
     *
     * @return value in [low, high)
     */
    @Override
    public double sample() {
        return this.low + this.random.nextDouble() * (this.high - this.low);
    }
}
