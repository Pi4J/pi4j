package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrailingEdgeDebounceProcessingTest {

    @Test
    void retainsLatestEventUntilPollTimeoutAfterQuietInterval() throws InterruptedException {
        var first = new LineEvent(100, 1, 0, 0, 0);
        var latest = new LineEvent(150, 2, 0, 1, 1);
        var processed = new ArrayList<LineEvent>();
        var processing = new TrailingEdgeDebounceProcessing(20_000_000, processed::addAll);

        processing.process(List.of(first, latest));
        assertTrue(processed.isEmpty());

        Thread.sleep(30);
        processing.process(List.of());
        assertEquals(List.of(latest), processed);
    }

    @Test
    void retainsPendingEventWhenEmptyBatchArrivesBeforeQuietInterval() {
        var latest = new LineEvent(150, 2, 0, 1, 1);
        var processed = new ArrayList<LineEvent>();
        var processing = new TrailingEdgeDebounceProcessing(20_000_000, processed::addAll);

        processing.process(List.of(latest));
        processing.process(List.of());

        assertTrue(processed.isEmpty());
    }

    @Test
    void dispatchesPendingEventWhenNextEventIsOutsideDebounceInterval() {
        var first = new LineEvent(100, 1, 0, 0, 0);
        var next = new LineEvent(200, 2, 0, 1, 1);
        var processed = new ArrayList<LineEvent>();
        var processing = new TrailingEdgeDebounceProcessing(100, processed::addAll);

        processing.process(List.of(first, next));

        assertEquals(List.of(first), processed);
    }

    @Test
    void replacesPendingEventWhenNextEventIsWithinDebounceInterval() {
        var first = new LineEvent(100, 1, 0, 0, 0);
        var latest = new LineEvent(150, 2, 0, 1, 1);
        var processed = new ArrayList<LineEvent>();
        var processing = new TrailingEdgeDebounceProcessing(100, processed::addAll);

        processing.process(List.of(first, latest));

        assertTrue(processed.isEmpty());
    }

    @Test
    void replacesPendingEventAcrossBatchesWithinDebounceInterval() {
        var first = new LineEvent(100, 1, 0, 0, 0);
        var middle = new LineEvent(150, 2, 0, 1, 1);
        var outsideInterval = new LineEvent(250, 1, 0, 2, 2);
        var processed = new ArrayList<LineEvent>();
        var processing = new TrailingEdgeDebounceProcessing(100, processed::addAll);

        processing.process(List.of(first));
        processing.process(List.of(middle));
        assertTrue(processed.isEmpty());

        processing.process(List.of(outsideInterval));
        assertEquals(List.of(middle), processed);
    }

    @Test
    void dispatchesPendingEventAcrossBatchesWhenNextEventIsOutsideDebounceInterval() {
        var first = new LineEvent(100, 1, 0, 0, 0);
        var next = new LineEvent(200, 2, 0, 1, 1);
        var processed = new ArrayList<LineEvent>();
        var processing = new TrailingEdgeDebounceProcessing(100, processed::addAll);

        processing.process(List.of(first));
        assertTrue(processed.isEmpty());

        processing.process(List.of(next));
        assertEquals(List.of(first), processed);
    }
}
