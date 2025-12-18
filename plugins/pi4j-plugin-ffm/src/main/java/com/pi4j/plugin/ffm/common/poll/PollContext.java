package com.pi4j.plugin.ffm.common.poll;

import com.pi4j.plugin.ffm.common.Pi4JNativeContext;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

/**
 * Class is describing native methods to polling (poll)
 */
class PollContext extends Pi4JNativeContext {

    // Native glibc 'poll' method
	static final MethodHandle POLL = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("poll").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));
}
