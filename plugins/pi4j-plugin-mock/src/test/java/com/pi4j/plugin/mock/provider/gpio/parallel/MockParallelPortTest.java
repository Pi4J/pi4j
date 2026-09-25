package com.pi4j.plugin.mock.provider.gpio.parallel;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import com.pi4j.io.gpio.parallel.ParallelPortProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

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
     * Container for test expectations
     * @param portValue the value on the port
     * @param state0 the state of the first output
     * @param state1 the state of the second output
     */
    record DigitalExpectation(int portValue, DigitalState state0, DigitalState state1) {}
}
