package com.pi4j.plugin.ffm2.file;

import io.github.digitalsmile.annotation.function.NativeCall;
import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryContext;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

class FileDescriptorContext implements NativeMemoryContext {
	private static final Arena ARENA = Arena.ofAuto();

	private static final SymbolLookup LIBC_LIB = Linker.nativeLinker().defaultLookup();

	static final MethodHandle OPEN64 = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("open64").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));

	static final MethodHandle CLOSE = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("close").orElseThrow(), FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));

	static final MethodHandle READ = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("read").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));

	static final MethodHandle WRITE = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("write").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS),
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
