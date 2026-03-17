package io.yarkivaev.example.weather;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.yarkivaev.sim.dsl.Scenario;
import io.yarkivaev.sim.dsl.Script;
import io.yarkivaev.sim.persist.Storage;
import io.yarkivaev.sim.publish.Publisher;
import io.yarkivaev.sim.runtime.Engine;
import io.yarkivaev.sim.runtime.Simulation;
import io.yarkivaev.sim.web.Route;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REST route for starting and stopping weather station simulations.
 *
 * <p>Example usage:
 * <pre>
 *   SimulationRoute route = new SimulationRoute(
 *       stations, scenarios, script, publisher
 *   );
 *   route.bindTo(app);
 * </pre>
 */
public final class SimulationRoute implements Route {

    /** Station storage backend. */
    private final Storage stations;

    /** Scenario storage backend. */
    private final Storage scenarios;

    /** DSL evaluator. */
    private final Script script;

    /** Message publisher. */
    private final Publisher publisher;

    /** Currently running simulations keyed by station identifier. */
    private final Map<String, Simulation> running = new ConcurrentHashMap<>();

    /**
     * Creates simulation route.
     *
     * @param stations station storage
     * @param scenarios scenario storage
     * @param script DSL evaluator
     * @param publisher message publisher
     */
    public SimulationRoute(
        final Storage stations,
        final Storage scenarios,
        final Script script,
        final Publisher publisher
    ) {
        this.stations = stations;
        this.scenarios = scenarios;
        this.script = script;
        this.publisher = publisher;
    }

    /**
     * Registers simulation control endpoints.
     *
     * @param app Javalin application
     */
    @Override
    public void bindTo(final Javalin app) {
        Gson gson = new Gson();
        app.get("/api/simulations", ctx -> {
            ctx.contentType("application/json").result(
                gson.toJson(this.running.entrySet().stream().map(entry -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("target", entry.getKey());
                    obj.addProperty("status", entry.getValue().status());
                    return obj;
                }).toList())
            );
        });
        app.post("/api/simulations/{stationId}/start", ctx -> {
            String sid = ctx.pathParam("stationId");
            if (this.running.containsKey(sid)) {
                ctx.status(409).result("Simulation already running for station " + sid);
                return;
            }
            this.stations.find(sid).ifPresentOrElse(sJson -> {
                JsonObject station = gson.fromJson(sJson, JsonObject.class);
                String scenarioId = station.get("scenario").getAsString();
                String name = station.get("name").getAsString();
                this.scenarios.find(scenarioId).ifPresentOrElse(scJson -> {
                    JsonObject scenarioDoc = gson.fromJson(scJson, JsonObject.class);
                    String dsl = scenarioDoc.get("dsl").getAsString();
                    Scenario scenario = this.script.evaluate(dsl);
                    String prefix = "weather/" + name;
                    Engine engine = new Engine(scenario, this.publisher, prefix, Duration.ofSeconds(2));
                    engine.start();
                    this.running.put(sid, engine);
                    ctx.status(200).result("Started");
                }, () -> {
                    ctx.status(404).result("Scenario not found: " + scenarioId);
                });
            }, () -> {
                ctx.status(404).result("Station not found: " + sid);
            });
        });
        app.post("/api/simulations/{stationId}/stop", ctx -> {
            String sid = ctx.pathParam("stationId");
            Simulation sim = this.running.remove(sid);
            if (sim == null) {
                ctx.status(404).result("No running simulation for station " + sid);
                return;
            }
            sim.stop();
            ctx.status(200).result("Stopped");
        });
        app.post("/api/simulations/start-all", ctx -> {
            int count = 0;
            for (String sJson : this.stations.list()) {
                JsonObject station = gson.fromJson(sJson, JsonObject.class);
                String sid = station.get("id").getAsString();
                if (this.running.containsKey(sid)) {
                    continue;
                }
                String scenarioId = station.get("scenario").getAsString();
                String name = station.get("name").getAsString();
                this.scenarios.find(scenarioId).ifPresent(scJson -> {
                    JsonObject scenarioDoc = gson.fromJson(scJson, JsonObject.class);
                    String dsl = scenarioDoc.get("dsl").getAsString();
                    Scenario scenario = this.script.evaluate(dsl);
                    String prefix = "weather/" + name;
                    Engine engine = new Engine(scenario, this.publisher, prefix, Duration.ofSeconds(2));
                    engine.start();
                    this.running.put(sid, engine);
                });
                count++;
            }
            ctx.status(200).result("Started " + count + " simulations");
        });
        app.post("/api/simulations/stop-all", ctx -> {
            int count = this.running.size();
            this.running.values().forEach(Simulation::stop);
            this.running.clear();
            ctx.status(200).result("Stopped " + count + " simulations");
        });
    }
}
