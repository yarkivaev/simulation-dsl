package io.yarkivaev.sim.dsl;

import io.yarkivaev.sim.distribution.Distribution;
import java.util.Optional;

/**
 * Definition of a continuous signal from DSL parsing.
 *
 * <p>Example usage:
 * <pre>
 *   SignalDef def = new SignalDef(
 *       "heart_rate", "bpm", dist, Optional.of(noise)
 *   );
 *   String name = def.name();
 * </pre>
 *
 * @param name signal identifier
 * @param unit measurement unit
 * @param distribution base value distribution
 * @param noise optional additive noise distribution
 */
public record SignalDef(
    String name,
    String unit,
    Distribution distribution,
    Optional<Distribution> noise
) {
}
