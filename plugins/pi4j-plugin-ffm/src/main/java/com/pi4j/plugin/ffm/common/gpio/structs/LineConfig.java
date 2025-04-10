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
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:167:8
 *
 * struct gpio_v2_line_config - Configuration for GPIO lines
 * @flags: flags for the GPIO lines, with values from &enum
 * gpio_v2_line_flag, such as %GPIO_V2_LINE_FLAG_ACTIVE_LOW,
 * %GPIO_V2_LINE_FLAG_OUTPUT etc, added together.  This is the default for
 * all requested lines but may be overridden for particular lines using
 * @attrs.
 * @num_attrs: the number of attributes in @attrs
 * @padding: reserved for future use and must be zero filled
 * @attrs: the configuration attributes associated with the requested
 * lines.  Any attribute should only be associated with a particular line
 * once.  If an attribute is associated with a line multiple times then the
 * first occurrence (i.e. lowest index) has precedence.
 */
public record LineConfig(long flags, int numAttrs, int[] padding,
		LineConfigAttribute[] attrs) implements Pi4JLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("flags"),
		ValueLayout.JAVA_INT.withName("num_attrs"),
		MemoryLayout.sequenceLayout(5, ValueLayout.JAVA_INT).withName("padding"),
		MemoryLayout.sequenceLayout(10, LineConfigAttribute.LAYOUT).withName("attrs")
	);

	private static final VarHandle VH_FLAGS = LAYOUT.varHandle(groupElement("flags"));

	private static final VarHandle VH_NUM_ATTRS = LAYOUT.varHandle(groupElement("num_attrs"));

	private static final MethodHandle MH_PADDING = LAYOUT.sliceHandle(groupElement("padding"));

	private static final MethodHandle MH_ATTRS = LAYOUT.sliceHandle(groupElement("attrs"));

	public static LineConfig create(MemorySegment memorySegment) throws Throwable {
		var lineconfigInstance = LineConfig.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			lineconfigInstance = lineconfigInstance.from(memorySegment);
		}
		return lineconfigInstance;
	}

	public static LineConfig createEmpty() {
		return new LineConfig(0, 0, new int[]{}, new LineConfigAttribute[]{});
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public LineConfig from(MemorySegment buffer) throws Throwable {
		var attrsMemorySegment = invokeExact(MH_ATTRS, buffer);
		var attrs = new LineConfigAttribute[10];
		for(int i = 0; i < 10; i++) {
			var tmp = LineConfigAttribute.createEmpty();
			attrs[i] = tmp.from(attrsMemorySegment.asSlice(LineConfigAttribute.LAYOUT.byteSize() * i, LineConfigAttribute.LAYOUT.byteSize()));
		}
		return new LineConfig(
			(long) VH_FLAGS.get(buffer, 0L),
			(int) VH_NUM_ATTRS.get(buffer, 0L),
			invokeExact(MH_PADDING, buffer).toArray(ValueLayout.JAVA_INT),
			attrs);
	}

	@Override
	public void to(MemorySegment buffer) throws Throwable {
		VH_FLAGS.set(buffer, 0L, flags);
		VH_NUM_ATTRS.set(buffer, 0L, numAttrs);
		var paddingTmp = invokeExact(MH_PADDING, buffer);
		for (int i = 0; i < padding.length; i++) {
			paddingTmp.setAtIndex(ValueLayout.JAVA_INT, i, padding[i]);
		}
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
            ", padding=" + Arrays.toString(padding) +
            ", attrs=" + Arrays.toString(attrs) +
            '}';
    }
}
