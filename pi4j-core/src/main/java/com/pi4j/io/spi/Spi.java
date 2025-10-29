package com.pi4j.io.spi;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Spi.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.IODataReader;
import com.pi4j.io.IODataWriter;

import java.nio.ByteBuffer;

/**
 * <p>Spi interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Spi extends IO<Spi, SpiConfig, SpiProvider>, AutoCloseable, IODataWriter, IODataReader {
    /** Constant <code>DEFAULT_BUS</code> */
    SpiBus DEFAULT_BUS = SpiBus.BUS_0;
    /** Constant <code>DEFAULT_MODE</code> */
    SpiMode DEFAULT_MODE = SpiMode.MODE_0;
    /** Constant <code>DEFAULT_CHIP_SELECT</code> */
    SpiChipSelect DEFAULT_CHIP_SELECT = SpiChipSelect.CS_0;
    /** Constant <code>DEFAULT_BAUD=1000000</code> */
    int DEFAULT_BAUD = 1000000; // 1MHz (range is 500kHz - 32MHz)
    /** Constant <code>DEFAULT_WRITE_LSB_FIRST</code> */
    int DEFAULT_WRITE_LSB_FIRST = 0;
    /** Constant <code>DEFAULT_READ_LSB_FIRST</code> */
    int DEFAULT_READ_LSB_FIRST = 0;
    /**
     * <p>newConfigBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    static SpiConfigBuilder newConfigBuilder(Context context){
        return SpiConfigBuilder.newInstance(context);
    }

    /**
     * SPI Device Communication State is OPEN
     *
     * @return The SPI device communication state
     */
    boolean isOpen();


    /**
     * <p>open.</p>
     */
    void open();

    /**
     * <p>close.</p>
     */
    void close();

    // ------------------------------------------------------------------------------------
    // XFER :: BYTE
    // ------------------------------------------------------------------------------------

    /**
     * This function reads a byte from the serial port associated with the handle.
     * If no data is ready PI_SER_READ_NO_DATA is returned.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @return Returns the read byte (&gt;=0) if OK, otherwise PI_BAD_HANDLE, PI_SER_READ_NO_DATA, or PI_SER_READ_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#serReadByte"
     * @param value a byte.
     */
    default int transferByte(int handle, byte value) {
        byte[] temp = new byte[] { value } ;
        int result = transfer(temp);
        if(result <= 0) return result;

        // we must convert the raw byte to an unsigned int for the return value
        // otherwise, anything higher than 0x80 may result in a negative int value
        return Byte.toUnsignedInt(temp[0]);
    }

    // ------------------------------------------------------------------------------------
    // XFER :: BYTE ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'write' byte array
     * from the given 'writeOffset' index to the specified length ('numberOfBytes').  Data
     * read from the SPI device is then copied to the 'read' byte array at the given 'readOffset'
     * using the same length ('numberOfBytes').  Both the 'write' and 'read' byte arrays must
     * be at least the size of the defined 'numberOfBytes' + their corresponding offsets.
     *
     * @param write the array of bytes to write to the SPI device
     * @param writeOffset the starting offset position in the provided 'write' buffer to
     *                    start writing to the SPI device from.
     * @param read the array of bytes to store read data in from the SPI device
     * @param readOffset the starting offset position in the provided 'read' buffer to place
     *                   data bytes read from the SPI device.
     * @param numberOfBytes the number of bytes to transfer/exchange (read &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
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
     * @param write the array of bytes to write to the SPI device
     * @param read the array of bytes to store read data in from the SPI device
     * @param numberOfBytes the number of bytes to transfer/exchange (read &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
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
     * @param read the array of bytes to store read data in from the SPI device
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
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
     * @param length the number of bytes to transfer/exchange (read &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
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
     * @param length the number of bytes to transfer/exchange (read &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
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
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
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
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only read the number of
     *        bytes up to the buffers' available space.
     *
     * @param write the ByteBuffer to write to the SPI device
     * @param writeOffset the starting offset position in the provided 'write' buffer to
     *                    start writing to the SPI device from.
     * @param read the ByteBuffer to store read data in from the SPI device
     * @param readOffset the starting offset position in the provided 'read' buffer to place
     *                   data bytes read from the SPI device.
     * @param numberOfBytes the number of bytes to transfer/exchange (read &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
     */
    default int transfer(ByteBuffer write, int writeOffset, ByteBuffer read, int readOffset, int numberOfBytes) {
        // perform bounds checking on requested length versus total remaining size available
        if(numberOfBytes > (write.capacity()-writeOffset)){
            numberOfBytes = write.capacity()-writeOffset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[numberOfBytes];
        int actualLength =transfer(write.array(), 0 , temp, 0, numberOfBytes);

        // return any error codes ( < 0)
        if(actualLength < 0) return actualLength;

        // perform bounds checking on number of bytes read versus the length requested
        if(actualLength < numberOfBytes) numberOfBytes = actualLength;

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
     *        from the current position index up to the length
     *        requested or up to the buffer's remaining limit;
     *        whichever is is lower .  If the buffer's current
     *        position is already at the buffer's limit, then we
     *        will automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's limit     *
     * <p>
     * NOTE:  The data bytes read from the SPI device are copied/
     *        inserted into the 'read' byte buffer starting at the current
     *        position index up to the length requested or up to the
     *        buffer's remaining limit; whichever is is lower .  If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     *
     * @param write the ByteBuffer to write to the SPI device
     * @param read the ByteBuffer to store read data in from the SPI device
     * @param numberOfBytes the number of bytes to transfer/exchange (read &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
     */
    default int transfer(ByteBuffer write, ByteBuffer read, int numberOfBytes) {
        // if the 'write' buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(write.position() == write.limit()) write.flip();

        // if the 'read' buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if(read.position() == read.limit()) read.rewind();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(numberOfBytes > write.remaining()) numberOfBytes = write.remaining();

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
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only read the number of
     *        bytes up to the buffers' available space.
     *
     * @param buffer the byte buffer to write to the SPI device and to store read data
     *               back from the SPI device
     * @param offset the starting offset position in the provided buffer to
     *               start writing to the SPI device from and the position
     *               used as the starting offset position to place data bytes
     *               read back from the SPI device.
     * @param length the number of bytes to transfer/exchange (read &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
     */
    default int transfer(ByteBuffer buffer, int offset, int length) {
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[length];
        int actualLength =transfer(buffer.array(), 0 , temp, 0, length);

        // return any error codes ( < 0)
        if(actualLength < 0) return actualLength;

        // perform bounds checking on number of bytes read versus the length requested
        if(actualLength < length) length = actualLength;

        // copy the data from the temporary byte array into the return buffer at the given offset
        buffer.position(offset);
        buffer.put(temp, 0, length);

        // return actual number of bytes read
        return length;
    }

    // ------------------------------------------------------------------------------------
    //  writeRead
    // ------------------------------------------------------------------------------------
    /**
     * This function writes bytes to the SPI device and then reads bytes from the device
     * Write data is taken from the 'buffer' byte array, data
     * read back from the SPI device is then copied to the 'read' byte array
     *
     * @param buffer the array of bytes to write to the SPI device

     * @param read   Buffer to contain read data
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
     */
    default int writeRead(byte[] buffer,  byte[] read) {
        return writeRead(buffer, 0, buffer.length, (short) 0, read, 0, read.length, (short) 0);
    }

    /**
     * This function writes bytes to the SPI device and then reads bytes from the device
     * Write data is taken from the 'buffer' byte array, data
     * read back from the SPI device is then copied to the 'read' byte array
     *
     * @param buffer the array of bytes to write to the SPI device
     * @param writeLength
     *               Number bytes written from buffer to the SPI
     * @param read   Buffer to contain read data
     * @param readlength the number of bytes to read (read &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
     */
    default int writeRead(byte[] buffer, int writeLength,  byte[] read,  int readlength) {
        return writeRead(buffer, 0, writeLength, (short) 0, read, 0, readlength, (short) 0);
    }


    /**
     * This function writes bytes to the SPI device and then reads bytes from the device.
     *  Write data is taken from the 'buffer' byte array from the given 'writeOffset' index to
     *  the specified 'writeLength' (number of bytes).
     *  Data read back from the SPI device is then copied to the 'read' byte array starting
     * at the given 'readOffset' using the 'readLength' (number of bytes).  The 'buffer' and  'read' byte
     * array must be at least the size of their defined 'length' + 'offset'.
     *
     * In addition. both the write and read operation can provide a configurable delay
     * allowing the effected SPI chip to complete any processing. The write operation and the read
     * operation each have an individual delay value.
     *
     * @param write              the array of bytes to write to the SPI device and to store read data
     *                           back from the SPI device
     * @param writeOffset        the starting offset position in the provided buffer to
     *                           start writing to the SPI device from and the position
     *                           used as the starting offset position to place data bytes
     *                           read back from the SPI device.
     * @param writeNumberOfBytes Number of bytes written from write buffer
     * @param writeDelayUsecs    Delay after SPI write record processed by kernel before the
     *                           read SPI record is processed. Value in usecs.
     * @param read               Buffer to contain read data
     * @param readOffset         Offset within the read buffer to begin placing read data
     * @param readNumberOfBytes  the number of bytes to transfer/exchange (read &amp; read))
     * @param readDelayUsecs    Delay after SPI read record processed by kernel before the
     *                           read SPI record is processed. Value in usecs.
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
     */
    default int writeRead(byte[] write, int writeOffset, int writeNumberOfBytes,  short writeDelayUsecs, byte[] read, int readOffset, int readNumberOfBytes,  short readDelayUsecs) {
        throw new IllegalStateException("writeRead Not supported in this provider. \n See https://www.pi4j.com/documentation/providers/");
    }


}
