package com.pi4j.io;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IODataWriter.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import com.pi4j.exception.Pi4JException;

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
public interface IODataWriter {

    // ------------------------------------------------------------------------------------
    // SINGLE BYTE
    // ------------------------------------------------------------------------------------

    /**
     * Write a single raw byte value.
     *
     * @param b byte to be written
     * @return a int.
     */
    int write(byte b);

    /**
     * Write a single raw byte value.
     *
     * @param b integer value that will be cast to a byte and written
     * @return a int.
     */
    default int write(int b) {
        return write((byte)b);
    }

    // ------------------------------------------------------------------------------------
    // BYTE ARRAY
    // ------------------------------------------------------------------------------------

    /**
     * Write an array of byte values with given offset (starting position) and length in the provided data array.
     *
     * @param data data array of bytes to be written
     * @param offset offset in data buffer to start at
     * @param length number of bytes to be written
     * @return The number of bytes written, possibly zero
     */
    int write(byte[] data, int offset, int length);

    /**
     * Write an array of byte values starting with the first byte in the array up to the provided length.
     *
     * @param data data array of bytes to be written
     * @param length number of bytes to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(byte[] data, int length) {
        return write(data, 0, length);
    }

    /**
     * Write an array of byte values (all bytes in array).
     *
     * @param data data array of bytes to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(byte... data) {
        return write(data, 0, data.length);
    }

    /**
     * Write multiple byte arrays.
     *
     * @param data data array of bytes to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(byte[]... data) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (byte[] ba : data) {
                os.write(ba);
            }
            return write(os.toByteArray());
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Write a collection of byte arrays.
     *
     * @param data collection of byte array of data to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(Collection<byte[]> data) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (byte[] ba : data) {
                os.write(ba);
            }
            return write(os.toByteArray());
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    // ------------------------------------------------------------------------------------
    // BYTE BUFFER
    // ------------------------------------------------------------------------------------

    /**
     * Write a buffer of byte values with given offset (starting position) and length in the provided data buffer.
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only read the number of
     *        bytes up to the buffers' available space.
     *
     * @param buffer byte buffer of data to be written
     * @param offset offset in data buffer to start at
     * @param length number of bytes to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(ByteBuffer buffer, int offset, int length) {
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }
        return write(buffer.array(), offset, length);
    }

    /**
     * Write a buffer of byte values starting with the first byte in the array up to the provided length.
     * <p>
     * NOTE:  The contents from the byte buffer is read
     *        from the current position index up to the length
     *        requested or up to the buffer's remaining limit;
     *        whichever is is lower .  If the buffer's current
     *        position is already at the buffer's limit, then we
     *        will automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's limit.
     *
     * @param buffer byte buffer of data to be written
     * @param length number of bytes to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(ByteBuffer buffer, int length) {
        // if the buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(buffer.position() == buffer.limit()) buffer.flip();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(length > buffer.remaining()) length = buffer.remaining();

        // write contents from the buffer starting at the current position up the the specified length
        return write(buffer, buffer.position(), length);
    }

    /**
     * Write a buffer of byte values (all bytes in buffer).
     * <p>
     * NOTE:  The contents from the byte buffer is read
     *        from the current position index up to the buffer's
     *        remaining limit.  If the buffer's current position
     *        is already at the buffer's limit, then we will
     *        automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's
     *        limit.
     *
     * @param buffer byte buffer of data to be written (from current position to limit)
     * @return The number of bytes written, possibly zero
     */
    default int write(ByteBuffer buffer) {
        // if the buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(buffer.position() == buffer.limit()) buffer.flip();

        // write contents from the buffer starting at the current position up the the remaining buffer size
        return write(buffer, buffer.position(), buffer.remaining());
    }

