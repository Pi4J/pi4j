package com.pi4j.plugin.ffm2.ioctl;

import io.github.digitalsmile.annotation.function.NativeCall;
import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryContext;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

class IoctlContext implements NativeMemoryContext {
	private static final Arena ARENA = Arena.ofAuto();

	private static final SymbolLookup LIBC_LIB = Linker.nativeLinker().defaultLookup();

	static final MethodHandle IOCTL = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("ioctl").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG),
			Linker.Option.captureCallState("errno"));

	static final MethodHandle IOCTL_0 = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("ioctl").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
			Linker.Option.captureCallState("errno"));

	static final MethodHandle IOCTL_1 = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("ioctl").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
			Linker.Option.captureCallState("errno"));

	static final MethodHandle IOCTL_2 = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("ioctl").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
			Linker.Option.captureCallState("errno"));

	@Override
	public MemorySegment allocate(long byteSize, long byteAlignment) {
		return ARENA.allocate(byteSize, byteAlignment);
	}

	@Override
	public void checkIsCreatedByArena(MemorySegment segment) {
		if ((!ARENA.scope().equals(segment.scope()) || !NativeCall.createdInContext(segment.scope())) && !Arena.global().scope().equals(segment.scope())) {
			throw new IllegalArgumentException("The scope of the MemorySegment arena is not the same as the scope of the arena");
		}
	}

	@Override
	public Arena getArena() {
		return ARENA;
	}

	@Override
	public void close() {
		ARENA.close();
	}
}
