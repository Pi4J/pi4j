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
 * Maps the Linux kernel {@code struct gpio_v2_line_config} (include/uapi/linux/gpio.h) to a
 * {@link MemorySegment}-backed record, holding the requested configuration for the lines of a
 * {@link LineRequest}. The {@code flags} apply to all requested lines by default, while individual
 * {@link LineConfigAttribute} entries can override that default for specific lines.
 *
 * @param flags     OR-combined default line flags from {@code enum gpio_v2_line_flag} (such as
 *                  {@code GPIO_V2_LINE_FLAG_ACTIVE_LOW} or {@code GPIO_V2_LINE_FLAG_OUTPUT}) applied to all
 *                  requested lines unless overridden by an entry in {@code attrs}
 * @param numAttrs  number of valid entries in {@code attrs}
 * @param attrs     per-line configuration overrides; an attribute should be associated with a given line only
 *                  once, and if it appears multiple times the lowest-indexed occurrence takes precedence
 */
public record LineConfig(long flags, int numAttrs,
                         LineConfigAttribute[] attrs) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_LONG.withName("flags"),
        ValueLayout.JAVA_INT.withName("num_attrs"),
        MemoryLayout.sequenceLayout(5, ValueLayout.JAVA_INT).withName("padding"),
        MemoryLayout.sequenceLayout(10, LineConfigAttribute.LAYOUT).withName("attrs")
    );

    private static final VarHandle VH_FLAGS = LAYOUT.varHandle(groupElement("flags"));

    private static final VarHandle VH_NUM_ATTRS = LAYOUT.varHandle(groupElement("num_attrs"));

    private static final MethodHandle MH_ATTRS = LAYOUT.sliceHandle(groupElement("attrs"));

    /**
     * Decodes a {@link LineConfig} from a native buffer holding a {@code gpio_v2_line_config} struct.
     * A {@link MemorySegment#NULL} buffer yields an empty instance.
     *
     * @param memorySegment native memory holding the encoded struct, or {@link MemorySegment#NULL}
     * @return the decoded configuration, or an empty configuration if the segment is null
     * @throws Throwable if reading the native memory fails
     */
    public static LineConfig create(MemorySegment memorySegment) throws Throwable {
        var lineconfigInstance = LineConfig.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            lineconfigInstance = lineconfigInstance.from(memorySegment);
        }
        return lineconfigInstance;
    }

    /**
     * Creates an empty configuration with zero flags and no attributes, suitable as a target for
     * {@link #from(MemorySegment)}.
     *
     * @return a zero-initialized {@link LineConfig}
     */
    public static LineConfig createEmpty() {
        return new LineConfig(0, 0, new LineConfigAttribute[]{});
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LineConfig from(MemorySegment buffer) throws Throwable {
        var attrsMemorySegment = invokeExact(MH_ATTRS, buffer);
        var attrs = new LineConfigAttribute[attrs().length];
        for (int i = 0; i < attrs.length; i++) {
            attrs[i] = attrs()[i].from(attrsMemorySegment.asSlice(LineConfigAttribute.LAYOUT.byteSize() * i, LineConfigAttribute.LAYOUT.byteSize()));
        }
        return new LineConfig(
            (long) VH_FLAGS.get(buffer, 0L),
            (int) VH_NUM_ATTRS.get(buffer, 0L),
            attrs);
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_FLAGS.set(buffer, 0L, flags);
        VH_NUM_ATTRS.set(buffer, 0L, numAttrs);
        var attrsTmp = invokeExact(MH_ATTRS, buffer);
        for (int i = 0; i < attrs.length; i++) {
            attrs[i].to(attrsTmp.asSlice(LineConfigAttribute.LAYOUT.byteSize() * i, LineConfigAttribute.LAYOUT.byteSize()));
        }
    }

    @Override
    public String toString() {
        return "LineConfig{" +
            "flags=" + flags +
            ", numAttrs=" + numAttrs +
            ", attrs=" + Arrays.toString(attrs) +
            '}';
    }
}
