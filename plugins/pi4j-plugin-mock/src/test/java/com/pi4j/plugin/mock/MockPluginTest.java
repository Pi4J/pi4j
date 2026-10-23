package com.pi4j.plugin.mock;

import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutput;
import com.pi4j.plugin.mock.provider.gpio.parallel.MockParallelPort;
import com.pi4j.plugin.mock.provider.i2c.MockI2C;
import com.pi4j.plugin.mock.provider.pwm.MockPwm;
import com.pi4j.plugin.mock.provider.spi.MockSpi;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.Consumer;

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
        Consumer<IO<?>> sharedCloseHandler = closedDevices::add;

        var digitalInput = new MockDigitalInput(
            DigitalInputConfigBuilder.newInstance().bcm(1).build(),
            sharedCloseHandler
        );
        var digitalOutput = new MockDigitalOutput(
            DigitalOutputConfigBuilder.newInstance().bcm(2).build(),
            sharedCloseHandler
        );
        var parallelPort = new MockParallelPort(
            ParallelPortConfigBuilder.newInstance().bcm(3).bcm(4).build(),
            sharedCloseHandler
        );
        var pwm = new MockPwm(
            PwmConfigBuilder.newInstance().chip(0).channel(0).build(),
            sharedCloseHandler
        );
        var i2c = new MockI2C(
            I2CConfigBuilder.newInstance().bus(1).device(0x10).build(),
            sharedCloseHandler
        );
        var spi = new MockSpi(
            SpiConfigBuilder.newInstance().bus(0).channel(0).build(),
            sharedCloseHandler
        );

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