package io.yarkivaev.sim.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory storage backed by a {@link ConcurrentHashMap}.
 *
 * <p>Example usage:
 * <pre>
 *   Storage store = new MemoryStorage();
 *   store.save("s1", "{\"name\":\"Basics\"}");
 *   Optional&lt;String&gt; doc = store.find("s1");
 * </pre>
 */
public final class MemoryStorage implements Storage {

    /** Documents keyed by identifier. */
    private final ConcurrentHashMap<String, String> documents;

    /**
     * Creates an empty thread-safe in-memory storage.
     */
    public MemoryStorage() {
        this.documents = new ConcurrentHashMap<>();
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
