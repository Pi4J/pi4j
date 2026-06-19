package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

/**
 * Class that provides native method handles, errno processing utilities, and a long-lived Arena
 * exclusively for native library lookups.
 * <p>
 * Per-call memory allocations (errno capture buffers, data buffers, etc.) must use a
 * short-lived {@link Arena#ofConfined()} created with try-with-resources inside each native
 * method call.  Using the global {@link #ARENA} for per-call allocations would cause all
 * segments to accumulate until JVM exit, resulting in an OutOfMemoryError.
 */
public class Pi4JNativeContext implements SegmentAllocator {
    /**
     * Global arena used <em>only</em> for long-lived allocations such as native library lookups
     * (e.g. {@link SymbolLookup#libraryLookup}).  Must not be used for per-call allocations.
     */
    protected static final Arena ARENA = Arena.ofAuto();
    protected static final SymbolLookup LIBC_LIB = Linker.nativeLinker().defaultLookup();

    /**
     * Layout of the errno captured-state struct.  Exposed so that Native classes can allocate
     * it in a per-call confined arena: {@code arena.allocate(CAPTURED_STATE_LAYOUT)}.
     */
    public static final StructLayout CAPTURED_STATE_LAYOUT = Linker.Option.captureStateLayout();
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
     * Inspects the return value of a native call and, if it indicates failure, reads {@code errno}
     * from the captured-state segment, resolves the matching message via the native {@code strerror}
     * function, and raises a {@link Pi4JException}. A non-negative {@code callResult} is treated as
     * success and the method returns without action.
     *
     * @param callResult    the value returned by the native call; a negative value signals an error
     * @param capturedState segment laid out as {@link #CAPTURED_STATE_LAYOUT} holding the captured
     *                      {@code errno} value
     * @param method        the name of the native function that was called, used in the message
     * @param args          the arguments passed to the native function, included in the message for
     *                      diagnostics
     * @throws Pi4JException if {@code callResult} is negative, carrying the {@code errno} number and
     *                       its textual description
     * @throws Throwable     if invoking the native {@code strerror} handle fails
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
     * @deprecated Use {@code arena.allocate(CAPTURED_STATE_LAYOUT)} with a per-call
     *             {@link Arena#ofConfined()} arena to avoid unbounded memory growth.
     */
    @Deprecated(since = "4.1.0", forRemoval = true)
    public MemorySegment allocateCapturedState() {
        return allocate(CAPTURED_STATE_LAYOUT);
    }

    /**
     * No-op.  The global {@link #ARENA} is used solely for library lookups and is
     * managed by the GC; it does not require explicit closing.
     */
    public void close() {
        // The global ARENA is only used for library lookups (e.g. SymbolLookup.libraryLookup)
        // and is intentionally kept alive for the JVM lifetime.  Per-call allocations use
        // short-lived Arena.ofConfined() arenas in each native method, so there is nothing
        // to release here.
    }

}
