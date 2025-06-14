package com.pi4j.plugin.ffm.common.poll;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import static com.pi4j.plugin.ffm.common.Pi4JNative.processError;

public class PollNative {
    private final PollContext context = new PollContext();

    public <T extends Pi4JLayout> T poll(T pollingData, int size, int timeout) {
        try {
            var pollingDataMemorySegment = context.allocate(pollingData.getMemoryLayout());
            pollingData.to(pollingDataMemorySegment);
            var capturedState = context.allocateCapturedState();
            var callResult = (int) PollContext.POLL.invoke(capturedState, pollingDataMemorySegment, size, timeout);
            processError(callResult, capturedState, "poll", pollingDataMemorySegment, size, timeout);
            return pollingData.from(pollingDataMemorySegment);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
