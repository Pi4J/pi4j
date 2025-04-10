package com.pi4j.plugin.ffm.common.file;

import com.pi4j.plugin.ffm.common.Pi4JNative;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

class FileDescriptorContext extends Pi4JNative {

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
}
