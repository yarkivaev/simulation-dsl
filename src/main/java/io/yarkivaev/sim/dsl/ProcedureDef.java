package io.yarkivaev.sim.dsl;

import io.yarkivaev.sim.distribution.Distribution;
import io.yarkivaev.sim.event.Occurrence;
import java.util.List;
import java.util.Optional;

/**
 * Definition of a procedure from DSL parsing.
 *
 * <p>Example usage:
 * <pre>
 *   ProcedureDef def = new ProcedureDef(
 *       "dopamine", occ, Optional.of(durDist), List.of(signalDef)
 *   );
 * </pre>
 *
 * @param name procedure identifier
 * @param occurrence when the procedure fires
 * @param duration optional duration distribution in seconds
 * @param signals signals emitted during the procedure
 */
public record ProcedureDef(
    String name,
    Occurrence occurrence,
    Optional<Distribution> duration,
    List<SignalDef> signals
) {
}
