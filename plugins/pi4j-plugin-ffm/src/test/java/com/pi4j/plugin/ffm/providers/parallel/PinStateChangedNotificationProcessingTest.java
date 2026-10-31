package com.pi4j.plugin.ffm.providers.parallel;

import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortBase;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class PinStateChangedNotificationProcessingTest {

    private final ParallelPort noopPort = new NoopPort();

    @Test
    void offsetMappingIsInvoked() {
        AtomicReference<ParallelPort.PinStateChangedNotification> capturedNotification = new AtomicReference<>();
        var processing = new PinStateChangedNotificationProcessing(
            noopPort,
            capturedNotification::set,
            offset -> offset + 1
        );

        processing.process(List.of(new LineEvent(0L, 0, 0, 0, 0)));

        var notification = capturedNotification.get();
        assertNotNull(notification);
        assertEquals(1, notification.events().length);
        assertEquals(1, notification.events()[0].offset());
    }

    @Test
    void flushEventsAreNotEmitted() {
        AtomicReference<ParallelPort.PinStateChangedNotification> capturedNotification = new AtomicReference<>();
        var processing = new PinStateChangedNotificationProcessing(
            noopPort,
            capturedNotification::set,
            IntUnaryOperator.identity()
        );

        processing.process(List.of());

        assertNull(capturedNotification.get());
    }

    @Test
    void eventsAreEmitted() {
        AtomicReference<ParallelPort.PinStateChangedNotification> capturedNotification = new AtomicReference<>();
        var processing = new PinStateChangedNotificationProcessing(
            noopPort,
            capturedNotification::set,
            IntUnaryOperator.identity()
        );

        processing.process(List.of(
            new LineEvent(0L, 1, 2, 3, 4),
            new LineEvent(1L, 2, 2, 4, 5)
        ));

        var notification = capturedNotification.get();
        assertEquals(2, notification.events().length);
        assertFalse(notification.eventsMissing());

        var event0 = notification.events()[0];
        assertEquals(2, event0.offset());
        assertEquals(DigitalState.HIGH, event0.newState());

        var event1 = notification.events()[1];
        assertEquals(2, event1.offset());
        assertEquals(DigitalState.LOW, event1.newState());
    }

    @Test
    void missingEventsAreReported() {
        AtomicReference<ParallelPort.PinStateChangedNotification> capturedNotification = new AtomicReference<>();
        var processing = new PinStateChangedNotificationProcessing(
            noopPort,
            capturedNotification::set,
            IntUnaryOperator.identity()
        );

        processing.process(List.of(
            new LineEvent(0L, 1, 2, 3, 4),
            new LineEvent(1L, 2, 2, 5, 6)
        ));

        var notification = capturedNotification.get();
        assertEquals(2, notification.events().length);
        assertTrue(notification.eventsMissing());

        var event0 = notification.events()[0];
        assertEquals(2, event0.offset());
        assertEquals(DigitalState.HIGH, event0.newState());

        var event1 = notification.events()[1];
        assertEquals(2, event1.offset());
        assertEquals(DigitalState.LOW, event1.newState());
    }

    @Test
    void unexpectedOffsetsThrowException() {
        AtomicReference<ParallelPort.PinStateChangedNotification> capturedNotification = new AtomicReference<>();
        var processing = new PinStateChangedNotificationProcessing(
            noopPort,
            capturedNotification::set,
            IntUnaryOperator.identity()
        );

        assertThrows(Pi4JException.class, () -> processing.process(List.of(
            new LineEvent(0L, 1, 32, 3, 4)
        )));
    }

    /**
     * A no-op implementation of {@link ParallelPortBase} that does not perform any actual I/O operations.
     */
    private static class NoopPort extends ParallelPortBase {

        private NoopPort() {
            super(ParallelPortConfigBuilder.newInstance().bcm(0).build(), _ -> {});
        }

        @Override
        protected void handleWrite(int value) {
            // no-op
        }

        @Override
        protected int handleRead() {
            return 0;
        }
    }
}