package io.yarkivaev.sim.persist;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory storage fake for testing purposes.
 *
 * <p>Example usage:
 * <pre>
 *   Storage store = new InMemoryStorage();
 *   store.save("key", "{\"data\":1}");
 *   Optional&lt;String&gt; doc = store.find("key");
 * </pre>
 */
final class InMemoryStorage implements Storage {

    /**
     * Documents keyed by identifier.
     */
    private final Map<String, String> documents;

    /**
     * Creates an empty in-memory storage.
     */
    InMemoryStorage() {
        this.documents = new LinkedHashMap<>();
    }

    /**
     * Saves a document under the given identifier.
     *
     * @param id document identifier
     * @param json document content as JSON
     */
    @Override
    public void save(final String id, final String json) {
        this.documents.put(id, json);
    }

    /**
     * Finds a document by identifier.
     *
     * @param id document identifier
     * @return the document if found
     */
    @Override
    public Optional<String> find(final String id) {
        return Optional.ofNullable(this.documents.get(id));
    }

    /**
     * Deletes a document by identifier.
     *
     * @param id document identifier
     */
    @Override
    public void delete(final String id) {
        this.documents.remove(id);
    }

    /**
     * Lists all documents in this storage.
     *
     * @return list of JSON documents
     */
    @Override
    public List<String> list() {
        return new ArrayList<>(this.documents.values());
    }
}
