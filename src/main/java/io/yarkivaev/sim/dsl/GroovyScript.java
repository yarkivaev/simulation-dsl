package io.yarkivaev.sim.dsl;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import io.yarkivaev.sim.distribution.Constant;
import io.yarkivaev.sim.distribution.Distribution;
import io.yarkivaev.sim.distribution.Normal;
import io.yarkivaev.sim.distribution.Poisson;
import io.yarkivaev.sim.distribution.Triangular;
import io.yarkivaev.sim.distribution.Uniform;
import io.yarkivaev.sim.event.Occurrence;
import io.yarkivaev.sim.event.PeriodicOccurrence;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Groovy-based DSL evaluator with sandboxed execution.
 *
 * <p>Registers builder functions ({@code signal}, {@code procedure},
 * {@code normal}, {@code uniform}, etc.) in the Groovy binding.
 * Execution collects definitions into a {@link Scenario}.
 *
 * <p>Example usage:
 * <pre>
 *   Script script = new GroovyScript(new Random());
 *   Scenario sc = script.evaluate(
 *       "signal \"hr\" unit \"bpm\" distribution normal(75, 5)"
 *   );
 * </pre>
 */
public final class GroovyScript implements Script {

    /**
     * Source of randomness for distributions.
     */
    private final Random random;

    /**
     * Creates a GroovyScript with the given random source.
     *
     * @param random source of randomness for all distributions
     */
    public GroovyScript(final Random random) {
        this.random = random;
    }

