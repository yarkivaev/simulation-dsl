package io.yarkivaev.sim.distribution;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;

/**
 * Tests for {@link Uniform} distribution.
 */
final class UniformTest {

    @Test
    void samplesValueAtOrAboveLow() {
        Random rng = new Random(99);
        Distribution dist = new Uniform(-3.0, 7.0, rng);
        assertThat(
            "Uniform sample was below the lower bound",
            dist.sample(), greaterThanOrEqualTo(-3.0)
        );
    }

    @Test
    void samplesValueBelowHigh() {
        Random rng = new Random(101);
        Distribution dist = new Uniform(10.0, 20.0, rng);
        assertThat(
            "Uniform sample was not below the upper bound",
            dist.sample(), lessThan(20.0)
        );
    }
}
