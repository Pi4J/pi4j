package com.pi4j.extension;

import com.pi4j.common.Descriptor;
import com.pi4j.common.Identity;
import com.pi4j.common.Lifecycle;

/**
 * Common base interface for pluggable Pi4J components that are registered with, and managed by,
 * the runtime. An extension carries {@link Identity} metadata (id, name, description) and a
 * {@link Lifecycle} (initialize/shutdown), allowing it to be discovered, described, and managed
 * uniformly. Providers and other registrable components extend this contract.
 *
 * @param <T> the self-type returned by lifecycle methods to support fluent, type-safe chaining
 */
public interface Extension<T> extends Identity, Lifecycle<T> {

    /**
     * Builds a {@link Descriptor} that summarizes this extension's identity, categorized under
     * {@code "EXTENSION"}, for use in logging and runtime inspection.
     *
     * @return a descriptor populated with this extension's id, name, description, and concrete type
     */
    default Descriptor describe() {
        return Descriptor.create()
                .id(this.id())
                .name(this.name())
                .category("EXTENSION")
                .description(this.description()).type(this.getClass());
    }
}
