package io.yarkivaev.sim.publish;

import org.eclipse.paho.mqttv5.common.MqttMessage;

/**
 * One simulator reading ready to publish.
 *
 * <p>QoS 0 because the next tick replaces the value. A QoS 1 acknowledgement
 * blocks the tick thread until the broker replies, and that reply was taking
 * seconds on the lab broker, so a two-second sample interval slipped past the
 * supervisor silence budget.
 *
 * <pre>{@code
 *   client.publish(topic, new OutboundReading(body).message());
 * }</pre>
 */
public final class OutboundReading {

    private final byte[] body;

    /**
     * Copies the reading payload.
     *
     * @param body UTF-8 JSON body
     */
    public OutboundReading(final byte[] body) {
        this.body = body.clone();
    }

    /**
     * Builds the MQTT message for this reading.
     *
     * @return message that does not wait for a broker acknowledgement
     */
    public MqttMessage message() {
        final MqttMessage message = new MqttMessage(this.body);
        message.setQos(0);
        return message;
    }
}
