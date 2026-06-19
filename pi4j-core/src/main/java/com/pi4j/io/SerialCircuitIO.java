package com.pi4j.io;

/**
 * A shared interface for SPI and I2C serial communication, centered around the
 * "writeThenRead" method that performs "connected" write and read operations.
 *
 * <p>For SPI, this means that the chip select keeps active between the write and
 * the read operation.
 *
 * <p>For I2C, this means that the operation is treated as an atomic
 * multibyte-register read.
 *
 * <p>If this strong connection between write and read is not intended, please use
 * separate write and read calls.
 *
 * <p>The write and read calls here delegate to {@link #writeThenRead} with the write or
 * read buffer null in order to require only one native implementation for
 * all cases. It is the common contract behind the SPI and I2C abstractions in
 * {@link com.pi4j.io.spi} and {@link com.pi4j.io.i2c}.
 */
public interface SerialCircuitIO extends AutoCloseable {

    /**
     * Reads from the device into the full length of the given array.
     *
     * @param data the buffer to fill with the bytes read from the device
     * @return the number of bytes requested, equal to {@code data.length} (returned for
     *         historical reasons; the buffer is always filled completely)
     */
    default int read(byte[] data) {
        return read(data, 0, data.length);
    }

    /**
     * Reads {@code length} bytes from the device into {@code buffer} starting at {@code offset}.
     *
     * @param buffer the buffer to receive the bytes read from the device
     * @param offset the start index in {@code buffer} where the read bytes are written
     * @param length the number of bytes to read
     * @return {@code length} (returned for historical reasons; the requested bytes are
     *         always read)
     */
    default int read(byte[] buffer, int offset, int length) {
        writeThenRead(null, 0, 0, 0, buffer, offset, length);
        return length;
    }

    /**
     * Writes the full given byte array to the device.
     *
     * @param buffer the bytes to write to the device
     * @return the number of bytes requested, equal to {@code buffer.length} (returned for
     *         historical reasons; the whole buffer is always written)
     */
    default int write(byte... buffer) {
        return write(buffer, 0, buffer.length);
    }

    /**
     * Writes {@code length} bytes from {@code buffer} starting at {@code offset} to the device.
     *
     * @param buffer the source buffer holding the bytes to write
     * @param offset the start index in {@code buffer} of the first byte to write
     * @param length the number of bytes to write
     * @return {@code length} (returned for historical reasons; the requested bytes are
     *         always written)
     */
    default int write(byte[] buffer, int offset, int length) {
        writeThenRead(buffer, offset, length, 0, null, 0, 0);
        return length;
    }

    /**
     * Performs a connected write-then-read across the full lengths of both buffers, with no
     * delay between the two operations.
     *
     * @param writeBuffer the bytes to write to the device before reading
     * @param readBuffer  the buffer to fill with the bytes read back from the device
     */
    default void writeThenRead(byte[] writeBuffer, byte[] readBuffer) {
        writeThenRead(writeBuffer, 0, writeBuffer.length, 0, readBuffer, 0, readBuffer.length);
    }

    /**
     * Performs a connected write-then-read across the full lengths of both buffers, pausing for
     * the given delay between the write and the read to allow the device to process the request.
     *
     * @param writeBuffer    the bytes to write to the device before reading
     * @param readDelayNanos the delay, in nanoseconds, to wait between the write and the read
     * @param readBuffer     the buffer to fill with the bytes read back from the device
     */
    default void writeThenRead(byte[] writeBuffer, int readDelayNanos, byte[] readBuffer) {
        writeThenRead(writeBuffer, 0, writeBuffer.length, readDelayNanos, readBuffer, 0, readBuffer.length);
    }

    /**
     * This function writes bytes to the device and then reads bytes from the device.
     * Write data is taken from the 'writeBuffer' byte array from the given 'writeOffset' index to
     * the specified 'writeLength' (number of bytes).
     *
     * <p>Data read back from the device is then copied to the 'readBuffer' byte array starting
     * at the given 'readOffset' using the 'readLength' (number of bytes).
     * Each byte array must be at least the size of its defined 'length' + 'offset'.
     *
     * <p>A configurable delay can be inserted between the write and the read operation, allowing
     * the affected chip to complete any processing before the data is read back.
     *
     * @param writeBuffer    the bytes to write; if {@code null}, only a read operation is performed
     * @param writeOffset    the start index in {@code writeBuffer} of the first byte to write
     * @param writeLength    the number of bytes to write
     * @param readDelayNanos the delay, in nanoseconds, to wait between the write and the read
     * @param readBuffer     the buffer to fill with the bytes read back; if {@code null}, only a
     *                       write operation is performed
     * @param readOffset     the start index in {@code readBuffer} where read bytes are written
     * @param readLength     the number of bytes to read
     */
    void writeThenRead(byte[] writeBuffer, int writeOffset, int writeLength, int readDelayNanos, byte[] readBuffer, int readOffset, int readLength);
}
