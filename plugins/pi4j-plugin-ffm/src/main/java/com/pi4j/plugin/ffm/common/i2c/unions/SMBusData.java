package com.pi4j.plugin.ffm.common.i2c.unions;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:141:7
 */
public record SMBusData(byte _byte, short word, byte[] block) implements Pi4JLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.unionLayout(
		ValueLayout.JAVA_BYTE.withName("byte"),
		MemoryLayout.paddingLayout(3),
		ValueLayout.JAVA_SHORT.withName("word"),
		MemoryLayout.paddingLayout(1),
		MemoryLayout.sequenceLayout(34, ValueLayout.JAVA_BYTE).withName("block"),
		MemoryLayout.paddingLayout(3)
	);

	private static final VarHandle VH_BYTE = LAYOUT.varHandle(groupElement("byte"));

	private static final VarHandle VH_WORD = LAYOUT.varHandle(groupElement("word"));

	private static final MethodHandle MH_BLOCK = LAYOUT.sliceHandle(groupElement("block"));

	public static SMBusData create(MemorySegment memorySegment) throws Throwable {
		var smbusdataInstance = SMBusData.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			smbusdataInstance = smbusdataInstance.from(memorySegment);
		}
		return smbusdataInstance;
	}

	public static SMBusData createEmpty() {
		return new SMBusData((byte) 0, (short) 0, new byte[]{});
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SMBusData from(MemorySegment buffer) throws Throwable {
		return new SMBusData(
			(byte) VH_BYTE.get(buffer, 0L),
			(short) VH_WORD.get(buffer, 0L),
			invokeExact(MH_BLOCK, buffer).toArray(ValueLayout.JAVA_BYTE));
	}

	@Override
	public void to(MemorySegment buffer) throws Throwable {
		VH_BYTE.set(buffer, 0L, _byte);
		VH_WORD.set(buffer, 0L, word);
		var blockTmp = invokeExact(MH_BLOCK, buffer);
		for (int i = 0; i < block.length; i++) {
			blockTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, block[i]);
		}
	}

    @Override
    public String toString() {
        return "SMBusData{" +
            "byte=" + _byte +
            ", word=" + word +
            ", block=" + Arrays.toString(block) +
            '}';
    }
}
