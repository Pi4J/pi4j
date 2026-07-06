package com.pi4j.common;

import com.pi4j.common.impl.DescriptorImpl;

import java.io.PrintStream;

/**
 * A mutable, hierarchical description node produced by {@link Describable#describe()}. Each descriptor holds
 * descriptive attributes (id, name, description, category, quantity, type, and value) and may contain child
 * descriptors, forming a tree that can be printed to report the structure and state of a Pi4J object.
 * Instances are created with {@link #create()} and configured fluently.
 */
public interface Descriptor {
    /**
     * Sets the identifier attribute of this descriptor.
     *
     * @param id a unique identifier for the described object
     * @return this descriptor instance for method chaining
     */
    Descriptor id(String id);

    /**
     * Sets the display name attribute of this descriptor.
     *
     * @param name a short, human-readable name for the described object
     * @return this descriptor instance for method chaining
     */
    Descriptor name(String name);

    /**
     * Sets the description attribute of this descriptor.
     *
     * @param description a longer explanatory text for the described object
     * @return this descriptor instance for method chaining
     */
    Descriptor description(String description);

    /**
     * Sets the category attribute of this descriptor, used to group or label the described object.
     *
     * @param category the category label for the described object
     * @return this descriptor instance for method chaining
     */
    Descriptor category(String category);

    /**
     * Sets the quantity attribute of this descriptor, typically the count of items the described object represents.
     *
     * @param quantity the quantity associated with the described object
     * @return this descriptor instance for method chaining
     */
    Descriptor quantity(Integer quantity);

    /**
     * Sets the type attribute of this descriptor, identifying the class of the described object.
     *
     * @param type the class representing the type of the described object
     * @return this descriptor instance for method chaining
     */
    Descriptor type(Class type);

    /**
     * Sets the parent of this descriptor, linking it as a child within a descriptor tree.
     *
     * @param parent the descriptor that contains this descriptor
     * @return this descriptor instance for method chaining
     */
    Descriptor parent(Descriptor parent);

    /**
     * Sets the value attribute of this descriptor, the current value or state of the described object.
     *
     * @param value the value associated with the described object
     * @return this descriptor instance for method chaining
     */
    Descriptor value(Object value);

    /**
     * Returns the identifier attribute of this descriptor.
     *
     * @return the configured identifier, or {@code null} if none was set
     */
    String id();

    /**
     * Returns the display name attribute of this descriptor.
     *
     * @return the configured name, or {@code null} if none was set
     */
    String name();

    /**
     * Returns the category attribute of this descriptor.
     *
     * @return the configured category, or {@code null} if none was set
     */
    String category();

    /**
     * Returns the description attribute of this descriptor.
     *
     * @return the configured description, or {@code null} if none was set
     */
    String description();

    /**
     * Returns the quantity attribute of this descriptor.
     *
     * @return the configured quantity, or {@code null} if none was set
     */
    Integer quantity();

    /**
     * Returns the value attribute of this descriptor.
     *
     * @return the configured value, or {@code null} if none was set
     */
    Object value();

    /**
     * Returns the type attribute of this descriptor.
     *
     * @return the configured type, or {@code null} if none was set
     */
    Class type();

    /**
     * Returns the parent of this descriptor within a descriptor tree.
     *
     * @return the parent descriptor, or {@code null} if this descriptor has no parent (root node)
     */
    Descriptor parent();

    /**
     * Creates a new, empty descriptor instance with no attributes or children set.
     *
     * @return a new {@link Descriptor}
     */
    static Descriptor create() {
        return new DescriptorImpl();
    }

    /**
     * Adds the given descriptor as a child of this descriptor and sets this descriptor as its parent.
     * A {@code null} argument is ignored.
     *
     * @param descriptor the child descriptor to attach
     * @return this descriptor instance for method chaining
     */
    Descriptor add(Descriptor descriptor);

    /**
     * Returns the number of direct child descriptors attached to this descriptor.
     *
     * @return the count of immediate children
     */
    int size();

    /**
     * Indicates whether this descriptor has no child descriptors.
     *
     * @return {@code true} if this descriptor has no children, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Indicates whether this descriptor has at least one child descriptor.
     *
     * @return {@code true} if this descriptor has one or more children, {@code false} otherwise
     */
    boolean isNotEmpty();

    /**
     * Prints this descriptor and its full tree of child descriptors to the given stream as indented text.
     *
     * @param stream the output stream to write the formatted description to
     */
    void print(PrintStream stream);
}