    /**
     * Write multiple byte buffers of data.
     * <p>
     * NOTE:  The contents from each byte buffer is read
     *        from the current position index up to the buffer's
     *        remaining limit.  If the buffer's current position
     *        is already at the buffer's limit, then we will
     *        automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's
     *        limit.
     *
     * @param buffer byte buffer of data to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(ByteBuffer ... buffer) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (ByteBuffer bb : buffer) {
                // if the buffer position is already at the buffer limit, then flip the buffer for
                //reading data from the buffer at the starting position to write to the I/O device
                if(bb.position() == bb.limit()) bb.flip();

                // write the byte array to the byte output stream
                os.write(bb.array());
            }
            return write(os.toByteArray());
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    // ------------------------------------------------------------------------------------
    // INPUT STREAM
    // ------------------------------------------------------------------------------------

    /**
     * Write a stream of data (all bytes available in input stream).
     *
     * @param stream stream of data to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(InputStream stream) {
        try {
            return write(stream.readAllBytes());
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Write a stream of stream of data up to the length (number of bytes) specified.
     *
     * @param stream stream of data to be written
     * @param length number of bytes to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(InputStream stream, int length) {
        try {
            return write(stream.readNBytes(length));
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Write multiple streams of data (all bytes available in each input stream).
     *
     * @param stream stream of data to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(InputStream ... stream) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (InputStream is : stream) {
                os.write(is.readAllBytes());
            }
            return write(os.toByteArray());
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    // ------------------------------------------------------------------------------------
    // CHARACTER SEQUENCE (Strings)
    // ------------------------------------------------------------------------------------

    /**
     * Writes a data string with specified character set (encoding).
     *
     * @param data string data (US_ASCII) to be written
     * @param charset character set to use for byte encoding
     * @return The number of bytes written, possibly zero
     */
    default int write(Charset charset, CharSequence data) {
        return write(data.toString().getBytes(charset));
    }

    /**
     * Writes a data string with specified character set (encoding).
     *
     * @param data string data (US_ASCII) to be written
     * @param charset character set to use for byte encoding
     * @return The number of bytes written, possibly zero
     */
    default int write(Charset charset, CharSequence ... data) {
        StringBuilder builder = new StringBuilder();
        for(var d : data){
            builder.append(d);
        }
        return write(charset, builder);
    }

    /**
     * Write a collection of ASCII character sequences
     * with specified character set (encoding).
     *
     * @param charset character set to use for byte encoding
     * @param data collection of character sequences of data to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(Charset charset, Collection<CharSequence> ... data) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (Collection<CharSequence> csc : data) {
                for (CharSequence cs : csc) {
                    os.write(cs.toString().getBytes(charset));
                }
            }
            return write(os.toByteArray());
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Writes an ASCII data string/character sequence.
     *
     * @param data string data (US_ASCII) to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(CharSequence data) {
        return write(StandardCharsets.US_ASCII, data);
    }

    /**
     * Writes multiple ASCII data strings/character sequences.
     *
     * @param data string data (US_ASCII) to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(CharSequence ... data) {
        return write(StandardCharsets.US_ASCII, data);
    }

    /**
     * Write a collection of ASCII character sequences.
     *
     * @param data collection of character sequences of data to be written
     * @return The number of bytes written, possibly zero
     */
    default int write(Collection<CharSequence> ... data) {
        return write(StandardCharsets.US_ASCII, data);
    }

    // ------------------------------------------------------------------------------------
    // CHARACTER ARRAYS
    // ------------------------------------------------------------------------------------

