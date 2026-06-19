package com.pi4j.plugin.ffm.common.i2c.smbus;


import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;

/**
 * Maps the Linux kernel {@code struct i2c_smbus_ioctl_data} (include/uapi/linux/i2c-dev.h), the
 * argument passed to the {@code I2C_SMBUS} ioctl to perform a single SMBus transaction. Implements
 * {@link Pi4JLayout} to marshal to/from the off-heap {@link #LAYOUT} representation.
 *
 * @param readWrite transfer direction: {@code I2C_SMBUS_READ} or {@code I2C_SMBUS_WRITE}
 * @param command   the SMBus command/register byte for the transaction
 * @param size      the SMBus transaction size code (e.g. {@code I2C_SMBUS_BYTE},
 *                  {@code I2C_SMBUS_BYTE_DATA}, {@code I2C_SMBUS_WORD_DATA}, {@code I2C_SMBUS_BLOCK_DATA})
 * @param data      the {@link SMBusData} payload read or written, or {@code null} when none is needed
 */
public record SMBusIoctlData(byte readWrite, byte command, int size, SMBusData data) implements Pi4JLayout {
    /**
     * Off-heap memory layout matching the kernel {@code struct i2c_smbus_ioctl_data}: the
     * {@code read_write} and {@code command} bytes, two padding bytes, the {@code size} code,
     * and a pointer to the {@link SMBusData} payload.
     */
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_BYTE.withName("read_write"),
        ValueLayout.JAVA_BYTE.withName("command"),
        MemoryLayout.paddingLayout(2),
        ValueLayout.JAVA_INT.withName("size"),
        ValueLayout.ADDRESS.withTargetLayout(SMBusData.LAYOUT).withName("data")
    );
    private static final VarHandle VH_READ_WRITE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("read_write"));
    private static final VarHandle VH_COMMAND = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("command"));
    private static final VarHandle VH_SIZE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("size"));
    private static final VarHandle MH_DATA = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("data"));

    /**
     * Creates an empty instance with zeroed fields and a {@code null} data payload.
     *
     * @return a new, zero-initialized {@link SMBusIoctlData}
     */
    public static SMBusIoctlData createEmpty() {
        return new SMBusIoctlData((byte) 0, (byte) 0, 0, null);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SMBusIoctlData from(MemorySegment buffer) throws Throwable {
        var readWrite = (byte) VH_READ_WRITE.get(buffer, 0L);
        var command = (byte) VH_COMMAND.get(buffer, 0L);
        var size = (int) VH_SIZE.get(buffer, 0L);
        var tmp = (MemorySegment) MH_DATA.get(buffer, 0L);
        var data0 = data.from(tmp);
        return new SMBusIoctlData(readWrite, command, size, data0);
    }

    @Override
    public void to(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        VH_READ_WRITE.set(buffer, 0L, readWrite);
        VH_COMMAND.set(buffer, 0L, command);
        VH_SIZE.set(buffer, 0L, size);
        var smbusOffHeap = allocator.allocate(SMBusData.LAYOUT);
        data.to(smbusOffHeap);
        MH_DATA.set(buffer, 0L, smbusOffHeap);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Not supported for this type: the {@link SMBusData} payload must be allocated off-heap, so the
     * {@link #to(MemorySegment, SegmentAllocator)} overload with an allocator must be used instead.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void to(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("Converting to MemorySegment without context is not supported");
    }

    @Override
    public String toString() {
        return "SMBusIoctlData{" +
            "readWrite=" + readWrite +
            ", command=" + command +
            ", size=" + size +
            ", data=" + data +
            '}';
    }
}
