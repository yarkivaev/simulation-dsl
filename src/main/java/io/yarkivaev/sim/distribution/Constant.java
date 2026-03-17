package io.yarkivaev.sim.distribution;

/**
 * Deterministic distribution that always returns the same value.
 *
 * <p>Example usage:
 * <pre>
 *   Distribution fio2 = new Constant(40.0);
 *   double percent = fio2.sample(); // always 40.0
 * </pre>
 *
 * @param value the constant value to return
 */
public record Constant(double value) implements Distribution {

    /**
     * Returns the constant value.
     *
     * @return the fixed value
     */
    @Override
    public double sample() {
        return this.value;
    }
}
