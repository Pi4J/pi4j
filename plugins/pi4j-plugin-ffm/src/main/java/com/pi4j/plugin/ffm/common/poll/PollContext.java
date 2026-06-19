package com.pi4j.plugin.ffm.common.poll;

import com.pi4j.plugin.ffm.common.Pi4JNativeContext;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

/**
 * Binds the libc {@code poll(2)} syscall as a {@link MethodHandle} with {@code errno} capture, used to
 * wait for events (such as GPIO line edge interrupts) on a set of file descriptors. The handle is
 * invoked by {@link PollNative}.
 */
class PollContext extends Pi4JNativeContext {

    // Native glibc 'poll' method
	static final MethodHandle POLL = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("poll").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));
}
