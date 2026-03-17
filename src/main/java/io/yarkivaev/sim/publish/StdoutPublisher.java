package io.yarkivaev.sim.publish;

/**
 * Publisher that prints messages to standard output.
 *
 * <p>Example usage:
 * <pre>
 *   Publisher pub = new StdoutPublisher();
 *   pub.publish("weather/alpine/temperature", "{\"value\":22.5}");
 *   pub.close();
 * </pre>
 */
public final class StdoutPublisher implements Publisher {

    /**
     * Creates a stdout publisher.
     */
    public StdoutPublisher() {
    }

    /**
     * Prints the message as {@code [topic] payload} to stdout.
     *
     * @param topic destination topic string
     * @param payload message content
     */
    @Override
    public void publish(final String topic, final String payload) {
        System.out.println("[" + topic + "] " + payload);
    }

    /**
     * No-op close since stdout requires no cleanup.
     */
    @Override
    public void close() {
    }
}
