package com.pi4j.plugin.mock;

import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.spi.Spi;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MockPluginTest {
    private final Context pi4j = new MockContext();

    @Test
    void canRecreateOutput() {
        var config = DigitalOutput.newConfigBuilder()
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
        var config = Pwm.newConfigBuilder()
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
        var config = Spi.newConfigBuilder()
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

        var digitalInput = mockContext.create(DigitalInput.newConfigBuilder().id("din").bcm(1).build());
        var digitalOutput = mockContext.create(DigitalOutput.newConfigBuilder().id("dout").bcm(2).build());
        var parallelPort = mockContext.create(ParallelPort.newConfigBuilder().id("pp").bcm(3).bcm(4).build());
        var pwm = mockContext.create(Pwm.newConfigBuilder().id("pwm").chip(0).channel(0).build());
        var i2c = mockContext.create(I2C.newConfigBuilder().id("i2c").bus(1).device(0x10).build());
        var spi = mockContext.create(Spi.newConfigBuilder().id("spi").bus(0).channel(0).build());

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