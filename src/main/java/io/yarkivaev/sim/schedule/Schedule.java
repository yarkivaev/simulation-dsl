package io.yarkivaev.sim.schedule;

/**
 * Marker for time-bounded simulation elements.
 *
 * <p>A schedule is either a {@link Segment} (has duration) or
 * a {@link Point} (instantaneous).
 *
 * <p>Example usage:
 * <pre>
 *   Schedule s = new Segment(Instant.now(), Duration.ofMinutes(60));
 *   Schedule p = new Point(Instant.now());
 * </pre>
 */
public sealed interface Schedule permits Segment, Point {
}