    /**
     * Writes an ASCII based character array (1 or more chars) with
     * a given offset and length.
     *
     * @param data ASCII character array used for data write
     * @param offset offset in data character array to start at
     * @param length number of character in character array to be written
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(char[] data, int offset, int length) {
        return write(StandardCharsets.US_ASCII, data, offset, length);
    }

    /**
     * Writes an ASCII based character array (1 or more chars) with
     * a given length starting from the 0 index position.
     *
     * @param data ASCII character array used for data write
     * @param length number of character in character array to be written
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(char[] data, int length) {
        return write(StandardCharsets.US_ASCII, data, length);
    }

    /**
     * Writes an ASCII based character array (1 or more chars).
     *
     * @param data ASCII character array used for data write
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(char ... data) {
        return write(StandardCharsets.US_ASCII, data);
    }

    /**
     * Writes a character based array with a given offset and length.
     * Specify the encoding to be used to encode the chars into bytes.
     *
     * @param charset character set to use for byte encoding
     * @param data ASCII character array used for data write
     * @param offset offset in data character array to start at
     * @param length number of character in character array to be written
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(Charset charset, char[] data, int offset, int length) {
        ByteBuffer bb = charset.encode(CharBuffer.wrap(data, offset, length));
        return write(bb.array());
    }

    /**
     * Writes a character based array starting at the first index with a given length.
     * Specify the encoding to be used to encode the chars into bytes.
     *
     * @param charset character set to use for byte encoding
     * @param data ASCII character array used for data write
     * @param length number of character in character array to be written
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(Charset charset, char[] data, int length) {
        ByteBuffer bb = charset.encode(CharBuffer.wrap(data, 0, length));
        return write(bb.array());
    }

    /**
     * Writes a character array (1 or more chars).
     *
     * @param data character array (1 or more chars) to be written
     * @param charset character set to use for byte encoding
     * @return The number of bytes written, possibly zero
     */
    default int write(Charset charset, char ... data) {
        ByteBuffer bb = charset.encode(CharBuffer.wrap(data));
        return write(bb.array());
    }

    /**
     * Write a collection of character arrays.  Specify the encoding
     * to be used to encode the chars into bytes.
     *
     * @param data collection of character sequences of data to be written
     * @return The number of bytes written, possibly zero
     * @param charset a {@link java.nio.charset.Charset} object.
     */
    default int write(Charset charset, Collection<char[]> data) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (char[] ca : data) {
                ByteBuffer bb = charset.encode(CharBuffer.wrap(ca));
                os.write(bb.array());
            }
            return write(os.toByteArray());
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    // ------------------------------------------------------------------------------------
    // CHARACTER BUFFERS
    // ------------------------------------------------------------------------------------

    /**
     * Writes an ASCII based character buffer with a given offset and length.
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only read the number of
     *        characters up to the buffers' available space.
     *
     * @param buffer ASCII character buffer used for data write
     * @param offset offset in data character array to start at
     * @param length number of character in character array to be written
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(CharBuffer buffer, int offset, int length) {
        return write(StandardCharsets.US_ASCII, buffer, offset, length);
    }

    /**
     * Writes an ASCII based character buffer starting at first index to a given length.
     * <p>
     * NOTE:  The contents from the character buffer is read
     *        from the current position index up to the length
     *        requested or up to the buffer's remaining limit;
     *        whichever is is lower .  If the buffer's current
     *        position is already at the buffer's limit, then we
     *        will automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's limit.
     *
     * @param buffer ASCII character buffer used for data write
     * @param length number of character in character array to be written
     * @return The number of bytes (not characters) written, possibly zero
     * @throws java.io.IOException thrown on write error
     */
    default int write(CharBuffer buffer, int length) throws IOException {
        return write(StandardCharsets.US_ASCII, buffer, length);
    }

