package io.yarkivaev.example.weather;

/**
 * Weather station with a name and assigned scenario.
 *
 * <p>Example usage:
 * <pre>
 *   Station station = new Station("s1", "Alpine Station", "sc1");
 *   String name = station.name();
 * </pre>
 *
 * @param id station identifier
 * @param name human-readable station name
 * @param scenario assigned scenario identifier
 */
public record Station(String id, String name, String scenario) {
}
