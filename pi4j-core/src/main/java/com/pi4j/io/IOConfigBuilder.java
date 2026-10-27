package com.pi4j.io;

import com.pi4j.context.Context;

/**
 * Base builder for assembling an {@link IOConfig} instance. Provides setters and getters for common identity
 * properties ({@code id}, {@code name}, {@code description}).
 *
 * @param <BUILDER_TYPE> the concrete builder subtype for fluent method chaining
 * @param <CONFIG_TYPE>  the configuration type produced by {@link #build()}
 */
public abstract class IOConfigBuilder<
    BUILDER_TYPE extends IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    CONFIG_TYPE extends IOConfig
> {

    protected String id;
    protected String name;
    protected String description;

    /**
     * PRIVATE CONSTRUCTOR
     */
    @Deprecated(since = "5.0")
    protected IOConfigBuilder(Context context) {
    }

    protected IOConfigBuilder() {
    }

    /**
     * Sets the unique identifier for the configuration being built.
     *
     * @param id the configuration identifier
     * @return this builder instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public BUILDER_TYPE id(String id) {
        this.id = id;
        return (BUILDER_TYPE) this;
    }

    /**
     * Returns the identifier currently set on this builder.
     *
     * @return the configuration identifier, or {@code null} if none has been set
     */
    public String id() {
        return this.id;
    }

    /**
     * Sets the human-readable name for the configuration being built.
     *
     * @param name the configuration name
     * @return this builder instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public BUILDER_TYPE name(String name) {
        this.name = name;
        return (BUILDER_TYPE) this;
    }

    /**
     * Returns the human-readable name currently set on this builder.
     *
     * @return the configuration name, or {@code null} if none has been set
     */
    public String name() {
        return this.name;
    }

    /**
     * Sets the human-readable description for the configuration being built.
     *
     * @param description the configuration description
     * @return this builder instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public BUILDER_TYPE description(String description) {
        this.description = description;
        return (BUILDER_TYPE) this;
    }

    /**
     * Returns the human-readable description currently set on this builder.
     *
     * @return the configuration description, or {@code null} if none has been set
     */
    public String description() {
        return this.description;
    }

    /**
     * Constructs and returns the configured instance from the state collected on this builder.
     *
     * @return the newly built instance
     */
    public abstract CONFIG_TYPE build();
}
