package com.pi4j.plugin.ffm.common.poll;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;

import java.lang.foreign.Arena;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.CAPTURED_STATE_LAYOUT;
import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;

/**
 * Java front end to the Linux {@code poll(2)} syscall bound by {@link PollContext}, used to block until
 * an event (such as a GPIO line edge) is reported on a file descriptor. The single call:
 * <ul>
 *   <li>allocates the {@code struct pollfd} buffer from a per-call {@link Arena#ofConfined()} arena;</li>
 *   <li>attaches an {@code errno} capture state so failures can be reported;</li>
 *   <li>invokes the native {@code poll} handle;</li>
 *   <li>translates failures into a {@link Pi4JException} via {@code processError};</li>
 *   <li>returns the updated {@link PollingData}, or {@code null} on timeout.</li>
 * </ul>
 */
public class PollNative {
    // Keep the context field to trigger PollContext class loading (and thus MethodHandle init).
    @SuppressWarnings("unused")
    private final PollContext context = new PollContext();

    /**
     * Waits for I/O events on the descriptor(s) described by {@code pollingData} by invoking {@code poll},
     * blocking until an event occurs or {@code timeout} elapses.
     *
     * @param pollingData the {@code struct pollfd} contents (file descriptor and requested events)
     * @param size        number of {@code pollfd} entries in the buffer (the {@code nfds} argument)
     * @param timeout     maximum time to block in milliseconds; a negative value blocks indefinitely
     * @return the {@link PollingData} updated with the reported events, or {@code null} if the call
     *         timed out before any event was reported
     * @throws Pi4JException if {@code poll} reports an error or the call cannot be invoked
     */
    public PollingData poll(PollingData pollingData, int size, int timeout) {
        try (var arena = Arena.ofConfined()) {
            var pollingDataMemorySegment = arena.allocate(PollingData.LAYOUT);
            pollingData.to(pollingDataMemorySegment);
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) PollContext.POLL.invoke(capturedState, pollingDataMemorySegment, size, timeout);
            processError(callResult, capturedState, "poll", pollingData, size, timeout);
            return callResult > 0 ? PollingData.create(pollingDataMemorySegment) : null;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }
}
