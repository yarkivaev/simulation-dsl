package io.yarkivaev.sim.publish;

/**
 * Sink for publishing simulation data to an external system.
 *
 * <p>Example usage:
 * <pre>
 *   Publisher pub = new MqttPublisher("tcp://localhost:1883", "client-1");
 *   pub.publish("icu/room1/bed/b1/heart_rate", "{\"value\":72}");
 *   pub.close();
 * </pre>
 */
public interface Publisher {

    /**
     * Publishes a message to the given topic.
     *
     * @param topic destination topic string
     * @param payload message content
     */
    void publish(String topic, String payload);

    /**
     * Closes the publisher and releases resources.
     */
    void close();
}
