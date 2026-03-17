package io.yarkivaev.sim.schedule;

import java.time.Duration;
import java.time.Instant;

/**
 * A schedule with a start time and a duration.
 *
 * <p>Example usage:
 * <pre>
 *   Segment seg = new Segment(Instant.now(), Duration.ofMinutes(30));
 *   Instant begin = seg.start();
 *   Duration len = seg.duration();
 * </pre>
 *
 * @param start when the segment begins
 * @param duration how long the segment lasts
 */
public record Segment(Instant start, Duration duration) implements Schedule {
}
