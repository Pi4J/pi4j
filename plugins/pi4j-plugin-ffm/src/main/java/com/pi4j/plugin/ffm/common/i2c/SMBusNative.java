package com.pi4j.plugin.ffm.common.i2c;

import com.pi4j.exception.Pi4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;
import static com.pi4j.plugin.ffm.common.i2c.SMBusContext.*;

public class SMBusNative implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SMBusNative.class);

    private final SMBusContext context = new SMBusContext();

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



    public byte readByte(int fd) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = (byte) READ_BYTE.invoke(capturedState, fd);
            processError(callResult, capturedState, "readByte", fd);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

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
