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
 * Maps the Linux kernel {@code struct gpio_v2_line_request} (include/uapi/linux/gpio.h) to a
 * {@link MemorySegment}-backed record, used to request a set of GPIO lines via the {@code GPIO_V2_GET_LINE_IOCTL}.
 * On success the kernel populates {@code fd} with an anonymous file descriptor that owns the requested lines and
 * is used for subsequent value and event operations.
 *
 * @param offsets          line offsets on the associated GPIO chip to request; the first {@code numLines}
 *                         entries are valid
 * @param consumer         desired consumer label for the requested lines, e.g. "my-bitbanged-relay"
 * @param config           requested {@link LineConfig} (flags and per-line attribute overrides) for the lines
 * @param numLines         number of lines being requested, i.e. valid entries in {@code offsets}; set to 1 for
 *                         a single line
 * @param eventBufferSize  suggested minimum number of line events the kernel should buffer (only relevant when
 *                         edge detection is enabled); the kernel may allocate more or cap it, and a value of
 *                         zero defaults to {@code numLines * 16}
 * @param fd               filled by the kernel on success with a valid anonymous file descriptor for the
 *                         requested lines; a zero or negative value indicates an error
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
     * Decodes a {@link LineRequest} from a native buffer holding a {@code gpio_v2_line_request} struct.
     * A {@link MemorySegment#NULL} buffer yields an empty instance.
     *
     * @param memorySegment native memory holding the encoded struct, or {@link MemorySegment#NULL}
     * @return the decoded request, or an empty request if the segment is null
     * @throws Throwable if reading the native memory fails
     */
    public static LineRequest create(MemorySegment memorySegment) throws Throwable {
        var linerequestInstance = LineRequest.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            linerequestInstance = linerequestInstance.from(memorySegment);
        }
        return linerequestInstance;
    }

    /**
     * Creates an empty request with no offsets, an empty consumer label, an empty {@link LineConfig} and
     * zeroed numeric fields, suitable as a target for {@link #from(MemorySegment)}.
     *
     * @return a zero-initialized {@link LineRequest}
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
