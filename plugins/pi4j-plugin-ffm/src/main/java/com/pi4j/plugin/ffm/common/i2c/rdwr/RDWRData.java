package com.pi4j.plugin.ffm.common.i2c.rdwr;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

/**
 * Source: include/uapi/linux/i2c-dev.h:50:8
 * <p>
 * This is the structure as used in the I2C_RDWR ioctl call
 */
public record RDWRData(I2CMessage[] msgs, int nmsgs) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.ADDRESS.withName("msgs"),
        ValueLayout.JAVA_INT.withName("nmsgs")
    );

    private static final VarHandle VH_MSGS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("msgs"));
    private static final VarHandle VH_NMSGS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("nmsgs"));

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RDWRData from(MemorySegment buffer) throws Throwable {
        var nmsgs = (int) VH_NMSGS.get(buffer, 0L);

        var bodySegment = ((MemorySegment) VH_MSGS.get(buffer, 0L))
            .reinterpret(nmsgs * I2CMessage.LAYOUT.byteSize());

        var messages = new I2CMessage[nmsgs];
        for (int i = 0; i < nmsgs; i++) {
            var messageSlice = bodySegment.asSlice(i * I2CMessage.LAYOUT.byteSize(), I2CMessage.LAYOUT.byteSize());
            var message = I2CMessage.createEmpty();
            message = message.from(messageSlice);
            messages[i] = message;
        }

        return new RDWRData(messages, nmsgs);
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("RDWRDate needs to be called with external Segment Allocator");
    }

    @Override
    public void to(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        VH_NMSGS.set(buffer, 0L, nmsgs);

        var bodySegment = allocator.allocate(msgs.length * I2CMessage.LAYOUT.byteSize());
        for (int i = 0; i < msgs.length; i++) {
            var messageSlice = bodySegment.asSlice(i * I2CMessage.LAYOUT.byteSize(), I2CMessage.LAYOUT.byteSize());
            msgs[i].to(messageSlice, allocator);
            messageSlice.copyFrom(messageSlice);
        }

        VH_MSGS.set(buffer, 0L, bodySegment);
    }

    @Override
    public String toString() {
        return "RDWRData{" +
            "msgs=" + Arrays.toString(msgs) +
            ", nmsgs=" + nmsgs +
            '}';
    }
}
