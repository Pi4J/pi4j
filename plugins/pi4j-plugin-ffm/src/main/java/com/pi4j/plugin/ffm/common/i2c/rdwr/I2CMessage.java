package com.pi4j.plugin.ffm.common.i2c.rdwr;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Arrays;


/**
 * Source: include/uapi/linux/i2c.h:73:0
 * <p>
 * struct i2c_msg - an I2C transaction segment beginning with START
 *
 * @addr: Slave address, either 7 or 10 bits. When this is a 10 bit address,
 * %I2C_M_TEN must be set in @flags and the adapter must support
 * %I2C_FUNC_10BIT_ADDR.
 * @flags: Supported by all adapters:
 * %I2C_M_RD: read data (from slave to master). Guaranteed to be 0x0001!
 * <p>
 * Optional:
 * %I2C_M_DMA_SAFE: the buffer of this message is DMA safe. Makes only sense
 * in kernelspace, because userspace buffers are copied anyway
 * <p>
 * Only if I2C_FUNC_10BIT_ADDR is set:
 * %I2C_M_TEN: this is a 10 bit chip address
 * <p>
 * Only if I2C_FUNC_SMBUS_READ_BLOCK_DATA is set:
 * %I2C_M_RECV_LEN: message length will be first received byte
 * <p>
 * Only if I2C_FUNC_NOSTART is set:
 * %I2C_M_NOSTART: skip repeated start sequence
 * <p>
 * Only if I2C_FUNC_PROTOCOL_MANGLING is set:
 * %I2C_M_NO_RD_ACK: in a read message, master ACK/NACK bit is skipped
 * %I2C_M_IGNORE_NAK: treat NACK from client as ACK
 * %I2C_M_REV_DIR_ADDR: toggles the Rd/Wr bit
 * %I2C_M_STOP: force a STOP condition after the message
 * @len: Number of data bytes in @buf being read from or written to the I2C
 * slave address. For read transactions where %I2C_M_RECV_LEN is set, the
 * caller guarantees that this buffer can hold up to %I2C_SMBUS_BLOCK_MAX
 * bytes in addition to the initial length byte sent by the slave (plus,
 * if used, the SMBus PEC); and this value will be incremented by the number
 * of block data bytes received.
 * @buf: The buffer into which data is read, or from which it's written.
 * <p>
 * An i2c_msg is the low level representation of one segment of an I2C
 * transaction.  It is visible to drivers in the @i2c_transfer() procedure,
 * to userspace from i2c-dev, and to I2C adapter drivers through the
 * @i2c_adapter.@master_xfer() method.
 * <p>
 * Except when I2C "protocol mangling" is used, all I2C adapters implement
 * the standard rules for I2C transactions.  Each transaction begins with a
 * START.  That is followed by the slave address, and a bit encoding read
 * versus write.  Then follow all the data bytes, possibly including a byte
 * with SMBus PEC.  The transfer terminates with a NAK, or when all those
 * bytes have been transferred and ACKed.  If this is the last message in a
 * group, it is followed by a STOP.  Otherwise it is followed by the next
 * @i2c_msg transaction segment, beginning with a (repeated) START.
 * <p>
 * Alternatively, when the adapter supports %I2C_FUNC_PROTOCOL_MANGLING then
 * passing certain @flags may have changed those standard protocol behaviors.
 * Those flags are only for use with broken/nonconforming slaves, and with
 * adapters which are known to support the specific mangling options they need.
 */
public record I2CMessage(int address, int flags, int len, byte[] buf) implements Pi4JLayout {
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
     * Creates empty I2CMessage object.
     *
     * @return empty I2CMessage object
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
