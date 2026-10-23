package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitingDebounceProcessingTest {

    @Test
    void receivesSingleEvent() {
        var events = List.of(new LineEvent(0, 1, 0, 0, 0));

        var result = new AtomicReference<>(new ArrayList<LineEvent>());
        var processing = new RateLimitingDebounceProcessing(51, eventList -> result.updateAndGet(list -> {
            list.addAll(eventList);
            return list;
        }));
        processing.process(events);

        var processedEvents = result.get();
        assertEquals(1, processedEvents.size());
        assertEquals(events.getFirst(), processedEvents.getFirst());
    }

    @Test
    void skipsEventsInSingleBatch() {
        var events = List.of(
            new LineEvent(100, 1, 0, 0, 0),
            new LineEvent(150, 2, 0, 1, 1),
            new LineEvent(200, 1, 0, 2, 2)
        );

        var result = new AtomicReference<>(new ArrayList<LineEvent>());
        var processing = new RateLimitingDebounceProcessing(51, eventList -> result.updateAndGet(list -> {
            list.addAll(eventList);
            return list;
        }));
        processing.process(events);

        var processedEvents = result.get();
        // We expect 2 events to be processed from the single batch
        assertEquals(2, processedEvents.size());
        assertEquals(events.get(0), processedEvents.get(0));
        assertEquals(events.get(2), processedEvents.get(1));
    }

    @Test
    void skipsEventsAcrossBatches() {
        var events1 = List.of(
            new LineEvent(100, 1, 0, 0, 0),
            new LineEvent(150, 2, 0, 1, 1),
            new LineEvent(200, 1, 0, 2, 2)
        );

        var events2 = List.of(
            new LineEvent(250, 2, 0, 3, 3),
            new LineEvent(300, 1, 0, 4, 4),
            new LineEvent(350, 2, 0, 5, 5)
        );

        var result = new AtomicReference<>(new ArrayList<LineEvent>());
        var processing = new RateLimitingDebounceProcessing(51, eventList -> result.updateAndGet(list -> {
            list.addAll(eventList);
            return list;
        }));

        processing.process(events1);
        processing.process(events2);

        var processedEvents = result.get();

        // We expect 3 events to be processed: 2 from the first batch and 1 from the second batch
        assertEquals(3, processedEvents.size());
        // got 2 from the first batch
        assertEquals(events1.get(0), processedEvents.get(0));
        assertEquals(events1.get(2), processedEvents.get(1));
        // got 1 from the second batch
        assertEquals(events2.get(1), processedEvents.get(2));
    }

}