package io.yarkivaev.sim.dsl;

/**
 * Evaluator that parses DSL text into a simulation scenario.
 *
 * <p>Example usage:
 * <pre>
 *   Script script = new GroovyScript(new Random());
 *   Scenario scenario = script.evaluate(
 *       "signal \"hr\" unit \"bpm\" distribution normal(75, 5)"
 *   );
 * </pre>
 */
public interface Script {

    /**
     * Parses DSL source code into a scenario.
     *
     * @param source the DSL text
     * @return parsed scenario
     */
    Scenario evaluate(String source);
}
