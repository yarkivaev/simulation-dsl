package io.yarkivaev.sim.signal;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.lessThan;

/**
 * Tests for {@link Sinusoidal} signal.
 */
final class SinusoidalTest {

    @Test
    void returnsBaselineAtEpoch() {
        Random rng = new Random();
        double baseline = 50.0 + rng.nextDouble() * 100.0;
        double amplitude = 1.0 + rng.nextDouble() * 20.0;
        Duration period = Duration.ofSeconds(10L + rng.nextInt(600));
        Signal signal = new Sinusoidal(baseline, amplitude, period);
        assertThat(
            "Sinusoidal did not return the baseline at the Unix epoch",
            signal.value(Instant.EPOCH), closeTo(baseline, 1.0e-9)
        );
    }

    @Test
    void peaksAtQuarterPeriod() {
        Random rng = new Random();
        double baseline = 20.0 + rng.nextDouble() * 50.0;
        double amplitude = 3.0 + rng.nextDouble() * 10.0;
        Duration period = Duration.ofSeconds(8L + rng.nextInt(120));
        Signal signal = new Sinusoidal(baseline, amplitude, period);
        Instant quarter = Instant.ofEpochMilli(period.toMillis() / 4L);
        assertThat(
            "Sinusoidal did not peak at baseline plus amplitude a quarter period after the epoch",
            signal.value(quarter), closeTo(baseline + amplitude, 1.0e-6)
        );
    }

    @Test
    void troughsAtThreeQuarterPeriod() {
        Random rng = new Random();
        double baseline = -10.0 + rng.nextDouble() * 200.0;
        double amplitude = 0.5 + rng.nextDouble() * 7.0;
        Duration period = Duration.ofSeconds(12L + rng.nextInt(240));
        Signal signal = new Sinusoidal(baseline, amplitude, period);
        Instant threeQuarters = Instant.ofEpochMilli(3L * period.toMillis() / 4L);
        assertThat(
            "Sinusoidal did not trough at baseline minus amplitude three quarters into a period",
            signal.value(threeQuarters), closeTo(baseline - amplitude, 1.0e-6)
        );
    }

    @Test
    void producesContinuousValuesAcrossSuccessiveInstants() {
        Random rng = new Random();
        double baseline = rng.nextDouble() * 80.0;
        double amplitude = 1.0 + rng.nextDouble() * 5.0;
        long periodSeconds = 30L + rng.nextInt(300);
        Duration period = Duration.ofSeconds(periodSeconds);
        Signal signal = new Sinusoidal(baseline, amplitude, period);
        Instant base = Instant.ofEpochSecond(rng.nextInt(1_000_000));
        double delta = Math.abs(
            signal.value(base.plusMillis(1L)) - signal.value(base)
        );
        double maxDelta =
            amplitude * (2.0 * Math.PI / (periodSeconds * 1000.0)) + 1.0e-9;
        assertThat(
            "Sinusoidal values one millisecond apart diverged beyond the derivative bound",
            delta, lessThan(maxDelta)
        );
    }
}
