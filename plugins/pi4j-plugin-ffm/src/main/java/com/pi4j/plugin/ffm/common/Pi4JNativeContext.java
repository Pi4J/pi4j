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
 * TODO: make Arena object customizable
 */
public class Pi4JNativeContext implements SegmentAllocator {
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
        ARENA.close();
    }

}
