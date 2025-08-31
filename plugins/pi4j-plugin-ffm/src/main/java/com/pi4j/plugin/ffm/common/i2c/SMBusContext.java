package com.pi4j.plugin.ffm.common.i2c;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.Pi4JArchitectureGuess;
import com.pi4j.plugin.ffm.common.Pi4JNativeContext;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

/**
 * Class is describing native methods to access SMBus protocol with libi2c-dev runtime dependency.
 */
class SMBusContext extends Pi4JNativeContext {
    private static final SymbolLookup LIBI2C;

    static {
        try {
            var path = Path.of(Pi4JArchitectureGuess.getLibraryPath("libi2c"));
            LIBI2C = SymbolLookup.libraryLookup(path, ARENA);
        } catch (Exception e) {
            throw new Pi4JException("Probably libi2c-dev package is missing. Try to install with `sudo apt-get install libi2c-dev`", e);
        }
    }

    // Native libi2c 'i2c_smbus_write_byte' method
    static final MethodHandle WRITE_BYTE = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("i2c_smbus_write_byte").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE),
        Linker.Option.captureCallState("errno"));

    // Native libi2c 'i2c_smbus_read_byte' method
    static final MethodHandle READ_BYTE = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_read_byte").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_BYTE, ValueLayout.JAVA_INT),
        Linker.Option.captureCallState("errno"));

    // Native libi2c 'i2c_smbus_write_byte_data' method
    static final MethodHandle WRITE_BYTE_DATA = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_write_byte_data").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE, ValueLayout.JAVA_BYTE),
        Linker.Option.captureCallState("errno"));

    // Native libi2c 'i2c_smbus_read_byte_data' method
    static final MethodHandle READ_BYTE_DATA = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_read_byte_data").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_BYTE, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE),
        Linker.Option.captureCallState("errno"));

    // Native libi2c 'i2c_smbus_read_block_data' method
    static final MethodHandle READ_BLOCK_DATA = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_read_block_data").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE, ValueLayout.ADDRESS),
        Linker.Option.captureCallState("errno"));

    // Native libi2c 'i2c_smbus_write_block_data' method
    static final MethodHandle WRITE_BLOCK_DATA = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_write_block_data").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE, ValueLayout.JAVA_INT, ValueLayout.ADDRESS),
        Linker.Option.captureCallState("errno"));

    // Native libi2c 'i2c_smbus_read_word_data' method
    static final MethodHandle READ_WORD_DATA = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_read_word_data").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE),
        Linker.Option.captureCallState("errno"));

    // Native libi2c 'i2c_smbus_write_word_data' method
    static final MethodHandle WRITE_WORD_DATA = Linker.nativeLinker().downcallHandle(
        LIBI2C.find("i2c_smbus_write_word_data").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE, ValueLayout.JAVA_INT),
        Linker.Option.captureCallState("errno"));

}
