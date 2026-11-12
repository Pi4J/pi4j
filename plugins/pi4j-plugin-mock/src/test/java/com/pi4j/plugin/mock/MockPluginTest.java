package com.pi4j.plugin.mock;

import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.spi.SpiConfigBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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

    /**
     * Demonstrates how contravariance (Consumer<? super T>) allows a single shared Consumer<IO<?>>
     * or Consumer<Object> to be passed to various distinct mock constructors without boilerplate adapters or casts.
     */
    @Test
    void contravarianceAllowsSharedHandlerAcrossDifferentMockTypes() {
        var closedDevices = new ArrayList<IO<?>>();
        var mockContext = new MockContext(closedDevices::add);

        var digitalInput = mockContext.create(DigitalInputConfigBuilder.newInstance().id("din").bcm(1).build());
        var digitalOutput = mockContext.create(DigitalOutputConfigBuilder.newInstance().id("dout").bcm(2).build());
        var parallelPort = mockContext.create(ParallelPortConfigBuilder.newInstance().id("pp").bcm(3).bcm(4).build());
        var pwm = mockContext.create(PwmConfigBuilder.newInstance().id("pwm").chip(0).channel(0).build());
        var i2c = mockContext.create(I2CConfigBuilder.newInstance().id("i2c").bus(1).device(0x10).build());
        var spi = mockContext.create(SpiConfigBuilder.newInstance().id("spi").bus(0).channel(0).build());

        digitalInput.close();
        digitalOutput.close();
        parallelPort.close();
        pwm.close();
        i2c.close();
        spi.close();

        assertEquals(6, closedDevices.size());
        assertTrue(closedDevices.contains(digitalInput));
        assertTrue(closedDevices.contains(digitalOutput));
        assertTrue(closedDevices.contains(parallelPort));
        assertTrue(closedDevices.contains(pwm));
        assertTrue(closedDevices.contains(i2c));
        assertTrue(closedDevices.contains(spi));
    }
}