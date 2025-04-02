package com.pi4j.plugin.ffm2.poll;

import io.github.digitalsmile.annotation.function.NativeCall;
import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryContext;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

class PollContext implements NativeMemoryContext {
	private static final Arena ARENA = Arena.ofAuto();

	private static final SymbolLookup LIBC_LIB = Linker.nativeLinker().defaultLookup();

	static final MethodHandle POLL = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("poll").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT),
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
