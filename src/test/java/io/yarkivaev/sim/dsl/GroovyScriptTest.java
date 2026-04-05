package io.yarkivaev.sim.dsl;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link GroovyScript} DSL evaluator.
 */
final class GroovyScriptTest {

    @Test
    void parsesSignalDefinition() {
        Script script = new GroovyScript(new Random(1));
        Scenario scenario = script.evaluate(
            "signal \"hr\" unit \"bpm\" distribution normal(75, 5)"
        );
        assertThat(
            "Scenario did not contain exactly one signal definition",
            scenario.signals(), hasSize(1)
        );
    }

    @Test
    void parsesSignalName() {
        Script script = new GroovyScript(new Random(2));
        Scenario scenario = script.evaluate(
            "signal \"\u00e9l\u00e8ve\" unit \"unit\" distribution constant(10)"
        );
        assertThat(
            "Signal name did not match the non-ASCII input",
            scenario.signals().get(0).name(), equalTo("\u00e9l\u00e8ve")
        );
    }

    @Test
    void parsesSignalUnit() {
        Script script = new GroovyScript(new Random(3));
        Scenario scenario = script.evaluate(
            "signal \"temp\" unit \"\u00b0C\" distribution constant(36.6)"
        );
        assertThat(
            "Signal unit did not match the input",
            scenario.signals().get(0).unit(), equalTo("\u00b0C")
        );
    }

    @Test
    void parsesSignalWithNoise() {
        Script script = new GroovyScript(new Random(4));
        Scenario scenario = script.evaluate(
            "signal \"spo2\" unit \"%\" distribution constant(98) noise constant(2)"
        );
        assertThat(
            "Signal with constant noise did not add base and noise values",
            scenario.signals().get(0).signal().value(Instant.EPOCH),
            equalTo(100.0)
        );
    }

    @Test
    void parsesMultipleSignals() {
        Script script = new GroovyScript(new Random(5));
        Scenario scenario = script.evaluate(
            "signal \"hr\" unit \"bpm\" distribution normal(75, 5)\n"
                + "signal \"rr\" unit \"rpm\" distribution uniform(12, 20)"
        );
        assertThat(
            "Scenario did not contain exactly two signal definitions",
            scenario.signals(), hasSize(2)
        );
    }

    @Test
    void parsesProcedureDefinition() {
        Script script = new GroovyScript(new Random(6));
        Scenario scenario = script.evaluate(
            "procedure \"dopamine\", {\n"
                + "    occurrence every(hours(4))\n"
                + "    signal \"rate\" unit \"mcg\" distribution constant(5)\n"
                + "}"
        );
        assertThat(
            "Scenario did not contain exactly one procedure definition",
            scenario.procedures(), hasSize(1)
        );
    }

    @Test
    void parsesProcedureName() {
        Script script = new GroovyScript(new Random(7));
        Scenario scenario = script.evaluate(
            "procedure \"\u00fc\u00df\u00e4\", {\n"
                + "    occurrence every(minutes(30))\n"
                + "}"
        );
        assertThat(
            "Procedure name did not match the non-ASCII input",
            scenario.procedures().get(0).name(), equalTo("\u00fc\u00df\u00e4")
        );
    }

    @Test
    void parsesProcedureWithDuration() {
        Script script = new GroovyScript(new Random(8));
        Scenario scenario = script.evaluate(
            "procedure \"infusion\", {\n"
                + "    occurrence every(hours(6))\n"
                + "    duration normal(minutes(60), minutes(10))\n"
                + "}"
        );
        assertThat(
            "Procedure duration distribution was not present",
            scenario.procedures().get(0).duration().isPresent(), is(true)
        );
    }

    @Test
    void rejectsInvalidSyntax() {
        Script script = new GroovyScript(new Random(9));
        boolean thrown = false;
        try {
            script.evaluate("{{{{invalid groovy code}}}}");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assertThat(
            "Invalid DSL syntax did not throw an exception",
            thrown, is(true)
        );
    }

    @Test
    void parsesEmptyScenario() {
        Script script = new GroovyScript(new Random(10));
        Scenario scenario = script.evaluate("");
        assertThat(
            "Empty source did not produce an empty signal list",
            scenario.signals(), hasSize(0)
        );
    }

    @Test
    void parsesTriangularDistribution() {
        Script script = new GroovyScript(new Random(11));
        Scenario scenario = script.evaluate(
            "signal \"temp\" unit \"C\" distribution triangular(36, 36.6, 37.2)"
        );
        assertThat(
            "Triangular distribution signal was not parsed",
            scenario.signals(), hasSize(1)
        );
    }

    @Test
    void parsesPoissonDistribution() {
        Script script = new GroovyScript(new Random(12));
        Scenario scenario = script.evaluate(
            "signal \"alerts\" unit \"count\" distribution poisson(3)"
        );
        assertThat(
            "Poisson distribution signal was not parsed",
            scenario.signals(), hasSize(1)
        );
    }

    @Test
    void parsesFormulaSinusoidalSignal() {
        Script script = new GroovyScript(new Random(13));
        Scenario scenario = script.evaluate(
            "signal \"hr\" unit \"bpm\" formula sinusoidal(80, 5, seconds(4))"
        );
        assertThat(
            "Formula sinusoidal signal did not return the baseline at the Unix epoch",
            scenario.signals().get(0).signal().value(Instant.EPOCH),
            closeTo(80.0, 1.0e-9)
        );
    }

    @Test
    void wrapsFormulaInNoisyWhenNoiseIsProvided() {
        Script script = new GroovyScript(new Random(14));
        Scenario scenario = script.evaluate(
            "signal \"hr\" unit \"bpm\" formula sinusoidal(50, 10, seconds(8)) noise constant(3)"
        );
        assertThat(
            "Constant noise was not added to the formula signal value at the epoch",
            scenario.signals().get(0).signal().value(Instant.EPOCH),
            closeTo(53.0, 1.0e-9)
        );
    }
}
