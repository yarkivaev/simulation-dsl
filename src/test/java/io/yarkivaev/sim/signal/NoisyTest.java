package io.yarkivaev.sim.signal;

import io.yarkivaev.sim.distribution.Constant;
import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests for {@link Noisy} signal.
 */
final class NoisyTest {

    @Test
    void addsNoiseToBase() {
        Signal base = new Periodic(new Constant(100.0));
        Signal noisy = new Noisy(base, new Constant(5.0));
        assertThat(
            "Noisy signal did not add noise to base value",
            noisy.value(Instant.parse("2025-06-01T12:00:00Z")),
            equalTo(105.0)
        );
    }

    @Test
    void addsNegativeNoise() {
        Signal base = new Periodic(new Constant(50.0));
        Signal noisy = new Noisy(base, new Constant(-3.0));
        assertThat(
            "Noisy signal did not subtract negative noise from base",
            noisy.value(Instant.parse("2025-06-01T12:00:00Z")),
            equalTo(47.0)
        );
    }
}
