package com.pi4j.plugin.ffm.common.i2c.rdwr;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

/**
 * Maps the Linux kernel {@code struct i2c_rdwr_ioctl_data} (include/uapi/linux/i2c-dev.h), the
 * argument passed to the {@code I2C_RDWR} ioctl to submit a batch of {@link I2CMessage} segments
 * as a single combined I2C transaction. Implements {@link Pi4JLayout} to marshal to/from the
 * off-heap {@link #LAYOUT} representation.
 *
 * @param msgs  the I2C message segments to transfer in order
 * @param nmsgs the number of messages in {@code msgs} to process
 */
public record RDWRData(I2CMessage[] msgs, int nmsgs) implements Pi4JLayout {
    /**
     * Off-heap memory layout matching the kernel {@code struct i2c_rdwr_ioctl_data}: a pointer
     * to the array of {@link I2CMessage} structs ({@code msgs}) and the message count ({@code nmsgs}).
     */
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

    /**
     * {@inheritDoc}
     * <p>
     * Not supported for this type: the message array must be allocated off-heap, so the
     * {@link #to(MemorySegment, SegmentAllocator)} overload with an allocator must be used instead.
     *
     * @throws UnsupportedOperationException always
     */
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
