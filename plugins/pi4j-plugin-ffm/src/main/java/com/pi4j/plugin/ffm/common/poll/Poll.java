package com.pi4j.plugin.ffm.common.poll;

import io.github.digitalsmile.annotation.NativeMemory;
import io.github.digitalsmile.annotation.NativeMemoryException;
import io.github.digitalsmile.annotation.function.NativeManualFunction;
import io.github.digitalsmile.annotation.function.Returns;
import io.github.digitalsmile.annotation.structure.Struct;
import io.github.digitalsmile.annotation.structure.Structs;
import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryLayout;

@NativeMemory(headers = "/usr/src/linux-headers-${linux-headers}/include/uapi/asm-generic/poll.h")
@Structs({
    @Struct(name = "pollfd", javaName = "PollingData")
})
public interface Poll {

    @NativeManualFunction(name = "poll", useErrno = true, nativeReturnType = int.class)
    <T extends NativeMemoryLayout> T poll(@Returns T pollingData, int size, int timeout) throws NativeMemoryException;
}
