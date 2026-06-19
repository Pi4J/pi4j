package com.pi4j.io;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOType.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.provider.Provider;

import java.lang.reflect.Method;

/**
 * Enumeration of the I/O categories supported by Pi4J.
 * <p>
 * Each constant binds together the four classes that make up one kind of I/O: its
 * {@link Provider}, its {@link IO} interface, its {@link IOConfig} and its {@link IOConfigBuilder}.
 * It is used throughout the runtime to resolve providers, build configurations and classify
 * {@link IO} instances by their concrete type.
 */
public enum IOType {

    /** Digital input pin (reads a logic HIGH/LOW state). */
    DIGITAL_INPUT(DigitalInputProvider.class, DigitalInput.class, DigitalInputConfig.class, DigitalInputConfigBuilder.class),
    /** Digital output pin (drives a logic HIGH/LOW state). */
    DIGITAL_OUTPUT(DigitalOutputProvider.class, DigitalOutput.class, DigitalOutputConfig.class, DigitalOutputConfigBuilder.class),
    /** Pulse-width modulation output. */
    PWM(PwmProvider.class, Pwm.class, PwmConfig.class, PwmConfigBuilder.class),
    /** I2C (Inter-Integrated Circuit) bus device. */
    I2C(I2CProvider.class, com.pi4j.io.i2c.I2C.class, I2CConfig.class, I2CConfigBuilder.class),
    /** SPI (Serial Peripheral Interface) bus device. */
    SPI(SpiProvider.class, Spi.class, I2CConfig.class, I2CConfigBuilder.class);

    private Class<? extends Provider> providerClass;
    private Class<? extends IO> ioClass;
    private Class<? extends IOConfig> configClass;
    private Class<? extends IOConfigBuilder> configBuilderClass;

    IOType(Class<? extends Provider> providerClass,
           Class<? extends IO> ioClass,
           Class<? extends IOConfig> configClass,
           Class<? extends IOConfigBuilder> configBuilderClass) {
        this.providerClass = providerClass;
        this.ioClass = ioClass;
        this.configClass = configClass;
        this.configBuilderClass = configBuilderClass;
    }

    /**
     * Returns the {@link Provider} interface class associated with this I/O type.
     *
     * @return the provider class for this type
     */
    public Class<? extends Provider> getProviderClass() {
        return providerClass;
    }

    /**
     * Returns the {@link IO} interface class associated with this I/O type.
     *
     * @return the I/O instance class for this type
     */
    public Class<? extends IO> getIOClass() {
        return ioClass;
    }

    /**
     * Returns the {@link IOConfig} class associated with this I/O type.
     *
     * @return the configuration class for this type
     */
    public Class<? extends IOConfig> getConfigClass() {
        return configClass;
    }

    /**
     * Returns the {@link IOConfigBuilder} class associated with this I/O type.
     *
     * @return the configuration builder class for this type
     */
    public Class<? extends IOConfigBuilder> getConfigBuilderClass() {
        return configBuilderClass;
    }

