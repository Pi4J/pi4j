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
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:32:8
 *
 * struct gpiochip_info - Information about a certain GPIO chip
 * @name: the Linux kernel name of this GPIO chip
 * @label: a functional name for this GPIO chip, such as a product
 * number, may be empty (i.e. label[0] == '\0')
 * @lines: number of GPIO lines on this chip
 */
public record ChipInfo(byte[] name, byte[] label, int lines) implements Pi4JLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("name"),
		MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("label"),
		ValueLayout.JAVA_INT.withName("lines")
	);

	private static final MethodHandle MH_NAME = LAYOUT.sliceHandle(groupElement("name"));

	private static final MethodHandle MH_LABEL = LAYOUT.sliceHandle(groupElement("label"));

	private static final VarHandle VH_LINES = LAYOUT.varHandle(groupElement("lines"));

	public static ChipInfo create(MemorySegment memorySegment) throws Throwable {
		var chipinfoInstance = ChipInfo.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			chipinfoInstance = chipinfoInstance.from(memorySegment);
		}
		return chipinfoInstance;
	}

	public static ChipInfo createEmpty() {
		return new ChipInfo(new byte[]{}, new byte[]{}, 0);
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ChipInfo from(MemorySegment buffer) throws Throwable {
        // we know that this two fields are strings, so when converting from byte array we trim the zeroes left in byte array for padding
        var name = new String(invokeExact(MH_NAME, buffer).toArray(ValueLayout.JAVA_BYTE)).trim();
        var label = new String(invokeExact(MH_LABEL, buffer).toArray(ValueLayout.JAVA_BYTE)).trim();
		return new ChipInfo(
            name.getBytes(),
            label.getBytes(),
			(int) VH_LINES.get(buffer, 0L));
	}

	@Override
	public void to(MemorySegment buffer) throws Throwable {
		var nameTmp = invokeExact(MH_NAME, buffer);
		for (int i = 0; i < name.length; i++) {
			nameTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, name[i]);
		}
		var labelTmp = invokeExact(MH_LABEL, buffer);
		for (int i = 0; i < label.length; i++) {
			labelTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, label[i]);
		}
		VH_LINES.set(buffer, 0L, lines);
	}

    @Override
    public String toString() {
        return "ChipInfo{" +
            "name=(" + new String(name) + ")" + Arrays.toString(name) +
            ", label=(" + new String(label) + ")" + Arrays.toString(label) +
            ", lines=" + lines +
            '}';
    }
}
