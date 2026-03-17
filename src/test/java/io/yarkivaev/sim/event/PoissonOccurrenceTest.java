package io.yarkivaev.sim.event;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link PoissonOccurrence}.
 */
final class PoissonOccurrenceTest {

    @Test
    void computesNextAfterReference() {
        Random rng = new Random(33);
        Occurrence occ = new PoissonOccurrence(Duration.ofMinutes(10), rng);
        Instant base = Instant.parse("2025-05-01T08:00:00Z");
        assertThat(
            "Poisson occurrence was not after the reference instant",
            occ.next(base).orElseThrow(), greaterThan(base)
        );
    }

    @Test
    void returnsPresent() {
        Random rng = new Random(44);
        Occurrence occ = new PoissonOccurrence(Duration.ofSeconds(30), rng);
        assertThat(
            "Poisson occurrence did not return a present value",
            occ.next(Instant.now()).isPresent(), is(true)
        );
    }
}
