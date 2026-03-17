package io.yarkivaev.sim.signal;

import io.yarkivaev.sim.distribution.Constant;
import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests for {@link Periodic} signal.
 */
final class PeriodicTest {

    @Test
    void producesDistributionValue() {
        Signal signal = new Periodic(new Constant(73.0));
        assertThat(
            "Periodic signal did not return the distribution value",
            signal.value(Instant.parse("2025-01-15T10:00:00Z")),
            equalTo(73.0)
        );
    }

    @Test
    void ignoresTimestamp() {
        Signal signal = new Periodic(new Constant(42.0));
        assertThat(
            "Periodic signal returned different values for different instants",
            signal.value(Instant.EPOCH), equalTo(signal.value(Instant.MAX))
        );
    }
}
