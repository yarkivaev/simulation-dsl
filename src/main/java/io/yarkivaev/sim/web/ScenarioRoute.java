package io.yarkivaev.sim.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.yarkivaev.sim.dsl.Script;
import io.yarkivaev.sim.persist.Storage;
import java.util.Map;
import java.util.UUID;

/**
 * REST route for scenario CRUD, validation, and preview.
 *
 * <p>Example usage:
 * <pre>
 *   ScenarioRoute route = new ScenarioRoute(storage, script, gson);
 *   route.bindTo(app);
 * </pre>
 */
public final class ScenarioRoute implements Route {

    /** Scenario storage backend. */
    private final Storage storage;

    /** DSL evaluator for validation. */
    private final Script script;

    /** JSON serializer. */
    private final Gson gson;

    /**
     * Creates scenario route with storage, DSL evaluator, and serializer.
     *
     * @param storage scenario document storage
     * @param script DSL evaluator for validation
     * @param gson JSON serializer
     */
    public ScenarioRoute(final Storage storage, final Script script, final Gson gson) {
        this.storage = storage;
        this.script = script;
        this.gson = gson;
    }

    /**
     * Registers scenario endpoints.
     *
     * @param app Javalin application
     */
    @Override
    public void bindTo(final Javalin app) {
        app.get("/api/scenarios", ctx -> {
            ctx.contentType("application/json").result(
                this.gson.toJson(this.storage.list().stream()
                    .map(json -> this.gson.fromJson(json, JsonObject.class))
                    .toList())
            );
        });
        app.post("/api/scenarios", ctx -> {
            JsonObject body = this.gson.fromJson(ctx.body(), JsonObject.class);
            String id = UUID.randomUUID().toString();
            body.addProperty("id", id);
            this.storage.save(id, body.toString());
            ctx.status(201).contentType("application/json").result(body.toString());
        });
        app.delete("/api/scenarios/{id}", ctx -> {
            this.storage.delete(ctx.pathParam("id"));
            ctx.status(204);
        });
        app.post("/api/scenarios/{id}/validate", ctx -> {
            String id = ctx.pathParam("id");
            JsonObject result = new JsonObject();
            this.storage.find(id).ifPresentOrElse(json -> {
                JsonObject doc = this.gson.fromJson(json, JsonObject.class);
                String dsl = doc.get("dsl").getAsString();
                try {
                    this.script.evaluate(dsl);
                    result.addProperty("valid", true);
                } catch (IllegalArgumentException ex) {
                    result.addProperty("valid", false);
                    result.addProperty("error", ex.getMessage());
                }
            }, () -> {
                ctx.status(404);
            });
            if (!result.isEmpty()) {
                ctx.contentType("application/json").result(result.toString());
            }
        });
        app.post("/api/scenarios/{id}/preview", ctx -> {
            String id = ctx.pathParam("id");
            this.storage.find(id).ifPresentOrElse(json -> {
                JsonObject doc = this.gson.fromJson(json, JsonObject.class);
                String dsl = doc.get("dsl").getAsString();
                try {
                    var scenario = this.script.evaluate(dsl);
                    JsonObject preview = new JsonObject();
                    preview.addProperty("signals", scenario.signals().size());
                    preview.addProperty("procedures", scenario.procedures().size());
                    ctx.contentType("application/json").result(preview.toString());
                } catch (IllegalArgumentException ex) {
                    ctx.status(400).contentType("application/json").result(
                        this.gson.toJson(Map.of("error", ex.getMessage()))
                    );
                }
            }, () -> {
                ctx.status(404);
            });
        });
    }
}
