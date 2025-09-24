package com.pi4j.plugin.ffm.common.poll;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;

import static com.pi4j.plugin.ffm.common.Pi4JNativeContext.processError;

/**
 * Class for calling native poll methods.
 * The logic behind the class is follows:
 * - allocate the needed buffers from Arena object with method parameters
 * - optionally add 'errno' context to caller
 * - call native function with 'invoke'
 * - process errors if any captured by 'errno'
 * - return call result if needed
 */
public class PollNative {
    private final PollContext context = new PollContext();

    /**
     * Calls native poll method and fills the polling data.
     *
     * @param pollingData data to filled by poll
     * @param size        size of the event buffer for polling data
     * @param timeout     time needed for timeout to occur, in milliseconds
     * @return filled {@link PollingData} with events
     */
    public PollingData poll(PollingData pollingData, int size, int timeout) {
        try {
            var pollingDataMemorySegment = context.allocate(pollingData.getMemoryLayout());
            pollingData.to(pollingDataMemorySegment);
            var capturedState = context.allocateCapturedState();
            var callResult = (int) PollContext.POLL.invoke(capturedState, pollingDataMemorySegment, size, timeout);
            processError(callResult, capturedState, "poll", pollingData, size, timeout);
            return pollingData.from(pollingDataMemorySegment);
        } catch (Throwable e) {
            throw new Pi4JException(e.getMessage(), e);
        }
    }
}
