package com.pi4j.plugin.ffm.common.file;

import com.pi4j.plugin.ffm.common.Pi4JNativeContext;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

/**
 * Class is describing native methods to access files (open/read/write/etc)
 */
class FileDescriptorContext extends Pi4JNativeContext {

    // Native glibc 'open64' method
	static final MethodHandle OPEN64 = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("open64").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));

    // Native glibc 'close' method
	static final MethodHandle CLOSE = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("close").orElseThrow(), FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));

    // Native glibc 'read' method
	static final MethodHandle READ = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("read").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));

    // Native glibc 'write' method
	static final MethodHandle WRITE = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("write").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));

    // Native glibc 'flock' method
    static final MethodHandle FLOCK = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("flock").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT),
        Linker.Option.captureCallState("errno"));

    // Native glibc 'access' method
    static final MethodHandle ACCESS = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("access").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
}
