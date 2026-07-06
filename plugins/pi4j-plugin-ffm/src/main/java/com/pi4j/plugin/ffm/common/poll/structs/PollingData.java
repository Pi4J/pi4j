package com.pi4j.plugin.ffm.common.poll.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * {@link MemorySegment}-backed mapping of the Linux {@code struct pollfd}
 * (include/uapi/asm-generic/poll.h) used by the {@code poll(2)} system call.
 * Each instance describes one file descriptor to watch, the events requested,
 * and the events the kernel reported after the call returns. Implements the
 * {@link Pi4JLayout} marshalling contract.
 *
 * @param fd      the file descriptor to be polled
 * @param events  bitmask of requested events to watch for (e.g. {@code POLLIN}, {@code POLLPRI})
 * @param revents bitmask of events reported by the kernel on return
 */
public record PollingData(int fd, short events, short revents) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("fd"),
        ValueLayout.JAVA_SHORT.withName("events"),
        ValueLayout.JAVA_SHORT.withName("revents")
    );

    private static final VarHandle VH_FD = LAYOUT.varHandle(groupElement("fd"));

    private static final VarHandle VH_EVENTS = LAYOUT.varHandle(groupElement("events"));

    private static final VarHandle VH_REVENTS = LAYOUT.varHandle(groupElement("revents"));

    /**
     * Decodes a {@code struct pollfd} from native memory into a new {@code PollingData}.
     * A {@link MemorySegment#NULL} segment yields an empty instance with all fields zeroed.
     *
     * @param memorySegment native memory holding a {@code struct pollfd}, or {@link MemorySegment#NULL}
     * @return a {@code PollingData} populated from the segment, or an empty instance for a NULL segment
     * @throws Throwable if reading the fields from native memory fails
     */
    public static PollingData create(MemorySegment memorySegment) throws Throwable {
        var pollingdataInstance = PollingData.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            pollingdataInstance = pollingdataInstance.from(memorySegment);
        }
        return pollingdataInstance;
    }

    /**
     * Creates a {@code PollingData} with all fields zeroed, suitable as a target buffer
     * to be filled from native memory.
     *
     * @return an empty {@code PollingData} with {@code fd}, {@code events} and {@code revents} set to zero
     */
    public static PollingData createEmpty() {
        return new PollingData(0, (short) 0, (short) 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PollingData from(MemorySegment buffer) throws Throwable {
        return new PollingData(
            (int) VH_FD.get(buffer, 0L),
            (short) VH_EVENTS.get(buffer, 0L),
            (short) VH_REVENTS.get(buffer, 0L));
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_FD.set(buffer, 0L, fd);
        VH_EVENTS.set(buffer, 0L, events);
        VH_REVENTS.set(buffer, 0L, revents);
    }

    @Override
    public String toString() {
        return "PollingData{" +
            "fd=" + fd +
            ", events=" + events +
            ", revents=" + revents +
            '}';
    }
}
