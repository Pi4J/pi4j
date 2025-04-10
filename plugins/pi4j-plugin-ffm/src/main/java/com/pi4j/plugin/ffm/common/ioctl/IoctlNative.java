package com.pi4j.plugin.ffm.common.ioctl;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.ValueLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNative.processError;

public class IoctlNative {
    private final IoctlContext context = new IoctlContext();

	public int callByValue(int fd, long command, long data) {
		try {
			var capturedState = context.allocateCapturedState();
			var callResult = (int) IoctlContext.IOCTL.invoke(capturedState, fd, command, data);
			processError(callResult, capturedState, "callByValue", fd, command, data);
			return callResult;
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public long call(int fd, long command, long data) {
		try {
			var dataMemorySegment = context.allocate(ValueLayout.JAVA_LONG);
			dataMemorySegment.set(ValueLayout.JAVA_LONG, 0, data);
			var capturedState = context.allocateCapturedState();
			var callResult = (int) IoctlContext.IOCTL_0.invoke(capturedState, fd, command, dataMemorySegment);
			processError(callResult, capturedState, "call", fd, command, dataMemorySegment);
			return dataMemorySegment.get(ValueLayout.JAVA_LONG, 0);
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public int call(int fd, long command, int data) {
		try {
			var dataMemorySegment = context.allocate(ValueLayout.JAVA_INT);
			dataMemorySegment.set(ValueLayout.JAVA_INT, 0, data);
			var capturedState = context.allocateCapturedState();
			var callResult = (int) IoctlContext.IOCTL_1.invoke(capturedState, fd, command, dataMemorySegment);
			processError(callResult, capturedState, "call", fd, command, dataMemorySegment);
			return dataMemorySegment.get(ValueLayout.JAVA_INT, 0);
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public <T extends Pi4JLayout> T call(int fd, long command, T data) {
		try {
			var dataMemorySegment = context.allocate(data.getMemoryLayout());
			data.to(dataMemorySegment);
			var capturedState = context.allocateCapturedState();
			var callResult = (int) IoctlContext.IOCTL_2.invoke(capturedState, fd, command, dataMemorySegment);
			processError(callResult, capturedState, "call", fd, command, dataMemorySegment);
			return data.from(dataMemorySegment);
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
