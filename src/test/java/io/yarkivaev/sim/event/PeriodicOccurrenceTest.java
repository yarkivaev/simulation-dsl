package io.yarkivaev.sim.event;

import io.yarkivaev.sim.distribution.Constant;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link PeriodicOccurrence}.
 */
final class PeriodicOccurrenceTest {

    @Test
    void computesNextWithZeroJitter() {
        Occurrence occ = new PeriodicOccurrence(
            Duration.ofHours(2), new Constant(0)
        );
        Instant base = Instant.parse("2025-03-01T00:00:00Z");
        assertThat(
            "Next occurrence was not exactly 2 hours after base",
            occ.next(base),
            equalTo(Optional.of(Instant.parse("2025-03-01T02:00:00Z")))
        );
    }

    @Test
    void computesNextWithPositiveJitter() {
        Occurrence occ = new PeriodicOccurrence(
            Duration.ofMinutes(30), new Constant(60)
        );
        Instant base = Instant.parse("2025-03-01T10:00:00Z");
        assertThat(
            "Next occurrence did not include the jitter offset",
            occ.next(base),
            equalTo(Optional.of(Instant.parse("2025-03-01T10:31:00Z")))
        );
    }

    @Test
    void clampsToMinimumOneSecond() {
        Occurrence occ = new PeriodicOccurrence(
            Duration.ofSeconds(1), new Constant(-100)
        );
        Instant base = Instant.parse("2025-03-01T00:00:00Z");
        assertThat(
            "Next occurrence was not clamped to at least 1 second",
            occ.next(base),
            equalTo(Optional.of(Instant.parse("2025-03-01T00:00:01Z")))
        );
    }

    @Test
    void returnsPresent() {
        Occurrence occ = new PeriodicOccurrence(
            Duration.ofMinutes(5), new Constant(0)
        );
        assertThat(
            "Periodic occurrence did not return a present value",
            occ.next(Instant.now()).isPresent(), is(true)
        );
    }
}
