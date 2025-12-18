package com.pi4j.library.linuxfs;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for LinuxFS Library
 * FILENAME      :  LinuxFile.java
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

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.pi4j.library.linuxfs.util.NativeLibraryLoader;

/**
 * Extends RandomAccessFile to provide access to Linux ioctl.
 */
@SuppressWarnings("restriction")
public class LinuxFile extends RandomAccessFile {

    private final int fdHandle;

    public LinuxFile(String name, String mode) throws IOException {
        super(name, mode);
        this.fdHandle = getPosixFD();
    }

    public static final int wordSize = getWordSize();
    public static final int localBufferSize = 2048; //about 1 page

    public static final ThreadLocal<ByteBuffer> localDataBuffer = new ThreadLocal<>();
    public static final ThreadLocal<IntBuffer> localOffsetsBuffer = new ThreadLocal<>();

    static {
        // Load the platform library
        NativeLibraryLoader.load("libpi4j-linuxfs.so", "pi4j-linuxfs");
    }

    /**
     * Runs an ioctl value command on a file descriptor.
     *
     * @param command
     *     ioctl command
     * @param value
     *     int ioctl value
     *
     * @throws IOException
     *     when something goes wrong
     */
    public void ioctl(long command, int value) throws IOException {
        int response = directIOCTL(this.fdHandle, command, value);
        if (response < 0)
            throw new LinuxFileException();
    }

    /**
     * Runs an ioctl on a file descriptor. Uses special offset buffer to produce real C-like structures with pointers.
     * Advanced use only! Must be able to produce byte-perfect data structures just as gcc would on this system,
     * including struct padding and pointer size.
     * <p>
     * The data ByteBuffer uses the current position to determine the head point of data passed to the ioctl. This is
     * useful for appending entry-point data structures at the end of the buffer, while referring to other
     * structures/data that come before them in the buffer.
     *
     * <p><b>I NEED A BETTER EXPL OF BUFFERS HERE</b></p>
     *
     * When assembling the structured data, use {@link LinuxFile#wordSize} to determine the size in bytes needed for a
     * pointer. Also be sure to consider GCC padding and structure alignment. GCC will try a field to its word size (32b
     * ints align at 4-byte, etc), and will align the structure size with the native word size (4-byte for 32b, 8-byte
     * for 64b).
     * <p>
     * Provided IntBuffer offsets must use native byte order (endianness).
     *
     * <pre>
     * {@code
     *    <NEED BETTER EXAMPLE HERE>
     * }
     * </pre>
     *
     * DANGER: check your buffer length! The possible length varies depending on the ioctl call. Overruns are very
     * possible. ioctl tries to determine EFAULTs, but sometimes you might trample JVM data if you are not careful.
     *
     * @param command
     *     ioctl command
     * @param data
     *     values in bytes for all structures, with 4 or 8 byte alignment enforced by filling holes before pointers
     * @param offsets
     *     ByteBuffer: offsets of pointer/ byte offset of pointedToData
     *
     * @throws IOException
     *     when something goes wrong
     */
    public void ioctl(final long command, ByteBuffer data, IntBuffer offsets) throws IOException {
        ByteBuffer originalData = data;

        if (data == null || offsets == null)
            throw new NullPointerException("data and offsets required!");

        if (offsets.order() != ByteOrder.nativeOrder())
            throw new IllegalArgumentException("provided IntBuffer offsets ByteOrder must be native!");

        //buffers must be direct
        try {
            if (!data.isDirect()) {
                ByteBuffer newBuf = getDataBuffer(data.limit());
                int pos = data.position(); //keep position

                data.rewind();
                newBuf.clear();
                newBuf.put(data);
                newBuf.position(pos); //restore position

                data = newBuf;
            }

            if (!offsets.isDirect()) {
                IntBuffer newBuf = getOffsetsBuffer(offsets.remaining());

                newBuf.clear();
                newBuf.put(offsets);
                newBuf.flip();

                offsets = newBuf;
            }
        } catch (BufferOverflowException e) {
            throw new ScratchBufferOverrun();
        }

        if ((offsets.remaining() & 1) != 0)
            throw new IllegalArgumentException("offset buffer must be even length!");

        for (int i = offsets.position(); i < offsets.limit(); i += 2) {
            final int ptrOffset = offsets.get(i);
            final int dataOffset = offsets.get(i + 1);

            if (dataOffset >= data.capacity() || dataOffset < 0)
                throw new IndexOutOfBoundsException("invalid data offset specified in buffer: " + dataOffset);

            if ((ptrOffset + wordSize) > data.capacity() || ptrOffset < 0)
                throw new IndexOutOfBoundsException("invalid pointer offset specified in buffer: " + ptrOffset);
        }
        int posFD = this.getPosixFD();
        final int response = directIOCTLStructure(posFD, command, data, data.position(), offsets,
            offsets.position(), offsets.limit());

        if (response < 0)
            throw new LinuxFileException();

        //fast forward positions
        offsets.position(offsets.limit());
        data.rewind();

        //if original data wasnt direct, copy it back in.
        if (originalData != data) {
            originalData.rewind();
            originalData.put(data);
            originalData.rewind();
        }
    }

