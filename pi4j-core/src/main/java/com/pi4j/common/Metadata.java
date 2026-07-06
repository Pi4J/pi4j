package com.pi4j.common;


import com.pi4j.common.impl.MetadataImpl;

import java.util.Collection;

/**
 * A keyed collection of {@link Metadatum} entries that lets Pi4J components carry arbitrary, descriptive
 * key/value attributes alongside their {@link Identity}. Entries are addressed by their string key and can be
 * added, queried, and removed. Create an empty collection with {@link #create()}.
 */
public interface Metadata {

//    static Metadata create(){
//        return new MetadataImpl();
//    }
//
//    static Metadata create(String key){
//        return create().key(key);
//    }
//
//    static Metadata create(String key, Object value){
//        return create(key).value(value);
//    }
//
//    static Metadata create(String key, Object value, String description){
//        return create(key, value).description(description);
//    }

    /**
     * Returns the number of metadata entries in this collection.
     *
     * @return the entry count
     */
    int size();

    /**
     * Indicates whether this collection contains no entries.
     *
     * @return {@code true} if there are no entries, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Indicates whether an entry with the given key is present.
     *
     * @param key the key to look up
     * @return {@code true} if an entry with this key exists, {@code false} otherwise
     */
    boolean contains(String key);

    /**
     * Removes the entry with the given key.
     *
     * @param key the key of the entry to remove
     * @return the removed {@link Metadatum}, or {@code null} if no entry had that key
     */
    Metadatum remove(String key);

    /**
     * Adds or replaces an entry, keyed by the metadatum's own key.
     *
     * @param metadatum the entry to store
     * @return this metadata collection for method chaining
     */
    Metadata put(Metadatum metadatum);

    /**
     * Removes all entries from this collection.
     *
     * @return this metadata collection for method chaining
     */
    Metadata clear();

    /**
     * Returns the entry stored under the given key.
     *
     * @param key the key to look up
     * @return the matching {@link Metadatum}, or {@code null} if none exists
     */
    Metadatum get(String key);

    /**
     * Returns all entries currently held in this collection.
     *
     * @return a collection of all {@link Metadatum} entries
     */
    Collection<Metadatum> all();

    /**
     * Adds or replaces every entry from the given collection.
     *
     * @param c the entries to store
     * @return this metadata collection for method chaining
     */
    default Metadata put(Collection<? extends Metadatum> c) {
        c.forEach((m) -> {
            put(m);
        });
        return this;
    }

    /**
     * Copies every entry from the given metadata collection into this one, adding or replacing as needed.
     *
     * @param metadata the source metadata whose entries are copied
     * @return this metadata collection for method chaining
     */
    default Metadata put(Metadata metadata) {
        metadata.all().forEach((m) -> {
            put(m);
        });
        return this;
    }

    /**
     * Indicates whether an entry with the same key as the given metadatum is present.
     *
     * @param metadatum the metadatum whose key is checked
     * @return {@code true} if an entry with that key exists, {@code false} otherwise
     */
    default boolean contains(Metadatum metadatum) {
        return contains(metadatum.key());
    }

    /**
     * Returns the value of the entry stored under the given key.
     *
     * @param key the key to look up
     * @return the value of the matching entry
     * @throws NullPointerException if no entry exists for the given key
     */
    default Object getValue(String key) {
        return get(key).value();
    }

    /**
     * Adds or replaces an entry that has only a key and no value.
     *
     * @param key the key of the entry to store
     * @return this metadata collection for method chaining
     */
    default Metadata put(String key) {
        return put(Metadatum.create(key));
    }

    /**
     * Adds or replaces an entry with the given key and value.
     *
     * @param key   the key of the entry to store
     * @param value the value to associate with the key
     * @return this metadata collection for method chaining
     */
    default Metadata put(String key, Object value) {
        return put(Metadatum.create(key, value));
    }

    /**
     * Adds or replaces an entry with the given key, value, and description.
     *
     * @param key         the key of the entry to store
     * @param value       the value to associate with the key
     * @param description a human-readable description of the entry
     * @return this metadata collection for method chaining
     */
    default Metadata put(String key, Object value, String description) {
        return put(Metadatum.create(key, value, description));
    }

    /**
     * Creates a new, empty metadata collection.
     *
     * @return a new {@link Metadata} instance
     */
    static Metadata create() {
        return new MetadataImpl();
    }
}
