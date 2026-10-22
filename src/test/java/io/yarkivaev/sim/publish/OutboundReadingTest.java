package io.yarkivaev.sim.publish;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests for {@link OutboundReading}.
 */
final class OutboundReadingTest {

    @Test
    void doesNotRequestBrokerAcknowledgement() {
        final byte[] body = ("cosφ-" + ThreadLocalRandom.current().nextInt(10, 99))
            .getBytes(StandardCharsets.UTF_8);
        assertThat(
            "reading publish waits for a QoS 1 acknowledgement and stalls the next sample",
            new OutboundReading(body).message().getQos(),
            equalTo(0)
        );
    }
}
