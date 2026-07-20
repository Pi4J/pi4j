package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.plugin.BaseSetup;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
public class GPIOTest extends BaseSetup {
    private static Context pi4j0;
    private static Context pi4j1;
    private static Context pi4jNonExistent;

    @BeforeAll
    public static void setup() throws IOException, InterruptedException {
        setup("gpio");
        pi4j0 = Pi4J.newContextBuilder()
            .add(new FFMDigitalInputProviderImpl(), new FFMDigitalOutputProviderImpl())
            .build();
        pi4j1 = Pi4J.newContextBuilder()
            .add(new FFMDigitalInputProviderImpl())
            .build();
        pi4jNonExistent = Pi4J.newContextBuilder()
            .add(new FFMDigitalInputProviderImpl())
            .build();
    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j0.shutdown();
        pi4j1.shutdown();
        pi4jNonExistent.shutdown();

        tearDown("gpio");
    }

    @Test
    public void testInputUnavailable() {
        assertThrows(Pi4JException.class, () -> pi4j1.digitalInput().create(
            DigitalInputConfigBuilder.newInstance()
                .bus(98)
                .bcm(99)
                .build())
        );
    }

    @Test
    public void testInputNonExistent() {
        assertThrows(Pi4JException.class, () -> pi4jNonExistent.digitalInput().create(
            DigitalInputConfigBuilder.newInstance()
                .bus(99)
                .bcm(0)
                .build())
        );
    }

    @Test
    public void testInputCreate() {
        var input = pi4j0.digitalInput().create(
            DigitalInputConfigBuilder.newInstance()
                .bus(97)
                .bcm(0)
                .build()
        );
        assertEquals(0, input.bcm());
    }

    @Test
    public void testInputState() {
        var input = pi4j0.digitalInput().create(
            DigitalInputConfigBuilder.newInstance()
                .bus(97)
                .bcm(1)
                .build()
        );
        assertEquals(DigitalState.LOW, input.state());
    }

    @Test
    public void testInputIsOccupied() {
        assertThrows(IllegalStateException.class, () -> pi4j0.digitalInput().create(
            DigitalInputConfigBuilder.newInstance()
                .bus(97)
                .bcm(2)
                .build()
        ));
    }

    @Test
    public void testInputCustomConfig() {
        var config = DigitalInputConfigBuilder.newInstance()
            .bus(97)
            .bcm(3)
            .debounce(99L, TimeUnit.MICROSECONDS)
            .pull(PullResistance.PULL_DOWN)
            .build();
        var input = pi4j0.digitalInput().create(config);
        assertEquals(99, input.config().debounce());
        assertEquals(3, input.bcm());
        assertEquals(PullResistance.PULL_DOWN, input.pull());
    }

    @Test
    public void testOutputCreate() {
        var output = pi4j0.digitalOutput().create(
            DigitalOutputConfigBuilder.newInstance()
                .bus(97)
                .bcm(4)
                .build()
        );
        assertEquals(4, output.bcm());
    }

    @Test
    public void testOutputChangeState() {
        var pin = pi4j0.digitalOutput().create(
            DigitalOutputConfigBuilder.newInstance()
                .bus(97)
                .bcm(5)
                .build()
        );
        pin.state(DigitalState.HIGH);
        assertEquals(DigitalState.HIGH, pin.state());
        pin.state(DigitalState.LOW);
        assertEquals(DigitalState.LOW, pin.state());
    }

    @Test
    public void testOutputCustomConfig() {
        var config = DigitalOutputConfigBuilder.newInstance()
            .bus(97)
            .bcm(6)
            .initial(DigitalState.HIGH)
            .build();
        var output = pi4j0.digitalOutput().create(config);
        assertEquals(DigitalState.HIGH, output.config().initialState());
        assertEquals(6, output.bcm());
    }


    @Test
    public void testOutputInitialStateHigh() {
        // Requesting an output with initial state HIGH must succeed through the real character
        // device ABI: the kernel validates the output-values attribute we attach to the line
        // request and rejects a malformed one with EINVAL. A successful creation therefore proves
        // the initial state is passed to the kernel at request time (issue #654).
        var config = DigitalOutputConfigBuilder.newInstance()
            .bus(97)
            .bcm(7)
            .initial(DigitalState.HIGH)
            .build();
        var output = pi4j0.digitalOutput().create(config);
        assertEquals(7, output.bcm());
        assertEquals(DigitalState.HIGH, output.config().initialState());
        assertEquals(DigitalState.HIGH, output.state());
    }


    @Test
    public void testGpioReconfigure() {
        var config = DigitalOutputConfigBuilder.newInstance()
            .bus(97)
            .bcm(8)
            .initial(DigitalState.HIGH)
            .build();
        var output = pi4j0.digitalOutput().create(config);
        assertEquals(DigitalState.HIGH, output.config().initialState());
        assertEquals(8, output.bcm());

        output.state(DigitalState.LOW);

        var input = output.reconfigure().digitalInput().create();
        assertEquals(DigitalState.LOW, input.state());
        assertEquals(8, output.bcm());
    }
}
