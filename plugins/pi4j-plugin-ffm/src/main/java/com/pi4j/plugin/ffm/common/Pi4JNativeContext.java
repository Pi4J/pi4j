package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

/**
 * Class that represents the custom segment allocator with underlying {@link Arena}, some base methods of errno/stderr
 * and error processing.
 * The Arena type is auto, meaning any MemorySegment that is allocated within context will be garbage collected
 * like any java object.
 */
public class Pi4JNativeContext implements SegmentAllocator {
    //TODO: make Arena object customizable
    /*
    The current design is lack of flexibility - the memory allocator will keep memory segments until hte JVM is stopped.
    We need to add parameters to the API interface, that can make Arena object customizable, e.g.
        - make Arena sharable between different native calls (so that memory segments can be chained and passed from one native call to the other)
        - make Arena more strict, depending on case of usage - shared, single-threaded, multi-threaded
        - make Arena a RingBuffer to guarantee the Arena size will not be growing, while JVM and native code is working
     */
    protected static final Arena ARENA = Arena.ofAuto();
    protected static final SymbolLookup LIBC_LIB = Linker.nativeLinker().defaultLookup();

    // Captured state for errno
    private static final StructLayout CAPTURED_STATE_LAYOUT = Linker.Option.captureStateLayout();
    // Errno var handle
    private static final VarHandle ERRNO_HANDLE = CAPTURED_STATE_LAYOUT.varHandle(
        MemoryLayout.PathElement.groupElement("errno"));

    // 1024 is enough for any error received from errno.
    private static final AddressLayout POINTER = ValueLayout.ADDRESS.withTargetLayout(
        MemoryLayout.sequenceLayout(1024, ValueLayout.JAVA_BYTE));
    // Strerror method handle
    private static final MethodHandle STR_ERROR = Linker.nativeLinker().downcallHandle(
        Linker.nativeLinker().defaultLookup().find("strerror").orElseThrow(),
        FunctionDescriptor.of(POINTER, ValueLayout.JAVA_INT));


    /**
     * Process the error and raise exception method.
     *
     * @param callResult    result of the call
     * @param capturedState state of errno
     * @param method        string representation of called method
     * @param args          arguments called to function
     */
    public static void processError(int callResult, MemorySegment capturedState, String method, Object... args) throws Throwable {
        if (callResult < 0) {
            int errno = (int) ERRNO_HANDLE.get(capturedState, 0L);
            var errnoStr = (MemorySegment) STR_ERROR.invokeExact(errno);
            throw new Pi4JException("Error during call to method '" + method + "' with data '" + Arrays.deepToString(args) + "': " +
                errnoStr.getString(0) + " (" + errno + ")");
        }
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        return ARENA.allocate(byteSize, byteAlignment);
    }

    /**
     * Create MemorySegment for capturing errno.
     *
     * @return memory segment used to capture errno
     */
    public MemorySegment allocateCapturedState() {
        return allocate(CAPTURED_STATE_LAYOUT);
    }

    /**
     * Closes underlying Arena.
     */
    public void close() {
        // do nothing :(
        // see comments to Arena object above
    }

}
