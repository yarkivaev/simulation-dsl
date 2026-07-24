package io.yarkivaev.sim.runtime;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Verifies ticker keeps scheduling after a failed tick.
 */
final class ScheduledTickerTest {

    @Test
    void continuesAfterRuntimeExceptionFromTask() throws Exception {
        final AtomicInteger runs = new AtomicInteger();
        final CountDownLatch done = new CountDownLatch(3);
        final Ticker ticker = new ScheduledTicker();
        ticker.schedule(() -> {
            final int count = runs.incrementAndGet();
            done.countDown();
            if (count == 1) {
                throw new IllegalStateException("transient tick failure \u00fc");
            }
        }, Duration.ofMillis(20));
        final boolean finished = done.await(2, TimeUnit.SECONDS);
        ticker.cancel();
        assertThat("ticker stopped after first failing tick", finished, equalTo(true));
    }
}
