package com.pi4j.test.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.MockContext;
import com.pi4j.plugin.mock.provider.gpio.parallel.MockParallelPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class DigitalInputOffTest {

    private Context pi4j;

    @BeforeEach
    public void beforeTest() throws Pi4JException {
        pi4j = new MockContext();
    }

    @AfterEach
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testIsOffDefault() {

        // create GPIO digital input config
        var config = DigitalInputConfigBuilder.newInstance()
            .id("test-input")
            .name("Test Digital Input")
            .bcm(1)
            .build();

        // create GPIO digital input instance
        var input = pi4j.create(config);

        // set MOCK state to LOW
        MockParallelPort.mockDigitalInputValue(input, 0);

        // ensure input is OFF and not ON
        assertTrue(input.isOff());
        assertFalse(input.isOn());
    }

    @Test
    public void testIsOffHigh() {

        // create GPIO digital input config
        var config = DigitalInputConfigBuilder.newInstance()
            .id("test-input")
            .name("Test Digital Input")
            .onState(DigitalState.HIGH)
            .bcm(1)
            .build();

        // create GPIO digital input instance
        var input = pi4j.create(config);

        // set MOCK state to LOW
        MockParallelPort.mockDigitalInputValue(input, 0);

        // ensure input is OFF and not ON
        assertTrue(input.isOff());
        assertFalse(input.isOn());
    }

    @Test
    public void testIsOffLow() {

        // create GPIO digital input config
        var config = DigitalInputConfigBuilder.newInstance()
            .id("test-input")
            .name("Test Digital Input")
            .onState(DigitalState.LOW)
            .bcm(1)
            .build();

        // create GPIO digital input instance
        var input = pi4j.create(config);

        // set MOCK state to HIGH
        MockParallelPort.mockDigitalInputValue(input, 1);

        // ensure input is OFF and not ON
        assertTrue(input.isOff());
        assertFalse(input.isOn());
    }
}