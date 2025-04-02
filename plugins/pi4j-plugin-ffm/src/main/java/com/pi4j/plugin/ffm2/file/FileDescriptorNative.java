package com.pi4j.plugin.ffm2.file;

import com.pi4j.plugin.ffm.common.file.FileDescriptor;
import io.github.digitalsmile.annotation.NativeMemoryException;
import io.github.digitalsmile.annotation.function.NativeCall;

import java.lang.foreign.ValueLayout;

public class FileDescriptorNative extends NativeCall implements FileDescriptor {
	public FileDescriptorNative() {
		super(new FileDescriptorContext());
	}

	@Override
	public int open(String path, int openFlag) throws NativeMemoryException {
		try {
			var pathMemorySegment = context.allocateFrom(path);
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) FileDescriptorContext.OPEN64.invoke(capturedState, pathMemorySegment, openFlag);
			processError(callResult, capturedState, "open", pathMemorySegment, openFlag);
			return callResult;
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}

	@Override
	public void close(int fd) throws NativeMemoryException {
		try {
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) FileDescriptorContext.CLOSE.invoke(capturedState, fd);
			processError(callResult, capturedState, "close", fd);
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] read(int fd, byte[] buffer, int size) throws NativeMemoryException {
		try {
			var bufferMemorySegment = context.allocateFrom(ValueLayout.JAVA_BYTE, buffer);
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) FileDescriptorContext.READ.invoke(capturedState, fd, bufferMemorySegment, size);
			processError(callResult, capturedState, "read", fd, bufferMemorySegment, size);
			return bufferMemorySegment.toArray(ValueLayout.JAVA_BYTE);
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}

	@Override
	public int write(int fd, byte[] data) throws NativeMemoryException {
		try {
			var dataMemorySegment = context.allocateFrom(ValueLayout.JAVA_BYTE, data);
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) FileDescriptorContext.WRITE.invoke(capturedState, fd, dataMemorySegment);
			processError(callResult, capturedState, "write", fd, dataMemorySegment);
			return callResult;
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}
}
