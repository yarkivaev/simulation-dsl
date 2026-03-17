package io.yarkivaev.sim.publish;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import java.nio.charset.StandardCharsets;

/**
 * MQTT v5 publisher using Eclipse Paho client.
 *
 * <p>Example usage:
 * <pre>
 *   Publisher pub = new MqttPublisher("tcp://broker:1883", "sim-client");
 *   pub.publish("icu/1/bed/1/hr", payload);
 *   pub.close();
 * </pre>
 */
public final class MqttPublisher implements Publisher {

    /**
     * Paho MQTT client instance.
     */
    private final MqttClient client;

    /**
     * Creates publisher connected to the given MQTT broker.
     *
     * @param url broker URL (e.g., "tcp://localhost:1883")
     * @param id client identifier
     */
    public MqttPublisher(final String url, final String id) {
        try {
            this.client = new MqttClient(url, id);
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setAutomaticReconnect(true);
            options.setCleanStart(true);
            this.client.connect(options);
        } catch (MqttException ex) {
            throw new IllegalStateException(
                String.format(
                    "Cannot connect to MQTT broker at %s: %s",
                    url, ex.getMessage()
                ),
                ex
            );
        }
    }

    /**
     * Publishes a message to the given topic.
     *
     * @param topic destination topic
     * @param payload message content
     */
    @Override
    public void publish(final String topic, final String payload) {
        try {
            MqttMessage message = new MqttMessage(
                payload.getBytes(StandardCharsets.UTF_8)
            );
            message.setQos(1);
            this.client.publish(topic, message);
        } catch (MqttException ex) {
            throw new IllegalStateException(
                String.format(
                    "Cannot publish to topic '%s': %s",
                    topic, ex.getMessage()
                ),
                ex
            );
        }
    }

    /**
     * Disconnects from the broker and releases resources.
     */
    @Override
    public void close() {
        try {
            if (this.client.isConnected()) {
                this.client.disconnect();
            }
            this.client.close();
        } catch (MqttException ex) {
            throw new IllegalStateException(
                String.format(
                    "Cannot close MQTT client: %s", ex.getMessage()
                ),
                ex
            );
        }
    }
}
