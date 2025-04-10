package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:196:8
 *
 * struct gpio_v2_line_request - Information about a request for GPIO lines
 * @offsets: an array of desired lines, specified by offset index for the
 * associated GPIO chip
 * @consumer: a desired consumer label for the selected GPIO lines such as
 * "my-bitbanged-relay"
 * @config: requested configuration for the lines.
 * @num_lines: number of lines requested in this request, i.e. the number
 * of valid fields in the %GPIO_V2_LINES_MAX sized arrays, set to 1 to
 * request a single line
 * @event_buffer_size: a suggested minimum number of line events that the
 * kernel should buffer.  This is only relevant if edge detection is
 * enabled in the configuration. Note that this is only a suggested value
 * and the kernel may allocate a larger buffer or cap the size of the
 * buffer. If this field is zero then the buffer size defaults to a minimum
 * of @num_lines 16.
 * @padding: reserved for future use and must be zero filled
 * @fd: if successful this field will contain a valid anonymous file handle
 * after a %GPIO_GET_LINE_IOCTL operation, zero or negative value means
 * error
 */
public record LineRequest(int[] offsets, byte[] consumer, LineConfig config, int numLines,
                          int eventBufferSize, int[] padding, int fd) implements Pi4JLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		MemoryLayout.sequenceLayout(64, ValueLayout.JAVA_INT).withName("offsets"),
		MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("consumer"),
		LineConfig.LAYOUT.withName("config"),
		ValueLayout.JAVA_INT.withName("num_lines"),
		ValueLayout.JAVA_INT.withName("event_buffer_size"),
		MemoryLayout.sequenceLayout(5, ValueLayout.JAVA_INT).withName("padding"),
		ValueLayout.JAVA_INT.withName("fd")
	);

	private static final MethodHandle MH_OFFSETS = LAYOUT.sliceHandle(groupElement("offsets"));

	private static final MethodHandle MH_CONSUMER = LAYOUT.sliceHandle(groupElement("consumer"));

	private static final MethodHandle MH_CONFIG = LAYOUT.sliceHandle(groupElement("config"));

	private static final VarHandle VH_NUM_LINES = LAYOUT.varHandle(groupElement("num_lines"));

	private static final VarHandle VH_EVENT_BUFFER_SIZE = LAYOUT.varHandle(groupElement("event_buffer_size"));

	private static final MethodHandle MH_PADDING = LAYOUT.sliceHandle(groupElement("padding"));

	private static final VarHandle VH_FD = LAYOUT.varHandle(groupElement("fd"));

	public static LineRequest create(MemorySegment memorySegment) throws Throwable {
		var linerequestInstance = LineRequest.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			linerequestInstance = linerequestInstance.from(memorySegment);
		}
		return linerequestInstance;
	}

	public static LineRequest createEmpty() {
		return new LineRequest(new int[]{}, new byte[]{}, LineConfig.createEmpty(), 0, 0, new int[]{}, 0);
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public LineRequest from(MemorySegment buffer) throws Throwable {
		var configMemorySegment = invokeExact(MH_CONFIG, buffer);
		var config = LineConfig.createEmpty().from(configMemorySegment);
		return new LineRequest(
			invokeExact(MH_OFFSETS, buffer).toArray(ValueLayout.JAVA_INT),
			invokeExact(MH_CONSUMER, buffer).toArray(ValueLayout.JAVA_BYTE),
			config,
			(int) VH_NUM_LINES.get(buffer, 0L),
			(int) VH_EVENT_BUFFER_SIZE.get(buffer, 0L),
			invokeExact(MH_PADDING, buffer).toArray(ValueLayout.JAVA_INT),
			(int) VH_FD.get(buffer, 0L));
	}

	@Override
	public void to(MemorySegment buffer) throws Throwable {
		var offsetsTmp = invokeExact(MH_OFFSETS, buffer);
		for (int i = 0; i < offsets.length; i++) {
			offsetsTmp.setAtIndex(ValueLayout.JAVA_INT, i, offsets[i]);
		}
		var consumerTmp = invokeExact(MH_CONSUMER, buffer);
		for (int i = 0; i < consumer.length; i++) {
			consumerTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, consumer[i]);
		}
		var configTmp = invokeExact(MH_CONFIG, buffer);
		config.to(configTmp);
		VH_NUM_LINES.set(buffer, 0L, numLines);
		VH_EVENT_BUFFER_SIZE.set(buffer, 0L, eventBufferSize);
		var paddingTmp = invokeExact(MH_PADDING, buffer);
		for (int i = 0; i < padding.length; i++) {
			paddingTmp.setAtIndex(ValueLayout.JAVA_INT, i, padding[i]);
		}
		VH_FD.set(buffer, 0L, fd);
	}

    @Override
    public String toString() {
        return "LineRequest{" +
            "offsets=" + Arrays.toString(offsets) +
            ", consumer=(" + new String(consumer) + ")" + Arrays.toString(consumer) +
            ", config=" + config +
            ", numLines=" + numLines +
            ", eventBufferSize=" + eventBufferSize +
            ", padding=" + Arrays.toString(padding) +
            ", fd=" + fd +
            '}';
    }
}
