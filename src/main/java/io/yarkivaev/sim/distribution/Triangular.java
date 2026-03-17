package io.yarkivaev.sim.distribution;

import java.util.Random;

/**
 * Triangular distribution sampler defined by minimum, peak, and maximum.
 *
 * <p>Example usage:
 * <pre>
 *   Distribution temp = new Triangular(36.0, 36.6, 37.2, new Random());
 *   double celsius = temp.sample();
 * </pre>
 *
 * @param low minimum value
 * @param peak most likely value
 * @param high maximum value
 * @param random source of randomness
 */
public record Triangular(double low, double peak, double high, Random random)
    implements Distribution {

    /**
     * Draws a value from the triangular distribution.
     *
     * @return value between low and high, clustered near peak
     */
    @Override
    public double sample() {
        double uniform = this.random.nextDouble();
        double pivot = (this.peak - this.low) / (this.high - this.low);
        if (uniform < pivot) {
            return this.low
                + Math.sqrt(uniform * (this.high - this.low) * (this.peak - this.low));
        }
        return this.high
            - Math.sqrt((1.0 - uniform) * (this.high - this.low) * (this.high - this.peak));
    }
}