    /**
     * Creates a new configuration builder for this I/O type by reflectively invoking the static
     * {@code newInstance(Context)} factory on the type's {@link IOConfigBuilder} class.
     *
     * @param context the Pi4J context the builder is associated with
     * @param <CB>    the concrete builder type expected by the caller
     * @return a new, empty configuration builder for this I/O type
     * @throws Pi4JException if the builder class cannot be instantiated
     */
    public <CB extends IOConfigBuilder> CB newConfigBuilder(Context context) {
        try {
            Method newInstance = getConfigBuilderClass().getMethod("newInstance", Context.class);
            return (CB) newInstance.invoke(null, context);
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Tests whether this constant is the same I/O type as the given one.
     *
     * @param type the type to compare against
     * @return {@code true} if {@code type} is this same constant, otherwise {@code false}
     */
    public boolean isType(IOType type) {
        return type == this;
    }

    /**
     * Returns the {@link IO} interface class for the given I/O type.
     *
     * @param type the I/O type to look up
     * @return the I/O instance class, or {@code null} if {@code type} is not recognized
     */
    public static Class<? extends IO> getIOClass(IOType type) {
        for (var typeInstance : IOType.values()) {
            if (typeInstance.equals(type)) {
                return typeInstance.getIOClass();
            }
        }
        return null;
    }

    /**
     * Returns the {@link Provider} class for the given I/O type.
     *
     * @param type the I/O type to look up
     * @return the provider class, or {@code null} if {@code type} is not recognized
     */
    public static Class<? extends Provider> getProviderClass(IOType type) {
        for (var typeInstance : IOType.values()) {
            if (typeInstance.equals(type)) {
                return typeInstance.getProviderClass();
            }
        }
        return null;
    }

    /**
     * Returns the {@link IOConfig} class for the given I/O type.
     *
     * @param type the I/O type to look up
     * @return the configuration class, or {@code null} if {@code type} is not recognized
     */
    public static Class<? extends IOConfig> getConfigClass(IOType type) {
        for (var typeInstance : IOType.values()) {
            if (typeInstance.equals(type)) {
                return typeInstance.getConfigClass();
            }
        }
        return null;
    }

    /**
     * Returns the I/O type whose enum constant name matches the given name (case-insensitive).
     *
     * @param name the constant name to match (e.g. {@code "I2C"})
     * @return the matching I/O type, or {@code null} if no constant name matches
     */
    public static IOType getByProviderClass(String name) {
        for (var type : IOType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Returns the I/O type reported by the given provider.
     *
     * @param provider the provider to query
     * @return the provider's I/O type
     */
    public static IOType getByIO(Provider provider) {
        return provider.type();
    }

    /**
     * Returns the I/O type whose provider interface is assignable from the given provider class.
     *
     * @param providerClass the provider implementation class to classify
     * @return the matching I/O type, or {@code null} if none matches
     */
    public static IOType getByProviderClass(Class<? extends Provider> providerClass) {
        for (var type : IOType.values()) {
            if (type.getProviderClass().isAssignableFrom(providerClass)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Returns the I/O type of the given I/O instance.
     *
     * @param io the I/O instance to classify
     * @return the instance's I/O type
     */
    public static IOType getByIO(IO io) {
        return io.type();
    }

    /**
     * Returns the I/O type whose {@link IO} interface is assignable from the given I/O class.
     *
     * @param ioClass the I/O implementation class to classify
     * @return the matching I/O type, or {@code null} if none matches
     */
    public static IOType getByIOClass(Class<? extends IO> ioClass) {
        for (var type : IOType.values()) {
            if (type.getIOClass().isAssignableFrom(ioClass)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Returns the I/O type whose {@link IOConfig} is assignable from the given configuration class.
     *
     * @param configClass the configuration implementation class to classify
     * @return the matching I/O type, or {@code null} if none matches
     */
    public static IOType getByConfigClass(Class<? extends IOConfig> configClass) {
        for (var type : IOType.values()) {
            if (type.getConfigClass().isAssignableFrom(configClass)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Parses a free-form textual I/O type name into the matching {@link IOType}.
     * <p>
     * Accepts the exact constant name as well as many common spellings, separators and aliases
     * (for example {@code "din"}, {@code "digital input"}, {@code "pulse-width"}, {@code "i²c"} or
     * {@code "serial peripheral interface"}); matching is case-insensitive.
     *
     * @param ioType the textual I/O type name to parse
     * @return the matching I/O type
     * @throws IllegalArgumentException if the text does not correspond to any known I/O type
     */
    public static IOType parse(String ioType) {

        try {
            IOType iot = IOType.valueOf(ioType);
            if (iot != null) {
                return iot;
            }
        } catch (Exception e) {
        }

        // lower case the string for comparisons
        ioType = ioType.toLowerCase();

        // DIGITAL INPUT
        if (ioType.startsWith("digital.i")) return DIGITAL_INPUT;
        if (ioType.startsWith("digital-i")) return DIGITAL_INPUT;
        if (ioType.startsWith("digital_i")) return DIGITAL_INPUT;
        if (ioType.startsWith("digital i")) return DIGITAL_INPUT;
        if (ioType.equalsIgnoreCase("din")) return DIGITAL_INPUT;

        // DIGITAL OUTPUT
        if (ioType.startsWith("digital.o")) return DIGITAL_OUTPUT;
        if (ioType.startsWith("digital-o")) return DIGITAL_OUTPUT;
        if (ioType.startsWith("digital_o")) return DIGITAL_OUTPUT;
        if (ioType.startsWith("digital o")) return DIGITAL_OUTPUT;
        if (ioType.equalsIgnoreCase("dout")) return DIGITAL_OUTPUT;

        // PWM
        if (ioType.equalsIgnoreCase("pwm")) return PWM;
        if (ioType.equalsIgnoreCase("p.w.m")) return PWM;
        if (ioType.equalsIgnoreCase("p-w-m")) return PWM;
        if (ioType.equalsIgnoreCase("p_w_m")) return PWM;
        if (ioType.startsWith("pulse.width")) return PWM;
        if (ioType.startsWith("pulse-width")) return PWM;
        if (ioType.startsWith("pulse_width")) return PWM;
        if (ioType.startsWith("pulse width")) return PWM;

        // I2C
        if (ioType.equalsIgnoreCase("i²c")) return I2C;
        if (ioType.equalsIgnoreCase("i2c")) return I2C;
        if (ioType.equalsIgnoreCase("i.2.c")) return I2C;
        if (ioType.equalsIgnoreCase("i-2-c")) return I2C;
        if (ioType.equalsIgnoreCase("i_2_c")) return I2C;
        if (ioType.equalsIgnoreCase("i 2 c")) return I2C;
        if (ioType.equalsIgnoreCase("inter.integrated.circuit")) return I2C;
        if (ioType.equalsIgnoreCase("inter-integrated-circuit")) return I2C;
        if (ioType.equalsIgnoreCase("inter_integrated_circuit")) return I2C;
        if (ioType.equalsIgnoreCase("inter integrated circuit")) return I2C;

        // SPI
        if (ioType.equalsIgnoreCase("spi")) return SPI;
        if (ioType.equalsIgnoreCase("serial.peripheral.interface")) return SPI;
        if (ioType.equalsIgnoreCase("serial-peripheral-interface")) return SPI;
        if (ioType.equalsIgnoreCase("serial_peripheral_interface")) return SPI;
        if (ioType.equalsIgnoreCase("serial peripheral interface")) return SPI;

        throw new IllegalArgumentException("Unknown IO TYPE: " + ioType);
    }
}