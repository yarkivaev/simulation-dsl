package io.yarkivaev.sim.distribution;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * Tests for {@link Poisson} distribution.
 */
final class PoissonTest {

    @Test
    void samplesNonNegativeCount() {
        Random rng = new Random(55);
        Distribution dist = new Poisson(3.0, rng);
        assertThat(
            "Poisson sample was negative",
            dist.sample(), greaterThanOrEqualTo(0.0)
        );
    }

    @Test
    void samplesAverageNearLambda() {
        double sum = 0;
        Random rng = new Random(12);
        Distribution dist = new Poisson(5.0, rng);
        for (int idx = 0; idx < 10000; idx++) {
            sum += dist.sample();
        }
        assertThat(
            "Poisson distribution average did not converge near lambda",
            sum / 10000, closeTo(5.0, 0.5)
        );
    }
}
