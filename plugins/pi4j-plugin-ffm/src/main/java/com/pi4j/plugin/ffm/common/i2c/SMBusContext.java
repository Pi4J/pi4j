package com.pi4j.plugin.ffm.common.i2c;

import com.pi4j.plugin.ffm.common.Pi4JNative;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

class SMBusContext extends Pi4JNative {

    // TODO: we need to provide the link for libi2c.so file as no hardcode
    // TODO: check installed libi2c-dev package, provide installation script
    private static final SymbolLookup LIBI2C = SymbolLookup.libraryLookup(Path.of("/usr/lib/x86_64-linux-gnu/libi2c.so"), ARENA);

    static final MethodHandle WRITE_BYTE = Linker.nativeLinker().downcallHandle(
            LIBI2C.find("i2c_smbus_write_byte").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE),
            Linker.Option.captureCallState("errno"));

    static final MethodHandle READ_BYTE = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_read_byte").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_BYTE, ValueLayout.JAVA_INT),
        Linker.Option.captureCallState("errno"));

    static final MethodHandle WRITE_BYTE_DATA = Linker.nativeLinker().downcallHandle(
            LIBI2C.find("i2c_smbus_write_byte_data").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE, ValueLayout.JAVA_BYTE),
            Linker.Option.captureCallState("errno"));

    static final MethodHandle READ_BYTE_DATA = Linker.nativeLinker().downcallHandle(
            LIBI2C.find("i2c_smbus_read_byte_data").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_BYTE, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE),
            Linker.Option.captureCallState("errno"));

    static final MethodHandle READ_BLOCK_DATA = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_read_block_data").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE, ValueLayout.ADDRESS),
        Linker.Option.captureCallState("errno"));

    static final MethodHandle WRITE_BLOCK_DATA = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_write_block_data").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE, ValueLayout.JAVA_INT, ValueLayout.ADDRESS),
        Linker.Option.captureCallState("errno"));

}
