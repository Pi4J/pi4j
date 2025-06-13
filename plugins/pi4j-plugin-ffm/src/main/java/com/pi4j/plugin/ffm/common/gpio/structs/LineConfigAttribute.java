package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:148:8
 *
 * struct gpio_v2_line_config_attribute - a configuration attribute
 * associated with one or more of the requested lines.
 * @attr: the configurable attribute
 * @mask: a bitmap identifying the lines to which the attribute applies,
 * with each bit number corresponding to the index into &struct
 * gpio_v2_line_request.offsets.
 */
public record LineConfigAttribute(LineAttribute attr, long mask) implements Pi4JLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		LineAttribute.LAYOUT.withName("attr"),
		ValueLayout.JAVA_LONG.withName("mask")
	);

	private static final MethodHandle MH_ATTR = LAYOUT.sliceHandle(groupElement("attr"));

	private static final VarHandle VH_MASK = LAYOUT.varHandle(groupElement("mask"));

	public static LineConfigAttribute create(MemorySegment memorySegment) throws Throwable {
		var lineconfigattributeInstance = LineConfigAttribute.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			lineconfigattributeInstance = lineconfigattributeInstance.from(memorySegment);
		}
		return lineconfigattributeInstance;
	}

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
