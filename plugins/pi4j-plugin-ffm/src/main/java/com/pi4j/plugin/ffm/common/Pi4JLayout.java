package com.pi4j.plugin.ffm.common;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.invoke.MethodHandle;

/**
 * Main interface to translate Java object to native struct and vice versa.
 */
public interface Pi4JLayout {

    /**
     * Method-helper for calling handle with specified {@link MemorySegment} to access the data behind it.
     *
     * @param handle valid method handle
     * @param buffer memory buffer, containing data
     * @return new memory segment with data to be accessed
     * @throws Throwable if any exception occurs during invokeExact call
     */
    default MemorySegment invokeExact(MethodHandle handle, MemorySegment buffer) throws Throwable {
        return (MemorySegment) handle.invokeExact(buffer, 0L);
    }

    /**
     * Gets the {@link MemoryLayout} of the structure.
     *
     * @return memory layout of the structure
     */
    MemoryLayout getMemoryLayout();

    /**
     * Converts {@link MemorySegment} buffer to a class / object structure.
     *
     * @param buffer memory segment to convert from
     * @param <T>    type of converted class / object structure
     * @return new class / object structure from a given buffer
     * @throws Throwable unchecked exception
     */
    <T extends Pi4JLayout> T from(MemorySegment buffer) throws Throwable;

    default <T extends Pi4JLayout> T from(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        return from(buffer);
    }

    /**
     * Converts a class / object structure into a {@link MemorySegment} buffer.
     *
     * @param buffer buffer to be filled with class / object structure
     * @throws Throwable unchecked exception
     */
    void to(MemorySegment buffer) throws Throwable;

    default void to(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        to(buffer);
    }

}
