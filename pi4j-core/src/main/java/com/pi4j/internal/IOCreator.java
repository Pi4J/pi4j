package com.pi4j.internal;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOCreator.java
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

import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.analog.*;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialConfigBuilder;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiConfigBuilder;

public interface IOCreator {
    
    <I extends IO> I create(IOConfig config, IOType type);

    <I extends IO> I create(String id);

    <I extends IO> I create(String id, IOType ioType);

    default <I extends IO> I create(String id, Class<I> ioClass) {
        return create(id, IOType.getByIOClass(ioClass));
    }

    default <I extends IO> I create(IOConfig config, Class<I> ioClass) {
        return (ioClass.cast(create((IOConfig) config, IOType.getByIOClass(ioClass))));
    }

    default <I extends IO> I create(IOConfigBuilder builder, IOType ioType) {
        return create((IOConfig) builder.build(), ioType);
    }

    default <I extends IO> I create(IOConfigBuilder builder, Class<I> ioClass) {
        return create((IOConfig) builder.build(), ioClass);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.gpio.analog.AnalogOutputConfig} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutput} object.
     */
    default AnalogOutput create(AnalogOutputConfig config) {
        return create(config, AnalogOutput.class);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.gpio.analog.AnalogInputConfig} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogInput} object.
     */
    default AnalogInput create(AnalogInputConfig config) {
        return create(config, AnalogInput.class);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalOutputConfig} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     */
    default DigitalOutput create(DigitalOutputConfig config) {
        return create(config, DigitalOutput.class);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalInputConfig} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalInput} object.
     */
    default DigitalInput create(DigitalInputConfig config) {
        return create(config, DigitalInput.class);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.pwm.PwmConfig} object.
     * @return a {@link com.pi4j.io.pwm.Pwm} object.
     */
    default Pwm create(PwmConfig config) {
        return create(config, Pwm.class);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.i2c.I2CConfig} object.
     * @return a {@link com.pi4j.io.i2c.I2C} object.
     */
    default I2C create(I2CConfig config) {
        return create(config, I2C.class);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.spi.SpiConfig} object.
     * @return a {@link com.pi4j.io.spi.Spi} object.
     */
    default Spi create(SpiConfig config) {
        return create(config, Spi.class);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.serial.SerialConfig} object.
     * @return a {@link com.pi4j.io.serial.Serial} object.
     */
    default Serial create(SerialConfig config) {
        return create(config, Serial.class);
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.gpio.analog.AnalogOutputConfigBuilder} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutput} object.
     */
    default AnalogOutput create(AnalogOutputConfigBuilder config) {
        return create(config.build());
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.gpio.analog.AnalogInputConfigBuilder} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogInput} object.
     */
    default AnalogInput create(AnalogInputConfigBuilder config) {
        return create(config.build());
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     */
    default DigitalOutput create(DigitalOutputConfigBuilder config) {
        return create(config.build());
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalInputConfigBuilder} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalInput} object.
     */
    default DigitalInput create(DigitalInputConfigBuilder config) {
        return create(config.build());
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.pwm.PwmConfigBuilder} object.
     * @return a {@link com.pi4j.io.pwm.Pwm} object.
     */
    default Pwm create(PwmConfigBuilder config) {
        return create(config.build());
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.i2c.I2CConfigBuilder} object.
     * @return a {@link com.pi4j.io.i2c.I2C} object.
     */
    default I2C create(I2CConfigBuilder config) {
        return create(config.build());
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     * @return a {@link com.pi4j.io.spi.Spi} object.
     */
    default Spi create(SpiConfigBuilder config) {
        return create(config.build());
    }

    /**
     * <p>create.</p>
     *
     * @param config a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     * @return a {@link com.pi4j.io.serial.Serial} object.
     */
    default Serial create(SerialConfigBuilder config) {
        return create(config.build());
    }
}
