package io.yarkivaev.sim.runtime;

import com.google.gson.JsonObject;
import io.yarkivaev.sim.dsl.ProcedureDef;
import io.yarkivaev.sim.dsl.Scenario;
import io.yarkivaev.sim.dsl.SignalDef;
import io.yarkivaev.sim.publish.Publisher;
import io.yarkivaev.sim.signal.Noisy;
import io.yarkivaev.sim.signal.Periodic;
import io.yarkivaev.sim.signal.Signal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Core simulation engine wiring signals, events, and publisher.
 *
 * <p>On each tick, samples all continuous signals and checks procedure
 * schedules, publishing results to MQTT topics.
 *
 * <p>Example usage:
 * <pre>
 *   Engine engine = new Engine(
 *       scenario, publisher, "icu/1/bed/1", Duration.ofSeconds(5)
 *   );
 *   engine.start();
 *   engine.stop();
 * </pre>
 */
public final class Engine implements Simulation {

    /**
     * Parsed simulation scenario.
     */
    private final Scenario scenario;

    /**
     * Message publisher.
     */
    private final Publisher publisher;

    /**
     * Topic prefix for all published messages.
     */
    private final String prefix;

    /**
     * Tick interval.
     */
    private final Duration interval;

    /**
     * Current engine state.
     */
    private final AtomicReference<String> state;

    /**
     * Periodic task scheduler.
     */
    private Ticker ticker;

    /**
     * Next scheduled occurrence instants per procedure.
     */
    private Map<String, Instant> occurrences;

    /**
     * Creates an engine for the given scenario.
     *
     * @param scenario parsed simulation scenario
     * @param publisher message publisher
     * @param prefix topic prefix (e.g., "icu/room1/bed/b1")
     * @param interval tick interval
     */
    public Engine(
        final Scenario scenario,
        final Publisher publisher,
        final String prefix,
        final Duration interval
    ) {
        this.scenario = scenario;
        this.publisher = publisher;
        this.prefix = prefix;
        this.interval = interval;
        this.state = new AtomicReference<>("STOPPED");
    }

    /**
     * Starts the simulation engine.
     */
    @Override
    public void start() {
        if (!this.state.compareAndSet("STOPPED", "RUNNING")) {
            throw new IllegalStateException("Engine is already running");
        }
        this.occurrences = new HashMap<>();
        Instant now = Instant.now();
        for (ProcedureDef proc : this.scenario.procedures()) {
            proc.occurrence().next(now).ifPresent(
                next -> this.occurrences.put(proc.name(), next)
            );
        }
        List<NamedSignal> signals = new ArrayList<>();
        for (SignalDef def : this.scenario.signals()) {
            Signal signal = new Periodic(def.distribution());
            if (def.noise().isPresent()) {
                signal = new Noisy(signal, def.noise().get());
            }
            signals.add(new NamedSignal(def.name(), def.unit(), signal));
        }
        this.ticker = new ScheduledTicker();
        this.ticker.schedule(() -> tick(signals), this.interval);
    }

    /**
     * Stops the simulation engine.
     */
    @Override
    public void stop() {
        if (!this.state.compareAndSet("RUNNING", "STOPPED")) {
            return;
        }
        if (this.ticker != null) {
            this.ticker.cancel();
        }
    }

    /**
     * Returns the current status.
     *
     * @return "RUNNING" or "STOPPED"
     */
    @Override
    public String status() {
        return this.state.get();
    }

    /**
     * Executes one tick of the simulation.
     *
     * @param signals named signals to sample
     */
    private void tick(final List<NamedSignal> signals) {
        Instant now = Instant.now();
        long ts = now.toEpochMilli();
        for (NamedSignal named : signals) {
            double value = named.signal.value(now);
            JsonObject payload = new JsonObject();
            payload.addProperty("ts", ts);
            payload.addProperty("value", Math.round(value * 100.0) / 100.0);
            payload.addProperty("unit", named.unit);
            this.publisher.publish(
                this.prefix + "/" + named.name, payload.toString()
            );
        }
        for (ProcedureDef proc : this.scenario.procedures()) {
            Instant next = this.occurrences.get(proc.name());
            if (next != null && !now.isBefore(next)) {
                JsonObject payload = new JsonObject();
                payload.addProperty("ts", ts);
                if (proc.duration().isPresent()) {
                    long duration = (long) proc.duration().get().sample();
                    payload.addProperty("event", "start");
                    payload.addProperty("duration", duration);
                    for (SignalDef sig : proc.signals()) {
                        Signal signal = new Periodic(sig.distribution());
                        double value = signal.value(now);
                        payload.addProperty("value",
                            Math.round(value * 100.0) / 100.0
                        );
                        payload.addProperty("unit", sig.unit());
                    }
                } else {
                    payload.addProperty("event", "point");
                }
                this.publisher.publish(
                    this.prefix + "/" + proc.name(), payload.toString()
                );
                proc.occurrence().next(now).ifPresent(
                    n -> this.occurrences.put(proc.name(), n)
                );
            }
        }
    }

    /**
     * Named signal associating a signal with its name and unit.
     *
     * @param name signal identifier
     * @param unit measurement unit
     * @param signal the signal source
     */
    private record NamedSignal(String name, String unit, Signal signal) {
    }
}
