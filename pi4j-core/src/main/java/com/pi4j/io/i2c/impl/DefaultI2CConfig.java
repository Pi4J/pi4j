package com.pi4j.io.i2c.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultI2CConfig.java
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

import com.pi4j.config.exception.ConfigMissingRequiredKeyException;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.io.impl.IOConfigBase;
import com.pi4j.util.StringUtil;

import java.util.Map;

/**
 * <p>DefaultI2CConfig class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultI2CConfig
    extends IOConfigBase<I2CConfig>
    implements I2CConfig {

    // private configuration properties
    protected Integer bus = null;
    protected Integer device = null;
    protected I2CImplementation i2cImplementation = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    private DefaultI2CConfig() {
        super();
    }

    // private configuration properties
    protected PullResistance pullResistance = PullResistance.OFF;

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link java.util.Map} object.
     */
    protected DefaultI2CConfig(Map<String, String> properties) {
        super(properties);

        // load (required) BUS property
        if (properties.containsKey(BUS_KEY)) {
            this.bus = Integer.parseInt(properties.get(BUS_KEY));
        } else {
            throw new ConfigMissingRequiredKeyException(BUS_KEY);
        }

        // load (required) DEVICE property
        if (properties.containsKey(DEVICE_KEY)) {
            this.device = Integer.parseInt(properties.get(DEVICE_KEY));
        } else {
            throw new ConfigMissingRequiredKeyException(DEVICE_KEY);
        }

        // i2c implementation
        if (properties.containsKey(I2C_IMPLEMENTATION)) {
            this.i2cImplementation = I2CImplementation.valueOf(properties.get(I2C_IMPLEMENTATION));
        } else {
            this.i2cImplementation = I2CImplementation.SMBUS;
        }

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "I2C-" + this.bus() + "." + this.device(), true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "I2C-" + this.bus() + "." + this.device(), true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "I2C-" + this.bus() + "." + this.device(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer bus() {
        return this.bus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer device() {
        return this.device;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUniqueIdentifier() {
        return (bus() << 8) + device();
    }

    @Override
    public I2CImplementation i2cImplementation() {
        return i2cImplementation;
    }
}
