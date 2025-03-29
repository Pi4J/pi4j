package com.pi4j.plugin.ffm.common.file;

import io.github.digitalsmile.annotation.NativeMemoryException;
import io.github.digitalsmile.annotation.function.ByAddress;
import io.github.digitalsmile.annotation.function.NativeManualFunction;
import io.github.digitalsmile.annotation.function.Returns;

public interface FileDescriptor {
    @NativeManualFunction(name = "open64", useErrno = true)
    int open(@ByAddress String path, int openFlag) throws NativeMemoryException;

    @NativeManualFunction(name = "close", useErrno = true)
    void close(int fd)throws NativeMemoryException;

    @NativeManualFunction(name = "read", useErrno = true, nativeReturnType = int.class)
    byte[] read(int fd, @Returns @ByAddress byte[] buffer, int size) throws NativeMemoryException;

    @NativeManualFunction(name = "write", useErrno = true)
    int write(int fd, @ByAddress byte[] data) throws NativeMemoryException;
}