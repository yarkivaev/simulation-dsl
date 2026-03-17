package io.yarkivaev.sim.runtime;

import java.time.Duration;

/**
 * Periodic task scheduler that fires a callback at regular intervals.
 *
 * <p>Example usage:
 * <pre>
 *   Ticker ticker = new ScheduledTicker();
 *   ticker.schedule(() -&gt; System.out.println("tick"), Duration.ofSeconds(5));
 *   ticker.cancel();
 * </pre>
 */
public interface Ticker {

    /**
     * Schedules a task to run at fixed intervals.
     *
     * @param task the runnable to execute
     * @param interval time between executions
     */
    void schedule(Runnable task, Duration interval);

    /**
     * Cancels the scheduled task.
     */
    void cancel();
}
