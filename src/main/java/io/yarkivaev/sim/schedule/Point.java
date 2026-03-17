package io.yarkivaev.sim.schedule;

import java.time.Instant;

/**
 * An instantaneous schedule at a single point in time.
 *
 * <p>Example usage:
 * <pre>
 *   Point pt = new Point(Instant.now());
 *   Instant when = pt.instant();
 * </pre>
 *
 * @param instant when the event occurs
 */
public record Point(Instant instant) implements Schedule {
}
