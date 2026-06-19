package com.pi4j.io.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CConfigBuilder.java
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

import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.io.i2c.impl.DefaultI2CConfigBuilder;

/**
 * Fluent builder for assembling an {@link I2CConfig}. Callers set the bus number, device address and optional
 * {@link I2CImplementation}, then call {@code build()} (inherited from {@link ConfigBuilder}) to produce the
 * immutable configuration passed to an {@link I2CProvider}.
 */
public interface I2CConfigBuilder extends
        IOConfigBuilder<I2CConfigBuilder, I2CConfig>,
        ConfigBuilder<I2CConfigBuilder, I2CConfig> {
    /**
     * Creates a new builder instance.
     *
     * @param context the Pi4J runtime context (unused by the current implementation)
     * @return a new {@link I2CConfigBuilder}
     * @deprecated As of version 5, please use {@link #newInstance()} instead.
     */
    @Deprecated
    static I2CConfigBuilder newInstance(Context context)  {
        return DefaultI2CConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new {@link I2CConfigBuilder}
     */
    static I2CConfigBuilder newInstance()  {
        return DefaultI2CConfigBuilder.newInstance();
    }

    /**
     * Sets the I2C bus number the device is attached to.
     *
     * @param bus the bus number (e.g. {@code 1} for {@code /dev/i2c-1})
     * @return this builder instance for method chaining
     */
    I2CConfigBuilder bus(Integer bus);

    /**
     * Sets the device (slave) address on the I2C bus.
     *
     * @param device the 7-bit device address
     * @return this builder instance for method chaining
     */
    I2CConfigBuilder device(Integer device);

    /**
     * Sets the low-level access strategy to use for this device.
     *
     * @param i2CImplementation the {@link I2CImplementation} to use
     * @return this builder instance for method chaining
     */
    I2CConfigBuilder i2cImplementation(I2CImplementation i2CImplementation);
}
