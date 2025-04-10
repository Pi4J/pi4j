package com.pi4j.plugin.ffm.common.poll.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/asm-generic/poll.h:36:8
 */
public record PollingData(int fd, short events, short revents) implements Pi4JLayout {
	public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
		ValueLayout.JAVA_INT.withName("fd"),
		ValueLayout.JAVA_SHORT.withName("events"),
		MemoryLayout.paddingLayout(2),
		ValueLayout.JAVA_SHORT.withName("revents")
	);

	private static final VarHandle VH_FD = LAYOUT.varHandle(groupElement("fd"));

	private static final VarHandle VH_EVENTS = LAYOUT.varHandle(groupElement("events"));

	private static final VarHandle VH_REVENTS = LAYOUT.varHandle(groupElement("revents"));

	public static PollingData create(MemorySegment memorySegment) throws Throwable {
		var pollingdataInstance = PollingData.createEmpty();
		if (!memorySegment.equals(MemorySegment.NULL)) {
			pollingdataInstance = pollingdataInstance.from(memorySegment);
		}
		return pollingdataInstance;
	}

	public static PollingData createEmpty() {
		return new PollingData(0, (short) 0, (short) 0);
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public PollingData from(MemorySegment buffer) throws Throwable {
		return new PollingData(
			(int) VH_FD.get(buffer, 0L),
			(short) VH_EVENTS.get(buffer, 0L),
			(short) VH_REVENTS.get(buffer, 0L));
	}

	@Override
	public void to(MemorySegment buffer) throws Throwable {
		VH_FD.set(buffer, 0L, fd);
		VH_EVENTS.set(buffer, 0L, events);
		VH_REVENTS.set(buffer, 0L, revents);
	}

    @Override
    public String toString() {
        return "PollingData{" +
            "fd=" + fd +
            ", events=" + events +
            ", revents=" + revents +
            '}';
    }
}
