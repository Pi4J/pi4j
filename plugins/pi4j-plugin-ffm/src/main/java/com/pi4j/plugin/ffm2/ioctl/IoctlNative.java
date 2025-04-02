package com.pi4j.plugin.ffm2.ioctl;

import com.pi4j.plugin.ffm.common.ioctl.Ioctl;
import io.github.digitalsmile.annotation.NativeMemoryException;
import io.github.digitalsmile.annotation.function.NativeCall;
import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryLayout;

import java.lang.foreign.ValueLayout;

public class IoctlNative extends NativeCall implements Ioctl {
	public IoctlNative() {
		super(new IoctlContext());
	}

	@Override
	public int callByValue(int fd, long command, long data) throws NativeMemoryException {
		try {
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) IoctlContext.IOCTL.invoke(capturedState, fd, command, data);
			processError(callResult, capturedState, "callByValue", fd, command, data);
			return callResult;
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}

	@Override
	public long call(int fd, long command, long data) throws NativeMemoryException {
		try {
			var dataMemorySegment = context.allocate(ValueLayout.JAVA_LONG);
			dataMemorySegment.set(ValueLayout.JAVA_LONG, 0, data);
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) IoctlContext.IOCTL_0.invoke(capturedState, fd, command, dataMemorySegment);
			processError(callResult, capturedState, "call", fd, command, dataMemorySegment);
			return dataMemorySegment.get(ValueLayout.JAVA_LONG, 0);
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}

	@Override
	public int call(int fd, long command, int data) throws NativeMemoryException {
		try {
			var dataMemorySegment = context.allocate(ValueLayout.JAVA_INT);
			dataMemorySegment.set(ValueLayout.JAVA_INT, 0, data);
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) IoctlContext.IOCTL_1.invoke(capturedState, fd, command, dataMemorySegment);
			processError(callResult, capturedState, "call", fd, command, dataMemorySegment);
			return dataMemorySegment.get(ValueLayout.JAVA_INT, 0);
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}

	@Override
	public <T extends NativeMemoryLayout> T call(int fd, long command, T data) throws
			NativeMemoryException {
		try {
			var dataMemorySegment = context.allocate(data.getMemoryLayout());
			data.toBytes(dataMemorySegment);
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) IoctlContext.IOCTL_2.invoke(capturedState, fd, command, dataMemorySegment);
			processError(callResult, capturedState, "call", fd, command, dataMemorySegment);
			return data.fromBytes(dataMemorySegment);
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}
}
