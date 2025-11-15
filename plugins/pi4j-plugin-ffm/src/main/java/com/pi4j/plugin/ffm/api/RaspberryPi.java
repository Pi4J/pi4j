package com.pi4j.plugin.ffm.api;

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.model.HeaderPin;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmPolarity;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.PwmFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.serial.SerialFFMProviderImpl;

// Example interface for device family
public interface RaspberryPi extends Pi4JApi.API {

    // This class is specific to board with default available interfaces and parameters.
    // Holds the pi4j context.
    // Provides variety of methods to help user start fast.
    // Can be extended to read device tree and provide more methods (e.g. i2c-10 for CM4 with alternative functions).
    // Good point to refactor context and all under hood stuff.
    //
    // All parameters and numbers are for reference, I did not check them to safe time for prototyping.
    class Model4B implements RaspberryPi {
        private final Context context;

        // Package private, no external creation.
        Model4B() {
            this.context = Pi4J.newContextBuilder()
                .add(new DigitalOutputFFMProviderImpl(), new DigitalInputFFMProviderImpl(), new SerialFFMProviderImpl(),
                    new I2CFFMProviderImpl(), new PwmFFMProviderImpl())
                .build();
        }

        public I2C i2c1(int device, I2CImplementation implementation) {
            var config = I2CConfig.newBuilder(context)
                .bus(1)
                .device(device)
                .i2cImplementation(implementation)
                .build();
            return context.create(config);
        }

        public I2C i2c1(int device) {
            return i2c1(device, I2CImplementation.DIRECT);
        }

        public Spi spi0(int baudRate, SpiMode mode) {
            var config = SpiConfig.newBuilder(context)
                .bus(0)
                .baud(baudRate)
                .mode(mode)
                .build();
            return context.create(config);
        }

        public Spi spi0(int baudRate) {
            return spi0(baudRate, SpiMode.MODE_1);
        }

        public Spi spi0(SpiMode mode) {
            return spi0(40_000, mode);
        }

        public Spi spi0() {
            return spi0(40_000, SpiMode.MODE_1);
        }

        public Spi spi1() {
            var config = SpiConfig.newBuilder(context)
                .bus(1)
                .baud(40_000)
                .mode(SpiMode.MODE_1)
                .build();
            return context.create(config);
        }

        public Pwm pwm0(PwmPolarity pwmPolarity, int frequency, int dutyCycle) {
            var config = PwmConfigBuilder.newInstance(context)
                .chip(0)
                .channel(0)
                .polarity(pwmPolarity)
                .frequency(frequency)
                .dutyCycle(dutyCycle)
                .build();
            return context.create(config);
        }

        public Pwm pwm0() {
            return pwm0(PwmPolarity.NORMAL, 10_000, 5_000);
        }

        public Pwm pwm1(PwmPolarity pwmPolarity, int frequency, int dutyCycle) {
            var config = PwmConfigBuilder.newInstance(context)
                .chip(0)
                .channel(1)
                .polarity(pwmPolarity)
                .frequency(frequency)
                .dutyCycle(dutyCycle)
                .build();
            return context.create(config);
        }

        public Pwm pwm1() {
            return pwm1(PwmPolarity.NORMAL, 10_000, 5_000);
        }

        public DigitalInput input(int pin) {
            var config = DigitalInputConfig.newBuilder(context)
                .bcm(pin)
                .build();
            return context.create(config);
        }

        public DigitalInput input(HeaderPin pin) {
            return input(pin.getPinNumber());
        }

        public DigitalOutput output(int pin) {
            var config = DigitalOutputConfig.newBuilder(context)
                .bcm(pin)
                .build();
            return context.create(config);
        }

        public DigitalOutput output(HeaderPin pin) {
            return output(pin.getPinNumber());
        }
    }

    // Great thing, as we can extend the same functionality or override it if needed.
    // E.g. cm4 has almost the same config as 4b.
    class Cm4 extends Model4B {
    }
}
