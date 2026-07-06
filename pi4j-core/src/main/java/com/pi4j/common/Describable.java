package com.pi4j.common;

/**
 * Implemented by Pi4J types that can produce a human-readable, tree-structured description of themselves.
 * The returned {@link Descriptor} can be printed or inspected to report identity, configuration, and any
 * nested child descriptors.
 */
public interface Describable {
    /**
     * Builds a {@link Descriptor} that summarizes this object, including any nested child descriptors.
     *
     * @return a descriptor describing this object's identity and state
     */
    Descriptor describe();
}
