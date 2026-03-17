package io.yarkivaev.sim.distribution;

/**
 * Random value source following a probability distribution.
 *
 * <p>Example usage:
 * <pre>
 *   Distribution dist = new Normal(75.0, 5.0, new Random());
 *   double value = dist.sample();
 * </pre>
 */
public interface Distribution {

    /**
     * Draws a single random value from this distribution.
     *
     * @return sampled value
     */
    double sample();
}
