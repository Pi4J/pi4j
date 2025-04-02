package com.pi4j.plugin.ffm2.gpio.structs;

import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:293:8
 *
 * struct gpio_v2_line_event - The actual event being pushed to userspace
 * @timestamp_ns: best estimate of time of event occurrence, in nanoseconds.
 * @id: event identifier with value from &enum gpio_v2_line_event_id
 * @offset: the offset of the line that triggered the event
 * @seqno: the sequence number for this event in the sequence of events for
 * all the lines in this line request
 * @line_seqno: the sequence number for this event in the sequence of
 * events on this particular line
 * @padding: reserved for future use
 *
 * By default the @timestamp_ns is read from %CLOCK_MONOTONIC and is
 * intended to allow the accurate measurement of the time between events.
 * It does not provide the wall-clock time.
 *
 * If the %GPIO_V2_LINE_FLAG_EVENT_CLOCK_REALTIME flag is set then the
 * @timestamp_ns is read from %CLOCK_REALTIME.
 */
public record LineEvent(long timestampNs, int id, int offset, int seqno, int lineSeqno,
		int[] padding) implements NativeMemoryLayout {
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

	private static final MethodHandle MH_PADDING = LAYOUT.sliceHandle(groupElement("padding"));

	public static LineEvent create(MemorySegment memorySegment) throws Throwable {
		var lineeventInstance = LineEvent.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			lineeventInstance = lineeventInstance.fromBytes(memorySegment);
		}
		return lineeventInstance;
	}

	public static LineEvent createEmpty() {
		return new LineEvent(0, 0, 0, 0, 0, new int[]{});
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public LineEvent fromBytes(MemorySegment buffer) throws Throwable {
		return new LineEvent(
			(long) VH_TIMESTAMP_NS.get(buffer, 0L),
			(int) VH_ID.get(buffer, 0L),
			(int) VH_OFFSET.get(buffer, 0L),
			(int) VH_SEQNO.get(buffer, 0L),
			(int) VH_LINE_SEQNO.get(buffer, 0L),
			invokeExact(MH_PADDING, buffer).toArray(ValueLayout.JAVA_INT));
	}

	@Override
	public void toBytes(MemorySegment buffer) throws Throwable {
		VH_TIMESTAMP_NS.set(buffer, 0L, timestampNs);
		VH_ID.set(buffer, 0L, id);
		VH_OFFSET.set(buffer, 0L, offset);
		VH_SEQNO.set(buffer, 0L, seqno);
		VH_LINE_SEQNO.set(buffer, 0L, lineSeqno);
		var paddingTmp = invokeExact(MH_PADDING, buffer);
		for (int i = 0; i < padding.length; i++) {
			paddingTmp.setAtIndex(ValueLayout.JAVA_INT, i, padding[i]);
		}
	}

	@Override
	public boolean isEmpty() {
		return timestampNs == 0 && id == 0 && offset == 0 && seqno == 0 && lineSeqno == 0 && padding.length == 0;
	}
}
