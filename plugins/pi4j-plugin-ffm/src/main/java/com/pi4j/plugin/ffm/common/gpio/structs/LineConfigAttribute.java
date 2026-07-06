package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Maps the Linux kernel {@code struct gpio_v2_line_config_attribute} (include/uapi/linux/gpio.h) to a
 * {@link MemorySegment}-backed record, binding a single {@link LineAttribute} to the subset of requested
 * lines selected by {@code mask}. Entries of this type populate the {@code attrs} array of a {@link LineConfig}.
 *
 * @param attr  the configurable attribute to apply
 * @param mask  a bitmap selecting the lines the attribute applies to; bit {@code n} corresponds to index
 *              {@code n} into {@link LineRequest#offsets()}
 */
public record LineConfigAttribute(LineAttribute attr, long mask) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        LineAttribute.LAYOUT.withName("attr"),
        ValueLayout.JAVA_LONG.withName("mask")
    );

    private static final MethodHandle MH_ATTR = LAYOUT.sliceHandle(groupElement("attr"));

    private static final VarHandle VH_MASK = LAYOUT.varHandle(groupElement("mask"));

    /**
     * Decodes a {@link LineConfigAttribute} from a native buffer holding a {@code gpio_v2_line_config_attribute}
     * struct. A {@link MemorySegment#NULL} buffer yields an empty instance.
     *
     * @param memorySegment native memory holding the encoded struct, or {@link MemorySegment#NULL}
     * @return the decoded config attribute, or an empty config attribute if the segment is null
     * @throws Throwable if reading the native memory fails
     */
    public static LineConfigAttribute create(MemorySegment memorySegment) throws Throwable {
        var lineconfigattributeInstance = LineConfigAttribute.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            lineconfigattributeInstance = lineconfigattributeInstance.from(memorySegment);
        }
        return lineconfigattributeInstance;
    }

    /**
     * Creates an empty config attribute with an empty {@link LineAttribute} and a zero mask, suitable as a
     * target for {@link #from(MemorySegment)}.
     *
     * @return a zero-initialized {@link LineConfigAttribute}
     */
    public static LineConfigAttribute createEmpty() {
        return new LineConfigAttribute(LineAttribute.createEmpty(), 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LineConfigAttribute from(MemorySegment buffer) throws Throwable {
        var attrMemorySegment = invokeExact(MH_ATTR, buffer);
        var attr = attr().from(attrMemorySegment);
        return new LineConfigAttribute(
            attr,
            (long) VH_MASK.get(buffer, 0L));
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        var attrTmp = invokeExact(MH_ATTR, buffer);
        attr.to(attrTmp);
        VH_MASK.set(buffer, 0L, mask);
    }

    @Override
    public String toString() {
        return "LineConfigAttribute{" +
            "attr=" + attr +
            ", mask=" + mask +
            '}';
    }
}
