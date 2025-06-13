package com.pi4j.plugin.ffm.common.i2c.rdwr;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

public record RDWRData(I2CMessage[] msgs, int nmsgs) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        MemoryLayout.sequenceLayout(1024, I2CMessage.LAYOUT).withName("msgs"),
        //ValueLayout.ADDRESS.withTargetLayout(I2CMessage.LAYOUT).withName("msgs"),
        ValueLayout.JAVA_INT.withName("nmsgs")
    );

    private static final MethodHandle MH_MSGS = LAYOUT.sliceHandle(MemoryLayout.PathElement.groupElement("msgs"));
    private static final VarHandle VH_NMSGS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nmsgs"));

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RDWRData from(MemorySegment buffer) throws Throwable {
        var nmsgs = (int) VH_NMSGS.get(buffer, 0L);
        var msgSegment = invokeExact(MH_MSGS, buffer);
        var msgs = new I2CMessage[nmsgs];
        for(int i = 0; i < nmsgs; i++) {
            var tmp = I2CMessage.createEmpty();
            msgs[i] = tmp.from(msgSegment.asSlice(I2CMessage.LAYOUT.byteSize() * i, I2CMessage.LAYOUT.byteSize()));
        }
        return new RDWRData(msgs, nmsgs);
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_NMSGS.set(buffer, 0L, nmsgs);
        var msgsTmp = invokeExact(MH_MSGS, buffer);
        for (int i = 0; i < msgs.length; i++) {
            msgs[i].to(msgsTmp.asSlice(I2CMessage.LAYOUT.byteSize() * i, I2CMessage.LAYOUT.byteSize()));
        }
    }

    @Override
    public String toString() {
        return "RDWRData{" +
            "msgs=" + Arrays.toString(msgs) +
            ", nmsgs=" + nmsgs +
            '}';
    }
}
