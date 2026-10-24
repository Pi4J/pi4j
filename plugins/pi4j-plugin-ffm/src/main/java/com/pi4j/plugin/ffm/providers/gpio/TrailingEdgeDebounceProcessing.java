package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.plugin.ffm.common.gpio.PinEventProcessing;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

/**
 * Debounces GPIO events by retaining the latest event in each debounce interval and dispatching
 * it when a later event passes the interval or the event watcher reports a polling timeout.
 * <p>
 * This implementation is Copilot's attempt at replicating the original {@link EventWatcher} functionality.
 */
public class TrailingEdgeDebounceProcessing implements PinEventProcessing {

    private final long debounceNs;
    private final PinEventProcessing callback;
    private final LongSupplier nanoTime;

    private LineEvent pendingEvent;
    private long lastEventReceivedTimeNs;

    /**
     * @param debounceNs the debounce interval in nanoseconds
     * @param callback the callback to process the debounced events
     */
    public TrailingEdgeDebounceProcessing(long debounceNs, PinEventProcessing callback) {
        this(debounceNs, callback, System::nanoTime);
    }

    /**
     * Package-private constructor for testing purposes, allowing injection of a custom time source.
     * @param debounceNs the debounce interval in nanoseconds
     * @param callback the callback to process the debounced events
     * @param nanoTime the time source to use for measuring intervals
     */
    TrailingEdgeDebounceProcessing(long debounceNs, PinEventProcessing callback, LongSupplier nanoTime) {
        this.debounceNs = debounceNs;
        this.callback = callback;
        this.nanoTime = nanoTime;
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
            lastEventReceivedTimeNs = nanoTime.getAsLong();
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
        if (pendingEvent != null && nanoTime.getAsLong() - lastEventReceivedTimeNs >= debounceNs) {
            var event = pendingEvent;
            pendingEvent = null;
            lastEventReceivedTimeNs = 0;
            callback.process(List.of(event));
        }
    }
}
