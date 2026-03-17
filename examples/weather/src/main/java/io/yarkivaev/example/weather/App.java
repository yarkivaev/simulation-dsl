package io.yarkivaev.example.weather;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.yarkivaev.sim.dsl.GroovyScript;
import io.yarkivaev.sim.dsl.Script;
import io.yarkivaev.sim.persist.MemoryStorage;
import io.yarkivaev.sim.persist.Storage;
import io.yarkivaev.sim.publish.Publisher;
import io.yarkivaev.sim.publish.StdoutPublisher;
import io.yarkivaev.sim.web.ScenarioRoute;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Weather simulator application entry point.
 *
 * <p>Pre-seeds one scenario and three stations, then starts Javalin on port 7070.
 *
 * <p>Example usage:
 * <pre>
 *   java -jar weather-simulator.jar
 * </pre>
 */
public final class App {

    /** Javalin server instance. */
    private final Javalin app;

    /**
     * Creates the application with a configured Javalin instance.
     *
     * @param app Javalin instance
     */
    public App(final Javalin app) {
        this.app = app;
    }

    /**
     * Starts the HTTP server on port 7070.
     */
    public void start() {
        this.app.start(7070);
    }

    /**
     * Application entry point.
     *
     * @param args command line arguments (unused)
     */
    public static void main(final String[] args) {
        String staticDir = env("STATIC_DIR", "static");
        Storage stations = new MemoryStorage();
        Storage scenarios = new MemoryStorage();
        Script script = new GroovyScript(new Random());
        Publisher publisher = new StdoutPublisher();
        Gson gson = new Gson();
        seed(scenarios, stations, gson);
        Javalin javalin = Javalin.create(config -> {
            if (new File(staticDir).isDirectory()) {
                config.staticFiles.add(staticDir, Location.EXTERNAL);
                config.spaRoot.addFile("/", staticDir + "/index.html", Location.EXTERNAL);
            }
        });
        new StationRoute(stations, gson).bindTo(javalin);
        new ScenarioRoute(scenarios, script, gson).bindTo(javalin);
        new SimulationRoute(stations, scenarios, script, publisher).bindTo(javalin);
        new App(javalin).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            javalin.stop();
            publisher.close();
        }));
    }

    private static void seed(
        final Storage scenarios,
        final Storage stations,
        final Gson gson
    ) {
        String scenarioId = "weather-basics";
        String dsl = resource("weather-basics.groovy");
        scenarios.save(
            scenarioId,
            gson.toJson(new Seed(scenarioId, "Weather Basics", dsl))
        );
        String[] names = {"Alpine Station", "Coastal Station", "Desert Station"};
        for (int idx = 0; idx < names.length; idx++) {
            String id = "station-" + (idx + 1);
            stations.save(
                id,
                gson.toJson(new Station(id, names[idx], scenarioId))
            );
        }
    }

    private static String resource(final String name) {
        try (InputStream stream = App.class.getClassLoader().getResourceAsStream(name)) {
            if (stream == null) {
                throw new IllegalStateException("Resource not found: " + name);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read resource: " + name, ex);
        }
    }

    private static String env(final String key, final String fallback) {
        String value = System.getenv(key);
        return value != null ? value : fallback;
    }

    /**
     * Seed data record for scenario documents.
     *
     * @param id scenario identifier
     * @param name scenario display name
     * @param dsl scenario DSL source
     */
    private record Seed(String id, String name, String dsl) {
    }
}
