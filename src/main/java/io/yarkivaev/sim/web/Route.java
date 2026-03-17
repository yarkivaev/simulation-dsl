package io.yarkivaev.sim.web;

import io.javalin.Javalin;

/**
 * HTTP route that can be bound to a Javalin application.
 *
 * <p>Example usage:
 * <pre>
 *   Route route = new StationRoute(storage, gson);
 *   route.bindTo(app);
 * </pre>
 */
public interface Route {

    /**
     * Registers HTTP endpoints on the given Javalin app.
     *
     * @param app the Javalin application
     */
    void bindTo(Javalin app);
}
