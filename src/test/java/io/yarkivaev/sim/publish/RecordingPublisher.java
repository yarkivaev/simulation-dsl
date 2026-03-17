package io.yarkivaev.sim.publish;

import java.util.ArrayList;
import java.util.List;

/**
 * Publisher fake that records published messages for test verification.
 *
 * <p>Example usage:
 * <pre>
 *   RecordingPublisher pub = new RecordingPublisher();
 *   pub.publish("topic", "payload");
 *   List&lt;RecordingPublisher.Entry&gt; entries = pub.entries();
 * </pre>
 */
final class RecordingPublisher implements Publisher {

    /**
     * Recorded message entries.
     */
    private final List<Entry> entries;

    /**
     * Creates an empty recording publisher.
     */
    RecordingPublisher() {
        this.entries = new ArrayList<>();
    }

    /**
     * Records a published message.
     *
     * @param topic destination topic
     * @param payload message content
     */
    @Override
    public void publish(final String topic, final String payload) {
        this.entries.add(new Entry(topic, payload));
    }

    /**
     * No-op close.
     */
    @Override
    public void close() {
    }

    /**
     * Returns all recorded entries.
     *
     * @return list of entries
     */
    List<Entry> entries() {
        return List.copyOf(this.entries);
    }

    /**
     * A recorded topic-payload pair.
     *
     * @param topic the topic
     * @param payload the payload
     */
    record Entry(String topic, String payload) {
    }
}
