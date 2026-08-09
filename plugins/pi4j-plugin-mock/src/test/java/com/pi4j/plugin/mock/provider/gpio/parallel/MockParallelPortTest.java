package com.pi4j.plugin.mock.provider.gpio.parallel;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import com.pi4j.io.gpio.parallel.ParallelPortProvider;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.pi4j.io.gpio.digital.DigitalState.HIGH;
import static com.pi4j.io.gpio.digital.DigitalState.LOW;
import static org.junit.jupiter.api.Assertions.*;

class MockParallelPortTest {

    private final ParallelPortProvider provider = new MockParallelPortProvider();

    private final ParallelPortConfigBuilder configBuilder = ParallelPortConfigBuilder.newInstance()
        .id("parallel-port")
        .bcm(1)
        .bcm(3);

    private final List<DigitalExpectation> digitalExpectations = List.of(
        new DigitalExpectation(0, LOW, LOW),
        new DigitalExpectation(1, HIGH, LOW),
        new DigitalExpectation(2, LOW, HIGH),
        new DigitalExpectation(3, HIGH, HIGH)
    );

    /**
     * Write a value to the port and assert that the value is reflected
     */
    @Test
    void canWriteToPort() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = context.create(configBuilder.initialDirection(ParallelPort.Direction.OUTPUT).build());

        port.write(0b10);
        assertEquals(0b10, port.read());
    }

    /**
     * We may want to fail writes to ports which are currently configured as inputs
     */
    @Test
    void cannotWriteToInputPort() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = context.create(configBuilder.initialDirection(ParallelPort.Direction.INPUT).build());

        assertThrows(IllegalStateException.class, () -> port.write(0b10));
    }

    /**
     * We may want to fail writes to ports which are outside the port mask and cannot be represented by the port
     */
    @Test
    void cannotWriteOutsideThePortMask() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = context.create(configBuilder.initialDirection(ParallelPort.Direction.OUTPUT).build());

        assertThrows(IllegalArgumentException.class, () -> port.write(0b1000));
    }

    /**
     * Port direction can be changed to allow writes to a port, which might be initially configured as an input
     */
    @Test
    void canChangePortDirection() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = context.create(configBuilder.initialDirection(ParallelPort.Direction.INPUT).build());

        assertThrows(IllegalStateException.class, () -> port.write(0b10));

        port.setDirection(ParallelPort.Direction.OUTPUT);
        port.write(0b10);
        assertEquals(0b10, port.read());
    }

    /**
     * Write a value to the port and assert that it is represented by the individual digital states
     */
    @TestFactory
    Stream<DynamicTest> canReadDigitalValues() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = context.create(configBuilder.build());
        var inputProvider = port.digitalInputProvider();
        var input0 = inputProvider.create(0);
        var input1 = inputProvider.create(1);

        return digitalExpectations.stream()
            .map(expectation -> DynamicTest.dynamicTest(expectation.toString(), () -> {
                ((MockParallelPort)port).mockValue(expectation.portValue);
                assertEquals(expectation.portValue, port.read());
                assertEquals(expectation.state0, input0.state());
                assertEquals(expectation.state1, input1.state());
            }));
    }

    /**
     * Write digital states and validate the value on the port
     */
    @TestFactory
    Stream<DynamicTest> writeOperationsAffectPort() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = (MockParallelPort)context.create(configBuilder.initialDirection(ParallelPort.Direction.OUTPUT).build());
        var outputProvider = port.digitalOutputProvider();
        var output0 = outputProvider.create(0);
        var output1 = outputProvider.create(1);

        return digitalExpectations.stream()
            .map(expectation -> DynamicTest.dynamicTest(expectation.toString(), () -> {
                output0.state(expectation.state0);
                assertEquals(expectation.state0, output0.state());
                output1.state(expectation.state1);
                assertEquals(expectation.state1, output1.state());
                assertEquals(expectation.portValue, port.read());
            }));
    }

    /**
     * Verify that digital inputs receive events when the port value changes
     */
    @Test
    void portTriggersDigitalEvents() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = (MockParallelPort)context.create(configBuilder.build());
        var inputProvider = port.digitalInputProvider();
        var eventCount = new AtomicInteger(0);
        var input = inputProvider.create(0).addListener(_ -> eventCount.incrementAndGet());

        port.mockValue(1);
        assertEquals(1, eventCount.get());
        assertEquals(HIGH, input.state());

        port.mockValue(0);
        assertEquals(2, eventCount.get());
        assertEquals(LOW, input.state());
    }

    /**
     * Verify that listeners can be removed from digital inputs
     */
    @Test
    void canRemoveListeners() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = (MockParallelPort)context.create(configBuilder.build());
        var inputProvider = port.digitalInputProvider();
        var eventCount = new AtomicInteger(0);

        DigitalStateChangeListener listener = _ -> eventCount.incrementAndGet();
        var input = inputProvider.create(0).addListener(listener);

        port.mockValue(1);
        assertEquals(1, eventCount.get());
        assertEquals(HIGH, input.state());

        port.mockValue(0);
        assertEquals(2, eventCount.get());
        assertEquals(LOW, input.state());

        input.removeListener(listener);
        port.mockValue(1);
        assertEquals(2, eventCount.get());
        assertEquals(HIGH, input.state());
    }

    /**
     * Verify that a listener can be removed from a digital device and that listeners on other devices are unaffected.
     */
    @Test
    void canRemoveIndividualListers() {
        var context = Pi4J.newContextBuilder().add(provider).build();
        var port = (MockParallelPort)context.create(configBuilder.build());
        var inputProvider = port.digitalInputProvider();

        var input0 = inputProvider.create(0);
        var eventCount0 = new AtomicInteger(0);
        DigitalStateChangeListener listener0 = _ -> eventCount0.incrementAndGet();
        input0.addListener(listener0);

        var input1 = inputProvider.create(1);
        var eventCount1 = new AtomicInteger(0);
        DigitalStateChangeListener listener1 = _ -> eventCount1.incrementAndGet();
        input1.addListener(listener1);

        port.mockValue(1);
        assertEquals(1, eventCount0.get());
        assertEquals(1, eventCount1.get());

        input0.removeListener(listener0);
        port.mockValue(0);
        assertEquals(1, eventCount0.get());
        assertEquals(2, eventCount1.get());
    }

    /**
     * Container for test expectations
     * @param portValue the value on the port
     * @param state0 the state of the first output
     * @param state1 the state of the second output
     */
    record DigitalExpectation(int portValue, DigitalState state0, DigitalState state1) {}
}