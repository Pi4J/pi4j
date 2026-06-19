package com.pi4j.plugin.ffm.common.ioctl;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.CAPTURED_STATE_LAYOUT;
import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;

/**
 * Thin Java wrapper around the Linux {@code ioctl(2)} syscall, reached through the Foreign Function
 * &amp; Memory API. Each call follows the same pattern:
 * <ul>
 *   <li>allocate the needed buffers from a per-call {@link Arena#ofConfined()} arena;</li>
 *   <li>attach an {@code errno} capture state so failures can be reported;</li>
 *   <li>invoke the native {@code ioctl} handle held by {@link IoctlContext};</li>
 *   <li>translate a negative return into a {@link Pi4JException} via {@code processError};</li>
 *   <li>return the result, reading back any value the kernel wrote into the argument buffer.</li>
 * </ul>
 * The request codes passed as the {@code command} argument are produced by {@link Command}/{@link IoctlMagic}.
 */
public class IoctlNative {
    // Keep the context field to trigger IoctlContext class loading (and thus MethodHandle init).
    @SuppressWarnings("unused")
    private final IoctlContext context = new IoctlContext();

    /**
     * Invokes {@code ioctl(fd, command, data)} passing {@code data} directly by value, for requests
     * whose argument is an integer rather than a pointer to a buffer.
     *
     * @param fd      open file descriptor of the device the request targets
     * @param command encoded ioctl request code (see {@link Command} / {@link IoctlMagic})
     * @param data    the scalar argument passed by value to the request
     * @return the raw value returned by {@code ioctl}, typically {@code 0} on success
     * @throws Pi4JException if the syscall fails (negative return); wraps the captured {@code errno}
     */
    public int callByValue(int fd, long command, long data) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) IoctlContext.IOCTL.invoke(capturedState, fd, command, data);
            processError(callResult, capturedState, "callByValue", fd, command, data);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Invokes {@code ioctl(fd, command, &data)} passing a pointer to a freshly allocated {@code long}
     * holding {@code data}, then reads the (possibly kernel-updated) value back out.
     *
     * @param fd      open file descriptor of the device the request targets
     * @param command encoded ioctl request code (see {@link Command} / {@link IoctlMagic})
     * @param data    the initial {@code long} value written into the argument buffer
     * @return the {@code long} value held in the argument buffer after the call returns
     * @throws Pi4JException if the syscall fails (negative return); wraps the captured {@code errno}
     */
    public long call(int fd, long command, long data) {
        try (var arena = Arena.ofConfined()) {
            var dataMemorySegment = arena.allocate(ValueLayout.JAVA_LONG);
            dataMemorySegment.set(ValueLayout.JAVA_LONG, 0, data);
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) IoctlContext.IOCTL_0.invoke(capturedState, fd, command, dataMemorySegment);
            processError(callResult, capturedState, "call", fd, command, data);
            return dataMemorySegment.get(ValueLayout.JAVA_LONG, 0);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Invokes {@code ioctl(fd, command, &data)} passing a pointer to a freshly allocated {@code int}
     * holding {@code data}, then reads the (possibly kernel-updated) value back out.
     *
     * @param fd      open file descriptor of the device the request targets
     * @param command encoded ioctl request code (see {@link Command} / {@link IoctlMagic})
     * @param data    the initial {@code int} value written into the argument buffer
     * @return the {@code int} value held in the argument buffer after the call returns
     * @throws Pi4JException if the syscall fails (negative return); wraps the captured {@code errno}
     */
    public int call(int fd, long command, int data) {
        try (var arena = Arena.ofConfined()) {
            var dataMemorySegment = arena.allocate(ValueLayout.JAVA_INT);
            dataMemorySegment.set(ValueLayout.JAVA_INT, 0, data);
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) IoctlContext.IOCTL_1.invoke(capturedState, fd, command, dataMemorySegment);
            processError(callResult, capturedState, "call", fd, command, data);
            return dataMemorySegment.get(ValueLayout.JAVA_INT, 0);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Invokes {@code ioctl(fd, command, &struct)} for requests whose argument is a kernel struct.
     * The {@code data} layout is serialized into a native buffer via {@link Pi4JLayout#to}, the syscall
     * is performed, and a new instance is rebuilt from the buffer via {@link Pi4JLayout#from} so the
     * caller observes any fields the kernel populated.
     *
     * @param fd      open file descriptor of the device the request targets
     * @param command encoded ioctl request code (see {@link Command} / {@link IoctlMagic})
     * @param data    the struct wrapper supplying the input bytes and target layout
     * @param <T>     the concrete {@link Pi4JLayout} struct type
     * @return a fresh instance of {@code T} decoded from the argument buffer after the call returns
     * @throws Pi4JException if the syscall fails (negative return); wraps the captured {@code errno}
     */
    public <T extends Pi4JLayout> T call(int fd, long command, T data) {
        try (var arena = Arena.ofConfined()) {
            var dataMemorySegment = arena.allocate(data.getMemoryLayout());
            data.to(dataMemorySegment, arena);
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) IoctlContext.IOCTL_1.invoke(capturedState, fd, command, dataMemorySegment);
            processError(callResult, capturedState, "call", fd, command, data);
            return data.from(dataMemorySegment, arena);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }
}
