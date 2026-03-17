# Simulation

Domain-agnostic simulation framework. Provides distributions, signals, scheduled events, a Groovy DSL for scenario definitions, MQTT publishing, MongoDB persistence, and a tick-based runtime engine.

## Modules

| Package | Purpose |
|---------|---------|
| `distribution` | Random value sources: `Normal`, `Uniform`, `Triangular`, `Constant`, `Poisson` |
| `signal` | Time-varying values: `Periodic` (samples a distribution), `Noisy` (adds noise to a base signal) |
| `event` | Event scheduling: `PeriodicOccurrence` (fixed interval + jitter), `PoissonOccurrence` (exponential inter-arrival) |
| `schedule` | Time bounds: `Segment` (start + duration), `Point` (instant) |
| `dsl` | Groovy DSL: `GroovyScript` parses scenario text into `Scenario` (lists of `SignalDef` and `ProcedureDef`) |
| `publish` | Message sink: `MqttPublisher` (Eclipse Paho MQTT v5), `StdoutPublisher` (console) |
| `persist` | Document storage: `MongoStorage` (MongoDB sync driver), `MemoryStorage` (in-memory) |
| `web` | REST API: `ScenarioRoute` for scenario CRUD, validation, and preview (Javalin) |
| `runtime` | Tick-based engine: `Engine` wires signals + events + publisher, `ScheduledTicker` drives the loop |

## DSL Example

```groovy
signal "heart_rate" unit "bpm" distribution normal(75, 5) noise uniform(-2, 2)
signal "spo2" unit "%" distribution normal(97, 1.5)
signal "temperature" unit "C" distribution normal(36.6, 0.3)

procedure "dopamine" {
    occurrence every(hours(4)) jitter(minutes(30))
    duration normal(minutes(60), minutes(10))
    signal "rate" unit "mcg/kg/min" distribution normal(5.0, 0.5)
}

procedure "enema" {
    occurrence every(hours(8)) jitter(hours(2))
}
```

## Weather Example

The `examples/weather/` directory contains a weather station simulation that demonstrates real usage of the library, including a Vue frontend and Javalin REST backend.

## Build

```bash
gradle build
```

Requires Java 21.

## Usage as Dependency

Gradle composite build:

```groovy
// settings.gradle of the consuming project
includeBuild '../path/to/simulation'

// build.gradle
dependencies {
    implementation 'io.yarkivaev.sim:simulation:1.0.0'
}
```
