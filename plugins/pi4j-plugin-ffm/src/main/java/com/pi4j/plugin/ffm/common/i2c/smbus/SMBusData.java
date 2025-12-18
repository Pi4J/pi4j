package com.pi4j.plugin.ffm.common.i2c.smbus;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: include/uapi/linux/i2c.h:141:7
 * <p>
 * Data for SMBus Messages
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

    /**
     * Creates SMBusData instance from MemorySegment provided.
     *
     * @param memorySegment buffer to construct SMBusData from
     * @return SMBusData instance
     * @throws Throwable if there is any exception while converting buffer to java object
     */
	public static SMBusData create(MemorySegment memorySegment) throws Throwable {
		var smbusdataInstance = SMBusData.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			smbusdataInstance = smbusdataInstance.from(memorySegment);
		}
		return smbusdataInstance;
	}

    /**
     * Creates empty SMBusData object.
     *
     * @return empty SMBusData object
     */
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
        byte[] buf = null;
        if (block != null && block.length > 0) {
            var bufMemorySegment = invokeExact(MH_BLOCK, buffer);
            buf = new byte[block.length];
            for (int i = 0; i < buf.length; i++) {
                buf[i] = bufMemorySegment.getAtIndex(ValueLayout.JAVA_BYTE, i);
            }
        }
		return new SMBusData(
			_byte != 0 ? (byte) VH_BYTE.get(buffer, 0L) : 0,
			word != 0 ? (short) VH_WORD.get(buffer, 0L) : 0,
            buf != null ? buf : new byte[]{}
        );
	}

	@Override
	public void to(MemorySegment buffer) throws Throwable {
        if (_byte != 0) {
            VH_BYTE.set(buffer, 0L, _byte);
        }
        if (word != 0) {
            VH_WORD.set(buffer, 0L, word);
        }
        if (block != null && block.length != 0) {
            var blockTmp = invokeExact(MH_BLOCK, buffer);
            for (int i = 0; i < block.length; i++) {
                blockTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, block[i]);
            }
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
