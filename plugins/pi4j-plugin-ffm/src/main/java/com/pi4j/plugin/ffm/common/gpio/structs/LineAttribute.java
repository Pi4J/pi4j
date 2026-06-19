package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Maps the Linux kernel {@code struct gpio_v2_line_attribute} (include/uapi/linux/gpio.h) to a
 * {@link MemorySegment}-backed record, representing a single configurable attribute of one or more GPIO lines.
 * The {@code id} selects which member of the kernel union is meaningful, so only one of {@code flags},
 * {@code values}, or {@code debouncePeriodUs} carries data for a given attribute.
 *
 * @param id                attribute identifier from {@code enum gpio_v2_line_attr_id} that selects which union
 *                          member below is active ({@code GPIO_V2_LINE_ATTR_ID_FLAGS},
 *                          {@code GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES} or {@code GPIO_V2_LINE_ATTR_ID_DEBOUNCE})
 * @param flags             when {@code id} is {@code GPIO_V2_LINE_ATTR_ID_FLAGS}, the OR-combined line flags from
 *                          {@code enum gpio_v2_line_flag}; overrides the default flags of the associated
 *                          {@link LineConfig} for the matching lines
 * @param values            when {@code id} is {@code GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES}, a bitmap of output
 *                          values (bit {@code n} corresponds to index {@code n} into {@link LineRequest#offsets()})
 * @param debouncePeriodUs  when {@code id} is {@code GPIO_V2_LINE_ATTR_ID_DEBOUNCE}, the desired debounce period
 *                          in microseconds
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

    private static final VarHandle VH_FLAGS = LAYOUT.select(groupElement("internal_union_0")).varHandle(groupElement("flags"));

    private static final VarHandle VH_VALUES = LAYOUT.select(groupElement("internal_union_0")).varHandle(groupElement("values"));

    private static final VarHandle VH_DEBOUNCE_PERIOD_US = LAYOUT.select(groupElement("internal_union_0")).varHandle(groupElement("debounce_period_us"));

    /**
     * Decodes a {@link LineAttribute} from a native buffer holding a {@code gpio_v2_line_attribute} struct.
     * A {@link MemorySegment#NULL} buffer yields an empty instance.
     *
     * @param memorySegment native memory holding the encoded struct, or {@link MemorySegment#NULL}
     * @return the decoded attribute, or an empty attribute if the segment is null
     * @throws Throwable if reading the native memory fails
     */
    public static LineAttribute create(MemorySegment memorySegment) throws Throwable {
        var lineattributeInstance = LineAttribute.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            lineattributeInstance = lineattributeInstance.from(memorySegment);
        }
        return lineattributeInstance;
    }

    /**
     * Creates an empty attribute with all fields set to zero, suitable as a target for {@link #from(MemorySegment)}.
     *
     * @return a zero-initialized {@link LineAttribute}
     */
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
            // guess the provided field by the current instance
            flags != 0 ? (long) VH_FLAGS.get(internalUnion0Buffer, 0L) : 0,
            values != 0 ? (long) VH_VALUES.get(internalUnion0Buffer, 0L) : 0,
            debouncePeriodUs != 0 ? (int) VH_DEBOUNCE_PERIOD_US.get(internalUnion0Buffer, 0L) : 0
        );
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_ID.set(buffer, 0L, id);

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
            ", flags=" + flags +
            ", values=" + values +
            ", debouncePeriodUs=" + debouncePeriodUs +
            '}';
    }
}
