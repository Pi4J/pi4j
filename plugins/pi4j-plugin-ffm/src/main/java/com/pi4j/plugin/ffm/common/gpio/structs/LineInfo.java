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
 * Maps the Linux kernel {@code struct gpio_v2_line_info} (include/uapi/linux/gpio.h) to a
 * {@link MemorySegment}-backed record, describing the current state of a single line on a GPIO chip as returned
 * by the {@code GPIO_V2_GET_LINEINFO_IOCTL}. The caller fills in {@code offset} to select the line before the
 * ioctl, and the kernel fills in the remaining fields.
 *
 * @param name      name of the line as exported by the GPIO chip (pin header, rail, etc.); may be empty
 * @param consumer  functional label of the current consumer of the line; empty if the line is unused or the
 *                  consumer did not set a label
 * @param offset    local offset of the line on its GPIO chip; supplied by the caller to select the line
 * @param numAttrs  number of valid entries in {@code attrs}
 * @param flags     OR-combined line flags from {@code enum gpio_v2_line_flag} (such as
 *                  {@code GPIO_V2_LINE_FLAG_ACTIVE_LOW} or {@code GPIO_V2_LINE_FLAG_OUTPUT})
 * @param attrs     configuration attributes currently associated with the line
 */
public record LineInfo(byte[] name, byte[] consumer, int offset, int numAttrs, long flags,
                       LineAttribute[] attrs) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("name"),
        MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("consumer"),
        ValueLayout.JAVA_INT.withName("offset"),
        ValueLayout.JAVA_INT.withName("num_attrs"),
        ValueLayout.JAVA_LONG.withName("flags"),
        MemoryLayout.sequenceLayout(10, LineAttribute.LAYOUT).withName("attrs"),
        MemoryLayout.sequenceLayout(4, ValueLayout.JAVA_INT).withName("padding")
    );

    private static final MethodHandle MH_NAME = LAYOUT.sliceHandle(groupElement("name"));

    private static final MethodHandle MH_CONSUMER = LAYOUT.sliceHandle(groupElement("consumer"));

    private static final VarHandle VH_OFFSET = LAYOUT.varHandle(groupElement("offset"));

    private static final VarHandle VH_NUM_ATTRS = LAYOUT.varHandle(groupElement("num_attrs"));

    private static final VarHandle VH_FLAGS = LAYOUT.varHandle(groupElement("flags"));

    private static final MethodHandle MH_ATTRS = LAYOUT.sliceHandle(groupElement("attrs"));

    /**
     * Decodes a {@link LineInfo} from a native buffer holding a {@code gpio_v2_line_info} struct.
     * A {@link MemorySegment#NULL} buffer yields an empty instance.
     *
     * @param memorySegment native memory holding the encoded struct, or {@link MemorySegment#NULL}
     * @return the decoded line info, or an empty line info if the segment is null
     * @throws Throwable if reading the native memory fails
     */
    public static LineInfo create(MemorySegment memorySegment) throws Throwable {
        var lineinfoInstance = LineInfo.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            lineinfoInstance = lineinfoInstance.from(memorySegment);
        }
        return lineinfoInstance;
    }

    /**
     * Creates an empty line info with empty name and consumer, zeroed numeric fields and no attributes,
     * suitable as a target for {@link #from(MemorySegment)}.
     *
     * @return a zero-initialized {@link LineInfo}
     */
    public static LineInfo createEmpty() {
        return new LineInfo(new byte[]{}, new byte[]{}, 0, 0, 0, new LineAttribute[]{});
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LineInfo from(MemorySegment buffer) throws Throwable {
        var attrsMemorySegment = invokeExact(MH_ATTRS, buffer);
        var attrs = new LineAttribute[attrs().length];
        for (int i = 0; i < attrs.length; i++) {
            attrs[i] = attrs()[i].from(attrsMemorySegment.asSlice(LineAttribute.LAYOUT.byteSize() * i, LineAttribute.LAYOUT.byteSize()));
        }
        var name = new String(invokeExact(MH_NAME, buffer).toArray(ValueLayout.JAVA_BYTE)).trim();
        var consumer = new String(invokeExact(MH_CONSUMER, buffer).toArray(ValueLayout.JAVA_BYTE)).trim();
        return new LineInfo(
            name.getBytes(),
            consumer.getBytes(),
            (int) VH_OFFSET.get(buffer, 0L),
            (int) VH_NUM_ATTRS.get(buffer, 0L),
            (long) VH_FLAGS.get(buffer, 0L),
            attrs);
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        var nameTmp = invokeExact(MH_NAME, buffer);
        for (int i = 0; i < name.length; i++) {
            nameTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, name[i]);
        }
        var consumerTmp = invokeExact(MH_CONSUMER, buffer);
        for (int i = 0; i < consumer.length; i++) {
            consumerTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, consumer[i]);
        }
        VH_OFFSET.set(buffer, 0L, offset);
        VH_NUM_ATTRS.set(buffer, 0L, numAttrs);
        VH_FLAGS.set(buffer, 0L, flags);
        var attrsTmp = invokeExact(MH_ATTRS, buffer);
        for (int i = 0; i < attrs.length; i++) {
            attrs[i].to(attrsTmp.asSlice(LineAttribute.LAYOUT.byteSize() * i, LineAttribute.LAYOUT.byteSize()));
        }
    }

    @Override
    public String toString() {
        return "LineInfo{" +
            "name=(" + new String(name) + ")" + Arrays.toString(name) +
            ", consumer=(" + new String(consumer) + ")" + Arrays.toString(consumer) +
            ", offset=" + offset +
            ", numAttrs=" + numAttrs +
            ", flags=" + flags +
            ", attrs=" + Arrays.toString(attrs) +
            '}';
    }
}
