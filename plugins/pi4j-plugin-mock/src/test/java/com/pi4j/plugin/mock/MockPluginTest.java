package com.pi4j.plugin.mock;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.spi.SpiConfigBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockPluginTest {
    private final Context pi4j = new MockContext();

    @Test
    void canRecreateOutput() {
        var config = DigitalOutputConfigBuilder.newInstance()
            .bcm(1)
            .build();

        var device = pi4j.create(config);
        assertNotNull(device);

        pi4j.shutdown(device.id());
        assertFalse(pi4j.registry().exists(device.id()));

        device = pi4j.create(config);
        assertTrue(pi4j.registry().exists(device.id()));
    }

    @Test
    void canRecreatePwmDevice() {
        var config = PwmConfigBuilder.newInstance(pi4j)
            .chip(0)
            .channel(0)
            .build();

        var device = pi4j.create(config);
        assertNotNull(device);

        pi4j.shutdown(device.id());
        assertFalse(pi4j.registry().exists(device.id()));

        device = pi4j.create(config);
        assertTrue(pi4j.registry().exists(device.id()));
    }

    @Test
    void canRecreateDevice() {
        var config = SpiConfigBuilder.newInstance()
            .bus(0)
            .channel(0)
            .build();

        var device = pi4j.create(config);
        assertNotNull(device);

        pi4j.shutdown(device.id());
        assertFalse(pi4j.registry().exists(device.id()));

        device = pi4j.create(config);
        assertTrue(pi4j.registry().exists(device.id()));
    }
}