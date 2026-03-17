package io.yarkivaev.sim.runtime;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Ticker backed by a single-thread scheduled executor.
 *
 * <p>Example usage:
 * <pre>
 *   Ticker ticker = new ScheduledTicker();
 *   ticker.schedule(() -&gt; publish(), Duration.ofSeconds(5));
 *   ticker.cancel();
 * </pre>
 */
public final class ScheduledTicker implements Ticker {

    /**
     * The underlying executor.
     */
    private final ScheduledExecutorService executor;

    /**
     * Creates a ticker with a new single-thread executor.
     */
    public ScheduledTicker() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Schedules the task at fixed rate.
     *
     * @param task the runnable
     * @param interval time between runs
     */
    @Override
    public void schedule(final Runnable task, final Duration interval) {
        this.executor.scheduleAtFixedRate(
            task, 0, interval.toMillis(), TimeUnit.MILLISECONDS
        );
    }

    /**
     * Cancels by shutting down the executor.
     */
    @Override
    public void cancel() {
        this.executor.shutdownNow();
    }
}
