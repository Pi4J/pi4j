package com.pi4j.io.spi;


import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.IODataReader;
import com.pi4j.io.IODataWriter;
import com.pi4j.io.SerialCircuitIO;

import java.nio.ByteBuffer;

/**
 * Represents an open SPI (Serial Peripheral Interface) device on a configured bus and channel,
 * supporting full-duplex byte transfers as well as separate read and write operations. An instance
 * is created from a {@link SpiProvider} using a {@link SpiConfig} produced by a {@link SpiConfigBuilder};
 * it inherits write behaviour from {@link IODataWriter}, read behaviour from {@link IODataReader}, and
 * full-duplex serial exchange behaviour from {@link SerialCircuitIO}.
 */
public interface Spi extends IO<Spi, SpiConfig, SpiProvider>, AutoCloseable, IODataWriter, IODataReader, SerialCircuitIO {
    /**
     * Default SPI bus ({@link SpiBus#BUS_0}) used when no bus is explicitly configured.
     */
    SpiBus DEFAULT_BUS = SpiBus.BUS_0;
    /**
     * Default SPI channel (chip-select) number used when none is configured.
     */
    int DEFAULT_CHANNEL = 0;
    /**
     * Default SPI clock mode ({@link SpiMode#MODE_0}) used when no mode is configured.
     */
    SpiMode DEFAULT_MODE = SpiMode.MODE_0;
    /**
     * Default SPI chip-select line ({@link SpiChipSelect#CS_0}) used when none is configured.
     */
    SpiChipSelect DEFAULT_CHIP_SELECT = SpiChipSelect.CS_0;
    /**
     * Default SPI clock frequency in Hz (1&nbsp;MHz); the supported range is typically 500&nbsp;kHz to 32&nbsp;MHz.
     */
    int DEFAULT_BAUD = 1000000; // 1MHz (range is 500kHz - 32MHz)
    /**
     * Default bit order for write operations; {@code 0} shifts the LSB first.
     */
    int DEFAULT_WRITE_LSB_FIRST = 0;
    /**
     * Default bit order for read operations; {@code 0} shifts the LSB first.
     */
    int DEFAULT_READ_LSB_FIRST = 0;

    /**
     * Creates a new {@link SpiConfigBuilder} for assembling an {@link SpiConfig}.
     *
     * @param context the Pi4J runtime context (retained for API symmetry; not required to build the configuration)
     * @return a new SPI configuration builder instance
     */
    static SpiConfigBuilder newConfigBuilder(Context context) {
        return SpiConfigBuilder.newInstance();
    }

    /**
     * Indicates whether this SPI device is currently open and ready for communication.
     *
     * @return {@code true} if the device has been opened and not yet closed, otherwise {@code false}
     */
    boolean isOpen();


    /**
     * Opens this SPI device, acquiring the underlying bus/channel resources needed for communication.
     */
    void open();

    /**
     * Closes this SPI device, releasing the underlying bus/channel resources.
     */
    void close();

    // ------------------------------------------------------------------------------------
    // XFER :: BYTE
    // ------------------------------------------------------------------------------------

    /**
     * Transfers a single byte over SPI: writes the given byte to the device while simultaneously
     * reading one byte back, and returns the read value.
     *
     * @param handle reserved for provider use; not consulted by this default implementation
     * @param value  the byte to write to the SPI device
     * @return the byte read back from the SPI device as an unsigned value (0&ndash;255) when the transfer
     *         succeeds, otherwise the negative error code returned by the underlying transfer
     */
    default int transferByte(int handle, byte value) {
        byte[] temp = new byte[]{value};
        int result = transfer(temp);
        if (result <= 0) return result;

        // we must convert the raw byte to an unsigned int for the return value
        // otherwise, anything higher than 0x80 may result in a negative int value
        return Byte.toUnsignedInt(temp[0]);
    }

