package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: include/uapi/linux/gpio.h:96:8
 *
 * struct gpio_v2_line_values - Values of GPIO lines
 * @bits: a bitmap containing the value of the lines, set to 1 for active
 * and 0 for inactive.
 * @mask: a bitmap identifying the lines to get or set, with each bit
 * number corresponding to the index into &struct
 * gpio_v2_line_request.offsets.
 */
public record LineValues(long bits, long mask) implements Pi4JLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("bits"),
		ValueLayout.JAVA_LONG.withName("mask")
	);

	private static final VarHandle VH_BITS = LAYOUT.varHandle(groupElement("bits"));

	private static final VarHandle VH_MASK = LAYOUT.varHandle(groupElement("mask"));

    /**
     * Creates LineValues instance from MemorySegment provided.
     *
     * @param memorySegment buffer to construct LineValues from
     * @return LineValues instance
     * @throws Throwable if there is any exception while converting buffer to java object
     */
	public static LineValues create(MemorySegment memorySegment) throws Throwable {
		var linevaluesInstance = LineValues.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			linevaluesInstance = linevaluesInstance.from(memorySegment);
		}
		return linevaluesInstance;
	}

    /**
     * Creates empty LineValues object.
     *
     * @return empty LineValues object
     */
	public static LineValues createEmpty() {
		return new LineValues(0, 0);
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public LineValues from(MemorySegment buffer) throws Throwable {
		return new LineValues(
			(long) VH_BITS.get(buffer, 0L),
			(long) VH_MASK.get(buffer, 0L));
	}

	@Override
	public void to(MemorySegment buffer) throws Throwable {
		VH_BITS.set(buffer, 0L, bits);
		VH_MASK.set(buffer, 0L, mask);
	}

    @Override
    public String toString() {
        return "LineValues{" +
            "bits=" + bits +
            ", mask=" + mask +
            '}';
    }
}