    /**
     * Writes an ASCII based character buffer.
     * <p>
     * NOTE:  The contents from the character buffer is read
     *        from the current position index up to the buffer's
     *        remaining limit.  If the buffer's current position
     *        is already at the buffer's limit, then we will
     *        automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's
     *        limit.
     *
     * @param buffer ASCII character buffer used for data write
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(CharBuffer buffer) {
        return write(StandardCharsets.US_ASCII, buffer);
    }

    /**
     * Writes multiple ASCII based character buffers.
     * <p>
     * NOTE:  The contents from each character buffer is read
     *        from the current position index up to the buffer's
     *        remaining limit.  If the buffer's current position
     *        is already at the buffer's limit, then we will
     *        automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's
     *        limit.
     *
     * @param buffer ASCII character buffer used for data write
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(CharBuffer ... buffer) {
        return write(StandardCharsets.US_ASCII, buffer);
    }

    /**
     * Writes a character buffer with a given offset and length
     * using a specified character set to encode the chars into bytes.
     * <p>
     * NOTE:  The buffer's internal position tracking is not
     *        used but rather only the explicit offset and
     *        length provided.  If the requested length is
     *        greater than the buffers capacity (minus offset)
     *        then the specified length will be ignored and
     *        this function will only read the number of
     *        characters up to the buffers' available space.
     *
     * @param charset character set to use for byte encoding
     * @param buffer character buffer used for data write
     * @param offset offset in data character array to start at
     * @param length number of character in character array to be written
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(Charset charset, CharBuffer buffer, int offset, int length) {
        // perform bounds checking on requested length versus total remaining size available
        if(length > (buffer.capacity()-offset)){
            length = buffer.capacity()-offset;
        }

        // convert the character array to a byte array and write the byte array
        ByteBuffer bb = charset.encode(CharBuffer.wrap(buffer.array(), offset, length));
        return write(bb.array());
    }

    /**
     * Writes a character buffer starting at first index to a
     * given length using a specified character set to encode the chars into bytes.
     * <p>
     * NOTE:  The contents from the character buffer is read
     *        from the current position index up to the length
     *        requested or up to the buffer's remaining limit;
     *        whichever is is lower .  If the buffer's current
     *        position is already at the buffer's limit, then we
     *        will automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's limit.
     *
     * @param charset character set to use for byte encoding
     * @param buffer character buffer used for data write
     * @param length number of character in character array to be written
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(Charset charset, CharBuffer buffer, int length) {
        // if the buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(buffer.position() == buffer.limit()) buffer.flip();

        // bounds check the requested length; only allow reading up to the remaining space in the buffer
        if(length > buffer.remaining()) length = buffer.remaining();

        // write contents from the buffer starting at the current position up the the specified length
        return write(charset, buffer, buffer.position(), length);
    }

    /**
     * Writes character buffer using a specified
     * character set to encode the chars into bytes.
     * <p>
     * NOTE:  The contents from the character buffer is read
     *        from the current position index up to the buffer's
     *        remaining limit.  If the buffer's current position
     *        is already at the buffer's limit, then we will
     *        automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's
     *        limit.
     *
     * @param charset character set to use for byte encoding
     * @param buffer character buffer used for data write
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(Charset charset, CharBuffer buffer) {
        // if the buffer position is already at the buffer limit, then flip the buffer for
        //reading data from the buffer at the starting position to write to the I/O device
        if(buffer.position() == buffer.limit()) buffer.flip();

        // write contents from the buffer starting at the current position up the the remaining buffer size
        return write(charset, buffer, buffer.position(), buffer.remaining());
    }

    /**
     * Writes multiple character buffers using a specified
     * character set to encode the chars into bytes.
     * <p>
     * NOTE:  The contents from each character buffer is read
     *        from the current position index up to the buffer's
     *        remaining limit.  If the buffer's current position
     *        is already at the buffer's limit, then we will
     *        automatically flip the buffer to begin reading
     *        data from the zero position up to the buffer's
     *        limit.
     *
     * @param charset character set to use for byte encoding
     * @param buffer character buffer used for data write
     * @return The number of bytes (not characters) written, possibly zero
     */
    default int write(Charset charset, CharBuffer ... buffer) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (CharBuffer cb : buffer) {
                // if the buffer position is already at the buffer limit, then flip the buffer for
                //reading data from the buffer at the starting position to write to the I/O device
                if(cb.position() == cb.limit()) cb.flip();

                // encode the contents of the character buffer from the current position up to the limit
                ByteBuffer bb = charset.encode(CharBuffer.wrap(cb.array(), cb.position(), cb.remaining()));

                // write the encoded byte buffer to the byte array output stream
                os.write(bb.array());
            }
            return write(os.toByteArray());
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }


    // ------------------------------------------------------------------------------------
    // OUTPUT STREAM
    // ------------------------------------------------------------------------------------

    /**
     * Get an output stream to write data to
     *
     * @return new output stream instance to write to
     */
    default OutputStream getOutputStream(){
        var t = this;
        return new OutputStream() {
            @Override
            public void write(int b) {
                t.write((byte)b);
            }

            @Override
            public void write(byte b[]) {
                t.write(b);
            }

            @Override
            public void write(byte b[], int off, int len) {
                this.write(b, off, len);
            }
        };
    }

    /**
     * Get an output stream to write data to
     *
     * @return new output stream instance to write to
     */
    default OutputStream out() {
        return getOutputStream();
    }
}
