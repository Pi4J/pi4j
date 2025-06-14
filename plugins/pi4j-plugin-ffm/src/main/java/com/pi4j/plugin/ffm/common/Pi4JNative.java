package com.pi4j.plugin.ffm.common;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

public class Pi4JNative implements SegmentAllocator {
    protected static final Arena ARENA = Arena.ofAuto();
    protected static final SymbolLookup LIBC_LIB = Linker.nativeLinker().defaultLookup();

    // Captured state for errno
    private static final StructLayout CAPTURED_STATE_LAYOUT = Linker.Option.captureStateLayout();
    // Errno var handle
    private static final VarHandle ERRNO_HANDLE = CAPTURED_STATE_LAYOUT.varHandle(
        MemoryLayout.PathElement.groupElement("errno"));

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
    public static void processError(int callResult, MemorySegment capturedState, String method, Object... args) {
        if (callResult < 0) {
            try {
                int errno = (int) ERRNO_HANDLE.get(capturedState, 0L);
                var errnoStr = (MemorySegment) STR_ERROR.invokeExact(errno);
                throw new RuntimeException("Error during call to method " + method + " with data '" + Arrays.toString(args) + "': " +
                    errnoStr.getString(0) + " (" + errno + ")");
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
       return ARENA.allocate(byteSize, byteAlignment);
    }

    public MemorySegment allocateCapturedState() {
        return allocate(CAPTURED_STATE_LAYOUT);
    }

    public void close() {
        ARENA.close();
    }

}
