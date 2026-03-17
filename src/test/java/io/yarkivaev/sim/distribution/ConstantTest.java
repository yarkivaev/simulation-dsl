package io.yarkivaev.sim.distribution;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests for {@link Constant} distribution.
 */
final class ConstantTest {

    @Test
    void samplesExactValue() {
        Distribution dist = new Constant(42.5);
        assertThat(
            "Constant distribution did not return the exact value",
            dist.sample(), equalTo(42.5)
        );
    }

    @Test
    void samplesNegativeValue() {
        Distribution dist = new Constant(-999.123);
        assertThat(
            "Constant distribution did not return the negative value",
            dist.sample(), equalTo(-999.123)
        );
    }
}
