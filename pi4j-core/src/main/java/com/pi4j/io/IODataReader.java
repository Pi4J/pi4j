package com.pi4j.io;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IODataReader.java
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

import com.pi4j.io.exception.IOReadException;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Data Writer Interface for Pi4J Data Communications
 *
 * @author Robert Savage
 * <p>
 * Based on previous contributions from:
 *        Daniel Sendula,
 *        <a href="http://raspelikan.blogspot.co.at">RasPelikan</a>
 * @version $Id: $Id
 */
public interface IODataReader extends Readable {

    // ------------------------------------------------------------------------------------
    // SINGLE BYTE
    // ------------------------------------------------------------------------------------

    /**
     * Read a single raw byte (8-bit) value from the I/O device.
     *
     * @return If successful, a zero or positive integer value representing the byte value (0-255)
     *         is returned.  If a read error was encountered, a negative error code may be returned.
     */
    int read();


    // ------------------------------------------------------------------------------------
    // BYTE ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * Read data from the I/O device into the provided byte array at the given
     * offset and up to the specified data length (number of bytes).
     *
     * @param buffer the byte array/buffer the read data will be copied/inserted into
     * @param offset the offset index in the data buffer to start copying read data
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    int read(byte[] buffer, int offset, int length);

    /**
     * Read data from the I/O device into the provided byte array starting at the zero
     * index (first byte) and up to the specified data length (number of bytes).
     *
     * @param buffer the byte array/buffer the read data will be copied/inserted into
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(byte[] buffer, int length) {
        return read(buffer, 0, length);
    }

    /**
     * Read data from the I/O device into the provided byte array starting at the zero
     * index (first byte) and up to the available space in the buffer/array.
     *
     * @param buffer the byte array/buffer the read data will be copied/inserted into
     * @return If successful, return the number of bytes read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(byte[] buffer) {
        return read(buffer, buffer.length);
    }


    // ------------------------------------------------------------------------------------
    // BYTE BUFFER
    // ------------------------------------------------------------------------------------

    /**
     * Read data from the I/O device into the provided byte buffer at the given
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
     * @param buffer the byte buffer the read data will be copied/inserted into
     * @param offset the offset index in the data buffer to start copying read data
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(ByteBuffer buffer, int offset, int length) {
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }

        // create a temporary byte array to read in the length of data bytes
        byte[] temp = new byte[length];
        int actualLength = read(temp, 0 ,length);

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
     * Read data from the I/O device into the provided byte buffer starting
     * with the first byte in the array up to the provided length.
     * <p>
     * NOTE:  The data bytes read from the I/O device are copied/
     *        inserted into the byte buffer starting at the current
     *        position index up to the length requested or up to the
     *        buffer's remaining limit; whichever is is lower .  If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     *
     * @param buffer the byte buffer the read data will be copied/inserted into
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(ByteBuffer buffer, int length) {
        // if the buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if(buffer.position() == buffer.limit()) buffer.rewind();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(length > buffer.remaining()) length = buffer.remaining();

        // read the buffer starting at the current position up the the specified length
        return read(buffer, buffer.position(), length);
    }

    /**
     * Read data from the I/O device into the provided byte buffer starting with
     * the first byte in the array up to available space remaining in the buffer.
     * <p>
     * NOTE:  The data bytes read from the I/O device are copied/
     *        inserted into the byte buffer starting at the current
     *        position index up to the buffer's remaining limit. If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     *
     * @param buffer byte buffer of data where data read from the I/O device
     *               will be copied (from current position to limit)
     * @return If successful, return the number of bytes read from the I2C device register;
     *         else on a read error, return a negative error code.
     */
    default int read(ByteBuffer buffer) {
        // if the buffer position is already at the buffer limit, then rewind the buffer for
        // writing new data into the buffer read from the I/O device
        if(buffer.position() == buffer.limit()) buffer.rewind();

        // read the buffer starting at the current position and fill up to the remaining size
        return read(buffer, buffer.position(), buffer.remaining());
    }


