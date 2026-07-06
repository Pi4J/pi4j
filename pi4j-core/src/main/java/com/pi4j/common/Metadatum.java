package com.pi4j.common;

import com.pi4j.common.impl.MetadatumImpl;

/**
 * A single key/value/description entry held within a {@link Metadata} collection. Each metadatum associates a
 * string key with an arbitrary value and an optional human-readable description, and is {@link Describable}
 * so it can be rendered into a {@link Descriptor}. Create instances with the {@code create} factory methods.
 */
public interface Metadatum extends Describable {

    /**
     * Sets the key that identifies this entry within its {@link Metadata} collection.
     *
     * @param key the unique key for this entry
     * @return this metadatum instance for method chaining
     */
    Metadatum key(String key);

    /**
     * Sets the value carried by this entry.
     *
     * @param value the value to associate with this entry's key
     * @return this metadatum instance for method chaining
     */
    Metadatum value(Object value);

    /**
     * Sets the human-readable description of this entry.
     *
     * @param description explanatory text describing this entry
     * @return this metadatum instance for method chaining
     */
    Metadatum description(String description);

    /**
     * Returns the key that identifies this entry.
     *
     * @return this entry's key
     */
    String key();

    /**
     * Returns the value carried by this entry.
     *
     * @return this entry's value, or {@code null} if none was set
     */
    Object value();

    /**
     * Returns the human-readable description of this entry.
     *
     * @return this entry's description, or {@code null} if none was set
     */
    String description();

    /**
     * Creates a new, empty metadatum with no key, value, or description set.
     *
     * @return a new {@link Metadatum}
     */
    static Metadatum create() {
        return new MetadatumImpl();
    }

    /**
     * Creates a new metadatum with the given key.
     *
     * @param key the key for the new entry
     * @return a new {@link Metadatum} with its key set
     */
    static Metadatum create(String key) {
        return create().key(key);
    }

    /**
     * Creates a new metadatum with the given key and value.
     *
     * @param key   the key for the new entry
     * @param value the value to associate with the key
     * @return a new {@link Metadatum} with its key and value set
     */
    static Metadatum create(String key, Object value) {
        return create(key).value(value);
    }

    /**
     * Creates a new metadatum with the given key, value, and description.
     *
     * @param key         the key for the new entry
     * @param value       the value to associate with the key
     * @param description a human-readable description of the entry
     * @return a new {@link Metadatum} with its key, value, and description set
     */
    static Metadatum create(String key, Object value, String description) {
        return create(key, value).description(description);
    }

    @Override
    default Descriptor describe() {
        return Descriptor.create().name(key()).description(description()).value(value());
    }
}
