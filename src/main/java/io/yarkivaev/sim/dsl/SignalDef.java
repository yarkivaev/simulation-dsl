package io.yarkivaev.sim.dsl;

import io.yarkivaev.sim.signal.Signal;

/**
 * Definition of a named signal source from DSL parsing.
 *
 * <p>Carries a fully-formed {@link Signal} produced by the DSL chain, so
 * downstream consumers (Engine, burst-mode historical generators) do not
 * need to know whether the signal came from a distribution, a formula, or
 * a noisy decorator — they just call {@code def.signal().value(instant)}.
 *
 * <p>Example usage:
 * <pre>
 *   SignalDef def = new SignalDef(
 *       "heart_rate", "bpm",
 *       new Noisy(new Sinusoidal(75, 5, Duration.ofSeconds(60)),
 *                 new Uniform(-1, 1, rng))
 *   );
 *   double value = def.signal().value(Instant.now());
 * </pre>
 *
 * @param name signal identifier
 * @param unit measurement unit
 * @param signal fully-formed signal source
 */
public record SignalDef(String name, String unit, Signal signal) {
}
