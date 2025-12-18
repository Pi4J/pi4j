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
 * Source: include/uapi/linux/gpio.h:196:8
 * <p>
 * struct gpio_v2_line_request - Information about a request for GPIO lines
 *
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
                          int eventBufferSize, int fd) implements Pi4JLayout {
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

    private static final VarHandle VH_FD = LAYOUT.varHandle(groupElement("fd"));

    /**
     * Creates LineRequest instance from MemorySegment provided.
     *
     * @param memorySegment buffer to construct LineRequest from
     * @return LineRequest instance
     * @throws Throwable if there is any exception while converting buffer to java object
     */
    public static LineRequest create(MemorySegment memorySegment) throws Throwable {
        var linerequestInstance = LineRequest.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            linerequestInstance = linerequestInstance.from(memorySegment);
        }
        return linerequestInstance;
    }

    /**
     * Creates empty LineRequest object.
     *
     * @return empty LineRequest object
     */
    public static LineRequest createEmpty() {
        return new LineRequest(new int[]{}, new byte[]{}, LineConfig.createEmpty(), 0, 0, 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LineRequest from(MemorySegment buffer) throws Throwable {
        var configMemorySegment = invokeExact(MH_CONFIG, buffer);
        var config = config().from(configMemorySegment);
        var consumer = new String(invokeExact(MH_CONSUMER, buffer).toArray(ValueLayout.JAVA_BYTE)).trim();

        var offsetsMemorySegment = invokeExact(MH_OFFSETS, buffer);
        var offsets = new int[offsets().length];
        for (int i = 0; i < offsets().length; i++) {
            offsets[i] = offsetsMemorySegment.getAtIndex(ValueLayout.JAVA_INT, i);
        }
        return new LineRequest(
            offsets,
            consumer.getBytes(),
            config,
            (int) VH_NUM_LINES.get(buffer, 0L),
            (int) VH_EVENT_BUFFER_SIZE.get(buffer, 0L),
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
            ", fd=" + fd +
            '}';
    }
}
