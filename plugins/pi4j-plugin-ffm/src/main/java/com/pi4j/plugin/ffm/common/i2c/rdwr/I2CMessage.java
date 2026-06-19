package com.pi4j.plugin.ffm.common.i2c.rdwr;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Arrays;


/**
 * Maps the Linux kernel {@code struct i2c_msg} (include/uapi/linux/i2c.h), one segment of an
 * I2C transaction beginning with a START condition. Several of these are batched together in a
 * {@link RDWRData} buffer and submitted through the {@code I2C_RDWR} ioctl. Implements
 * {@link Pi4JLayout} to marshal to/from the off-heap {@link #LAYOUT} representation.
 *
 * @param address the slave address, either 7 or 10 bits (a 10-bit address additionally requires
 *                the {@code I2C_M_TEN} flag and adapter support)
 * @param flags   the message flags bitmask (e.g. {@code I2C_M_RD = 0x0001} to read from slave to
 *                master, {@code I2C_M_TEN}, {@code I2C_M_RECV_LEN}, {@code I2C_M_NOSTART}, ...)
 * @param len     the number of data bytes in {@code buf} to read from or write to the slave
 * @param buf     the data buffer that is read into, or written from
 */
public record I2CMessage(int address, int flags, int len, byte[] buf) implements Pi4JLayout {
    /**
     * Off-heap memory layout matching the kernel {@code struct i2c_msg}: three 16-bit fields
     * ({@code address}, {@code flags}, {@code len}), two padding bytes, and a pointer to the
     * data buffer ({@code buf}).
     */
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_SHORT.withName("address"),
        ValueLayout.JAVA_SHORT.withName("flags"),
        ValueLayout.JAVA_SHORT.withName("len"),
        MemoryLayout.paddingLayout(2),
        ValueLayout.ADDRESS.withName("buf")
    );
    private static final VarHandle VH_ADDRESS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("address"));
    private static final VarHandle VH_FLAGS = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("flags"));
    private static final VarHandle VH_LEN = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("len"));
    private static final VarHandle VH_BUFFER = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("buf"));

    /**
     * Creates an empty message with zeroed address, flags and length and an empty buffer,
     * typically used as a target for {@link #from(MemorySegment)} when reading a struct back.
     *
     * @return a new, zero-initialized {@link I2CMessage}
     */
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

        var bufferAddress = (MemorySegment) VH_BUFFER.get(buffer, 0L);
        var bufferSegment = bufferAddress.reinterpret(len);
        var buf = new byte[len];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = bufferSegment.getAtIndex(ValueLayout.JAVA_BYTE, i);
        }

        return new I2CMessage(address, flags, len, buf);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Not supported for this type: the data buffer pointer must be allocated off-heap, so the
     * {@link #to(MemorySegment, SegmentAllocator)} overload with an allocator must be used instead.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void to(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("I2CMessage needs to be called with external Segment Allocator");
    }

    @Override
    public void to(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        VH_ADDRESS.set(buffer, 0L, (short) address);
        VH_FLAGS.set(buffer, 0L, (short) flags);
        VH_LEN.set(buffer, 0L, (short) len);

        var bufferSegment = allocator.allocateFrom(ValueLayout.JAVA_BYTE, buf);
        VH_BUFFER.set(buffer, 0L, bufferSegment);
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
