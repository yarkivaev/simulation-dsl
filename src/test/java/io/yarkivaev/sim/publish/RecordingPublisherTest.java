package io.yarkivaev.sim.publish;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests for {@link Publisher} interface using {@link RecordingPublisher} fake.
 */
final class RecordingPublisherTest {

    @Test
    void recordsPublishedTopic() {
        RecordingPublisher pub = new RecordingPublisher();
        pub.publish("icu/\u00e9/bed/1/hr", "{\"v\":72}");
        assertThat(
            "Recorded topic did not match the published topic",
            pub.entries().get(0).topic(),
            equalTo("icu/\u00e9/bed/1/hr")
        );
    }

    @Test
    void recordsPublishedPayload() {
        RecordingPublisher pub = new RecordingPublisher();
        pub.publish("topic", "{\"data\":\"\u00fc\u00f6\u00e4\"}");
        assertThat(
            "Recorded payload did not match the published payload",
            pub.entries().get(0).payload(),
            equalTo("{\"data\":\"\u00fc\u00f6\u00e4\"}")
        );
    }

    @Test
    void recordsMultipleMessages() {
        RecordingPublisher pub = new RecordingPublisher();
        pub.publish("a", "1");
        pub.publish("b", "2");
        pub.publish("c", "3");
        assertThat(
            "Publisher did not record all three messages",
            pub.entries(), hasSize(3)
        );
    }

    @Test
    void startsEmpty() {
        RecordingPublisher pub = new RecordingPublisher();
        assertThat(
            "New publisher was not empty",
            pub.entries(), hasSize(0)
        );
    }
}
