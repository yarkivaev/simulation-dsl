package io.yarkivaev.sim.distribution;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

/**
 * Tests for {@link Normal} distribution.
 */
final class NormalTest {

    @Test
    void samplesValueNearMean() {
        double sum = 0;
        Random rng = new Random(42);
        Distribution dist = new Normal(100.0, 5.0, rng);
        for (int idx = 0; idx < 10000; idx++) {
            sum += dist.sample();
        }
        assertThat(
            "Normal distribution average did not converge near the mean",
            sum / 10000, closeTo(100.0, 1.0)
        );
    }

    @Test
    void samplesValueAboveLowerBound() {
        Random rng = new Random(7);
        Distribution dist = new Normal(50.0, 0.001, rng);
        assertThat(
            "Normal sample with tiny deviation was not above 49",
            dist.sample(), greaterThan(49.0)
        );
    }

    @Test
    void samplesValueBelowUpperBound() {
        Random rng = new Random(13);
        Distribution dist = new Normal(50.0, 0.001, rng);
        assertThat(
            "Normal sample with tiny deviation was not below 51",
            dist.sample(), lessThan(51.0)
        );
    }
}
