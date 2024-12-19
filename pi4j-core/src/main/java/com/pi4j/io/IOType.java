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
import com.pi4j.io.gpio.analog.*;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.onewire.OneWire;
import com.pi4j.io.onewire.OneWireConfig;
import com.pi4j.io.onewire.OneWireConfigBuilder;
import com.pi4j.io.onewire.OneWireProvider;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialConfigBuilder;
import com.pi4j.io.serial.SerialProvider;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.provider.Provider;

import java.lang.reflect.Method;

/**
 * <p>IOType class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public enum IOType {

    ANALOG_INPUT(AnalogInputProvider.class, AnalogInput.class, AnalogInputConfig.class, AnalogInputConfigBuilder.class),
    ANALOG_OUTPUT(AnalogOutputProvider.class, AnalogOutput.class, AnalogOutputConfig.class, AnalogOutputConfigBuilder.class),
    DIGITAL_INPUT(DigitalInputProvider.class, DigitalInput.class, DigitalInputConfig.class, DigitalInputConfigBuilder.class),
    DIGITAL_OUTPUT(DigitalOutputProvider.class, DigitalOutput.class, DigitalOutputConfig.class, DigitalOutputConfigBuilder.class),
    PWM(PwmProvider.class, Pwm.class, PwmConfig.class, PwmConfigBuilder.class),
    I2C(I2CProvider.class, com.pi4j.io.i2c.I2C.class, I2CConfig.class, I2CConfigBuilder.class),
    SPI(SpiProvider.class, Spi.class, I2CConfig.class, I2CConfigBuilder.class),
    SERIAL(SerialProvider.class, Serial.class, SerialConfig.class, SerialConfigBuilder.class),
    ONE_WIRE(OneWireProvider.class, OneWire.class, OneWireConfig.class, OneWireConfigBuilder.class);

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
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<? extends Provider> getProviderClass() {
        return providerClass;
    }
    /**
     * <p>getIOClass.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<? extends IO> getIOClass() {
        return ioClass;
    }
    /**
     * <p>Getter for the field <code>configClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<? extends IOConfig> getConfigClass() {
        return configClass;
    }

    /**
     * <p>Getter for the field <code>configBuilderClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<? extends IOConfigBuilder> getConfigBuilderClass() {
        return configBuilderClass;
    }

    public <CB extends IOConfigBuilder>CB newConfigBuilder(Context context) {
        try {
            Method newInstance = getConfigBuilderClass().getMethod("newInstance", Context.class);
            return (CB) newInstance.invoke(null, context);
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * <p>isType.</p>
     *
     * @param type a {@link com.pi4j.io.IOType} object.
     * @return a boolean.
     */
    public boolean isType(IOType type){
        return type == this;
    }

    /**
     * <p>getIOClass.</p>
     *
     * @param type a {@link com.pi4j.io.IOType} object.
     * @return a {@link java.lang.Class} object.
     */
    public static Class<? extends IO> getIOClass(IOType type){
        for(var typeInstance : IOType.values()){
            if(typeInstance.equals(type)){
                return typeInstance.getIOClass();
            }
        }
        return null;
    }

    /**
     * <p>Getter for the field <code>providerClass</code>.</p>
     *
     * @param type a {@link com.pi4j.io.IOType} object.
     * @return a {@link java.lang.Class} object.
     */
    public static Class<? extends Provider> getProviderClass(IOType type){
        for(var typeInstance : IOType.values()){
            if(typeInstance.equals(type)){
                return typeInstance.getProviderClass();
            }
        }
        return null;
    }

    /**
     * <p>Getter for the field <code>configClass</code>.</p>
     *
     * @param type a {@link com.pi4j.io.IOType} object.
     * @return a {@link java.lang.Class} object.
     */
    public static Class<? extends IOConfig> getConfigClass(IOType type){
        for(var typeInstance : IOType.values()){
            if(typeInstance.equals(type)){
                return typeInstance.getConfigClass();
            }
        }
        return null;
    }

    /**
     * <p>getByProviderClass.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.IOType} object.
     */
    public static IOType getByProviderClass(String name){
        for(var type : IOType.values()){
            if(type.name().equalsIgnoreCase(name)){
                return type;
            }
        }
        return null;
    }

    /**
     * <p>getByIO.</p>
     *
     * @param provider a {@link com.pi4j.provider.Provider} object.
     * @return a {@link com.pi4j.io.IOType} object.
     */
    public static IOType getByIO(Provider provider){
        return provider.type();
    }

    /**
     * <p>getByProviderClass.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @return a {@link com.pi4j.io.IOType} object.
     */
    public static IOType getByProviderClass(Class<? extends Provider> providerClass){
        for(var type : IOType.values()){
            if(type.getProviderClass().isAssignableFrom(providerClass)){
                return type;
            }
        }
        return null;
    }

    /**
     * <p>getByIO.</p>
     *
     * @param io a {@link com.pi4j.io.IO} object.
     * @return a {@link com.pi4j.io.IOType} object.
     */
    public static IOType getByIO(IO io){
        return io.type();
    }

    /**
     * <p>getByIOClass.</p>
     *
     * @param ioClass a {@link java.lang.Class} object.
     * @return a {@link com.pi4j.io.IOType} object.
     */
    public static IOType getByIOClass(Class<? extends IO> ioClass){
        for(var type : IOType.values()){
            if(type.getIOClass().isAssignableFrom(ioClass)){
                return type;
            }
        }
        return null;
    }

    /**
     * <p>getByConfigClass.</p>
     *
     * @param configClass a {@link java.lang.Class} object.
     * @return a {@link com.pi4j.io.IOType} object.
     */
    public static IOType getByConfigClass(Class<? extends IOConfig> configClass){
        for(var type : IOType.values()){
            if(type.getConfigClass().isAssignableFrom(configClass)){
                return type;
            }
        }
        return null;
    }

    /**
     * <p>parse.</p>
     *
     * @param ioType a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.IOType} object.
     */
    public static IOType parse(String ioType) {

        try {
            IOType iot = IOType.valueOf(ioType);
            if (iot != null) {
                return iot;
            }
        } catch (Exception e){}

        // lower case the string for comparisons
        ioType = ioType.toLowerCase();

        // ANALOG INPUT
        if(ioType.startsWith("analog.i")) return ANALOG_INPUT;
        if(ioType.startsWith("analog-i")) return ANALOG_INPUT;
        if(ioType.startsWith("analog_i")) return ANALOG_INPUT;
        if(ioType.startsWith("analog i")) return ANALOG_INPUT;
        if(ioType.equalsIgnoreCase("ain")) return ANALOG_INPUT;

        // ANALOG OUTPUT
        if(ioType.startsWith("analog.o")) return ANALOG_OUTPUT;
        if(ioType.startsWith("analog-o")) return ANALOG_OUTPUT;
        if(ioType.startsWith("analog_o")) return ANALOG_OUTPUT;
        if(ioType.startsWith("analog o")) return ANALOG_OUTPUT;
        if(ioType.equalsIgnoreCase("aout")) return ANALOG_OUTPUT;

        // DIGITAL INPUT
        if(ioType.startsWith("digital.i")) return DIGITAL_INPUT;
        if(ioType.startsWith("digital-i")) return DIGITAL_INPUT;
        if(ioType.startsWith("digital_i")) return DIGITAL_INPUT;
        if(ioType.startsWith("digital i")) return DIGITAL_INPUT;
        if(ioType.equalsIgnoreCase("din")) return DIGITAL_INPUT;

        // DIGITAL OUTPUT
        if(ioType.startsWith("digital.o")) return DIGITAL_OUTPUT;
        if(ioType.startsWith("digital-o")) return DIGITAL_OUTPUT;
        if(ioType.startsWith("digital_o")) return DIGITAL_OUTPUT;
        if(ioType.startsWith("digital o")) return DIGITAL_OUTPUT;
        if(ioType.equalsIgnoreCase("dout")) return DIGITAL_OUTPUT;

        // PWM
        if(ioType.equalsIgnoreCase("pwm")) return PWM;
        if(ioType.equalsIgnoreCase("p.w.m")) return PWM;
        if(ioType.equalsIgnoreCase("p-w-m")) return PWM;
        if(ioType.equalsIgnoreCase("p_w_m")) return PWM;
        if(ioType.startsWith("pulse.width")) return PWM;
        if(ioType.startsWith("pulse-width")) return PWM;
        if(ioType.startsWith("pulse_width")) return PWM;
        if(ioType.startsWith("pulse width")) return PWM;

        // I2C
        if(ioType.equalsIgnoreCase("i²c")) return I2C;
        if(ioType.equalsIgnoreCase("i2c")) return I2C;
        if(ioType.equalsIgnoreCase("i.2.c")) return I2C;
        if(ioType.equalsIgnoreCase("i-2-c")) return I2C;
        if(ioType.equalsIgnoreCase("i_2_c")) return I2C;
        if(ioType.equalsIgnoreCase("i 2 c")) return I2C;
        if(ioType.equalsIgnoreCase("inter.integrated.circuit")) return I2C;
        if(ioType.equalsIgnoreCase("inter-integrated-circuit")) return I2C;
        if(ioType.equalsIgnoreCase("inter_integrated_circuit")) return I2C;
        if(ioType.equalsIgnoreCase("inter integrated circuit")) return I2C;

        // SPI
        if(ioType.equalsIgnoreCase("spi")) return SPI;
        if(ioType.equalsIgnoreCase("serial.peripheral.interface")) return SPI;
        if(ioType.equalsIgnoreCase("serial-peripheral-interface")) return SPI;
        if(ioType.equalsIgnoreCase("serial_peripheral_interface")) return SPI;
        if(ioType.equalsIgnoreCase("serial peripheral interface")) return SPI;

        // SERIAL
        if(ioType.equalsIgnoreCase("serial")) return SERIAL;
        if(ioType.equalsIgnoreCase("uart")) return SERIAL;
        if(ioType.equalsIgnoreCase("rs232")) return SERIAL;


        throw new IllegalArgumentException("Unknown IO TYPE: " + ioType);
    }
}

//    ANALOG_INPUT(AnalogInputProvider.class, AnalogInput.class, AnalogInputConfig.class, AnalogInputConfigBuilder.class),
//    ANALOG_OUTPUT(AnalogOutputProvider.class, AnalogOutput.class, AnalogOutputConfig.class, AnalogOutputConfigBuilder.class),
//    DIGITAL_INPUT(DigitalInputProvider.class, DigitalInput.class, DigitalInputConfig.class, DigitalInputConfigBuilder.class),
//    DIGITAL_OUTPUT(DigitalOutputProvider.class, DigitalOutput.class, DigitalOutputConfig.class, DigitalOutputConfigBuilder.class),
//    PWM(PwmProvider.class, Pwm.class, PwmConfig.class, PwmConfigBuilder.class),
//    I2C(I2CProvider.class, com.pi4j.io.i2c.I2C.class, I2CConfig.class, I2CConfigBuilder.class),
//    SPI(SpiProvider.class, Spi.class, I2CConfig.class, I2CConfigBuilder.class),
//    SERIAL(SerialProvider.class, Serial.class, SerialConfig.class, SerialConfigBuilder.class);
