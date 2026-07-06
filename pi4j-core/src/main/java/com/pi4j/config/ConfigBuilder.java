package com.pi4j.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

/**
 * Builder contract for assembling a {@link Config} instance. It provides setters for the common
 * identity properties and a family of {@code load} overloads that bulk-import configuration from
 * maps, {@link Properties} objects, streams, readers or files, optionally filtered by a key prefix.
 * It is the common super-interface of the I/O-specific configuration builders.
 *
 * @param <BUILDER_TYPE> the concrete builder sub-type, returned by setters to enable type-safe chaining
 * @param <CONFIG_TYPE> the configuration type produced by {@link #build()}
 */
public interface ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends Builder<CONFIG_TYPE> {
    /**
     * Sets the unique identifier for the configuration being built.
     *
     * @param id the configuration identifier
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE id(String id);

    /**
     * Returns the identifier currently set on this builder.
     *
     * @return the configuration identifier, or {@code null} if none has been set
     */
    String id();

    /**
     * Sets the human-readable name for the configuration being built.
     *
     * @param name the configuration name
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE name(String name);

    /**
     * Sets the human-readable description for the configuration being built.
     *
     * @param description the configuration description
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE description(String description);

    /**
     * Imports configuration values from the given key/value map.
     *
     * @param properties the property entries to load
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE load(Map<String, String> properties);

    /**
     * Imports configuration values from the given {@link Properties} object.
     *
     * @param properties the properties to load
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE load(Properties properties);

    /**
     * Imports configuration values from the given key/value map, keeping only entries whose key
     * begins with the supplied prefix (with the prefix stripped).
     *
     * @param properties the property entries to load
     * @param prefixFilter the key prefix that entries must start with to be included
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE load(Map<String, String> properties, String prefixFilter);

    /**
     * Imports configuration values from the given {@link Properties} object, keeping only entries
     * whose key begins with the supplied prefix (with the prefix stripped).
     *
     * @param properties the properties to load
     * @param prefixFilter the key prefix that entries must start with to be included
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE load(Properties properties, String prefixFilter);

    /**
     * Imports configuration values by reading {@code .properties}-formatted data from the given stream.
     *
     * @param stream the input stream to read property data from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    BUILDER_TYPE load(InputStream stream) throws IOException;

    /**
     * Imports configuration values by reading {@code .properties}-formatted data from the given stream,
     * keeping only entries whose key begins with the supplied prefix (with the prefix stripped).
     *
     * @param stream the input stream to read property data from
     * @param prefixFilter the key prefix that entries must start with to be included
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    BUILDER_TYPE load(InputStream stream, String prefixFilter) throws IOException;

    /**
     * Imports configuration values by reading {@code .properties}-formatted data from the given reader.
     *
     * @param reader the reader to read property data from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    BUILDER_TYPE load(Reader reader) throws IOException;

    /**
     * Imports configuration values by reading {@code .properties}-formatted data from the given reader,
     * keeping only entries whose key begins with the supplied prefix (with the prefix stripped).
     *
     * @param reader the reader to read property data from
     * @param prefixFilter the key prefix that entries must start with to be included
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    BUILDER_TYPE load(Reader reader, String prefixFilter) throws IOException;

    /**
     * Imports configuration values by reading {@code .properties}-formatted data from the given file.
     *
     * @param file the file to read property data from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    BUILDER_TYPE load(File file) throws IOException;

    /**
     * Imports configuration values by reading {@code .properties}-formatted data from the given file,
     * keeping only entries whose key begins with the supplied prefix (with the prefix stripped).
     *
     * @param file the file to read property data from
     * @param prefixFilter the key prefix that entries must start with to be included
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    BUILDER_TYPE load(File file, String prefixFilter) throws IOException;
}
