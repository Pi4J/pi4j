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

public enum IOType {

    DIGITAL_INPUT(DigitalInputProvider.class, DigitalInput.class, DigitalInputConfig.class, DigitalInputConfigBuilder.class),
    DIGITAL_OUTPUT(DigitalOutputProvider.class, DigitalOutput.class, DigitalOutputConfig.class, DigitalOutputConfigBuilder.class),
    PWM(PwmProvider.class, Pwm.class, PwmConfig.class, PwmConfigBuilder.class),
    I2C(I2CProvider.class, com.pi4j.io.i2c.I2C.class, I2CConfig.class, I2CConfigBuilder.class),
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
     * <p>Getter for the field <code>providerClass</code>.</p>
     */
    public Class<? extends Provider> getProviderClass() {
        return providerClass;
    }

    public Class<? extends IO> getIOClass() {
        return ioClass;
    }

    /**
     * <p>Getter for the field <code>configClass</code>.</p>
     */
    public Class<? extends IOConfig> getConfigClass() {
        return configClass;
    }

    /**
     * <p>Getter for the field <code>configBuilderClass</code>.</p>
     */
    public Class<? extends IOConfigBuilder> getConfigBuilderClass() {
        return configBuilderClass;
    }

    public <CB extends IOConfigBuilder> CB newConfigBuilder(Context context) {
        try {
            Method newInstance = getConfigBuilderClass().getMethod("newInstance", Context.class);
            return (CB) newInstance.invoke(null, context);
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
    }

    public boolean isType(IOType type) {
        return type == this;
    }

    public static Class<? extends IO> getIOClass(IOType type) {
        for (var typeInstance : IOType.values()) {
            if (typeInstance.equals(type)) {
                return typeInstance.getIOClass();
            }
        }
        return null;
    }

    /**
     * <p>Getter for the field <code>providerClass</code>.</p>
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
     * <p>Getter for the field <code>configClass</code>.</p>
     */
    public static Class<? extends IOConfig> getConfigClass(IOType type) {
        for (var typeInstance : IOType.values()) {
            if (typeInstance.equals(type)) {
                return typeInstance.getConfigClass();
            }
        }
        return null;
    }

    public static IOType getByProviderClass(String name) {
        for (var type : IOType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static IOType getByIO(Provider provider) {
        return provider.type();
    }

    public static IOType getByProviderClass(Class<? extends Provider> providerClass) {
        for (var type : IOType.values()) {
            if (type.getProviderClass().isAssignableFrom(providerClass)) {
                return type;
            }
        }
        return null;
    }

    public static IOType getByIO(IO io) {
        return io.type();
    }

    public static IOType getByIOClass(Class<? extends IO> ioClass) {
        for (var type : IOType.values()) {
            if (type.getIOClass().isAssignableFrom(ioClass)) {
                return type;
            }
        }
        return null;
    }

    public static IOType getByConfigClass(Class<? extends IOConfig> configClass) {
        for (var type : IOType.values()) {
            if (type.getConfigClass().isAssignableFrom(configClass)) {
                return type;
            }
        }
        return null;
    }

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