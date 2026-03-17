package io.yarkivaev.sim.runtime;

/**
 * Controllable simulation lifecycle.
 *
 * <p>Example usage:
 * <pre>
 *   Simulation sim = new Engine(
 *       scenario, publisher, "icu/1/bed/1", Duration.ofSeconds(5)
 *   );
 *   sim.start();
 *   sim.stop();
 * </pre>
 */
public interface Simulation {

    /**
     * Starts the simulation.
     */
    void start();

    /**
     * Stops the simulation.
     */
    void stop();

    /**
     * Returns the current simulation status.
     *
     * @return status string: "RUNNING" or "STOPPED"
     */
    String status();
}
