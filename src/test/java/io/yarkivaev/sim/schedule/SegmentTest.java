package io.yarkivaev.sim.schedule;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests for {@link Segment} schedule.
 */
final class SegmentTest {

    @Test
    void retainsStart() {
        Instant start = Instant.parse("2025-04-01T09:00:00Z");
        Segment segment = new Segment(start, Duration.ofMinutes(45));
        assertThat(
            "Segment did not retain the start instant",
            segment.start(), equalTo(start)
        );
    }

    @Test
    void retainsDuration() {
        Duration duration = Duration.ofHours(3);
        Segment segment = new Segment(Instant.EPOCH, duration);
        assertThat(
            "Segment did not retain the duration",
            segment.duration(), equalTo(duration)
        );
    }
}
