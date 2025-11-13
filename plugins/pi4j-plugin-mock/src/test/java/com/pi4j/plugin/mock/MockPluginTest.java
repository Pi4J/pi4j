package com.pi4j.plugin.mock;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProviderImpl;
import com.pi4j.plugin.mock.provider.pwm.MockPwmProviderImpl;
import com.pi4j.plugin.mock.provider.spi.MockSpiProviderImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockPluginTest {
    private final Context pi4j = Pi4J.newContextBuilder()
        .add(new MockPwmProviderImpl())
        .add(new MockSpiProviderImpl())
        .add(new MockDigitalOutputProviderImpl())
        .build();

    @Test
    void canRecreateOutput() {
        var config = DigitalOutputConfigBuilder.newInstance(pi4j)
            .bcm(1)
            .build();

        var device = pi4j.create(config);
        assertNotNull(device);

        pi4j.registry().remove(device.id());
        assertFalse(pi4j.registry().exists(device.id()));

        device = pi4j.create(config);
        assertTrue(pi4j.registry().exists(device.id()));
    }

    @Test
    void canRecreatePwmDevice() {
        var config = PwmConfigBuilder.newInstance(pi4j)
            .bus(0)
            .channel(0)
            .build();

        var device = pi4j.pwm().create(config);
        assertNotNull(device);

        pi4j.registry().remove(device.id());
        assertFalse(pi4j.registry().exists(device.id()));

        device = pi4j.create(config);
        assertTrue(pi4j.registry().exists(device.id()));
    }

    @Test
    void canRecreateDevice() {
        var config = SpiConfigBuilder.newInstance(pi4j)
            .bus(0)
            .channel(0)
            .build();

        var device = pi4j.create(config);
        assertNotNull(device);

        pi4j.registry().remove(device.id());
        assertFalse(pi4j.registry().exists(device.id()));

        device = pi4j.create(config);
        assertTrue(pi4j.registry().exists(device.id()));
    }
}