package com.pi4j.extension;

import com.pi4j.common.IdentityBase;

/**
 * Convenience base class for {@link Extension} implementations. It inherits identity handling
 * (id, name, description, metadata) from {@link IdentityBase} so that concrete extensions only
 * need to add their specific behaviour.
 *
 * @param <T> the self-type returned by fluent lifecycle and identity methods
 */
public abstract class ExtensionBase<T> extends IdentityBase<T> implements Extension<T> {

    /**
     * Creates an extension with no identity values set; id, name, and description may be
     * assigned later through the inherited fluent setters.
     */
    public ExtensionBase(){
        super();
    }

    /**
     * Creates an extension with the given identifier.
     *
     * @param id the unique identifier for this extension
     */
    public ExtensionBase(String id){
        super(id);
    }

    /**
     * Creates an extension with the given identifier and display name.
     *
     * @param id the unique identifier for this extension
     * @param name the human-readable display name for this extension
     */
    public ExtensionBase(String id, String name){
        super(id, name);
    }

    /**
     * Creates an extension with the given identifier, display name, and description.
     *
     * @param id the unique identifier for this extension
     * @param name the human-readable display name for this extension
     * @param description a human-readable description of this extension's purpose
     */
    public ExtensionBase(String id, String name, String description){
        super(id, name, description);
    }

}
