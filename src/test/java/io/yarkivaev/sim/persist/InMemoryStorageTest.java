package io.yarkivaev.sim.persist;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link Storage} interface using {@link InMemoryStorage} fake.
 */
final class InMemoryStorageTest {

    @Test
    void savesAndFindsDocument() {
        Storage store = new InMemoryStorage();
        store.save("id-1", "{\"name\":\"\u00e9\u00e0\u00fc\"}");
        assertThat(
            "Saved document was not found by its identifier",
            store.find("id-1").orElseThrow(),
            equalTo("{\"name\":\"\u00e9\u00e0\u00fc\"}")
        );
    }

    @Test
    void returnsEmptyForMissingDocument() {
        Storage store = new InMemoryStorage();
        assertThat(
            "Missing document did not return empty",
            store.find("nonexistent").isEmpty(), is(true)
        );
    }

    @Test
    void deletesDocument() {
        Storage store = new InMemoryStorage();
        store.save("rm-1", "{\"x\":1}");
        store.delete("rm-1");
        assertThat(
            "Deleted document was still found",
            store.find("rm-1").isEmpty(), is(true)
        );
    }

    @Test
    void listsAllDocuments() {
        Storage store = new InMemoryStorage();
        store.save("a", "{\"v\":1}");
        store.save("b", "{\"v\":2}");
        store.save("c", "{\"v\":3}");
        assertThat(
            "Storage did not list all three documents",
            store.list(), hasSize(3)
        );
    }

    @Test
    void overwritesExistingDocument() {
        Storage store = new InMemoryStorage();
        store.save("ow-1", "{\"old\":true}");
        store.save("ow-1", "{\"new\":true}");
        assertThat(
            "Overwritten document did not contain the new content",
            store.find("ow-1").orElseThrow(),
            equalTo("{\"new\":true}")
        );
    }

    @Test
    void listsAfterDeletion() {
        Storage store = new InMemoryStorage();
        store.save("x", "{\"v\":1}");
        store.save("y", "{\"v\":2}");
        store.delete("x");
        assertThat(
            "Storage did not contain exactly one document after deletion",
            store.list(), hasSize(1)
        );
    }
}
