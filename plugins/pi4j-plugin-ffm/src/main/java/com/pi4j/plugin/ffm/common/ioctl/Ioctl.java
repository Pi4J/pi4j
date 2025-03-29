package com.pi4j.plugin.ffm.common.ioctl;

import io.github.digitalsmile.annotation.NativeMemoryException;
import io.github.digitalsmile.annotation.function.ByAddress;
import io.github.digitalsmile.annotation.function.NativeManualFunction;
import io.github.digitalsmile.annotation.function.Returns;
import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryLayout;

public interface Ioctl {
    @NativeManualFunction(name = "ioctl", useErrno = true)
    int callByValue(int fd, long command, long data) throws NativeMemoryException;

    @NativeManualFunction(name = "ioctl", useErrno = true)
    long call(int fd, long command, @Returns @ByAddress long data) throws NativeMemoryException;

    @NativeManualFunction(name = "ioctl", useErrno = true)
    int call(int fd, long command, @Returns @ByAddress int data)  throws NativeMemoryException;

    @NativeManualFunction(name = "ioctl", useErrno = true, nativeReturnType = int.class)
    <T extends NativeMemoryLayout> T call(int fd, long command, @Returns T data) throws NativeMemoryException;
}
