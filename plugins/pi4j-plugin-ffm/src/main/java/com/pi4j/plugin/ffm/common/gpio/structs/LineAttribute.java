package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:130:8
 * <p>
 * struct gpio_v2_line_attribute - a configurable attribute of a line
 *
 * @id: attribute identifier with value from &enum gpio_v2_line_attr_id
 * @padding: reserved for future use and must be zero filled
 * @flags: if id is %GPIO_V2_LINE_ATTR_ID_FLAGS, the flags for the GPIO
 * line, with values from &enum gpio_v2_line_flag, such as
 * %GPIO_V2_LINE_FLAG_ACTIVE_LOW, %GPIO_V2_LINE_FLAG_OUTPUT etc, added
 * together.  This overrides the default flags contained in the &struct
 * gpio_v2_line_config for the associated line.
 * @values: if id is %GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES, a bitmap
 * containing the values to which the lines will be set, with each bit
 * number corresponding to the index into &struct
 * gpio_v2_line_request.offsets.
 * @debounce_period_us: if id is %GPIO_V2_LINE_ATTR_ID_DEBOUNCE, the
 * desired debounce period, in microseconds
 */
public record LineAttribute(int id, long flags, long values,
                            int debouncePeriodUs) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("id"),
        ValueLayout.JAVA_INT.withName("padding"),
        MemoryLayout.unionLayout(
            ValueLayout.JAVA_LONG.withName("flags"),
            ValueLayout.JAVA_LONG.withName("values"),
            ValueLayout.JAVA_INT.withName("debounce_period_us")
        ).withName("internal_union_0")
    );

    private static final VarHandle VH_ID = LAYOUT.varHandle(groupElement("id"));

    //private static final VarHandle VH_PADDING = LAYOUT.varHandle(groupElement("padding"));

    private static final VarHandle VH_FLAGS = LAYOUT.select(groupElement("internal_union_0")).varHandle(groupElement("flags"));

    private static final VarHandle VH_VALUES = LAYOUT.select(groupElement("internal_union_0")).varHandle(groupElement("values"));

    private static final VarHandle VH_DEBOUNCE_PERIOD_US = LAYOUT.select(groupElement("internal_union_0")).varHandle(groupElement("debounce_period_us"));

    public static LineAttribute create(MemorySegment memorySegment) throws Throwable {
        var lineattributeInstance = LineAttribute.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            lineattributeInstance = lineattributeInstance.from(memorySegment);
        }
        return lineattributeInstance;
    }

    public static LineAttribute createEmpty() {
        return new LineAttribute(0, 0, 0, 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LineAttribute from(MemorySegment buffer) throws Throwable {
        // Unions require to work with MemorySegment as a slice
        var internalUnion0Size = LAYOUT.select(groupElement("internal_union_0")).byteSize();
        var internalUnion0Buffer = buffer.asSlice(LAYOUT.byteSize() - internalUnion0Size, internalUnion0Size);
        return new LineAttribute(
            (int) VH_ID.get(buffer, 0L),
            //(int) VH_PADDING.get(buffer, 0L),
            // guess the provided field by the current instance
            flags != 0 ? (long) VH_FLAGS.get(internalUnion0Buffer, 0L) : 0,
            values != 0 ? (long) VH_VALUES.get(internalUnion0Buffer, 0L) : 0,
            debouncePeriodUs != 0 ? (int) VH_DEBOUNCE_PERIOD_US.get(internalUnion0Buffer, 0L) : 0
        );
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_ID.set(buffer, 0L, id);
        //VH_PADDING.set(buffer, 0L, padding);

        // Unions require to work with MemorySegment as a slice
        var internalUnion0Size = LAYOUT.select(groupElement("internal_union_0")).byteSize();
        var internalUnion0Buffer = buffer.asSlice(LAYOUT.byteSize() - internalUnion0Size, internalUnion0Size);
        if (flags > 0) {
            VH_FLAGS.set(internalUnion0Buffer, 0L, flags);
        }
        if (values > 0) {
            VH_VALUES.set(internalUnion0Buffer, 0L, values);
        }
        if (debouncePeriodUs > 0) {
            VH_DEBOUNCE_PERIOD_US.set(internalUnion0Buffer, 0L, debouncePeriodUs);
        }
    }

    @Override
    public String toString() {
        return "LineAttribute{" +
            "id=" + id +
            //", padding=" + padding +
            ", flags=" + flags +
            ", values=" + values +
            ", debouncePeriodUs=" + debouncePeriodUs +
            '}';
    }
}
