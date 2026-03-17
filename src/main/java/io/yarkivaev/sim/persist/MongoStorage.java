package io.yarkivaev.sim.persist;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

/**
 * MongoDB-backed document storage.
 *
 * <p>Example usage:
 * <pre>
 *   MongoClient client = MongoClients.create("mongodb://localhost:27017");
 *   MongoDatabase db = client.getDatabase("sim");
 *   Storage store = new MongoStorage(db.getCollection("rooms"));
 *   store.save("r1", "{\"name\":\"ICU-1\"}");
 * </pre>
 */
public final class MongoStorage implements Storage {

    /**
     * MongoDB collection.
     */
    private final MongoCollection<Document> collection;

    /**
     * Creates storage backed by the given collection.
     *
     * @param collection MongoDB collection
     */
    public MongoStorage(final MongoCollection<Document> collection) {
        this.collection = collection;
    }

    /**
     * Saves or replaces a document with the given id.
     *
     * @param id document identifier
     * @param json document content
     */
    @Override
    public void save(final String id, final String json) {
        Document doc = Document.parse(json);
        doc.put("_id", id);
        this.collection.replaceOne(
            eq("_id", id), doc,
            new ReplaceOptions().upsert(true)
        );
    }

    /**
     * Finds a document by its identifier.
     *
     * @param id document identifier
     * @return the JSON document if found
     */
    @Override
    public Optional<String> find(final String id) {
        Document doc = this.collection.find(eq("_id", id)).first();
        if (doc == null) {
            return Optional.empty();
        }
        doc.remove("_id");
        return Optional.of(doc.toJson());
    }

    /**
     * Removes a document by its identifier.
     *
     * @param id document identifier
     */
    @Override
    public void delete(final String id) {
        this.collection.deleteOne(eq("_id", id));
    }

    /**
     * Lists all documents in the collection.
     *
     * @return list of JSON strings with id field
     */
    @Override
    public List<String> list() {
        List<String> results = new ArrayList<>();
        try (MongoCursor<Document> cursor = this.collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Object docId = doc.remove("_id");
                doc.put("id", docId);
                results.add(doc.toJson());
            }
        }
        return results;
    }
}
