package com.pi4j.plugin.ffm.common.i2c;

import com.pi4j.exception.Pi4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;
import static com.pi4j.plugin.ffm.common.i2c.SMBusContext.*;

/**
 * Class for calling native SMBus native methods with libi2c-dev.
 * The logic behind the class is follows:
 * - allocate the needed buffers from Arena object with method parameters
 * - optionally add 'errno' context to caller
 * - call native function with 'invoke'
 * - process errors if any captured by 'errno'
 * - return call result if needed
 */
public class SMBusNative implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SMBusNative.class);

    private final SMBusContext context = new SMBusContext();

    /**
     * Writes byte with SMBus protocol.
     *
     * @param fd   file descriptor of opened SMBus device
     * @param data one byte of data
     * @return size of data written
     */
    public int writeByte(int fd, byte data) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = (int) WRITE_BYTE.invoke(capturedState, fd, data);
            processError(callResult, capturedState, "writeByte", fd, data);
            return callResult;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Reads byte with SMBus protocol.
     *
     * @param fd file descriptor of opened SMBus device
     * @return one byte from bus
     */
    public byte readByte(int fd) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = Byte.toUnsignedInt((byte) READ_BYTE.invoke(capturedState, fd));
            processError(callResult, capturedState, "readByte", fd);
            return (byte) callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Writes byte to the given register with SMBus protocol.
     *
     * @param fd       file descriptor of opened SMBus device
     * @param register register to be written
     * @param data     one byte of data
     * @return size of data written
     */
    public int writeByteData(int fd, byte register, byte data) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = (int) WRITE_BYTE_DATA.invoke(capturedState, fd, register, data);
            processError(callResult, capturedState, "writeByteData", fd, register, data);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Reads byte from given register.
     *
     * @param fd       file descriptor of opened SMBus device
     * @param register register where to read
     * @return one byte from bus
     */
    public byte readByteData(int fd, byte register) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = (byte) READ_BYTE_DATA.invoke(capturedState, fd, register);
            processError(Byte.toUnsignedInt(callResult), capturedState, "readByteData", fd, register);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Writes byte array to the given register with SMBus protocol.
     *
     * @param fd       file descriptor of opened SMBus device
     * @param register register to be written
     * @param data     byte array of data
     * @return size of data written
     */
    public int writeBlockData(int fd, byte register, byte[] data) {
        try {
            var capturedState = context.allocateCapturedState();
            var memoryBuffer = context.allocateFrom(ValueLayout.JAVA_BYTE, data);
            var callResult = (int) WRITE_BLOCK_DATA.invoke(capturedState, fd, register, data.length, memoryBuffer);
            processError(callResult, capturedState, "writeBlockData", fd, register, data.length, data);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Reads byte array from provided register with SMBus protocol.
     *
     * @param fd       file descriptor of opened SMBus device
     * @param register register where to read
     * @param data     byte array of data to be read
     * @return byte array of data from bus
     */
    public byte[] readBlockData(int fd, byte register, byte[] data) {
        try {
            var capturedState = context.allocateCapturedState();
            var memoryBuffer = context.allocateFrom(ValueLayout.JAVA_BYTE, data);
            var callResult = (int) READ_BLOCK_DATA.invoke(capturedState, fd, register, memoryBuffer);
            processError(callResult, capturedState, "readBlockData", fd, register, data);
            return memoryBuffer.toArray(ValueLayout.JAVA_BYTE);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Writes int data to provided register with SMBus protocol.
     *
     * @param fd       file descriptor of opened SMBus device
     * @param register register to be written
     * @param data     one int data to write
     * @return size of data written
     */
    public int writeWordData(int fd, byte register, int data) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = (int) WRITE_WORD_DATA.invoke(capturedState, fd, register, data);
            processError(callResult, capturedState, "writeWordData", fd, register, data);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Reads int data from provided register with SMBus protocol.
     *
     * @param fd       file descriptor of opened SMBus device
     * @param register register where to read
     * @return one int data read from bus
     */
    public int readWordData(int fd, byte register) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = (int) READ_WORD_DATA.invoke(capturedState, fd, register);
            processError(callResult, capturedState, "readWordData", fd, register);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        context.close();
    }
}