    // ------------------------------------------------------------------------------------
    // XFER :: BYTE ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * Transfers (writes and reads simultaneously) multiple bytes with this SPI device. Write data is
     * taken from the {@code write} array starting at {@code writeOffset} for {@code numberOfBytes} bytes,
     * and data read back from the device is copied into the {@code read} array starting at {@code readOffset}
     * using the same length. Both arrays must be at least {@code numberOfBytes} plus their respective offsets
     * in size. This is the primary transfer operation that all other {@code transfer} overloads delegate to.
     *
     * @param write         the array of bytes to write to the SPI device
     * @param writeOffset   the index in {@code write} at which to begin sending bytes
     * @param read          the array into which bytes read back from the SPI device are stored
     * @param readOffset    the index in {@code read} at which to begin storing received bytes
     * @param numberOfBytes the number of bytes to exchange in this transfer
     * @return the number of bytes read back, or a negative provider-specific error code on failure
     */
    int transfer(byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes);

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'write' byte array
     * from the zero index (first byte) to the specified length ('numberOfBytes').  Data
     * read from the SPI device is then copied to the 'read' byte array starting at the zero
     * index (first byte) using the same length ('numberOfBytes').  Both the 'write' and 'read'
     * byte arrays must be at least the size of the defined 'numberOfBytes' + their corresponding
     * offsets.
     *
     * @param write         the array of bytes to write to the SPI device
     * @param read          the array of bytes to store read data in from the SPI device
     * @param numberOfBytes the number of bytes to exchange in this transfer
     * @return the number of bytes read back, or a negative provider-specific error code on failure
     */
    default int transfer(byte[] write, byte[] read, int numberOfBytes) {
        return transfer(write, 0, read, 0, numberOfBytes);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the entire contents of
     * the  'write' byte array (from the zero index (first byte) to the array size).  Data
     * read from the SPI device is then copied to the 'read' byte array starting at the zero
     * index (first byte) using the same length of the number of bytes in the 'write' array.
     * The 'read' byte array must be at least the size of the defined 'write' byte array.
     *
     * @param write the array of bytes to write to the SPI device
     * @param read  the array of bytes to store read data in from the SPI device
     * @return the number of bytes read back, or a negative provider-specific error code on failure
     */
    default int transfer(byte[] write, byte[] read) {
        return transfer(write, 0, read, 0, write.length);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'buffer' byte array
     * from the given 'offset' index to the specified 'length' (number of bytes).  Data
     * read back from the SPI device is then copied to the same 'buffer' byte array starting
     * at the given 'offset' using the same 'length' (number of bytes).  The 'buffer' byte
     * array must be at least the size of the defined 'length' + 'offset'.
     *
     * @param buffer the array of bytes to write to the SPI device and to store read data
     *               back from the SPI device
     * @param offset the starting offset position in the provided buffer to
     *               start writing to the SPI device from and the position
     *               used as the starting offset position to place data bytes
     *               read back from the SPI device.
     * @param length the number of bytes to exchange in this transfer
     * @return the number of bytes read back, or a negative provider-specific error code on failure
     */
    default int transfer(byte[] buffer, int offset, int length) {
        return transfer(buffer, offset, buffer, offset, length);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'buffer' byte array
     * from the zero index (first byte) to the specified length (number of bytes).  Data
     * read from the SPI device is then copied back to the same 'buffer' byte array starting
     * at the zero index (first byte) using the same length.  The 'buffer' byte array must
     * be at least the size of the defined 'length'.
     *
     * @param buffer the array of bytes to write to the SPI device and to store read data
     *               back from the SPI device
     * @param length the number of bytes to exchange in this transfer
     * @return the number of bytes read back, or a negative provider-specific error code on failure
     */
    default int transfer(byte[] buffer, int length) {
        return transfer(buffer, 0, length);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the entire 'buffer' byte array
     * contents.  Data read back from the SPI device is then copied back to the same 'buffer'
     * byte array starting at the zero index (first byte).
     *
     * @param buffer the array of bytes to write to the SPI device and to store read data
     *               back from the SPI device
     * @return the number of bytes read back, or a negative provider-specific error code on failure
     */
    default int transfer(byte[] buffer) {
        return transfer(buffer, 0, buffer.length);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'write' byte buffer
     * from the given 'writeOffset' index to the specified length ('numberOfBytes').  Data
     * read from the SPI device is then copied to the 'read' byte buffer at the given 'readOffset'
     * using the same length ('numberOfBytes').  Both the 'write' and 'read' byte buffers must
     * at least have the available capacity of the defined 'numberOfBytes' + their corresponding
     * offsets.
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     * used but rather only the explicit offset and
     * length provided.  If the requested length is
     * greater than the buffers capacity (minus offset)
     * then the specified length will be ignored and
     * this function will only read the number of
     * bytes up to the buffers' available space.
     *
     * @param write         the ByteBuffer to write to the SPI device
     * @param writeOffset   the starting offset position in the provided 'write' buffer to
     *                      start writing to the SPI device from.
     * @param read          the ByteBuffer to store read data in from the SPI device
     * @param readOffset    the starting offset position in the provided 'read' buffer to place
     *                      data bytes read from the SPI device.
     * @param numberOfBytes the number of bytes to exchange in this transfer
     * @return the number of bytes actually read back into {@code read}, or a negative provider-specific
     *         error code on failure
     */
    default int transfer(ByteBuffer write, int writeOffset, ByteBuffer read, int readOffset, int numberOfBytes) {
        // perform bounds checking on requested length versus total remaining size available
        if (numberOfBytes > (write.capacity() - writeOffset)) {
            numberOfBytes = write.capacity() - writeOffset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[numberOfBytes];
        int actualLength = transfer(write.array(), 0, temp, 0, numberOfBytes);

        // return any error codes ( < 0)
        if (actualLength < 0) return actualLength;

        // perform bounds checking on number of bytes read versus the length requested
        if (actualLength < numberOfBytes) numberOfBytes = actualLength;

        // copy the data from the temporary byte array into the return buffer at the given offset
        read.position(readOffset);
        read.put(temp, 0, numberOfBytes);

        // return actual number of bytes read
        return numberOfBytes;
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'write' byte buffer
     * from the buffer's current position up to the specified length ('numberOfBytes').  Data
     * read from the SPI device is then copied to the 'read' byte buffer starting at the buffer's
     * current position using the same length ('numberOfBytes').  Both the 'write' and 'read'
     * byte buffers must at least have the available capacity of the defined 'numberOfBytes' +
     * their corresponding current positions.
     * <p>
     * NOTE:  The contents from the 'write' byte buffer is read
     * from the current position index up to the length
     * requested or up to the buffer's remaining limit;
     * whichever is is lower .  If the buffer's current
     * position is already at the buffer's limit, then we
     * will automatically flip the buffer to begin reading
     * data from the zero position up to the buffer's limit     *
     * <p>
     * NOTE:  The data bytes read from the SPI device are copied/
     * inserted into the 'read' byte buffer starting at the current
     * position index up to the length requested or up to the
     * buffer's remaining limit; whichever is is lower .  If
     * the buffer's current position is already at the buffer's
     * limit, then we will automatically rewind the buffer to
     * begin writing data from the zero position up to the
     * buffer's limit.
     *
     * @param write         the ByteBuffer to write to the SPI device
     * @param read          the ByteBuffer to store read data in from the SPI device
     * @param numberOfBytes the number of bytes to exchange in this transfer
     * @return the number of bytes actually read back into {@code read}, or a negative provider-specific
     *         error code on failure
     */
    default int transfer(ByteBuffer write, ByteBuffer read, int numberOfBytes) {
        // if the 'write' buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if (write.position() == write.limit()) write.flip();

        // if the 'read' buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if (read.position() == read.limit()) read.rewind();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if (numberOfBytes > write.remaining()) numberOfBytes = write.remaining();

        // write contents from the buffer starting at the current position up to the specified length
        return transfer(write, write.position(), read, read.position(), numberOfBytes);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the byte buffer
     * from the given 'offset' index to the specified length (number of bytes).  Data
     * read from the SPI device is then copied to the byte buffer at the given 'offset'
     * using the same length (number of bytes). The byte buffer must at least have the
     * available capacity of the defined 'length' + 'offset'.
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     * used but rather only the explicit offset and
     * length provided.  If the requested length is
     * greater than the buffers capacity (minus offset)
     * then the specified length will be ignored and
     * this function will only read the number of
     * bytes up to the buffers' available space.
     *
     * @param buffer the byte buffer to write to the SPI device and to store read data
     *               back from the SPI device
     * @param offset the starting offset position in the provided buffer to
     *               start writing to the SPI device from and the position
     *               used as the starting offset position to place data bytes
     *               read back from the SPI device.
     * @param length the number of bytes to exchange in this transfer
     * @return the number of bytes actually read back into {@code buffer}, or a negative provider-specific
     *         error code on failure
     */
    default int transfer(ByteBuffer buffer, int offset, int length) {
        // perform bounds checking on requested length versus total remaining size available
        if (length > (buffer.capacity() - offset)) {
            length = buffer.capacity() - offset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[length];
        int actualLength = transfer(buffer.array(), 0, temp, 0, length);

        // return any error codes ( < 0)
        if (actualLength < 0) return actualLength;

        // perform bounds checking on number of bytes read versus the length requested
        if (actualLength < length) length = actualLength;

        // copy the data from the temporary byte array into the return buffer at the given offset
        buffer.position(offset);
        buffer.put(temp, 0, length);

        // return actual number of bytes read
        return length;
    }

    // --------------------
    // Disambiguation
    // ---------------------

    @Override
    default int read(byte[] data) {
        return SerialCircuitIO.super.read(data);
    }

    @Override
    default int read(byte[] data, int offset, int length) {
        return SerialCircuitIO.super.read(data, offset, length);
    }

    @Override
    default int write(byte... data) {
        return SerialCircuitIO.super.write(data);
    }

    @Override
    default int write(byte[] data, int offset, int length) {
        return SerialCircuitIO.super.write(data, offset, length);
    }

    // ------------------------------------------------------------------------------------
    //  writeThenRead
    // ------------------------------------------------------------------------------------

    /**
     * Writes the entire {@code write} array to the SPI device and then reads bytes back into the
     * {@code read} array, performing the two operations as distinct (non-overlapping) SPI records
     * with no inter-record delay.
     *
     * @param write the bytes to send to the SPI device
     * @param read  the array that receives the bytes read back; its length determines how many bytes are read
     */
    default void writeThenRead(byte[] write, byte[] read) {
        writeThenRead(write, 0, write.length, (short) 0, read, 0, read.length);
    }

    /**
     * Writes the entire {@code write} array to the SPI device, waits the given delay, and then reads
     * bytes back into the {@code read} array, allowing the target chip time to prepare its response.
     *
     * @param write          the bytes to send to the SPI device
     * @param readDelayNanos delay in nanoseconds, applied after the write record is processed by the
     *                       kernel and before the read record is processed
     * @param read           the array that receives the bytes read back; its length determines how many bytes are read
     */
    default void writeThenRead(byte[] write, int readDelayNanos, byte[] read) {
        writeThenRead(write, 0, write.length, (short) readDelayNanos, read, 0, read.length);
    }

    /**
     * Writes bytes to the SPI device and then reads bytes back as two distinct SPI records, with a
     * configurable inter-record delay so the target chip can complete any processing between the
     * write and the read. Write data is taken from {@code write} starting at {@code writeOffset} for
     * {@code writeLength} bytes; data read back is stored in {@code read} starting at {@code readOffset}
     * for {@code readLength} bytes. Each array must be at least its respective length plus offset in size.
     * <p>
     * Not all providers support this operation; the default implementation always throws.
     *
     * @param write          the bytes to send to the SPI device
     * @param writeOffset    the index in {@code write} at which to begin sending bytes
     * @param writeLength    the number of bytes to send from {@code write}
     * @param readDelayNanos delay in nanoseconds applied after the write record and before the read record
     * @param read           the array that receives the bytes read back
     * @param readOffset     the index in {@code read} at which to begin storing received bytes
     * @param readLength     the number of bytes to read back into {@code read}
     * @throws IllegalStateException if the active provider does not support {@code writeThenRead}
     */
    default void writeThenRead(byte[] write, int writeOffset, int writeLength, int readDelayNanos, byte[] read, int readOffset, int readLength) {
        throw new IllegalStateException("writeThenRead Not supported in this provider. \n See https://www.pi4j.com/documentation/providers/");
    }
}
