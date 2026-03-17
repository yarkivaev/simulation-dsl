package io.yarkivaev.sim.dsl;

import java.util.List;

/**
 * Parsed simulation scenario containing signal and procedure definitions.
 *
 * <p>Example usage:
 * <pre>
 *   Scenario sc = new Scenario(List.of(signalDef), List.of(procDef));
 *   List&lt;SignalDef&gt; signals = sc.signals();
 * </pre>
 *
 * @param signals continuous signal definitions
 * @param procedures procedure definitions
 */
public record Scenario(List<SignalDef> signals, List<ProcedureDef> procedures) {
}
