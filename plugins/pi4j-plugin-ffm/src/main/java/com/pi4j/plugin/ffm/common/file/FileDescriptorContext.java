package com.pi4j.plugin.ffm.common.file;

import com.pi4j.plugin.ffm.common.Pi4JNativeContext;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

/**
 * Holds the downcall {@link MethodHandle}s bound to the glibc file-access functions
 * ({@code open64}, {@code close}, {@code read}, {@code write}, {@code lseek64}, {@code flock},
 * {@code access}) used by {@link FileDescriptorNative}. Extends {@link Pi4JNativeContext} to reuse the
 * shared native linker setup and {@code errno} capture infrastructure.
 */
class FileDescriptorContext extends Pi4JNativeContext {

    // Native glibc 'open64' method
	static final MethodHandle OPEN64 = Linker.nativeLinker().downcallHandle(
			LIBC_LIB.find("open64").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
			Linker.Option.captureCallState("errno"));

    // Native glibc 'lseek64' method (off64_t lseek64(int fd, off64_t offset, int whence))
    static final MethodHandle LSEEK64 = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("lseek64").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT),
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
