package com.pi4j.plugin.ffm.common.ioctl;

import com.pi4j.plugin.ffm.common.Pi4JNativeContext;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Class is describing native methods to access ioctl (hardware and syscalls)
 */
class IoctlContext extends Pi4JNativeContext {

    // Native glibc 'ioctl' method
    // Accepts long as data
	static final MethodHandle IOCTL = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("ioctl").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG),
			Linker.Option.captureCallState("errno"));

    // Native glibc 'ioctl' method
    // Accepts reference to object as data and returns long
    static final MethodHandle IOCTL_0 = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("ioctl").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
			Linker.Option.captureCallState("errno"));

    // Native glibc 'ioctl' method
    // Accepts reference to object as data and returns int
    static final MethodHandle IOCTL_1 = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("ioctl").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
			Linker.Option.captureCallState("errno"));
}
