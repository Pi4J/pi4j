package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import com.pi4j.plugin.ffm.providers.parallel.FFMParallelPortProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * There's not a huge amount new to test here which hasn't already been tested in some way by
 * {@link GPIOTest}. This file is probably pretty pointless.
 * <p>
 * This should be replaced with a relevant smoke test
 */
@Disabled("not really a test, more of an example")
public class ParallelPortTest {

    @Test
    void testOutput() {
        var context = Pi4J.newContextBuilder()
            .add(new FFMParallelPortProvider())
            .build();

        var device = context.create(ParallelPortConfigBuilder.newInstance()
            .id("test-gpio")
            .initialDirection(ParallelPort.Direction.OUTPUT)
            .bcm(20)
            .bcm(21)
            .build()
        );


        device.write(0);
        device.write(1);
        device.write(2);
        device.write(3);
        device.write(0);

        context.shutdown();
    }

    /**
     * This test assumes that output pins 20 and 21 are jumpered to input pins 12 and 16.
     * A value is output on 20, 21 and read back on 12, 16
     */
    @Test
    void testInput() {
        var context = Pi4J.newContextBuilder()
            .add(new FFMParallelPortProvider())
            .build();

        var outputPort = context.create(ParallelPortConfigBuilder.newInstance()
            .id("test-output")
            .initialDirection(ParallelPort.Direction.OUTPUT)
            .bcm(20)
            .bcm(21)
            .build()
        );

        var inputPort = context.create(ParallelPortConfigBuilder.newInstance()
            .id("test-input")
            .initialDirection(ParallelPort.Direction.INPUT)
            .bcm(12)
            .bcm(16)
            .build()
        );

        Consumer<Integer> echoTest = value -> {
            outputPort.write(value);
            assertEquals(value, inputPort.read());
        };

        echoTest.accept(0);
        echoTest.accept(1);
        echoTest.accept(2);
        echoTest.accept(3);
        echoTest.accept(0);

        context.shutdown();
    }

    /**
     * This test assumes that output pins 20 and 21 are jumpered to input pins 12 and 16.
     * A value is output on 20, 21 and read back on 12, 16
     */
    @Test
    void directionExample() {
        var context = Pi4J.newContextBuilder()
            .add(new FFMParallelPortProvider())
            .build();

        var outputPort = context.create(ParallelPortConfigBuilder.newInstance()
            .id("test-output")
            .initialDirection(ParallelPort.Direction.OUTPUT)
            .bcm(20)
            .bcm(21)
            .build()
        );

        var inputPort = context.create(ParallelPortConfigBuilder.newInstance()
            .id("test-input")
            .initialDirection(ParallelPort.Direction.INPUT)
            .bcm(12)
            .bcm(16)
            .build()
        );

        Consumer<Integer> echoTest = value -> {
            outputPort.write(value);
            assertEquals(value, inputPort.read());
        };

        echoTest.accept(0);
        echoTest.accept(1);
        echoTest.accept(2);
        echoTest.accept(3);
        echoTest.accept(0);

        // switch port directions and test
        outputPort.setDirection(ParallelPort.Direction.INPUT);
        inputPort.setDirection(ParallelPort.Direction.OUTPUT);

        inputPort.write(1);
        assertEquals(1, outputPort.read());

        // set both ports to input before shutdown to avoid possible contention in other tests
        inputPort.setDirection(ParallelPort.Direction.INPUT);

        context.shutdown();
    }
}
