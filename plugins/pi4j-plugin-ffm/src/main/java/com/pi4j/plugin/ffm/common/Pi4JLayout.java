package com.pi4j.plugin.ffm.common;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.invoke.MethodHandle;

/**
 * Contract for Java types that mirror a native kernel struct and can marshal themselves to and from
 * a {@link MemorySegment}. Implementations expose the struct's {@link MemoryLayout} and convert
 * between the record/object representation and the raw off-heap bytes used in ioctl and
 * read/write calls by the FFM backend.
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

    /**
     * Converts a {@link MemorySegment} buffer to a class / object structure, with an allocator
     * available for any auxiliary off-heap memory required during conversion. The default
     * implementation ignores the allocator and delegates to {@link #from(MemorySegment)}.
     *
     * @param buffer    memory segment to convert from
     * @param allocator allocator for any additional native segments needed while converting
     * @param <T>       type of converted class / object structure
     * @return new class / object structure from the given buffer
     * @throws Throwable if reading from the buffer fails
     */
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

    /**
     * Converts a class / object structure into a {@link MemorySegment} buffer, with an allocator
     * available for any auxiliary off-heap memory required during conversion. The default
     * implementation ignores the allocator and delegates to {@link #to(MemorySegment)}.
     *
     * @param buffer    buffer to be filled with the class / object structure
     * @param allocator allocator for any additional native segments needed while converting
     * @throws Throwable if writing to the buffer fails
     */
    default void to(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        to(buffer);
    }

}
