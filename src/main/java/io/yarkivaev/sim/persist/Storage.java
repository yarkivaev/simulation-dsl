package io.yarkivaev.sim.persist;

import java.util.List;
import java.util.Optional;

/**
 * Key-value storage for JSON documents.
 *
 * <p>Example usage:
 * <pre>
 *   Storage store = new MongoStorage(collection);
 *   store.save("r1", "{\"name\":\"ICU-1\"}");
 *   Optional&lt;String&gt; doc = store.find("r1");
 * </pre>
 */
public interface Storage {

    /**
     * Saves a document under the given identifier.
     *
     * @param id document identifier
     * @param json document content as JSON
     */
    void save(String id, String json);

    /**
     * Finds a document by identifier.
     *
     * @param id document identifier
     * @return the document if found
     */
    Optional<String> find(String id);

    /**
     * Deletes a document by identifier.
     *
     * @param id document identifier
     */
    void delete(String id);

    /**
     * Lists all documents in this storage.
     *
     * @return list of JSON documents
     */
    List<String> list();
}
