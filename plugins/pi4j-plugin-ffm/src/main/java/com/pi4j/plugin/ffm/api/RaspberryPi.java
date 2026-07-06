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
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;

/**
 * Board-family facade grouping the Raspberry Pi board models supported by the FFM plugin. Concrete
 * nested types such as {@link Model4B} expose the I2C, SPI, PWM and GPIO interfaces available on a
 * given board with sensible defaults, backed by the FFM provider implementations that talk to the
 * Linux kernel directly. Obtain an instance via {@link Pi4JApi#board(Class)}.
 */
public interface RaspberryPi extends Pi4JApi.API {

    /**
     * Facade for the Raspberry Pi 4 Model B. Builds and holds a {@link Context} populated with the
     * FFM GPIO, I2C and PWM provider implementations, and exposes the interfaces present on this
     * board (i2c1, spi0, spi1, pwm0, pwm1 plus generic GPIO input/output) with default settings so
     * callers can start quickly without assembling configurations by hand.
     */
    class Model4B implements RaspberryPi {
        private final Context context;

        /**
         * Creates the facade and builds a {@link Context} registering the FFM digital output,
         * digital input, I2C and PWM providers. Package-private so instances are obtained only
         * through {@link Pi4JApi#board(Class)}.
         */
        Model4B() {
            this.context = Pi4J.newContextBuilder()
                .add(
                    new FFMDigitalOutputProviderImpl(),
                    new FFMDigitalInputProviderImpl(),
                    new FFMI2CProviderImpl(),
                    new FFMPwmProviderImpl()
                )
                .build();
        }

        /**
         * Opens an {@link I2C} device on I2C bus 1 at the given 7-bit slave address using the
         * specified low-level implementation strategy.
         *
         * @param device         the 7-bit I2C slave address of the target device
         * @param implementation the access mode, for example {@link I2CImplementation#DIRECT}
         * @return a new {@link I2C} handle bound to bus 1 and the given address
         */
        public I2C i2c1(int device, I2CImplementation implementation) {
            var config = I2CConfig.newBuilder()
                .bus(1)
                .device(device)
                .i2cImplementation(implementation)
                .build();
            return context.create(config);
        }

        /**
         * Opens an {@link I2C} device on bus 1 at the given 7-bit slave address using the
         * {@link I2CImplementation#DIRECT} access mode.
         *
         * @param device the 7-bit I2C slave address of the target device
         * @return a new {@link I2C} handle bound to bus 1 and the given address
         */
        public I2C i2c1(int device) {
            return i2c1(device, I2CImplementation.DIRECT);
        }

        /**
         * Opens an {@link Spi} device on SPI bus 0 with the given clock speed and transfer mode.
         *
         * @param baudRate the SPI clock frequency in hertz
         * @param mode     the SPI clock polarity/phase mode, for example {@link SpiMode#MODE_1}
         * @return a new {@link Spi} handle bound to bus 0
         */
        public Spi spi0(int baudRate, SpiMode mode) {
            var config = SpiConfig.newBuilder()
                .bus(0)
                .baud(baudRate)
                .mode(mode)
                .build();
            return context.create(config);
        }

        /**
         * Opens an {@link Spi} device on bus 0 with the given clock speed and the default
         * {@link SpiMode#MODE_1} transfer mode.
         *
         * @param baudRate the SPI clock frequency in hertz
         * @return a new {@link Spi} handle bound to bus 0
         */
        public Spi spi0(int baudRate) {
            return spi0(baudRate, SpiMode.MODE_1);
        }

        /**
         * Opens an {@link Spi} device on bus 0 with the given transfer mode and a default clock of
         * 40&nbsp;000&nbsp;Hz.
         *
         * @param mode the SPI clock polarity/phase mode
         * @return a new {@link Spi} handle bound to bus 0
         */
        public Spi spi0(SpiMode mode) {
            return spi0(40_000, mode);
        }

        /**
         * Opens an {@link Spi} device on bus 0 with default settings: a 40&nbsp;000&nbsp;Hz clock
         * and {@link SpiMode#MODE_1}.
         *
         * @return a new {@link Spi} handle bound to bus 0
         */
        public Spi spi0() {
            return spi0(40_000, SpiMode.MODE_1);
        }

        /**
         * Opens an {@link Spi} device on SPI bus 1 with default settings: a 40&nbsp;000&nbsp;Hz
         * clock and {@link SpiMode#MODE_1}.
         *
         * @return a new {@link Spi} handle bound to bus 1
         */
        public Spi spi1() {
            var config = SpiConfig.newBuilder()
                .bus(1)
                .baud(40_000)
                .mode(SpiMode.MODE_1)
                .build();
            return context.create(config);
        }

