package io.yarkivaev.sim.signal;

import java.time.Duration;
import java.time.Instant;

/**
 * Signal that produces a deterministic sinusoidal curve as a function of time.
 *
 * <p>The value at instant {@code t} is
 * {@code baseline + amplitude * sin(2*pi * seconds(t) / seconds(period))},
 * where {@code seconds(t)} is time measured from the Unix epoch. Anchoring
 * the phase to the Unix epoch means streaming and backdated burst-mode
 * replay produce a single continuous curve with no discontinuity at their
 * boundary.
 *
 * <p>Example usage:
 * <pre>
 *   Signal hr = new Sinusoidal(75.0, 5.0, Duration.ofSeconds(60));
 *   double bpm = hr.value(Instant.now());
 * </pre>
 *
 * @param baseline mean value around which the signal oscillates
 * @param amplitude peak deviation from the baseline
 * @param period wavelength of one full cycle
 */
public record Sinusoidal(double baseline, double amplitude, Duration period)
    implements Signal {

    /**
     * Samples the sinusoidal curve at the given instant.
     *
     * @param instant the point in time
     * @return baseline plus amplitude times sin(phase)
     */
    @Override
    public double value(final Instant instant) {
        double periodSeconds = this.period.toNanos() / 1_000_000_000.0;
        double seconds = instant.toEpochMilli() / 1000.0;
        double phase = 2.0 * Math.PI * seconds / periodSeconds;
        return this.baseline + this.amplitude * Math.sin(phase);
    }
}
