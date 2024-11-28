package com.pi4j.library.pigpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PiGpio_SPI.java
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
 * <p>PiGpio_SPI interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface PiGpio_SPI {

    /**
     * This function opens a SPI device channel at a specified baud rate and with specified flags.
     * Data will be transferred at baud bits per second.
     * The flags may be used to modify the default behaviour of 4-wire operation, mode 0, active low chip select.
     * <p>
     * The Pi has two SPI peripherals: main and auxiliary.
     * The main SPI has two chip selects (channels), the auxiliary has three.
     * The auxiliary SPI is available on all models but the A and B.
     * <p>
     * The GPIO pins used are given in the following table.
     * <p>
     *             MISO    MOSI   SCLK   CE0   CE1   CE2
     *             -------------------------------------
     *   Main SPI    9      10     11      8	 7	   -
     *   Aux SPI    19      20     21     18	17    16
     * <p>
     *
     *  spiChan  : 0-1 (0-2 for the auxiliary SPI)
     *  baud     : 32K-125M (values above 30M are unlikely to work)
     *  spiFlags : see below
     * <p>
     * spiFlags consists of the least significant 22 bits.
     * -----------------------------------------------------------------
     * 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
     *  b  b  b  b  b  b  R  T  n  n  n  n  W  A u2 u1 u0 p2 p1 p0  m  m
     * -----------------------------------------------------------------
     * <p>
     * [mm] defines the SPI mode.
     *      (Warning: modes 1 and 3 do not appear to work on the auxiliary SPI.)
     * <p>
     *      Mode POL  PHA
     *      -------------
     *       0    0    0
     *       1    0    1
     *       2    1    0
     *       3    1    1
     * <p>
     * [px] is 0 if CEx is active low (default) and 1 for active high.
     * [ux] is 0 if the CEx GPIO is reserved for SPI (default) and 1 otherwise.
     * [A] is 0 for the main SPI, 1 for the auxiliary SPI.
     * [W] is 0 if the device is not 3-wire, 1 if the device is 3-wire. Main SPI only.
     * [nnnn] defines the number of bytes (0-15) to write before switching the MOSI line to MISO to read data. This field is ignored if W is not set. Main SPI only.
     * [T] is 1 if the least significant bit is transmitted on MOSI first, the default (0) shifts the most significant bit out first. Auxiliary SPI only.
     * [R] is 1 if the least significant bit is received on MISO first, the default (0) receives the most significant bit first. Auxiliary SPI only.
     * [bbbbbb] defines the word size in bits (0-32). The default (0) sets 8 bits per word. Auxiliary SPI only.
     * <p>
     * The spiRead, spiWrite, and spiXfer functions transfer data packed into 1, 2, or 4 bytes according to the word size in bits.
     *  - For bits 1-8 there will be one byte per word.
     *  - For bits 9-16 there will be two bytes per word.
     *  - For bits 17-32 there will be four bytes per word.
     * <p>
     * Multi-byte transfers are made in least significant byte first order.
     * E.g. to transfer 32 11-bit words buf should contain 64 bytes and count should be 64.
     * E.g. to transfer the 14 bit value 0x1ABC send the bytes 0xBC followed by 0x1A.
     * The other bits in flags should be set to zero.
     *
     * @param channel the SPI device/channel to open [0-1 (0-2 for the auxiliary SPI)]
     * @param baud  baud rate in bits per second
     * @param flags  optional flags to define SPI modes and other SPI communication characteristic, see details above.
     * @return Returns a handle (&gt;=0) if OK, otherwise PI_BAD_SPI_CHANNEL, PI_BAD_SPI_SPEED, PI_BAD_FLAGS, PI_NO_AUX_SPI, or PI_SPI_OPEN_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiOpen">PIGPIO::spiOpen</a>
     */
    int spiOpen(int channel, int baud, int flags);

    /**
     * This function opens a SPI device channel at a specified baud rate and with default options using SPI mode 0.
     * Data will be transferred at baud bits per second.
     * <p>
     * The Pi has two SPI peripherals: main and auxiliary.
     * The main SPI has two chip selects (channels), the auxiliary has three.
     * The auxiliary SPI is available on all models but the A and B.
     * <p>
     * The GPIO pins used are given in the following table.
     * <p>
     *             MISO    MOSI   SCLK   CE0   CE1   CE2
     *             -------------------------------------
     *   Main SPI    9      10     11      8	 7	   -
     *   Aux SPI    19      20     21     18	17    16
     * <p>
     *
     *  spiChan  : 0-1 (0-2 for the auxiliary SPI)
     *  baud     : 32K-125M (values above 30M are unlikely to work)
     * <p>
     * The spiRead, spiWrite, and spiXfer functions transfer data packed into 1, 2, or 4 bytes according to the word size in bits.
     *  - For bits 1-8 there will be one byte per word.
     *  - For bits 9-16 there will be two bytes per word.
     *  - For bits 17-32 there will be four bytes per word.
     * <p>
     * Multi-byte transfers are made in least significant byte first order.
     * E.g. to transfer 32 11-bit words buf should contain 64 bytes and count should be 64.
     * E.g. to transfer the 14 bit value 0x1ABC send the bytes 0xBC followed by 0x1A.
     * The other bits in flags should be set to zero.
     *
     * @param channel the SPI device/channel to open [0-1 (0-2 for the auxiliary SPI)]
     * @param baud  baud rate in bits per second
     * @return Returns a handle (&gt;=0) if OK, otherwise PI_BAD_SPI_CHANNEL, PI_BAD_SPI_SPEED, PI_BAD_FLAGS, PI_NO_AUX_SPI, or PI_SPI_OPEN_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiOpen">PIGPIO::spiOpen</a>
     */
    default int spiOpen(int channel, int baud) {
        return spiOpen(channel, baud, 0);
    }

    /**
     * This functions closes the SPI device identified by the handle.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiClose">PIGPIO::spiClose</a>
     */
    int spiClose(int handle);


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
    default int spiWriteByte(int handle, byte value){
        byte[] temp = new byte[] { value } ;
        return spiWrite(handle, temp);
    }

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
    default int spiReadByte(int handle){
        byte[] temp = new byte[1];
        int result = spiRead(handle, temp);
        if(result <= 0) return result;

        // we must convert the raw byte to an unsigned int for the return value
        // otherwise, anything higher than 0x80 may result in a negative int value
        return Byte.toUnsignedInt(temp[0]);
    }

    // ------------------------------------------------------------------------------------
    // XFER :: BYTE
    // ------------------------------------------------------------------------------------

    /**
     * This function reads a byte from the serial port associated with the handle.
     * If no data is ready PI_SER_READ_NO_DATA is returned.
     *
     * @param handle the open serial device handle; (&gt;=0, as returned by a call to serOpen)
     * @return Returns the read byte (&gt;=0) if OK, otherwise PI_BAD_HANDLE, PI_SER_READ_NO_DATA, or PI_SER_READ_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serReadByte">PIGPIO::serReadByte</a>
     * @param value a byte.
     */
    default int spiXferByte(int handle, byte value){
        byte[] temp = new byte[] { value } ;
        int result = spiXfer(handle, temp);
        if(result <= 0) return result;

        // we must convert the raw byte to an unsigned int for the return value
        // otherwise, anything higher than 0x80 may result in a negative int value
        return Byte.toUnsignedInt(temp[0]);
    }

    // ------------------------------------------------------------------------------------
    // WRITE :: BYTE ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * This function writes multiple bytes from the byte array ('data') to the SPI
     * device associated with the handle from the given offset index to the specified length.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param data the array of bytes to write
     * @param offset the starting offset position in the provided buffer to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    int spiWrite(int handle, byte[] data, int offset, int length);

    /**
     * This function writes multiple bytes from the byte array ('data') to the SPI
     * device associated with the handle from the first byte (offset=0) to the specified length.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param data the array of bytes to write
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, byte[] data, int length){
        return spiWrite(handle, data, 0, length);
    }

    /**
     * This function writes multiple bytes from the byte array ('data') to the SPI
     * device associated with the handle.  The entire contents of the byte array are written.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param data the array of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, byte[] data){
        return spiWrite(handle, data, 0, data.length);
    }

    // ------------------------------------------------------------------------------------
    // WRITE :: BYTE BUFFER
    // ------------------------------------------------------------------------------------

    /**
     * This function writes multiple bytes from the byte buffer to the SPI device
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
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer the byte buffer of data to write
     * @param offset the starting offset position in the provided buffer to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, ByteBuffer buffer, int offset, int length){
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }
        return spiWrite(handle, buffer.array(), offset, length);
    }

    /**
     * This function writes multiple bytes from the byte buffer to the SPI device
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
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer the byte buffer of data to write
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, ByteBuffer buffer, int length){
        // if the buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(buffer.position() == buffer.limit()) buffer.flip();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(length > buffer.remaining()) length = buffer.remaining();

        // write contents from the buffer starting at the current position up to the specified length
        return spiWrite(handle, buffer, buffer.position(), length);
    }

    /**
     * This function writes multiple bytes from the byte buffer to the SPI device
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
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer the byte buffer of data to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, ByteBuffer buffer){
        // if the buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(buffer.position() == buffer.limit()) buffer.flip();

        // write contents from the buffer starting at the current position up to the remaining buffer size
        return spiWrite(handle, buffer, buffer.position(), buffer.remaining());
    }

    // ------------------------------------------------------------------------------------
    // WRITE :: CHARACTER SEQUENCE
    // ------------------------------------------------------------------------------------

    /**
     * This function writes encoded bytes from the provided character sequence to the SPI
     * device associated with the handle from the given offset index to the specified length.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param charset the character set/type used to encode the character sequence/string to bytes
     * @param data the character/string data to write
     * @param offset the starting offset position in the provided data to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, Charset charset, CharSequence data, int offset, int length){
        return spiWrite(handle, data.toString().getBytes(charset), offset, length);
    }

    /**
     * This function writes encoded bytes from the provided character sequence to the SPI
     * device associated with the handle from the first character (offset=0) to the specified length.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param charset the character set/type used to encode the character sequence/string to bytes
     * @param data the character/string data to write
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, Charset charset, CharSequence data, int length){
        return spiWrite(handle, charset, data, 0, length);
    }

    /**
     * This function writes encoded bytes from the provided character sequence to the SPI
     * device associated with the handle.  The entire contents of the character sequence are written.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param charset the character set/type used to encode the character sequence/string to bytes
     * @param data the character/string data to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, CharSequence data, Charset charset){
        return spiWrite(handle, charset, data, 0, data.length());
    }

    /**
     * This function writes ASCII encoded bytes from the provided character sequence to the SPI
     * device associated with the handle from the given offset index to the specified length.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param data the ASCII character/string data to write
     * @param offset the starting offset position in the provided data to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, CharSequence data, int offset, int length){
        return spiWrite(handle, StandardCharsets.US_ASCII, data, offset, length);
    }

    /**
     * This function writes ASCII encoded bytes from the provided character sequence to the SPI
     * device associated with the handle from the first byte (offset=0) to the specified length.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param data the ASCII character/string data to write
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, CharSequence data, int length){
        return spiWrite(handle, data, 0, length);
    }

    /**
     * This function writes ASCII encoded bytes from the provided character sequence to the SPI
     * device associated with the handle.  The entire contents of the character sequence are written.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param data the ASCII character/string data to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiWrite(int handle, CharSequence data){
        return spiWrite(handle, data, data.length());
    }

    // ------------------------------------------------------------------------------------
    // READ :: BYTE ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * This function reads a number of bytes specified by the 'length' parameter from the
     * SPI device associated with the handle and copies them to the 'buffer' byte array parameter.
     * The 'offset' parameter determines where to start copying/inserting read data in the byte array.
     * If no data is ready, zero is returned; otherwise, the number of bytes read is returned.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer a byte array to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiRead">PIGPIO::spiRead</a>
     */
    int spiRead(int handle, byte[] buffer, int offset, int length);

    /**
     * This function reads a number of bytes specified by the 'length' parameter from the
     * SPI device associated with the handle and copies them to the 'buffer' byte array parameter
     * starting current buffer position.  If no data is ready, zero is returned; otherwise,
     * the number of bytes read is returned.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer a byte array to receive the read data
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiRead">PIGPIO::spiRead</a>
     */
    default int spiRead(int handle, byte[] buffer, int length){
        return spiRead(handle, buffer, 0, length);
    }

    /**
     * This function reads a number of bytes specified by the size pf the provided buffer array from the
     * SPI device associated with the handle and copies them to the 'buffer' byte array parameter
     * starting at the first index position.  If no data is ready, zero is returned; otherwise,
     * the number of bytes read is returned.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer a byte array to receive the read data
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiRead">PIGPIO::spiRead</a>
     */
    default int spiRead(int handle, byte[] buffer){
        return spiRead(handle, buffer, 0, buffer.length);
    }

    // ------------------------------------------------------------------------------------
    // READ :: BYTE BUFFER
    // ------------------------------------------------------------------------------------

    /**
     * Read data from the SPI device into the provided byte buffer at the given
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
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer a byte buffer (with pre-allocated capacity) to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiRead">PIGPIO::spiRead</a>
     */
    default int spiRead(int handle, ByteBuffer buffer, int offset, int length){
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[length];
        int actualLength = spiRead(handle, temp, 0 ,length);

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
     * Read data from the SPI device into the provided byte buffer starting
     * with the buffer's current position up to the provided length.
     * <p>
     * NOTE:  The data bytes read from the SPI device are copied/
     *        inserted into the byte buffer starting at the current
     *        position index up to the length requested or up to the
     *        buffer's remaining limit; whichever is is lower .  If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer a byte buffer (with pre-allocated capacity) to receive the read data
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiRead">PIGPIO::spiRead</a>
     */
    default int spiRead(int handle, ByteBuffer buffer, int length){
        // if the buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if(buffer.position() == buffer.limit()) buffer.rewind();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(length > buffer.remaining()) length = buffer.remaining();

        // read the buffer starting at the current position up the the specified length
        return spiRead(handle, buffer, buffer.position(), length);
    }

    /**
     * Read data from the SPI device into the provided byte buffer starting with
     * the buffer's current position up to available space remaining in the buffer.
     * <p>
     * NOTE:  The data bytes read from the SPI device are copied/
     *        inserted into the byte buffer starting at the current
     *        position index up to the buffer's remaining limit. If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer a byte buffer (with pre-allocated capacity) to receive the read data
     * @return Returns the number of bytes read if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiRead">PIGPIO::spiRead</a>
     */
    default int spiRead(int handle, ByteBuffer buffer){
        // if the buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if(buffer.position() == buffer.limit()) buffer.rewind();

        // read the buffer starting at the current position and fill up to the remaining size
        return spiRead(handle, buffer, buffer.position(), buffer.remaining());
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
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param write the array of bytes to write to the SPI device
     * @param writeOffset the starting offset position in the provided 'write' buffer to
     *                    start writing to the SPI device from.
     * @param read the array of bytes to store read data in from the SPI device
     * @param readOffset the starting offset position in the provided 'read' buffer to place
     *                   data bytes read from the SPI device.
     * @param numberOfBytes the number of bytes to transfer/exchange (write &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    int spiXfer(int handle, byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes);

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'write' byte array
     * from the zero index (first byte) to the specified length ('numberOfBytes').  Data
     * read from the SPI device is then copied to the 'read' byte array starting at the zero
     * index (first byte) using the same length ('numberOfBytes').  Both the 'write' and 'read'
     * byte arrays must be at least the size of the defined 'numberOfBytes' + their corresponding
     * offsets.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param write the array of bytes to write to the SPI device
     * @param read the array of bytes to store read data in from the SPI device
     * @param numberOfBytes the number of bytes to transfer/exchange (write &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiXfer(int handle, byte[] write, byte[] read, int numberOfBytes){
        return spiXfer(handle, write, 0, read, 0, numberOfBytes);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the entire contents of
     * the  'write' byte array (from the zero index (first byte) to the array size).  Data
     * read from the SPI device is then copied to the 'read' byte array starting at the zero
     * index (first byte) using the same length of the number of bytes in the 'write' array.
     * The 'read' byte array must be at least the size of the defined 'write' byte array.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param write the array of bytes to write to the SPI device
     * @param read the array of bytes to store read data in from the SPI device
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiXfer(int handle, byte[] write, byte[] read){
        return spiXfer(handle, write, 0, read, 0, write.length);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'buffer' byte array
     * from the given 'offset' index to the specified 'length' (number of bytes).  Data
     * read back from the SPI device is then copied to the same 'buffer' byte array starting
     * at the given 'offset' using the same 'length' (number of bytes).  The 'buffer' byte
     * array must be at least the size of the defined 'length' + 'offset'.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer the array of bytes to write to the SPI device and to store read data
     *               back from the SPI device
     * @param offset the starting offset position in the provided buffer to
     *               start writing to the SPI device from and the position
     *               used as the starting offset position to place data bytes
     *               read back from the SPI device.
     * @param length the number of bytes to transfer/exchange (write &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiXfer(int handle, byte[] buffer, int offset, int length){
        return spiXfer(handle, buffer, offset, buffer, offset, length);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'buffer' byte array
     * from the zero index (first byte) to the specified length (number of bytes).  Data
     * read from the SPI device is then copied back to the same 'buffer' byte array starting
     * at the zero index (first byte) using the same length.  The 'buffer' byte array must
     * be at least the size of the defined 'length'.
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer the array of bytes to write to the SPI device and to store read data
     *               back from the SPI device
     * @param length the number of bytes to transfer/exchange (write &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiXfer(int handle, byte[] buffer, int length){
        return spiXfer(handle, buffer, 0, length);
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the entire 'buffer' byte array
     * contents.  Data read back from the SPI device is then copied back to the same 'buffer'
     * byte array starting at the zero index (first byte).
     *
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer the array of bytes to write to the SPI device and to store read data
     *               back from the SPI device
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiXfer(int handle, byte[] buffer){
        return spiXfer(handle, buffer, 0, buffer.length);
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
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param write the ByteBuffer to write to the SPI device
     * @param writeOffset the starting offset position in the provided 'write' buffer to
     *                    start writing to the SPI device from.
     * @param read the ByteBuffer to store read data in from the SPI device
     * @param readOffset the starting offset position in the provided 'read' buffer to place
     *                   data bytes read from the SPI device.
     * @param numberOfBytes the number of bytes to transfer/exchange (write &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiXfer(int handle, ByteBuffer write, int writeOffset, ByteBuffer read, int readOffset, int numberOfBytes){
        // perform bounds checking on requested length versus total remaining size available
        if(numberOfBytes > (write.capacity()-writeOffset)){
            numberOfBytes = write.capacity()-writeOffset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[numberOfBytes];
        int actualLength =spiXfer(handle, write.array(), 0 , temp, 0, numberOfBytes);

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
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param write the ByteBuffer to write to the SPI device
     * @param read the ByteBuffer to store read data in from the SPI device
     * @param numberOfBytes the number of bytes to transfer/exchange (write &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiXfer(int handle, ByteBuffer write, ByteBuffer read, int numberOfBytes){
        // if the 'write' buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(write.position() == write.limit()) write.flip();

        // if the 'read' buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if(read.position() == read.limit()) read.rewind();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(numberOfBytes > write.remaining()) numberOfBytes = write.remaining();

        // write contents from the buffer starting at the current position up to the specified length
        return spiXfer(handle, write, write.position(), read, read.position(), numberOfBytes);
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
     * @param handle the open SPI device handle; (&gt;=0, as returned by a call to spiOpen)
     * @param buffer the byte buffer to write to the SPI device and to store read data
     *               back from the SPI device
     * @param offset the starting offset position in the provided buffer to
     *               start writing to the SPI device from and the position
     *               used as the starting offset position to place data bytes
     *               read back from the SPI device.
     * @param length the number of bytes to transfer/exchange (write &amp; read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    default int spiXfer(int handle, ByteBuffer buffer, int offset, int length){
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[length];
        int actualLength =spiXfer(handle, buffer.array(), 0 , temp, 0, length);

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
}
