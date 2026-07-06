package com.pi4j.plugin.ffm.common.i2c;

import com.pi4j.exception.Pi4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.CAPTURED_STATE_LAYOUT;
import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;
import static com.pi4j.plugin.ffm.common.i2c.SMBusContext.*;

/**
 * Performs SMBus read/write transactions by invoking the {@code i2c_smbus_*} helper functions
 * from {@code libi2c} (see {@link SMBusContext}). This is the low-level native backend used by
 * the FFM I2C implementation of the pi4j-core {@link com.pi4j.io.i2c.I2C} contract.
 * <p>
 * Each call follows the same pattern:
 * <ul>
 *   <li>allocate the needed buffers from a per-call {@link Arena#ofConfined()} arena</li>
 *   <li>add the {@code errno} capture state buffer</li>
 *   <li>invoke the native function</li>
 *   <li>translate a captured {@code errno} into a {@link Pi4JException}</li>
 *   <li>return the call result</li>
 * </ul>
 */
public class SMBusNative implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SMBusNative.class);

    // Keep the context field to trigger SMBusContext class loading (loads libi2c library).
    @SuppressWarnings("unused")
    private final SMBusContext context = new SMBusContext();

    /**
     * Writes byte with SMBus protocol.
     *
     * @param fd   file descriptor of the opened I2C/SMBus device
     * @param data the single data byte to send
     * @return the native call result (number of bytes written, or {@code 0} on success)
     * @throws Pi4JException if the native call fails (a non-zero {@code errno} was captured)
     */
    public int writeByte(int fd, byte data) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
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
     * @param fd file descriptor of the opened I2C/SMBus device
     * @return the byte read from the bus, returned in signed integer form
     * @throws Pi4JException if the native call fails (a non-zero {@code errno} was captured)
     */
    public int readByte(int fd) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) READ_BYTE.invoke(capturedState, fd);
            processError(callResult, capturedState, "readByte", fd);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Writes byte to the given register with SMBus protocol.
     *
     * @param fd       file descriptor of the opened I2C/SMBus device
     * @param register the device register address to write to
     * @param data     the single data byte to store in the register
     * @return the native call result (number of bytes written, or {@code 0} on success)
     * @throws Pi4JException if the native call fails (a non-zero {@code errno} was captured)
     */
    public int writeByteData(int fd, byte register, byte data) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
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
     * @param fd       file descriptor of the opened I2C/SMBus device
     * @param register the device register address to read from
     * @return the byte read from the register
     * @throws Pi4JException if the native call fails (a non-zero {@code errno} was captured)
     */
    public byte readByteData(int fd, byte register) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
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
     * @param fd       file descriptor of the opened I2C/SMBus device
     * @param register the device register address to write to
     * @param data     the block of bytes to write; its length is sent as the block count
     * @return the native call result (number of bytes written, or {@code 0} on success)
     * @throws Pi4JException if the native call fails (a non-zero {@code errno} was captured)
     */
    public int writeBlockData(int fd, byte register, byte[] data) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var memoryBuffer = arena.allocateFrom(ValueLayout.JAVA_BYTE, data);
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
     * @param fd       file descriptor of the opened I2C/SMBus device
     * @param register the device register address to read from
     * @param data     a byte array sized to the expected block length, used to allocate the read buffer
     * @return a byte array holding the bytes read from the register
     * @throws Pi4JException if the native call fails (a non-zero {@code errno} was captured)
     */
    public byte[] readBlockData(int fd, byte register, byte[] data) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var memoryBuffer = arena.allocateFrom(ValueLayout.JAVA_BYTE, data);
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
     * @param fd       file descriptor of the opened I2C/SMBus device
     * @param register the device register address to write to
     * @param data     the 16-bit word value to store (passed as an {@code int})
     * @return the native call result (number of bytes written, or {@code 0} on success)
     * @throws Pi4JException if the native call fails (a non-zero {@code errno} was captured)
     */
    public int writeWordData(int fd, byte register, int data) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
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
     * @param fd       file descriptor of the opened I2C/SMBus device
     * @param register the device register address to read from
     * @return the 16-bit word read from the register, returned as an {@code int}
     * @throws Pi4JException if the native call fails (a non-zero {@code errno} was captured)
     */
    public int readWordData(int fd, byte register) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) READ_WORD_DATA.invoke(capturedState, fd, register);
            processError(callResult, capturedState, "readWordData", fd, register);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        // The global Arena (used for library lookups) does not need explicit closing.
    }
}