    /**
     * Parses the DSL source into a scenario.
     *
     * @param source the Groovy DSL text
     * @return parsed scenario with signal and procedure definitions
     */
    @Override
    public Scenario evaluate(final String source) {
        List<SignalDef> signals = new ArrayList<>();
        List<ProcedureDef> procedures = new ArrayList<>();
        Binding binding = binding(signals, procedures);
        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setPackageAllowed(false);
        secure.setIndirectImportCheckEnabled(true);
        CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(secure);
        GroovyShell shell = new GroovyShell(binding, config);
        try {
            shell.evaluate(source);
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                String.format(
                    "DSL evaluation failed for source [%.50s]: %s",
                    source, ex.getMessage()
                ),
                ex
            );
        }
        return new Scenario(List.copyOf(signals), List.copyOf(procedures));
    }

    /**
     * Builds the Groovy binding with all DSL functions.
     *
     * @param signals collector for signal definitions
     * @param procedures collector for procedure definitions
     * @return configured binding
     */
    @SuppressWarnings("rawtypes")
    private Binding binding(
        final List<SignalDef> signals,
        final List<ProcedureDef> procedures
    ) {
        Binding binding = new Binding();
        Random rng = this.random;
        binding.setVariable("normal", new Closure<Distribution>(null) {
            public Distribution call(final Object... args) {
                return new Normal(number(args[0]), number(args[1]), rng);
            }
        });
        binding.setVariable("uniform", new Closure<Distribution>(null) {
            public Distribution call(final Object... args) {
                return new Uniform(number(args[0]), number(args[1]), rng);
            }
        });
        binding.setVariable("triangular", new Closure<Distribution>(null) {
            public Distribution call(final Object... args) {
                return new Triangular(
                    number(args[0]), number(args[1]), number(args[2]), rng
                );
            }
        });
        binding.setVariable("constant", new Closure<Distribution>(null) {
            public Distribution call(final Object... args) {
                return new Constant(number(args[0]));
            }
        });
        binding.setVariable("poisson", new Closure<Distribution>(null) {
            public Distribution call(final Object... args) {
                return new Poisson(number(args[0]), rng);
            }
        });
        binding.setVariable("hours", new Closure<Duration>(null) {
            public Duration call(final Object... args) {
                return Duration.ofHours(((Number) args[0]).longValue());
            }
        });
        binding.setVariable("minutes", new Closure<Duration>(null) {
            public Duration call(final Object... args) {
                return Duration.ofMinutes(((Number) args[0]).longValue());
            }
        });
        binding.setVariable("seconds", new Closure<Duration>(null) {
            public Duration call(final Object... args) {
                return Duration.ofSeconds(((Number) args[0]).longValue());
            }
        });
        binding.setVariable("every", new Closure<Occurrence>(null) {
            public Occurrence call(final Object... args) {
                return new PeriodicOccurrence(
                    (Duration) args[0], new Constant(0)
                );
            }
        });
        binding.setVariable("signal", new Closure<SignalChain>(null) {
            public SignalChain call(final Object... args) {
                return new SignalChain(
                    (String) args[0], signals, rng
                );
            }
        });
        binding.setVariable("procedure", new Closure<Void>(null) {
            public Void call(final Object... args) {
                String name = (String) args[0];
                Closure<?> body = (Closure<?>) args[1];
                ProcedureChain chain = new ProcedureChain(name, rng);
                body.setDelegate(chain);
                body.setResolveStrategy(Closure.DELEGATE_FIRST);
                body.call();
                procedures.add(chain.build());
                return null;
            }
        });
        return binding;
    }

    /**
     * Converts a Number or Duration argument to a double.
     *
     * @param argument the value from Groovy
     * @return numeric representation
     */
    private static double number(final Object argument) {
        if (argument instanceof Duration duration) {
            return duration.toSeconds();
        }
        return ((Number) argument).doubleValue();
    }

    /**
     * Chainable builder collecting signal definition attributes.
     *
     * <p>Used internally by the DSL to build {@link SignalDef} instances
     * through method chaining.
     *
     * <p>Example usage (from Groovy DSL):
     * <pre>
     *   signal "hr" unit "bpm" distribution normal(75, 5) noise uniform(-1, 1)
     * </pre>
     */
    static final class SignalChain {

        /**
         * Name of the signal being defined.
         */
        private final String name;

        /**
         * Target list to add completed definitions to.
         */
        private final List<SignalDef> target;

        /**
         * Source of randomness.
         */
        private final Random rng;

        /**
         * Measurement unit label.
         */
        private String measurement = "";

        /**
         * Base value distribution.
         */
        private Distribution dist;

        /**
         * Additive noise distribution.
         */
        private Distribution noiseDist;

        /**
         * Creates a signal chain builder.
         *
         * @param name signal name
         * @param target collector list
         * @param rng randomness source
         */
        SignalChain(
            final String name,
            final List<SignalDef> target,
            final Random rng
        ) {
            this.name = name;
            this.target = target;
            this.rng = rng;
        }

        /**
         * Sets the measurement unit.
         *
         * @param unit unit label
         * @return this chain
         */
        SignalChain unit(final String unit) {
            this.measurement = unit;
            return this;
        }

        /**
         * Sets the base distribution and adds the definition.
         *
         * @param distribution base value distribution
         * @return this chain
         */
        SignalChain distribution(final Distribution distribution) {
            this.dist = distribution;
            this.target.removeIf(s -> s.name().equals(this.name));
            this.target.add(new SignalDef(
                this.name, this.measurement, this.dist,
                this.noiseDist != null
                    ? Optional.of(this.noiseDist)
                    : Optional.empty()
            ));
            return this;
        }

        /**
         * Sets the noise distribution and updates the definition.
         *
         * @param noise additive noise distribution
         * @return this chain
         */
        SignalChain noise(final Distribution noise) {
            this.noiseDist = noise;
            this.target.removeIf(s -> s.name().equals(this.name));
            if (this.dist != null) {
                this.target.add(new SignalDef(
                    this.name, this.measurement,
                    this.dist, Optional.of(this.noiseDist)
                ));
            }
            return this;
        }
    }

    /**
     * Builder collecting procedure definition attributes inside a closure.
     *
     * <p>Used internally by the DSL to build {@link ProcedureDef} instances.
     *
     * <p>Example usage (from Groovy DSL):
     * <pre>
     *   procedure "dopamine", {
     *       occurrence every(hours(4))
     *       signal "rate" unit "mcg/kg/min" distribution normal(5, 0.5)
     *   }
     * </pre>
     */
    static final class ProcedureChain {

        /**
         * Procedure name.
         */
        private final String name;

        /**
         * Source of randomness.
         */
        private final Random rng;

        /**
         * When the procedure fires.
         */
        private Occurrence occ;

        /**
         * Optional duration distribution.
         */
        private Distribution dur;

        /**
         * Signals emitted during the procedure.
         */
        private final List<SignalDef> signals = new ArrayList<>();

        /**
         * Creates a procedure chain builder.
         *
         * @param name procedure name
         * @param rng randomness source
         */
        ProcedureChain(final String name, final Random rng) {
            this.name = name;
            this.rng = rng;
        }

        /**
         * Sets the occurrence schedule.
         *
         * @param occurrence when the procedure fires
         * @return an occurrence chain for optional jitter
         */
        OccurrenceChain occurrence(final Occurrence occurrence) {
            this.occ = occurrence;
            return new OccurrenceChain(this);
        }

        /**
         * Sets the duration distribution.
         *
         * @param distribution duration in seconds
         */
        void duration(final Distribution distribution) {
            this.dur = distribution;
        }

        /**
         * Starts a signal definition within this procedure.
         *
         * @param signalName signal name
         * @return signal chain builder
         */
        SignalChain signal(final String signalName) {
            return new SignalChain(signalName, this.signals, this.rng);
        }

        /**
         * Builds the completed procedure definition.
         *
         * @return procedure definition
         */
        ProcedureDef build() {
            if (this.occ == null) {
                throw new IllegalStateException(
                    String.format(
                        "Procedure '%s' must define an occurrence", this.name
                    )
                );
            }
            return new ProcedureDef(
                this.name, this.occ,
                this.dur != null
                    ? Optional.of(this.dur)
                    : Optional.empty(),
                List.copyOf(this.signals)
            );
        }
    }

    /**
     * Chain for applying jitter to an occurrence inside a procedure.
     *
     * <p>Example (Groovy DSL):
     * <pre>
     *   occurrence every(hours(4)) jitter minutes(30)
     * </pre>
     */
    static final class OccurrenceChain {

        /**
         * Parent procedure chain.
         */
        private final ProcedureChain procedure;

        /**
         * Creates an occurrence chain for the given procedure.
         *
         * @param procedure parent chain
         */
        OccurrenceChain(final ProcedureChain procedure) {
            this.procedure = procedure;
        }

        /**
         * Applies jitter as a uniform distribution around the given duration.
         *
         * @param jitter jitter duration converted to uniform [-seconds, +seconds]
         * @return this chain
         */
        OccurrenceChain jitter(final Duration jitter) {
            if (this.procedure.occ instanceof PeriodicOccurrence po) {
                long seconds = jitter.toSeconds();
                this.procedure.occ = new PeriodicOccurrence(
                    po.interval(),
                    new Uniform(
                        -seconds, seconds, this.procedure.rng
                    )
                );
            }
            return this;
        }
    }
}
