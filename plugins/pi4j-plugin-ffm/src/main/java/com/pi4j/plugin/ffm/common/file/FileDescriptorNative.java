package com.pi4j.plugin.ffm.common.file;

import com.pi4j.exception.Pi4JException;

import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNative.processError;

public class FileDescriptorNative {
    private final FileDescriptorContext context = new FileDescriptorContext();

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

	public void close(int fd)  {
		try {
			var capturedState = context.allocateCapturedState();
			var callResult = (int) FileDescriptorContext.CLOSE.invoke(capturedState, fd);
			processError(callResult, capturedState, "close", fd);
		} catch (Throwable e) {
			throw new Pi4JException(e.getMessage(), e);
		}
	}

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

    public int access(String path, int flag) {
        try {
            var pathMemorySegment = context.allocateFrom(path);
            return (int) FileDescriptorContext.ACCESS.invoke(pathMemorySegment, flag);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }
}
