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
 * <p>The write and read calls here delegate to writeThenRead with the write or
 * read buffer null in order to require only one native implementation for
 * all cases.
 */
public interface SerialCircuitIO extends AutoCloseable {

    /** Reads the full data array. Returns the size of the buffer for historic reasons. */
    default int read(byte[] data) {
        return read(data, 0, data.length);
    }

    /**
     * Read 'length' bytes into 'buffer' at offset 'offset'.
     * Returns 'length' for historical reasons.
     */
    default int read(byte[] buffer, int offset, int length) {
        writeThenRead(null, 0, 0, 0, buffer, offset, length);
        return length;
    }

    /** Writes the full byte array. Returns the buffer size for historical reasons. */
    default int write(byte[] buffer) {
        return write(buffer, 0, buffer.length);
    }

    /**
     * Writes 'length' bytes from the 'buffer' at offset 'offset'.
     * Returns 'length' for historical reasons.
     */
    default int write(byte[] buffer, int offset, int length) {
        writeThenRead(buffer, offset, length, 0, null, 0, 0);
        return length;
    }

    default void writeThenRead(byte[] writeBuffer, byte[] readBuffer) {
        writeThenRead(writeBuffer, 0, writeBuffer.length, 0, readBuffer, 0, readBuffer.length);
    }

    /**
     *
     * @param writeBuffer The write buffer. If null, only a read operation is performed.
     * @param writeOffset The start offset in the write buffer
     * @param writeLength The number of bytes to write.
     * @param writeDelayUsecs Delay between write and read in usecs
     * @param readBuffer The read buffer. If null, only a write operation is performed
     * @param readOffset The start offset in the read buffer.
     * @param readLength The number of bytes to read.
     */
    void writeThenRead(byte[] writeBuffer, int writeOffset, int writeLength, int writeDelayUsecs, byte[] readBuffer, int readOffset, int readLength);
}
