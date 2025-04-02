package com.pi4j.plugin.ffm2.gpio.structs;

import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:96:8
 *
 * struct gpio_v2_line_values - Values of GPIO lines
 * @bits: a bitmap containing the value of the lines, set to 1 for active
 * and 0 for inactive.
 * @mask: a bitmap identifying the lines to get or set, with each bit
 * number corresponding to the index into &struct
 * gpio_v2_line_request.offsets.
 */
public record LineValues(long bits, long mask) implements NativeMemoryLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("bits"),
		ValueLayout.JAVA_LONG.withName("mask")
	);

	private static final VarHandle VH_BITS = LAYOUT.varHandle(groupElement("bits"));

	private static final VarHandle VH_MASK = LAYOUT.varHandle(groupElement("mask"));

	public static LineValues create(MemorySegment memorySegment) throws Throwable {
		var linevaluesInstance = LineValues.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			linevaluesInstance = linevaluesInstance.fromBytes(memorySegment);
		}
		return linevaluesInstance;
	}

	public static LineValues createEmpty() {
		return new LineValues(0, 0);
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public LineValues fromBytes(MemorySegment buffer) throws Throwable {
		return new LineValues(
			(long) VH_BITS.get(buffer, 0L),
			(long) VH_MASK.get(buffer, 0L));
	}

	@Override
	public void toBytes(MemorySegment buffer) throws Throwable {
		VH_BITS.set(buffer, 0L, bits);
		VH_MASK.set(buffer, 0L, mask);
	}

	@Override
	public boolean isEmpty() {
		return bits == 0 && mask == 0;
	}
}
