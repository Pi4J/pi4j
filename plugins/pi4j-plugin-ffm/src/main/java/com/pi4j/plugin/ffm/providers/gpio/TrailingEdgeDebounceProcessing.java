package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.plugin.ffm.common.gpio.PinEventProcessing;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Debounces GPIO events by retaining the latest event in each debounce interval and dispatching
 * it when a later event passes the interval or the event watcher reports a polling timeout.
 * <p>
 * This implementation is Copilot's attempt at replicating the original {@link EventWatcher} functionality.
 */
public class TrailingEdgeDebounceProcessing implements PinEventProcessing {

    private final long debounceNs;
    private final PinEventProcessing callback;

    private LineEvent pendingEvent;
    private long lastEventReceivedTimeNs;

    public TrailingEdgeDebounceProcessing(long debounceNs, PinEventProcessing callback) {
        this.debounceNs = debounceNs;
        this.callback = callback;
    }

    @Override
    public void process(List<LineEvent> events) {
        if (events.isEmpty()) {
            this.flush();
            return;
        }

        List<LineEvent> readyEvents = new ArrayList<>();
        for (LineEvent event : events) {
            if (pendingEvent != null && event.timestampNs() - pendingEvent.timestampNs() >= debounceNs) {
                readyEvents.add(pendingEvent);
            }

            pendingEvent = event;
            lastEventReceivedTimeNs = System.nanoTime();
        }

        if (!readyEvents.isEmpty()) {
            callback.process(readyEvents);
        }
    }

    /**
     * Called when the event watcher's poll times out. Dispatches the pending event if the debounce
     * interval has elapsed since an event was received.
     */
    private void flush() {
        if (pendingEvent != null && System.nanoTime() - lastEventReceivedTimeNs >= debounceNs) {
            var event = pendingEvent;
            pendingEvent = null;
            lastEventReceivedTimeNs = 0;
            callback.process(List.of(event));
        }
    }
}
