package io.yarkivaev.sim.distribution;

import java.util.Random;

/**
 * Poisson distribution sampler using Knuth's algorithm.
 *
 * <p>Example usage:
 * <pre>
 *   Distribution events = new Poisson(4.0, new Random());
 *   double count = events.sample();
 * </pre>
 *
 * @param lambda expected number of occurrences
 * @param random source of randomness
 */
public record Poisson(double lambda, Random random) implements Distribution {

    /**
     * Draws an integer count from the Poisson distribution.
     *
     * @return sampled count as a double
     */
    @Override
    public double sample() {
        double limit = Math.exp(-this.lambda);
        double product = 1.0;
        int count = -1;
        do {
            count++;
            product *= this.random.nextDouble();
        } while (product > limit);
        return count;
    }
}
