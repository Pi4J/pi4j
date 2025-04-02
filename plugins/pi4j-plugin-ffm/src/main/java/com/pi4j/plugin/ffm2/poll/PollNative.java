package com.pi4j.plugin.ffm2.poll;

import com.pi4j.plugin.ffm.common.poll.Poll;
import io.github.digitalsmile.annotation.NativeMemoryException;
import io.github.digitalsmile.annotation.function.NativeCall;
import io.github.digitalsmile.annotation.types.interfaces.NativeMemoryLayout;

public class PollNative extends NativeCall implements Poll {
	public PollNative() {
		super(new PollContext());
	}

	@Override
	public <T extends NativeMemoryLayout> T poll(T pollingData, int size, int timeout) throws
			NativeMemoryException {
		try {
			var pollingDataMemorySegment = context.allocate(pollingData.getMemoryLayout());
			pollingData.toBytes(pollingDataMemorySegment);
			var capturedState = context.allocate(CAPTURED_STATE_LAYOUT);
			var callResult = (int) PollContext.POLL.invoke(capturedState, pollingDataMemorySegment, size, timeout);
			processError(callResult, capturedState, "poll", pollingDataMemorySegment, size, timeout);
			return pollingData.fromBytes(pollingDataMemorySegment);
		} catch (Throwable e) {
			throw new NativeMemoryException(e.getMessage(), e);
		}
	}
}
