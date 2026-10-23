package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.plugin.ffm.common.gpio.PinEventProcessing;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Copilot describes this as:
 * <blockquote>
 * This is a **leading-edge debounce / cooldown**, not a “wait until the signal settles” debounce:
 * <ul>
 *  <li> It processes each event in timestamp order and compares it with the **last accepted** event.
 *  <li> If the time since that event is less than `debounceNs`, it drops the new event. Otherwise, it accepts it and
 *      updates the reference timestamp.
 *  <li> After checking the whole input batch, it calls the callback once with the accepted events; if none were accepted,
 *      it doesn’t call the callback.
 * </ul>
 * For example, with a 10 ms interval, events at 100 ms, 105 ms, and 110 ms result in 100 ms and 110 ms being delivered.
 * The interval check uses `<`, so an event exactly 10 ms later is accepted.
 * <p>
 * It doesn’t delay or merge events, or deliver the last suppressed event later. One edge case: timestamp `0` is used
 * as the “no previous event” sentinel, so an event at timestamp zero won’t establish the cooldown reference.
 * </blockquote>
 */
public class RateLimitingDebounceProcessing implements PinEventProcessing {

    private final long debounceNs;
    private final PinEventProcessing callback;

    private long lastEventTimestampNs = 0;

    /**
     * @param debounceNs the debounce time in nanoseconds
     * @param callback the callback to process the filtered events
     */
    public RateLimitingDebounceProcessing(long debounceNs, PinEventProcessing callback) {
        this.debounceNs = debounceNs;
        this.callback = callback;
    }

    @Override
    public void process(List<LineEvent> eventList) {
        List<LineEvent> eventBuffer = new ArrayList<>();

        for (LineEvent event : eventList) {
            // Within debounce period, ignore this event
            if ((lastEventTimestampNs > 0) && (event.timestampNs() - lastEventTimestampNs < debounceNs)) {
                continue;
            }

            // First event or outside debounce period
            lastEventTimestampNs = event.timestampNs();
            eventBuffer.add(event);
        }

        if (!eventBuffer.isEmpty()) {
            callback.process(eventBuffer);
        }
    }
}
