package com.pi4j.common;

import com.pi4j.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenient base class for {@link Extension} implementations that provides default {@link Identity}-style
 * storage for id, name, description, and {@link Metadata}. Subclasses inherit sensible defaults derived from
 * the concrete class name and may override them via the constructors.
 *
 * @param <T> the self-type returned by the {@link Extension} lifecycle, enabling fluent subclass APIs
 */
public abstract class IdentityBase<T> implements Extension<T> {

    /** Logger bound to the concrete subclass for diagnostic output. */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The unique identifier; defaults to the concrete class's fully qualified name. */
    protected String id;
    /** The display name; defaults to the concrete class's simple name. */
    protected String name;
    /** The description; defaults to the concrete class's fully qualified name. */
    protected String description;
    /** The metadata collection for arbitrary key/value attributes. */
    protected Metadata metadata = Metadata.create();

    /**
     * Creates an instance whose id and description default to the concrete class's fully qualified name and
     * whose name defaults to its simple name.
     */
    public IdentityBase() {
        this.id = getClass().getName();
        this.name = this.getClass().getSimpleName();
        this.description = this.getClass().getName();
    }

    /**
     * Creates an instance with the given id, leaving name and description at their class-derived defaults.
     *
     * @param id the unique identifier to assign
     */
    public IdentityBase(String id) {
        this();
        this.id = id;
    }

    /**
     * Creates an instance with the given id and name, leaving description at its class-derived default.
     *
     * @param id   the unique identifier to assign
     * @param name the human-readable name to assign
     */
    public IdentityBase(String id, String name) {
        this(id);
        this.name = name;
    }

    /**
     * Creates an instance with the given id, name, and description.
     *
     * @param id          the unique identifier to assign
     * @param name        the human-readable name to assign
     * @param description the description to assign
     */
    public IdentityBase(String id, String name, String description) {
        this(id, name);
        this.description = description;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public Metadata metadata() {
        return this.metadata;
    }
}