        /**
         * Opens a {@link Pwm} channel on PWM chip 0, channel 0 with the given polarity, frequency
         * and duty cycle.
         *
         * @param pwmPolarity the output polarity, for example {@link PwmPolarity#NORMAL}
         * @param frequency   the PWM frequency in hertz
         * @param dutyCycle   the duty cycle applied to the channel
         * @return a new {@link Pwm} handle for chip 0, channel 0
         */
        public Pwm pwm0(PwmPolarity pwmPolarity, int frequency, double dutyCycle) {
            var config = PwmConfigBuilder.newInstance(context)
                .chip(0)
                .channel(0)
                .polarity(pwmPolarity)
                .frequency(frequency)
                .dutyCycle(dutyCycle)
                .build();
            return context.create(config);
        }

        /**
         * Opens a {@link Pwm} channel on PWM chip 0, channel 0 with default settings:
         * {@link PwmPolarity#NORMAL} polarity, a 10&nbsp;000&nbsp;Hz frequency and a duty cycle of
         * 5000.
         *
         * @return a new {@link Pwm} handle for chip 0, channel 0
         */
        public Pwm pwm0() {
            return pwm0(PwmPolarity.NORMAL, 10_000, 5_000);
        }

        /**
         * Opens a {@link Pwm} channel on PWM chip 0, channel 1 with the given polarity, frequency
         * and duty cycle.
         *
         * @param pwmPolarity the output polarity, for example {@link PwmPolarity#NORMAL}
         * @param frequency   the PWM frequency in hertz
         * @param dutyCycle   the duty cycle applied to the channel
         * @return a new {@link Pwm} handle for chip 0, channel 1
         */
        public Pwm pwm1(PwmPolarity pwmPolarity, int frequency, double dutyCycle) {
            var config = PwmConfigBuilder.newInstance(context)
                .chip(0)
                .channel(1)
                .polarity(pwmPolarity)
                .frequency(frequency)
                .dutyCycle(dutyCycle)
                .build();
            return context.create(config);
        }

        /**
         * Opens a {@link Pwm} channel on PWM chip 0, channel 1 with default settings:
         * {@link PwmPolarity#NORMAL} polarity, a 10&nbsp;000&nbsp;Hz frequency and a duty cycle of
         * 5000.
         *
         * @return a new {@link Pwm} handle for chip 0, channel 1
         */
        public Pwm pwm1() {
            return pwm1(PwmPolarity.NORMAL, 10_000, 5_000);
        }

        /**
         * Opens a {@link DigitalInput} on the GPIO line identified by the given BCM pin number.
         *
         * @param pin the Broadcom (BCM) GPIO line number
         * @return a new {@link DigitalInput} bound to the given line
         */
        public DigitalInput input(int pin) {
            var config = DigitalInputConfig.newBuilder()
                .bcm(pin)
                .build();
            return context.create(config);
        }

        /**
         * Opens a {@link DigitalInput} on the GPIO line of the given header pin, using the pin
         * number reported by {@link HeaderPin#getPinNumber()}.
         *
         * @param pin the header pin to use
         * @return a new {@link DigitalInput} bound to that line
         */
        public DigitalInput input(HeaderPin pin) {
            return input(pin.getPinNumber());
        }

        /**
         * Opens a {@link DigitalOutput} on the GPIO line identified by the given BCM pin number.
         *
         * @param pin the Broadcom (BCM) GPIO line number
         * @return a new {@link DigitalOutput} bound to the given line
         */
        public DigitalOutput output(int pin) {
            var config = DigitalOutputConfig.newBuilder()
                .bcm(pin)
                .build();
            return context.create(config);
        }

        /**
         * Opens a {@link DigitalOutput} on the GPIO line of the given header pin, using the pin
         * number reported by {@link HeaderPin#getPinNumber()}.
         *
         * @param pin the header pin to use
         * @return a new {@link DigitalOutput} bound to that line
         */
        public DigitalOutput output(HeaderPin pin) {
            return output(pin.getPinNumber());
        }
    }

    /**
     * Facade for the Raspberry Pi Compute Module 4, which shares the same default interface layout
     * as {@link Model4B} and therefore reuses its configuration unchanged.
     */
    class Cm4 extends Model4B {
    }
}
