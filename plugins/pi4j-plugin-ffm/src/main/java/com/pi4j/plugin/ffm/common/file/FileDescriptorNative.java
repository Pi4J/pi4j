package com.pi4j.plugin.ffm.common.file;

import com.pi4j.exception.Pi4JException;

import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;

/**
 * Class for calling native file methods.
 * The logic behind the class is follows:
 * - allocate the needed buffers from Arena object with method parameters
 * - optionally add 'errno' context to caller
 * - call native function with 'invoke'
 * - process errors if any captured by 'errno'
 * - return call result if needed
 */
public class FileDescriptorNative {
    private final FileDescriptorContext context = new FileDescriptorContext();

    /**
     * Opens file. Delegate to native 'open64' glibc method.
     *
     * @param path     the path to be opened in file system
     * @param openFlag flag to be used when opening (see {@link  FileFlag} enum for details)
     * @return internal file descriptor to be used for other file manipulations
     */
    public int open(String path, int openFlag) {
        try {
            var pathMemorySegment = context.allocateFrom(path);
            var capturedState = context.allocateCapturedState();
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
     * @param fd file descriptor of closing file
     */
    public void close(int fd) {
        try {
            var capturedState = context.allocateCapturedState();
            var callResult = (int) FileDescriptorContext.CLOSE.invoke(capturedState, fd);
            processError(callResult, capturedState, "close", fd);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Reads file. Delegate to native 'read' glibc method.
     *
     * @param fd     file descriptor of file to read
     * @param buffer byte buffer which will contain the result of read
     * @param size   byte buffer size to be read
     * @return the byte buffer with the data read from file descriptor
     */
    public byte[] read(int fd, byte[] buffer, int size) {
        try {
            var bufferMemorySegment = context.allocateFrom(ValueLayout.JAVA_BYTE, buffer);
            var capturedState = context.allocateCapturedState();
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
     * @param fd   file descriptor of file to write
     * @param data byte buffer, containing data to write
     * @return size of data written
     */
    public int write(int fd, byte[] data) {
        try {
            var dataMemorySegment = context.allocateFrom(ValueLayout.JAVA_BYTE, data);
            var capturedState = context.allocateCapturedState();
            var callResult = (int) FileDescriptorContext.WRITE.invoke(capturedState, fd, dataMemorySegment, data.length);
            processError(callResult, capturedState, "write", fd, data);
            return callResult;
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }

    /**
     * Locks the file on filesystem. Delegate to native 'flock' glibc method.
     *
     * @param fd       file descriptor of file to lock
     * @param lockFlag lock flags as described in {@link FileFlag}
     * @return file locking descriptor
     */
    public int flock(int fd, int lockFlag) {
        try {
            var capturedState = context.allocateCapturedState();
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
     * @param flag access flags as described in {@link FileFlag}
     * @return the result of checking, 0 means all checks are passed
     */
    public int access(String path, int flag) {
        try {
            var pathMemorySegment = context.allocateFrom(path);
            return (int) FileDescriptorContext.ACCESS.invoke(pathMemorySegment, flag);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }
}
