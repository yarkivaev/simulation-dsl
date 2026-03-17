package io.yarkivaev.sim.schedule;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Tests for {@link Point} schedule.
 */
final class PointTest {

    @Test
    void retainsInstant() {
        Instant when = Instant.parse("2025-12-25T00:00:00Z");
        Point point = new Point(when);
        assertThat(
            "Point did not retain the instant",
            point.instant(), equalTo(when)
        );
    }

    @Test
    void implementsSchedule() {
        Point point = new Point(Instant.EPOCH);
        assertThat(
            "Point did not implement Schedule",
            (Schedule) point, instanceOf(Schedule.class)
        );
    }
}
