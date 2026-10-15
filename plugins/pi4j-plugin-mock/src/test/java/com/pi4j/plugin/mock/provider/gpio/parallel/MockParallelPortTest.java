package com.pi4j.plugin.mock.provider.gpio.parallel;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import com.pi4j.plugin.mock.MockContext;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MockParallelPortTest {

    private final ParallelPortConfigBuilder configBuilder = ParallelPortConfigBuilder.newInstance()
        .id("parallel-port")
        .bcm(1)
        .bcm(3);

    /**
     * Write a value to the port and assert that the value is reflected
     */
    @Test
    void canWriteToPort() {
        var context = new MockContext();
        var port = context.create(configBuilder.initialDirection(ParallelPort.Direction.OUTPUT).build());

        port.write(0b10);
        assertEquals(0b10, port.read());
    }

    /**
     * We may want to fail writes to ports which are currently configured as inputs
     */
    @Test
    void cannotWriteToInputPort() {
        var context = new MockContext();
        var port = context.create(configBuilder.initialDirection(ParallelPort.Direction.INPUT).build());

        assertThrows(IllegalStateException.class, () -> port.write(0b10));
    }

    /**
     * We may want to fail writes to ports which are outside the port mask and cannot be represented by the port
     */
    @Test
    void cannotWriteOutsideThePortMask() {
        var context = new MockContext();
        var port = context.create(configBuilder.initialDirection(ParallelPort.Direction.OUTPUT).build());

        assertThrows(IllegalArgumentException.class, () -> port.write(0b1000));
    }

    /**
     * Port direction can be changed to allow writes to a port, which might be initially configured as an input
     */
    @Test
    void canChangePortDirection() {
        var context = new MockContext();
        var port = context.create(configBuilder.initialDirection(ParallelPort.Direction.INPUT).build());

        assertThrows(IllegalStateException.class, () -> port.write(0b10));

        port.setDirection(ParallelPort.Direction.OUTPUT);
        port.write(0b10);
        assertEquals(0b10, port.read());
    }

    @Test
    void generatesMockValueEvents() {
        var context = new MockContext();
        var port = (MockParallelPort) context.create(configBuilder.initialDirection(ParallelPort.Direction.INPUT).build());
        var events = new ArrayList<ParallelPort.PinStateChangedEvent>();

        port.addListener(notification -> events.addAll(Arrays.asList(notification.events())));

        port.mockValue(1);
        assertTrue(events.contains(new ParallelPort.PinStateChangedEvent(0, DigitalState.HIGH)));

        events.clear();

        port.mockValue(2);
        assertTrue(events.contains(new ParallelPort.PinStateChangedEvent(0, DigitalState.LOW)));
        assertTrue(events.contains(new ParallelPort.PinStateChangedEvent(1, DigitalState.HIGH)));
    }

    @Test
    void doesNotRaiseEventsForOutputPorts() {
        var context = new MockContext();
        var port = (MockParallelPort) context.create(configBuilder.initialDirection(ParallelPort.Direction.OUTPUT).build());
        var events = new ArrayList<ParallelPort.PinStateChangedEvent>();
        port.addListener(notification -> events.addAll(Arrays.asList(notification.events())));
        port.mockValue(1);
        assertTrue(events.isEmpty());
    }

    @Test
    void noEventsAreRaisedWhenValueIsUnchanged() {
        var context = new MockContext();
        var port = (MockParallelPort) context.create(configBuilder.initialDirection(ParallelPort.Direction.INPUT).build());
        var events = new ArrayList<ParallelPort.PinStateChangedEvent>();
        port.addListener(notification -> events.addAll(Arrays.asList(notification.events())));

        port.mockValue(0);
        assertTrue(events.isEmpty());

        port.mockValue(1);
        assertFalse(events.isEmpty());
    }

    @TestFactory
    Stream<DynamicTest> canGenerateChangeEventsFromDiff() {
        record Expectation(int value, int previous, List<ParallelPort.PinStateChangedEvent> expected) {}

        return Stream.of(
            new Expectation(0b1010, 0b1111, List.of(
                new ParallelPort.PinStateChangedEvent(0, DigitalState.LOW),
                new ParallelPort.PinStateChangedEvent(2, DigitalState.LOW)
            )),
            new Expectation(0b1111, 0b1010, List.of(
                new ParallelPort.PinStateChangedEvent(0, DigitalState.HIGH),
                new ParallelPort.PinStateChangedEvent(2, DigitalState.HIGH)
            )),
            new Expectation(0b1010, 0b0101, List.of(
                new ParallelPort.PinStateChangedEvent(0, DigitalState.LOW),
                new ParallelPort.PinStateChangedEvent(1, DigitalState.HIGH),
                new ParallelPort.PinStateChangedEvent(2, DigitalState.LOW),
                new ParallelPort.PinStateChangedEvent(3, DigitalState.HIGH)
            ))
        ).map(expectation -> DynamicTest.dynamicTest(expectation.previous() + " -> " + expectation.value(), () -> {
            var notification = MockParallelPort.eventsForChange(null, expectation.value(), expectation.previous());
            var expected = expectation.expected();

            assertEquals(expected.size(), notification.events().length);
            assertTrue(Arrays.asList(notification.events()).containsAll(expected));
        }));
    }
}
