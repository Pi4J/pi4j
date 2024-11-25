package com.pi4j.library.pigpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PiGpio_Serial.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>PiGpio_Serial interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface PiGpio_Serial {

    /**
     * This function opens a serial device at a specified baud rate and with specified flags.
     * The device name must start with "/dev/tty" or "/dev/serial".
     *
     * @param device the serial device to open (Example: "/dev/ttyAMA0")
     * @param baud  the baud rate in bits per second, see below
     *              The baud rate must be one of 50, 75, 110, 134, 150, 200, 300, 600, 1200,
     *              1800, 2400, 4800, 9600, 19200, 38400, 57600, 115200, or 230400.
     * @param flags  No flags are currently defined. This parameter should be set to zero.
     * @return Returns a handle (&gt;=0) if OK, otherwise PI_NO_HANDLE, or PI_SER_OPEN_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serOpen">PIGPIO::serOpen</a>
     */
    int serOpen(CharSequence device, int baud, int flags);

    /**
     * This function opens a serial device at a specified baud rate and with specified flags.
     * The device name must start with "/dev/tty" or "/dev/serial".
     *
     * @param device the serial device to open (Example: "/dev/ttyAMA0")
     * @param baud  the baud rate in bits per second, see below
     *              The baud rate must be one of 50, 75, 110, 134, 150, 200, 300, 600, 1200,
     *              1800, 2400, 4800, 9600, 19200, 38400, 57600, 115200, or 230400.
     * @return Returns a handle (&gt;=0) if OK, otherwise PI_NO_HANDLE, or PI_SER_OPEN_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serOpen">PIGPIO::serOpen</a>
     */
    default int serOpen(CharSequence device, int baud) {
        return serOpen(device, baud, 0);
    }

    /**
     * This function closes the serial device associated with the handle.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serClose">PIGPIO::serClose</a>
     */
    int serClose(int handle);

    // ------------------------------------------------------------------------------------
    // WRITE :: BYTE
    // ------------------------------------------------------------------------------------

    /**
     * This function writes a single byte "value" to the serial port associated with the handle.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param value byte value to write to serial port
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWriteByte">PIGPIO::serWriteByte</a>
     */
    int serWriteByte(int handle, byte value);

    // ------------------------------------------------------------------------------------
    // READ :: BYTE
    // ------------------------------------------------------------------------------------

    /**
     * This function reads a byte from the serial port associated with the handle.
     * If no data is ready PI_SER_READ_NO_DATA is returned.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @return Returns the read byte (&gt;=0) if OK, otherwise PI_BAD_HANDLE, PI_SER_READ_NO_DATA, or PI_SER_READ_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serReadByte">PIGPIO::serReadByte</a>
     */
    int serReadByte(int handle);

    // ------------------------------------------------------------------------------------
    // WRITE :: BYTE ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * This function writes multiple bytes from the byte array ('data') to the serial
     * device associated with the handle from the given offset index to the specified length.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param data the array of bytes to write
     * @param offset the starting offset position in the provided buffer to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    int serWrite(int handle, byte[] data, int offset, int length);

    /**
     * This function writes multiple bytes from the byte array ('data') to the serial
     * device associated with the handle from the first byte (offset=0) to the specified length.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param data the array of bytes to write
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, byte[] data, int length){
        return serWrite(handle, data, 0, length);
    }

    /**
     * This function writes multiple bytes from the byte array ('data') to the serial
     * device associated with the handle.  The entire contents of the byte array are written.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param data the array of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, byte[] data){
        return serWrite(handle, data, 0, data.length);
    }

    // ------------------------------------------------------------------------------------
    // WRITE :: BYTE BUFFER
    // ------------------------------------------------------------------------------------

    /**
     * This function writes multiple bytes from the byte buffer to the serial device
     * associated with the handle from the given offset index to the specified length.
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only read the number of
     *        bytes up to the buffers' available space.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer the byte buffer of data to write
     * @param offset the starting offset position in the provided buffer to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, ByteBuffer buffer, int offset, int length){
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }
        return serWrite(handle, buffer.array(), offset, length);
    }

    /**
     * This function writes multiple bytes from the byte buffer to the serial device
     * associated with the handle from the current buffer position to the specified length.
     * <p>
     * NOTE:  The contents from the byte buffer is read
     *        from the current position index up to the length
     *        requested or up to the buffer's remaining limit;
     *        whichever is is lower .  If the buffer's current
     *        position is already at the buffer's limit, then we
     *        will automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's limit.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer the byte buffer of data to write
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, ByteBuffer buffer, int length){
        // if the buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(buffer.position() == buffer.limit()) buffer.flip();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(length > buffer.remaining()) length = buffer.remaining();

        // write contents from the buffer starting at the current position up to the specified length
        return serWrite(handle, buffer, buffer.position(), length);
    }

    /**
     * This function writes multiple bytes from the byte buffer to the serial device
     * associated with the handle.  The contents of the byte buffer are written from
     * the buffer's current position to the buffer's limit.
     * <p>
     * NOTE:  The contents from the byte buffer is read
     *        from the current position index up to the buffer's
     *        remaining limit.  If the buffer's current position
     *        is already at the buffer's limit, then we will
     *        automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's
     *        limit.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer the byte buffer of data to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, ByteBuffer buffer){
        // if the buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(buffer.position() == buffer.limit()) buffer.flip();

        // write contents from the buffer starting at the current position up to the remaining buffer size
        return serWrite(handle, buffer, buffer.position(), buffer.remaining());
    }

    // ------------------------------------------------------------------------------------
    // WRITE :: CHARACTER SEQUENCE
    // ------------------------------------------------------------------------------------

    /**
     * This function writes encoded bytes from the provided character sequence to the serial
     * device associated with the handle from the given offset index to the specified length.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param charset the character set/type used to encode the character sequence/string to bytes
     * @param data the character/string data to write
     * @param offset the starting offset position in the provided data to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, Charset charset, CharSequence data, int offset, int length){
        return serWrite(handle, data.toString().getBytes(charset), offset, length);
    }

    /**
     * This function writes encoded bytes from the provided character sequence to the serial
     * device associated with the handle from the first character (offset=0) to the specified length.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param charset the character set/type used to encode the character sequence/string to bytes
     * @param data the character/string data to write
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, Charset charset, CharSequence data, int length){
        return serWrite(handle, charset, data, 0, length);
    }

    /**
     * This function writes encoded bytes from the provided character sequence to the serial
     * device associated with the handle.  The entire contents of the character sequence are written.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param charset the character set/type used to encode the character sequence/string to bytes
     * @param data the character/string data to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, CharSequence data, Charset charset){
        return serWrite(handle, charset, data, 0, data.length());
    }

    /**
     * This function writes ASCII encoded bytes from the provided character sequence to the serial
     * device associated with the handle from the given offset index to the specified length.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param data the ASCII character/string data to write
     * @param offset the starting offset position in the provided data to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, CharSequence data, int offset, int length){
        return serWrite(handle, StandardCharsets.US_ASCII, data, offset, length);
    }

    /**
     * This function writes ASCII encoded bytes from the provided character sequence to the serial
     * device associated with the handle from the first byte (offset=0) to the specified length.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param data the ASCII character/string data to write
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, CharSequence data, int length){
        return serWrite(handle, data, 0, length);
    }

    /**
     * This function writes ASCII encoded bytes from the provided character sequence to the serial
     * device associated with the handle.  The entire contents of the character sequence are written.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param data the ASCII character/string data to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    default int serWrite(int handle, CharSequence data){
        return serWrite(handle, data, data.length());
    }

    // ------------------------------------------------------------------------------------
    // READ :: BYTE ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * This function reads a number of bytes specified by the 'length' parameter from the
     * serial device associated with the handle and copies them to the 'buffer' byte array parameter.
     * The 'offset' parameter determines where to start copying/inserting read data in the byte array.
     * If no data is ready, zero is returned; otherwise, the number of bytes read is returned.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer a byte array to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_READ_NO_DATA.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serRead">PIGPIO::serRead</a>
     */
    int serRead(int handle, byte[] buffer, int offset, int length);

    /**
     * This function reads a number of bytes specified by the 'length' parameter from the
     * serial device associated with the handle and copies them to the 'buffer' byte array parameter
     * starting at the first index position.  If no data is ready, zero is returned; otherwise,
     * the number of bytes read is returned.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer a byte array to receive the read data
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_READ_NO_DATA.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serRead">PIGPIO::serRead</a>
     */
    default int serRead(int handle, byte[] buffer, int length){
        return serRead(handle, buffer, 0, length);
    }

    /**
     * This function reads a number of bytes specified by the size pf the provided buffer array from the
     * serial device associated with the handle and copies them to the 'buffer' byte array parameter
     * starting at the first index position.  If no data is ready, zero is returned; otherwise,
     * the number of bytes read is returned.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer a byte array to receive the read data
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_READ_NO_DATA.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serRead">PIGPIO::serRead</a>
     */
    default int serRead(int handle, byte[] buffer){
        return serRead(handle, buffer, 0, buffer.length);
    }

    // ------------------------------------------------------------------------------------
    // READ :: BYTE BUFFER
    // ------------------------------------------------------------------------------------

    /**
     * Read data from the serial device into the provided byte buffer at the given
     * offset and up to the specified data length (number of bytes).
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only write the number of
     *        bytes up to the buffers' available space.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer a byte buffer (with pre-allocated capacity) to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_READ_NO_DATA.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serRead">PIGPIO::serRead</a>
     */
    default int serRead(int handle, ByteBuffer buffer, int offset, int length){
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[length];
        int actualLength = serRead(handle, temp, 0 ,length);

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

    /**
     * Read data from the serial device into the provided byte buffer starting
     * with the current buffer position to the provided length.
     * <p>
     * NOTE:  The data bytes read from the serial device are copied/
     *        inserted into the byte buffer starting at the current
     *        position index up to the length requested or up to the
     *        buffer's remaining limit; whichever is is lower .  If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer a byte buffer (with pre-allocated capacity) to receive the read data
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_READ_NO_DATA.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serRead">PIGPIO::serRead</a>
     */
    default int serRead(int handle, ByteBuffer buffer, int length){
        // if the buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if(buffer.position() == buffer.limit()) buffer.rewind();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(length > buffer.remaining()) length = buffer.remaining();

        // read the buffer starting at the current position up the the specified length
        return serRead(handle, buffer, buffer.position(), length);
    }

    /**
     * Read data from the serial device into the provided byte buffer starting with
     * the buffer's current position up to available space remaining in the buffer.
     * <p>
     * NOTE:  The data bytes read from the serial device are copied/
     *        inserted into the byte buffer starting at the current
     *        position index up to the buffer's remaining limit. If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @param buffer a byte buffer (with pre-allocated capacity) to receive the read data
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_READ_NO_DATA.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serRead">PIGPIO::serRead</a>
     */
    default int serRead(int handle, ByteBuffer buffer){
        // if the buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if(buffer.position() == buffer.limit()) buffer.rewind();

        // read the buffer starting at the current position and fill up to the remaining size
        return serRead(handle, buffer, buffer.position(), buffer.remaining());
    }

    // ------------------------------------------------------------------------------------
    // READ :: AVAILABLE
    // ------------------------------------------------------------------------------------

    /**
     * This function returns the number of bytes available to be read from the device associated with the handle.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @return Returns the number of bytes of data available (&gt;=0) if OK, otherwise PI_BAD_HANDLE.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serDataAvailable">PIGPIO::serDataAvailable</a>
     */
    int serDataAvailable(int handle);


    // ------------------------------------------------------------------------------------
    // READ :: DRAIN RECEIVE BUFFER
    // ------------------------------------------------------------------------------------

    /**
     * This function will drain the current serial receive buffer of any lingering bytes.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @return Returns the number of bytes of data drained (&gt;=0) if OK, otherwise PI_BAD_HANDLE.
     */
    int serDrain(int handle);
}
