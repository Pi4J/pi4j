package com.pi4j.plugin.ffm.common.ioctl;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;

/**
 * Class for calling native ioctl methods.
 * The logic behind the class is follows:
 * - allocate the needed buffers from Arena object with method parameters
 * - optionally add 'errno' context to caller
 * - call native function with 'invoke'
 * - process errors if any captured by 'errno'
 * - return call result if needed
 */
public class IoctlNative {
    private final IoctlContext context = new IoctlContext();

    /**
     * Calls ioctl on file descriptor with long command. Data is provided by value.
     *
     * @param fd      file descriptor for calling ioctl
     * @param command long command for ioctl see {@link Command}
     * @param data    long data for ioctl
     * @return int ioctl call result
     */
    public int callByValue(int fd, long command, long data) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = (int) IoctlContext.IOCTL.invoke(capturedState, fd, command, data);
            processError(callResult, capturedState, "callByValue", fd, command, data);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Calls ioctl on file descriptor with long command. Data is provided reference.
     *
     * @param fd      file descriptor for calling ioctl
     * @param command long command for ioctl see {@link Command}
     * @param data    long data for ioctl
     * @return long ioctl call result
     */
    public long call(int fd, long command, long data) {
        try {
            var dataMemorySegment = context.allocate(ValueLayout.JAVA_LONG);
            dataMemorySegment.set(ValueLayout.JAVA_LONG, 0, data);
            var capturedState = context.allocateCapturedState();
            var callResult = (int) IoctlContext.IOCTL_0.invoke(capturedState, fd, command, dataMemorySegment);
            processError(callResult, capturedState, "call", fd, command, data);
            return dataMemorySegment.get(ValueLayout.JAVA_LONG, 0);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Calls ioctl on file descriptor with long command. Data is provided reference.
     *
     * @param fd      file descriptor for calling ioctl
     * @param command long command for ioctl see {@link Command}
     * @param data    int data for ioctl
     * @return int ioctl call result
     */
    public int call(int fd, long command, int data) {
        try {
            var dataMemorySegment = context.allocate(ValueLayout.JAVA_INT);
            dataMemorySegment.set(ValueLayout.JAVA_INT, 0, data);
            var capturedState = context.allocateCapturedState();
            var callResult = (int) IoctlContext.IOCTL_1.invoke(capturedState, fd, command, dataMemorySegment);
            processError(callResult, capturedState, "call", fd, command, data);
            return dataMemorySegment.get(ValueLayout.JAVA_INT, 0);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Calls ioctl on file descriptor with long command. Data is provided reference.
     * Data should be implementation of {@link Pi4JLayout}
     *
     * @param fd      file descriptor for calling ioctl
     * @param command long command for ioctl see {@link Command}
     * @param data    layout implementing {@link Pi4JLayout}
     * @param <T>     layout object implementing {@link Pi4JLayout}
     * @return dereferenced value of data provided in argument
     */
    public <T extends Pi4JLayout> T call(int fd, long command, T data) {
        try {
            var dataMemorySegment = context.allocate(data.getMemoryLayout());
            data.to(dataMemorySegment);
            var capturedState = context.allocateCapturedState();
            var callResult = (int) IoctlContext.IOCTL_1.invoke(capturedState, fd, command, dataMemorySegment);
            processError(callResult, capturedState, "call", fd, command, data);
            return data.from(dataMemorySegment);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }
}