    // ------------------------------------------------------------------------------------
    // CHAR ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * Read character data from the I/O device into the provided character array at the given
     * offset and up to the specified data length (number of characters).  Specify the character
     * set to be used to decode the bytes into chars.
     *
     * @param charset character set to use for byte decoding
     * @param buffer the character array the read data will be copied/inserted into
     * @param offset the offset index in the character array to start copying read data
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(Charset charset, char[] buffer, int offset, int length) {
        // determine the maximum number of bytes that may be needed for this character set
        // and create a byte array to temporarily read the raw data from the I/O device
        int maxBytes = (int)charset.newDecoder().maxCharsPerByte() * length;
        byte[] rx = new byte[maxBytes];

        // read data from I/O device into temporary byte buffer
        int actualLength = read(rx, maxBytes);

        // return any error codes ( < 0)
        if(actualLength < 0) return actualLength;

        // decode byte array into char buffer
        CharBuffer cb = charset.decode(ByteBuffer.wrap(rx));

        // perform bounds checking on number of bytes read versus the length requested
        if(actualLength < length) length = actualLength;

        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.length-offset)){
            length = buffer.length-offset;
        }

        // stuff the decoded characters into the provided char array
        cb.get(buffer, offset, length);

        // return number of bytes read
        return actualLength;
    }

    /**
     * Read data from the I/O device into the provided character array starting
     * with the first byte in the array up to the provided length. Specify the
     * character set to be used to decode the bytes into chars.
     *
     * @param charset character set to use for byte decoding
     * @param buffer the character array the read data will be copied/inserted into
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(Charset charset, char[] buffer, int length) {
        return read(buffer, 0, length);
    }

    /**
     * Read data from the I/O device into the provided character array starting at the zero
     * index (first byte) and up to the available space in the buffer/array.  Specify the
     * character set to be used to decode the bytes into chars.
     *
     * @param charset character set to use for byte decoding
     * @param buffer the character array the read data will be copied/inserted into
     * @return If successful, return the number of bytes read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(Charset charset, char[] buffer) {
        return read(buffer, buffer.length);
    }

    /**
     * Read ASCII character data from the I/O device into the provided character array at the given
     * offset and up to the specified data length (number of characters).
     * ASCII is the internal character set used to decode the bytes into chars.
     *
     * @param buffer the character array the read data will be copied/inserted into
     * @param offset the offset index in the character array to start copying read data
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(char[] buffer, int offset, int length) {
        return read(StandardCharsets.US_ASCII, buffer, offset, length);
    }

    /**
     * Read ASCII data from the I/O device into the provided character array starting
     * with the first byte in the array up to the provided length.
     * ASCII is the internal character set used to decode the bytes into chars.
     *
     * @param buffer the character array the read data will be copied/inserted into
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(char[] buffer, int length) {
        return read(StandardCharsets.US_ASCII, buffer, length);
    }

    /**
     * Read ASCII data from the I/O device into the provided character array starting at the zero
     * index (first byte) and up to the available space in the buffer/array.
     * ASCII is the internal character set used to decode the bytes into chars.
     *
     * @param buffer the character array the read data will be copied/inserted into
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(char[] buffer) {
        return read(StandardCharsets.US_ASCII, buffer, buffer.length);
    }


    // ------------------------------------------------------------------------------------
    // CHAR BUFFER
    // ------------------------------------------------------------------------------------

    /**
     * Read character data from the I/O device into the provided character buffer at the given
     * offset and up to the specified data length (number of characters).  Specify the character
     * set to be used to decode the bytes into chars.
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only write the number of
     *        characters up to the buffers' available space.
     *
     * @param charset character set to use for byte decoding
     * @param buffer the character array the read data will be copied/inserted into
     * @param offset the offset index in the character buffer to start copying read data
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(Charset charset, CharBuffer buffer, int offset, int length) {
        // validate length argument
        if(length <=0) throw new IllegalArgumentException("Invalid read request; length must be greater than zero.");

        // determine the maximum number of bytes that may be needed for this character set
        // and create a byte array to temporarily read the raw data from the I/O device
        int maxBytes = (int)charset.newDecoder().maxCharsPerByte() * length;
        byte[] rx = new byte[maxBytes];

        // read data from I/O device into temporary byte buffer
        int actualLength = read(rx, maxBytes);

        // return any error codes ( < 0)
        if(actualLength < 0) return actualLength;

        // decode byte array into char buffer
        CharBuffer cb = charset.decode(ByteBuffer.wrap(rx));

        // perform bounds checking on number of bytes read versus the length requested
        if(actualLength < length) length = actualLength;

        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }

        // set the buffer position to the provided offset index
        buffer.position(offset);

        // stuff the decoded characters into the provided CharBuffer
        buffer.put(cb.array(), 0, length);

        // return number of characters put into the array
        return actualLength;
    }

    /**
     * Read character data from the I/O device into the provided character buffer starting
     * at the zero index (first position) up to the specified data length (number of characters).
     * Specify the character set to be used to decode the bytes into chars.
     * <p>
     * NOTE:  The data characters read and decoded from the I/O device are
     *        copied/inserted into the character buffer starting at the current
     *        position index up to the length requested or up to the buffer's
     *        remaining limit; whichever is is lower .  If the buffer's current
     *        position is already at the buffer's limit, then we will automatically
     *        rewind the buffer to begin writing data from the zero position up to
     *        the buffer's limit.
     *
     * @param charset character set to use for byte decoding
     * @param buffer the character array the read data will be copied/inserted into
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(Charset charset, CharBuffer buffer, int length) {
        // if the buffer position is already at the buffer limit, then rewind the buffer for reading
        if(buffer.position() == buffer.limit()) buffer.rewind();

        return read(buffer, buffer.position(), length);
    }

    /**
     * Read character data from the I/O device into the provided character buffer starting
     * at the zero index (first position) up to available space remaining in the buffer.
     * Specify the character set to be used to decode the bytes into chars.
     * <p>
     * NOTE:  The data characters read from the I/O device are copied/
     *        inserted into the character buffer starting at the current
     *        position index up to the buffer's remaining limit. If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     *
     * @param charset character set to use for byte decoding
     * @param buffer the character array the read data will be copied/inserted into
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(Charset charset, CharBuffer buffer) {
        // if the buffer position is already at the buffer limit, then rewind the buffer for reading
        if(buffer.position() == buffer.limit()) buffer.rewind();

        // read the buffer starting at the current position and fill up to the remaining size
        return read(buffer, buffer.position(), buffer.remaining());
    }

    /**
     * Read ASCII character data from the I/O device into the provided character buffer at the given
     * offset and up to the specified data length (number of characters).  ASCII is the internal
     * character set used to decode the bytes into chars.
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only write the number of
     *        characters up to the buffers' available space.
     *
     * @param buffer the character array the read data will be copied/inserted into
     * @param offset the offset index in the character buffer to start copying read data
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(CharBuffer buffer, int offset, int length) {
        return read(StandardCharsets.US_ASCII, buffer, offset, length);
    }

    /**
     * Read ASCII character data from the I/O device into the provided character buffer starting
     * at the zero index (first position) up to the specified data length (number of characters).
     * ASCII is the internal character set used to decode the bytes into chars.
     * <p>
     * NOTE:  The data characters read and decoded from the I/O device are
     *        copied/inserted into the character buffer starting at the current
     *        position index up to the length requested or up to the buffer's
     *        remaining limit; whichever is is lower .  If the buffer's current
     *        position is already at the buffer's limit, then we will automatically
     *        rewind the buffer to begin writing data from the zero position up to
     *        the buffer's limit.
     *
     * @param buffer the character array the read data will be copied/inserted into
     * @param length the number of bytes to read
     * @return If successful, return the number of bytes (not characters) read from the I/O device;
     *         else on a read error, return a negative error code.
     */
    default int read(CharBuffer buffer, int length) {
        return read(StandardCharsets.US_ASCII, buffer, length);
    }

    /**
     * {@inheritDoc}
     *
     * Read ASCII character data from the I/O device into the provided character buffer starting
     * at the zero index (first position) up to available space remaining in the buffer.
     * ASCII is the internal character set used to decode the bytes into chars.
     * <p>
     * NOTE:  The data characters read from the I/O device are copied/
     *        inserted into the character buffer starting at the current
     *        position index up to the buffer's remaining limit. If
     *        the buffer's current position is already at the buffer's
     *        limit, then we will automatically rewind the buffer to
     *        begin writing data from the zero position up to the
     *        buffer's limit.
     */
    default int read(CharBuffer buffer) {
        return read(StandardCharsets.US_ASCII, buffer);
    }


    // ------------------------------------------------------------------------------------
    // AUXILIARY CONVENIENCE METHODS
    // ------------------------------------------------------------------------------------

    /**
     * Read a single raw byte (8-bit) value from the I/O device.
     *
     * @return The 8-bit byte value
     */
    default byte readByte() {
        int actual = read();
        if(actual < 0) throw new IOReadException("I2C READ ERROR; " + actual);
        return (byte) actual;
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new byte array.  The 'offset' parameter allows you to skip
     * a certain number of bytes in the read data an excludes them from the returned
     * data byte array.
     * <p>
     * Note: the resulting byte array size will be at most the 'length' -  'offset'.
     *
     * @param offset the offset index in the data read to start copying read data
     * @param length the number of bytes to read
     * @return a new byte array containing the data bytes read from the I/O device.
     * @throws java.lang.IllegalArgumentException An java.lang.IllegalArgumentException is thrown if one of the
     *                         method parameters are invalid.
     */
    default byte[] readNBytes(int offset, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length <= 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (length < offset) {
            throw new IllegalArgumentException("length < offset");
        }
        int bufferSize = length-offset;
        byte[] temp = new byte[bufferSize];
        int actual = read(temp, offset, length);
        if(actual < 0) throw new IOReadException(actual);
        return Arrays.copyOf(temp, actual);
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new byte array.
     *
     * @param length the number of bytes to read
     * @return a new byte array containing the data bytes read from the I/O device.
     */
    default byte[] readNBytes(int length) {
        return readNBytes(0 ,length);
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new ByteBuffer.  The 'offset' parameter allows you to skip
     * a certain number of bytes in the read data and excludes them from the returned
     * data ByteBuffer.
     * <p>
     * Note: the resulting byte buffer size will be at most the 'length' -  'offset'.
     *
     * @param offset the offset index in the data read to start copying read data
     * @param length the number of bytes to read
     * @return a new ByteBuffer containing the data bytes read from the I/O device.
     * @throws java.lang.IllegalArgumentException An java.lang.IllegalArgumentException is thrown if one of the
     *                         method parameters are invalid.
     */
    default ByteBuffer readByteBuffer(int offset, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length <= 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (length < offset) {
            throw new IllegalArgumentException("length < offset");
        }
        int bufferSize = length-offset;
        byte[] temp = new byte[bufferSize];
        int actual = read(temp, offset, length);
        if(actual < 0) throw new IOReadException(actual);
        return ByteBuffer.wrap(temp, 0, actual);
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new ByteBuffer.  The 'offset' parameter allows you to skip
     * a certain number of bytes in the read data and excludes them from the returned
     * data ByteBuffer.
     *
     * @param length the number of bytes to read
     * @return a new ByteBuffer containing the data bytes read from the I/O device.
     * @throws java.lang.IllegalArgumentException An java.lang.IllegalArgumentException is thrown if one of the
     *                         method parameters are invalid.
     */
    default ByteBuffer readByteBuffer(int length) {
        return readByteBuffer(0, length);
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new character array.  The 'offset' parameter allows you to
     * skip a certain number of bytes in the read data and excludes them from the returned
     * character array.
     *
     * @param charset character set to use for byte decoding
     * @param offset the offset index in the raw bytes read to start from
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character array containing the decoded character data read from the I/O device.
     * @throws java.lang.IllegalArgumentException An java.lang.IllegalArgumentException is thrown if one of the
     *                         method parameters are invalid.
     */
    default char[] readCharArray(Charset charset, int offset, int numberOfBytes) {
        byte[] temp = new byte[numberOfBytes];
        int actual = read(temp, offset, numberOfBytes);
        if(actual < 0) throw new IOReadException(actual);
        CharBuffer cb = charset.decode(ByteBuffer.wrap(temp, 0, actual));
        return cb.array();
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new character array.  ASCII is the internal character
     * set used to decode the bytes into chars.
     *
     * @param charset character set to use for byte decoding
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character array containing the decoded character data read from the I/O device.
     */
    default char[] readCharArray(Charset charset, int numberOfBytes) {
        return readCharArray(charset, 0, numberOfBytes);
    }

    /**
     * Read ASCII data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new character array.  The 'offset' parameter allows you to
     * skip a certain number of bytes in the read data and excludes them from the returned
     * character buffer.  ASCII is the internal character set used to decode the bytes into chars.
     *
     * @param offset the offset index in the raw bytes read to start from
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character array containing the decoded character data read from the I/O device.
     */
    default char[] readCharArray(int offset, int numberOfBytes) {
        return readCharArray(StandardCharsets.US_ASCII, offset, numberOfBytes);
    }

    /**
     * Read ASCII data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new character buffer.
     *
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character array containing the decoded character data read from the I/O device.
     */
    default char[] readCharArray(int numberOfBytes) {
        return readCharArray(0, numberOfBytes);
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new CharBuffer instance.  The 'offset' parameter allows you to
     * skip a certain number of bytes in the read data and excludes them from the returned
     * character buffer.
     *
     * @param charset character set to use for byte decoding
     * @param offset the offset index in the raw bytes read to start from
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character buffer (CharBuffer) containing the decoded character data read from the I/O device.
     * @throws java.lang.IllegalArgumentException An java.lang.IllegalArgumentException is thrown if one of the
     *                         method parameters are invalid.
     */
    default CharBuffer readCharBuffer(Charset charset, int offset, int numberOfBytes) {
        byte[] temp = new byte[numberOfBytes];
        int actual = read(temp, offset, numberOfBytes);
        if(actual < 0) throw new IOReadException(actual);
        CharBuffer cb = charset.decode(ByteBuffer.wrap(temp, 0, actual));
        return cb.flip();
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new CharBuffer instance.
     *
     * @param charset character set to use for byte decoding
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character buffer (CharBuffer) containing the decoded character data read from the I/O device.
     */
    default CharBuffer readCharBuffer(Charset charset, int numberOfBytes) {
        return readCharBuffer(charset, 0, numberOfBytes);
    }

    /**
     * Read ASCII data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new CharBuffer instance.  The 'offset' parameter allows you to
     * skip a certain number of bytes in the read data and excludes them from the returned
     * character buffer.  ASCII is the internal character set used to decode the bytes into chars.
     *
     * @param offset the offset index in the raw bytes read to start from
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character buffer (CharBuffer) containing the decoded character data read from the I/O device.
     */
    default CharBuffer readCharBuffer(int offset, int numberOfBytes) {
        return readCharBuffer(StandardCharsets.US_ASCII, offset, numberOfBytes);
    }

    /**
     * Read ASCII data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new CharBuffer instance.  ASCII is the internal character
     * set used to decode the bytes into chars.
     *
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character buffer (CharBuffer) containing the decoded character data read from the I/O device.
     */
    default CharBuffer readCharBuffer(int numberOfBytes) {
        return readCharBuffer(0, numberOfBytes);
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new String instance.  The 'offset' parameter allows you to
     * skip a certain number of bytes in the read data and excludes them from the returned
     * string.
     *
     * @param charset character set to use for byte decoding
     * @param offset the offset index in the raw bytes read to start from
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character buffer (CharBuffer) containing the decoded character data read from the I/O device.
     */
    default String readString(Charset charset, int offset, int numberOfBytes) {
        byte[] temp = new byte[numberOfBytes];
        int actual = read(temp, offset, numberOfBytes);
        if(actual < 0) throw new IOReadException(actual);
        return new String(temp, 0, actual, charset);
    }

    /**
     * Read data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new String instance.
     *
     * @param charset character set to use for byte decoding
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character buffer (CharBuffer) containing the decoded character data read from the I/O device.
     */
    default String readString(Charset charset, int numberOfBytes) {
        return readString(charset, 0, numberOfBytes);
    }

    /**
     * Read ASCII data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new String instance.  The 'offset' parameter allows you to
     * skip a certain number of bytes in the read data and excludes them from the returned
     * string.  ASCII is the internal character set used to decode the bytes into chars.
     *
     * @param offset the offset index in the raw bytes read to start from
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character buffer (CharBuffer) containing the decoded character data read from the I/O device.
     */
    default String readString(int offset, int numberOfBytes) {
        return readString(StandardCharsets.US_ASCII, offset, numberOfBytes);
    }

    /**
     * Read ASCII data from the I/O device up to a specified length (number of bytes) and
     * return the data read in a new String instance.  ASCII is the internal character set
     * used to decode the bytes into chars.
     *
     * @param numberOfBytes the number of bytes to read (not number of characters)
     * @return a new character buffer (CharBuffer) containing the decoded character data read from the I/O device.
     */
    default String readString(int numberOfBytes) {
        return readString(0, numberOfBytes);
    }


    // ------------------------------------------------------------------------------------
    // INPUT STREAM
    // ------------------------------------------------------------------------------------

    /**
     * Get an InputStream to read data from the I/O device.
     *
     * @return InputStream instance
     */
    default InputStream getInputStream(){
        var t = this;
        return new InputStream() {

//            @Override
//            public int available () {
//                return t.available();
//            }

            @Override
            public int read() {
                return t.read();
            }

            @Override
            public int read(byte[] b, int off, int len) {
                Objects.checkFromIndexSize(off, len, b.length);
                return t.read(b, off, len);
            }

            @Override
            public int readNBytes(byte[] b, int off, int len) {
                Objects.checkFromIndexSize(off, len, b.length);
                return t.read(b, off, len);
            }

            @Override
            public byte[] readNBytes(int len) {
                return t.readNBytes(len);
            }
        };
    }

    /**
     * Get an InputStream to read data from the I/O device.
     *
     * @return InputStream instance
     */
    default InputStream in() {
        return getInputStream();
    }
}
