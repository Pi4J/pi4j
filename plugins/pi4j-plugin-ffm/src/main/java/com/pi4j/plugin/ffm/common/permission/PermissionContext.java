package com.pi4j.plugin.ffm.common.permission;

import com.pi4j.plugin.ffm.common.Pi4JNativeContext;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

/**
 * Class is describing native methods to help detecting permissions.
 */
class PermissionContext extends Pi4JNativeContext {

    // Native glibc 'setgrent' method
    static final MethodHandle SET_GR_ENT = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("setgrent").orElseThrow(),
        FunctionDescriptor.ofVoid(),
        Linker.Option.captureCallState("errno"));

    // Native glibc 'getgrent' method
    static final MethodHandle GET_GR_ENT = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("getgrent").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.ADDRESS),
        Linker.Option.captureCallState("errno"));

    // Native glibc 'endgrent' method
    static final MethodHandle END_GR_ENT = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("endgrent").orElseThrow(),
        FunctionDescriptor.ofVoid(),
        Linker.Option.captureCallState("errno"));

    // Native glibc 'getgrouplist' method
    static final MethodHandle GET_GROUP_LIST = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("getgrouplist").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS, ValueLayout.ADDRESS),
        Linker.Option.captureCallState("errno"));

    // Native glibc 'getgrgid' method
    static final MethodHandle GET_GROUP_DATA = Linker.nativeLinker().downcallHandle(
        LIBC_LIB.find("getgrgid").orElseThrow(),
        FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
        Linker.Option.captureCallState("errno"));
}