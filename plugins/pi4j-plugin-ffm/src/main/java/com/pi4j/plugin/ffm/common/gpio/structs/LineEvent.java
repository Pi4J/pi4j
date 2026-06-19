package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Maps the Linux kernel {@code struct gpio_v2_line_event} (include/uapi/linux/gpio.h) to a
 * {@link MemorySegment}-backed record. Such an event is read from the line request file descriptor when edge
 * detection is enabled and a configured edge is observed on one of the requested lines.
 * <p>
 * By default {@code timestampNs} is sampled from {@code CLOCK_MONOTONIC} (suitable for measuring the time
 * between events, not wall-clock time); if {@code GPIO_V2_LINE_FLAG_EVENT_CLOCK_REALTIME} is set on the line it
 * is sampled from {@code CLOCK_REALTIME} instead.
 *
 * @param timestampNs  best estimate of the time the event occurred, in nanoseconds, from the configured clock
 * @param id           event identifier from {@code enum gpio_v2_line_event_id} (rising vs. falling edge)
 * @param offset       offset of the line that triggered the event
 * @param seqno        sequence number of this event across all lines in the originating {@link LineRequest}
 * @param lineSeqno    sequence number of this event among the events on this particular line
 */
public record LineEvent(long timestampNs, int id, int offset, int seqno, int lineSeqno) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_LONG.withName("timestamp_ns"),
        ValueLayout.JAVA_INT.withName("id"),
        ValueLayout.JAVA_INT.withName("offset"),
        ValueLayout.JAVA_INT.withName("seqno"),
        ValueLayout.JAVA_INT.withName("line_seqno"),
        MemoryLayout.sequenceLayout(6, ValueLayout.JAVA_INT).withName("padding")
    );

    private static final VarHandle VH_TIMESTAMP_NS = LAYOUT.varHandle(groupElement("timestamp_ns"));

    private static final VarHandle VH_ID = LAYOUT.varHandle(groupElement("id"));

    private static final VarHandle VH_OFFSET = LAYOUT.varHandle(groupElement("offset"));

    private static final VarHandle VH_SEQNO = LAYOUT.varHandle(groupElement("seqno"));

    private static final VarHandle VH_LINE_SEQNO = LAYOUT.varHandle(groupElement("line_seqno"));

    /**
     * Decodes a {@link LineEvent} from a native buffer holding a {@code gpio_v2_line_event} struct.
     * A {@link MemorySegment#NULL} buffer yields an empty instance.
     *
     * @param memorySegment native memory holding the encoded struct, or {@link MemorySegment#NULL}
     * @return the decoded event, or an empty event if the segment is null
     * @throws Throwable if reading the native memory fails
     */
    public static LineEvent create(MemorySegment memorySegment) throws Throwable {
        var lineeventInstance = LineEvent.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            lineeventInstance = lineeventInstance.from(memorySegment);
        }
        return lineeventInstance;
    }

    /**
     * Creates an empty event with all fields set to zero, suitable as a target for {@link #from(MemorySegment)}.
     *
     * @return a zero-initialized {@link LineEvent}
     */
    public static LineEvent createEmpty() {
        return new LineEvent(0, 0, 0, 0, 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LineEvent from(MemorySegment buffer) throws Throwable {
        return new LineEvent(
            (long) VH_TIMESTAMP_NS.get(buffer, 0L),
            (int) VH_ID.get(buffer, 0L),
            (int) VH_OFFSET.get(buffer, 0L),
            (int) VH_SEQNO.get(buffer, 0L),
            (int) VH_LINE_SEQNO.get(buffer, 0L));
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_TIMESTAMP_NS.set(buffer, 0L, timestampNs);
        VH_ID.set(buffer, 0L, id);
        VH_OFFSET.set(buffer, 0L, offset);
        VH_SEQNO.set(buffer, 0L, seqno);
        VH_LINE_SEQNO.set(buffer, 0L, lineSeqno);
    }

    @Override
    public String toString() {
        return "LineEvent{" +
            "timestampNs=" + timestampNs +
            ", id=" + id +
            ", offset=" + offset +
            ", seqno=" + seqno +
            ", lineSeqno=" + lineSeqno +
            '}';
    }
}