    /**
     * Gets the real POSIX file descriptor for use by custom jni calls.
     *
     * @return the real POSIX file descriptor
     *
     * @throws IOException
     *     if reading fails
     */
    protected int getPosixFD() throws IOException {
        final int fd = getPosixFD(getFD());
        if (fd < 1)
            throw new IOException("failed to get POSIX file descriptor!");
        return fd;
    }

    /**
     * Return the word size, i.e. 4 or 8, depending if the system ist 32-bit or 64-bit respectively
     *
     * @return the word size, i.e. 4 or 8, depending if the system ist 32-bit or 64-bit respectively
     */
    protected static int getWordSize() {
        final String archDataModel = System.getProperty("sun.arch.data.model");
        return "64".equals(archDataModel) ? 8 : 4;
    }

    protected synchronized IntBuffer getOffsetsBuffer(int size) {
        final int byteSize = size * 4;
        IntBuffer buf = localOffsetsBuffer.get();

        if (byteSize > localBufferSize)
            throw new ScratchBufferOverrun();
        // if no buffer currently exists, new one always created.
        // if buffer exists and limit is not equal to this getOffsetsBuffer, allocate new
        if (buf == null) {
            ByteBuffer bb = ByteBuffer.allocateDirect(localBufferSize);

            //keep native order, set before cast to IntBuffer
            bb.order(ByteOrder.nativeOrder());

            buf = bb.asIntBuffer();
            localOffsetsBuffer.set(buf);
        } else if(buf.limit() != size) {
            localOffsetsBuffer.remove();
            ByteBuffer bb = ByteBuffer.allocateDirect(localBufferSize);

            //keep native order, set before cast to IntBuffer
            bb.order(ByteOrder.nativeOrder());

            buf = bb.asIntBuffer();
            localOffsetsBuffer.set(buf);
        }
        return buf;
    }


    protected  synchronized ByteBuffer getDataBuffer(int size) {
        ByteBuffer buf = localDataBuffer.get();

        if (size > localBufferSize)
            throw new ScratchBufferOverrun();

        if (buf == null) {
            buf = ByteBuffer.allocateDirect(size);
            localDataBuffer.set(buf);
        }else if(buf.limit() != size){
            localDataBuffer.remove();
            buf = ByteBuffer.allocateDirect(size);
            localDataBuffer.set(buf);
        }

        return buf;
    }

    public static class ScratchBufferOverrun extends IllegalArgumentException {
        private static final long serialVersionUID = -418203522640826177L;

        public ScratchBufferOverrun() {
            super(
                "Scratch buffer overrun! Provide direct ByteBuffer for data larger than " + localBufferSize + " bytes");
        }
    }

    public static class LinuxFileException extends IOException {
        private static final long serialVersionUID = -2581606746434701394L;
        int code;

        public LinuxFileException() {
            this(errno());
        }

        LinuxFileException(int code) {
            super(strerror(code));

            this.code = code;
        }

        /**
         * Gets the POSIX code associated with this IO error
         *
         * @return POSIX error code
         */
        public int getCode() {
            return code;
        }
    }

    protected static native int getPosixFD(FileDescriptor fileDescriptor);

    protected static native int errno();

    protected static native String strerror(int code);

    protected static native int directIOCTL(int fd, long command, int value);

    protected static native int directIOCTLStructure(int fd, long command, ByteBuffer data, int dataOffset,
        IntBuffer offsetMap, int offsetMapOffset, int offsetCapacity);
}
