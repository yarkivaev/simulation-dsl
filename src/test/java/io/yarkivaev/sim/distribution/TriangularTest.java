package io.yarkivaev.sim.distribution;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * Tests for {@link Triangular} distribution.
 */
final class TriangularTest {

    @Test
    void samplesValueAtOrAboveLow() {
        Random rng = new Random(77);
        Distribution dist = new Triangular(10.0, 15.0, 20.0, rng);
        assertThat(
            "Triangular sample was below the minimum",
            dist.sample(), greaterThanOrEqualTo(10.0)
        );
    }

    @Test
    void samplesValueAtOrBelowHigh() {
        Random rng = new Random(88);
        Distribution dist = new Triangular(1.0, 5.0, 9.0, rng);
        assertThat(
            "Triangular sample exceeded the maximum",
            dist.sample(), lessThanOrEqualTo(9.0)
        );
    }
}
