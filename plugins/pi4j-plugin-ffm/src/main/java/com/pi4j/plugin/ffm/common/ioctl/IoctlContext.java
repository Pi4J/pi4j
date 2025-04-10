package com.pi4j.plugin.ffm.common.ioctl;

import com.pi4j.plugin.ffm.common.Pi4JNative;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

class IoctlContext extends Pi4JNative {

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
}
