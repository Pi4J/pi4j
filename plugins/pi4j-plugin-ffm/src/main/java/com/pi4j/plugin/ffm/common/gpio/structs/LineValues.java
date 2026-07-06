package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Maps the Linux kernel {@code struct gpio_v2_line_values} (include/uapi/linux/gpio.h) to a
 * {@link MemorySegment}-backed record, carrying the logical values of GPIO lines for the
 * {@code GPIO_V2_LINE_GET_VALUES_IOCTL} and {@code GPIO_V2_LINE_SET_VALUES_IOCTL} operations.
 *
 * @param bits  bitmap of line values, 1 for active and 0 for inactive; bit {@code n} corresponds to index
 *              {@code n} into {@link LineRequest#offsets()}
 * @param mask  bitmap selecting which lines to get or set; only lines whose bit is set are affected
 */
public record LineValues(long bits, long mask) implements Pi4JLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("bits"),
		ValueLayout.JAVA_LONG.withName("mask")
	);

	private static final VarHandle VH_BITS = LAYOUT.varHandle(groupElement("bits"));

	private static final VarHandle VH_MASK = LAYOUT.varHandle(groupElement("mask"));

    /**
     * Decodes a {@link LineValues} from a native buffer holding a {@code gpio_v2_line_values} struct.
     * A {@link MemorySegment#NULL} buffer yields an empty instance.
     *
     * @param memorySegment native memory holding the encoded struct, or {@link MemorySegment#NULL}
     * @return the decoded values, or empty values if the segment is null
     * @throws Throwable if reading the native memory fails
     */
	public static LineValues create(MemorySegment memorySegment) throws Throwable {
		var linevaluesInstance = LineValues.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			linevaluesInstance = linevaluesInstance.from(memorySegment);
		}
		return linevaluesInstance;
	}

    /**
     * Creates empty values with a zero bitmap and zero mask, suitable as a target for {@link #from(MemorySegment)}.
     *
     * @return a zero-initialized {@link LineValues}
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
