package io.yarkivaev.sim.runtime;

import io.yarkivaev.sim.distribution.Constant;
import io.yarkivaev.sim.dsl.Scenario;
import io.yarkivaev.sim.dsl.SignalDef;
import io.yarkivaev.sim.publish.Publisher;
import io.yarkivaev.sim.signal.Periodic;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link Engine} simulation lifecycle.
 */
final class EngineTest {

    @Test
    void startsInStoppedState() {
        Scenario scenario = new Scenario(List.of(), List.of());
        Simulation engine = new Engine(
            scenario, new StubPublisher(), "test/prefix",
            Duration.ofSeconds(60)
        );
        assertThat(
            "Engine did not start in STOPPED state",
            engine.status(), equalTo("STOPPED")
        );
    }

    @Test
    void transitionsToRunning() {
        Scenario scenario = new Scenario(List.of(), List.of());
        Simulation engine = new Engine(
            scenario, new StubPublisher(), "test/prefix",
            Duration.ofSeconds(60)
        );
        engine.start();
        try {
            assertThat(
                "Engine did not transition to RUNNING state",
                engine.status(), equalTo("RUNNING")
            );
        } finally {
            engine.stop();
        }
    }

    @Test
    void transitionsBackToStopped() {
        Scenario scenario = new Scenario(List.of(), List.of());
        Simulation engine = new Engine(
            scenario, new StubPublisher(), "test/prefix",
            Duration.ofSeconds(60)
        );
        engine.start();
        engine.stop();
        assertThat(
            "Engine did not transition back to STOPPED state",
            engine.status(), equalTo("STOPPED")
        );
    }

    @Test
    void rejectsDoubleStart() {
        Scenario scenario = new Scenario(List.of(), List.of());
        Simulation engine = new Engine(
            scenario, new StubPublisher(), "test/prefix",
            Duration.ofSeconds(60)
        );
        engine.start();
        boolean thrown = false;
        try {
            engine.start();
        } catch (IllegalStateException ex) {
            thrown = true;
        } finally {
            engine.stop();
        }
        assertThat(
            "Double start did not throw an exception",
            thrown, is(true)
        );
    }

    @Test
    void publishesSignalOnTick() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        List<String> topics = new ArrayList<>();
        Publisher pub = new Publisher() {
            @Override
            public void publish(final String topic, final String payload) {
                topics.add(topic);
                latch.countDown();
            }
            @Override
            public void close() {
            }
        };
        SignalDef def = new SignalDef(
            "hr", "bpm", new Periodic(new Constant(72))
        );
        Scenario scenario = new Scenario(List.of(def), List.of());
        Simulation engine = new Engine(
            scenario, pub, "test", Duration.ofMillis(50)
        );
        engine.start();
        try {
            latch.await(5, TimeUnit.SECONDS);
            assertThat(
                "Engine did not publish at least one signal message",
                topics.size(), greaterThanOrEqualTo(1)
            );
        } finally {
            engine.stop();
        }
    }

    @Test
    void stopsIdempotently() {
        Scenario scenario = new Scenario(List.of(), List.of());
        Simulation engine = new Engine(
            scenario, new StubPublisher(), "test/prefix",
            Duration.ofSeconds(60)
        );
        engine.stop();
        assertThat(
            "Stopping a non-started engine changed the state",
            engine.status(), equalTo("STOPPED")
        );
    }

    /**
     * No-op publisher stub for lifecycle tests.
     *
     * <p>Example usage:
     * <pre>
     *   Publisher pub = new StubPublisher();
     * </pre>
     */
    private static final class StubPublisher implements Publisher {

        /**
         * No-op flag.
         */
        private final boolean active = true;

        /**
         * No-op publish.
         *
         * @param topic ignored
         * @param payload ignored
         */
        @Override
        public void publish(final String topic, final String payload) {
        }

        /**
         * No-op close.
         */
        @Override
        public void close() {
        }
    }
}
