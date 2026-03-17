package io.yarkivaev.sim.distribution;

import java.util.Random;

/**
 * Gaussian (normal) distribution sampler.
 *
 * <p>Example usage:
 * <pre>
 *   Distribution heartRate = new Normal(75.0, 5.0, new Random());
 *   double bpm = heartRate.sample();
 * </pre>
 *
 * @param mean center of the bell curve
 * @param deviation standard deviation
 * @param random source of randomness
 */
public record Normal(double mean, double deviation, Random random) implements Distribution {

    /**
     * Draws a value from the normal distribution.
     *
     * @return value centered on mean with given deviation
     */
    @Override
    public double sample() {
        return this.mean + this.random.nextGaussian() * this.deviation;
    }
}
