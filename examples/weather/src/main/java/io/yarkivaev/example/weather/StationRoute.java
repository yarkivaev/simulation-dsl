package io.yarkivaev.example.weather;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.yarkivaev.sim.persist.Storage;
import io.yarkivaev.sim.web.Route;
import java.util.UUID;

/**
 * REST route for weather station CRUD operations.
 *
 * <p>Example usage:
 * <pre>
 *   StationRoute route = new StationRoute(storage, gson);
 *   route.bindTo(app);
 * </pre>
 */
public final class StationRoute implements Route {

    /** Station storage backend. */
    private final Storage storage;

    /** JSON serializer. */
    private final Gson gson;

    /**
     * Creates station route with storage and serializer.
     *
     * @param storage station document storage
     * @param gson JSON serializer
     */
    public StationRoute(final Storage storage, final Gson gson) {
        this.storage = storage;
        this.gson = gson;
    }

    /**
     * Registers station endpoints.
     *
     * @param app Javalin application
     */
    @Override
    public void bindTo(final Javalin app) {
        app.get("/api/stations", ctx -> {
            ctx.contentType("application/json").result(
                this.gson.toJson(this.storage.list().stream()
                    .map(json -> this.gson.fromJson(json, JsonObject.class))
                    .toList())
            );
        });
        app.post("/api/stations", ctx -> {
            JsonObject body = this.gson.fromJson(ctx.body(), JsonObject.class);
            String id = UUID.randomUUID().toString();
            body.addProperty("id", id);
            this.storage.save(id, body.toString());
            ctx.status(201).contentType("application/json").result(body.toString());
        });
        app.delete("/api/stations/{id}", ctx -> {
            this.storage.delete(ctx.pathParam("id"));
            ctx.status(204);
        });
    }
}
