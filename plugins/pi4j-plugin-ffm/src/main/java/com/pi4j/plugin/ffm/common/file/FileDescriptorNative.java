package com.pi4j.plugin.ffm.common.file;

import com.pi4j.exception.Pi4JException;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.CAPTURED_STATE_LAYOUT;
import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;

/**
 * High-level wrapper around the glibc file syscalls exposed by {@link FileDescriptorContext},
 * providing the {@code open}/{@code close}/{@code read}/{@code write}/{@code flock}/{@code access}
 * operations the FFM backend uses to talk to character device nodes (gpiochip, i2c, spidev, pwm).
 * The logic behind the class is follows:
 * - allocate the needed buffers from a per-call {@link Arena#ofConfined()} arena
 * - optionally add 'errno' context to caller
 * - call native function with 'invoke'
 * - process errors if any captured by 'errno'
 * - return call result if needed
 */
public class FileDescriptorNative {
    // Keep the context field to trigger FileDescriptorContext class loading (and thus MethodHandle init).
    @SuppressWarnings("unused")
    private final FileDescriptorContext context = new FileDescriptorContext();

    /**
     * Opens file. Delegate to native 'open64' glibc method.
     *
     * @param path     the path to be opened in file system
     * @param openFlag bitmask of open flags (e.g. {@code O_RDWR}); see {@link FileFlag} for the values
     * @return the non-negative file descriptor to be used for subsequent file operations
     * @throws Pi4JException if the native {@code open64} call fails, carrying the {@code errno} detail
     */
    public int open(String path, int openFlag) {
        try (var arena = Arena.ofConfined()) {
            var pathMemorySegment = arena.allocateFrom(path);
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) FileDescriptorContext.OPEN64.invoke(capturedState, pathMemorySegment, openFlag);
            processError(callResult, capturedState, "open", path, openFlag);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Closes file. Delegate to native 'close' glibc method.
     *
     * @param fd file descriptor of the file to close
     * @throws Pi4JException if the native {@code close} call fails, carrying the {@code errno} detail
     */
    public void close(int fd) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) FileDescriptorContext.CLOSE.invoke(capturedState, fd);
            processError(callResult, capturedState, "close", fd);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Reads file. Delegate to native 'read' glibc method.
     *
     * @param fd     file descriptor of the file to read
     * @param buffer byte buffer sized to receive the read; its length defines the native segment size
     * @param size   number of bytes to request from the read call
     * @return a byte array holding the bytes read back from the file descriptor
     * @throws Pi4JException if the native {@code read} call fails, carrying the {@code errno} detail
     */
    public byte[] read(int fd, byte[] buffer, int size) {
        try (var arena = Arena.ofConfined()) {
            var bufferMemorySegment = arena.allocateFrom(ValueLayout.JAVA_BYTE, buffer);
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) FileDescriptorContext.READ.invoke(capturedState, fd, bufferMemorySegment, size);
            processError(callResult, capturedState, "read", fd, buffer, size);
            return bufferMemorySegment.toArray(ValueLayout.JAVA_BYTE);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Writes to file. Delegate to native 'write' glibc method.
     *
     * @param fd   file descriptor of the file to write
     * @param data the bytes to write; the full array length is passed as the write count
     * @return the number of bytes actually written
     * @throws Pi4JException if the native {@code write} call fails, carrying the {@code errno} detail
     */
    public int write(int fd, byte[] data) {
        try (var arena = Arena.ofConfined()) {
            var dataMemorySegment = arena.allocateFrom(ValueLayout.JAVA_BYTE, data);
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) FileDescriptorContext.WRITE.invoke(capturedState, fd, dataMemorySegment, data.length);
            processError(callResult, capturedState, "write", fd, data);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Repositions the file offset of an open descriptor. Delegate to native 'lseek64' glibc method.
     * <p>
     * Used to rewind long-lived descriptors (e.g. sysfs PWM attribute files that are opened once and
     * reused) to the start before each read or write, so the operation always acts on the whole value.
     *
     * @param fd     file descriptor whose offset is to be changed
     * @param offset the new offset, interpreted according to {@code whence}
     * @param whence one of {@code SEEK_SET}, {@code SEEK_CUR} or {@code SEEK_END}; see {@link FileFlag}
     * @return the resulting offset measured in bytes from the beginning of the file
     * @throws Pi4JException if the native {@code lseek64} call fails, carrying the {@code errno} detail
     */
    public long lseek(int fd, long offset, int whence) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (long) FileDescriptorContext.LSEEK64.invoke(capturedState, fd, offset, whence);
            if (callResult < 0) {
                processError(-1, capturedState, "lseek", fd, offset, whence);
            }
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Locks the file on filesystem. Delegate to native 'flock' glibc method.
     *
     * @param fd       file descriptor of the file to lock
     * @param lockFlag the {@code flock} operation, e.g. {@code LOCK_EX} or {@code LOCK_UN}; see {@link FileFlag}
     * @return the native call result, {@code 0} on success
     * @throws Pi4JException if the native {@code flock} call fails, carrying the {@code errno} detail
     */
    public int flock(int fd, int lockFlag) {
        try (var arena = Arena.ofConfined()) {
            var capturedState = arena.allocate(CAPTURED_STATE_LAYOUT);
            var callResult = (int) FileDescriptorContext.FLOCK.invoke(capturedState, fd, lockFlag);
            processError(callResult, capturedState, "flock", fd, lockFlag);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Checking file access/exitance for current user. Delegate to native 'access' glibc method.
     * NOTE: this method checks access only for the current user and is not taking in account SUID/GUID type of calls.
     *
     * @param path the path to be checked in file system
     * @param flag the access mode to test, e.g. {@code R_OK}, {@code W_OK} or {@code F_OK}; see {@link FileFlag}
     * @return {@code 0} if all requested access checks pass, a negative value otherwise
     * @throws Pi4JException if invoking the native {@code access} handle fails
     */
    public int access(String path, int flag) {
        try (var arena = Arena.ofConfined()) {
            var pathMemorySegment = arena.allocateFrom(path);
            return (int) FileDescriptorContext.ACCESS.invoke(pathMemorySegment, flag);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }
}
