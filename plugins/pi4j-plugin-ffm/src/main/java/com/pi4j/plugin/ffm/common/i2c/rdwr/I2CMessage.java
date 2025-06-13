package com.pi4j.plugin.ffm.common.i2c.rdwr;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

public record I2CMessage(int address, int flags, int len, byte[] buf) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("address"),
        ValueLayout.JAVA_INT.withName("flags"),
        ValueLayout.JAVA_INT.withName("len"),
        MemoryLayout.sequenceLayout(1024, ValueLayout.JAVA_BYTE).withName("buf")
    );
    private static final VarHandle VH_ADDRESS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("address"));
    private static final VarHandle VH_FLAGS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("flags"));
    private static final VarHandle VH_LEN = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("len"));
    private static final MethodHandle MH_BUF = LAYOUT.sliceHandle(MemoryLayout.PathElement.groupElement("buf"));

    public static I2CMessage createEmpty() {
        return new I2CMessage((byte) 0, (byte) 0, 0, new byte[0]);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public I2CMessage from(MemorySegment buffer) throws Throwable {
        var address = (int) VH_ADDRESS.get(buffer, 0L);
        var flags = (int) VH_FLAGS.get(buffer, 0L);
        var len = (int) VH_LEN.get(buffer, 0L);
        var buf = invokeExact(MH_BUF, buffer).toArray(ValueLayout.JAVA_BYTE);
        return new I2CMessage(address, flags, len, buf);
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_ADDRESS.set(buffer, 0L, address);
        VH_FLAGS.set(buffer, 0L, flags);
        VH_LEN.set(buffer, 0L, len);
        var bufTmp = invokeExact(MH_BUF, buffer);
        for (int i = 0; i < buf.length; i++) {
            bufTmp.setAtIndex(ValueLayout.JAVA_INT, i, buf[i]);
        }
    }

    @Override
    public String toString() {
        return "I2CMessage{" +
            "address=" + address +
            ", flags=" + flags +
            ", len=" + len +
            ", buf=" + Arrays.toString(buf) +
            '}';
    }
}
