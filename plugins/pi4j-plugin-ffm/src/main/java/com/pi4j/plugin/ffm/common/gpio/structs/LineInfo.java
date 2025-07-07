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
 * Source: include/uapi/linux/gpio.h:224:8
 * <p>
 * struct gpio_v2_line_info - Information about a certain GPIO line
 *
 * @name: the name of this GPIO line, such as the output pin of the line on
 * the chip, a rail or a pin header name on a board, as specified by the
 * GPIO chip, may be empty (i.e. name[0] == '\0')
 * @consumer: a functional name for the consumer of this GPIO line as set
 * by whatever is using it, will be empty if there is no current user but
 * may also be empty if the consumer doesn't set this up
 * @offset: the local offset on this GPIO chip, fill this in when
 * requesting the line information from the kernel
 * @num_attrs: the number of attributes in @attrs
 * @flags: flags for this GPIO line, with values from &enum
 * gpio_v2_line_flag, such as %GPIO_V2_LINE_FLAG_ACTIVE_LOW,
 * %GPIO_V2_LINE_FLAG_OUTPUT etc, added together.
 * @attrs: the configuration attributes associated with the line
 * @padding: reserved for future use
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
     * Creates LineInfo instance from MemorySegment provided.
     *
     * @param memorySegment buffer to construct LineInfo from
     * @return LineInfo instance
     * @throws Throwable if there is any exception while converting buffer to java object
     */
    public static LineInfo create(MemorySegment memorySegment) throws Throwable {
        var lineinfoInstance = LineInfo.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            lineinfoInstance = lineinfoInstance.from(memorySegment);
        }
        return lineinfoInstance;
    }

    /**
     * Creates empty LineInfo object.
     *
     * @return empty LineInfo object
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
