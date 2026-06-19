package com.pi4j.io.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CConfig.java
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

import com.pi4j.config.BusConfig;
import com.pi4j.config.DeviceConfig;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;

/**
 * Immutable configuration for an {@link I2C} device, combining the bus number, device (slave) address and the
 * desired {@link I2CImplementation}. Instances are produced by an {@link I2CConfigBuilder} and consumed by an
 * {@link I2CProvider} when creating a device.
 */
public interface I2CConfig extends IOConfig<I2CConfig>, BusConfig<I2CConfig>, DeviceConfig<I2CConfig> {

    /** Configuration property key identifying the selected {@link I2CImplementation}. */
    String I2C_IMPLEMENTATION = "i2c_implementation";

    /**
     * Returns the low-level access strategy selected for this device.
     *
     * @return the configured {@link I2CImplementation}, or {@code null} if none was specified
     */
    I2CImplementation i2cImplementation();

    /**
     * Returns the low-level access strategy selected for this device.
     *
     * @return the configured {@link I2CImplementation}, or {@code null} if none was specified
     */
    default I2CImplementation getI2CImplementation() {
        return i2cImplementation();
    }

    /**
     * Creates a new configuration builder.
     *
     * @param context the Pi4J runtime context (unused by the current implementation)
     * @return a new {@link I2CConfigBuilder} instance
     * @deprecated As of version 5, please use {@link #newBuilder()} instead.
     */
    static I2CConfigBuilder newBuilder(Context context) {
        return I2CConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new configuration builder.
     *
     * @return a new {@link I2CConfigBuilder} instance
     */
    static I2CConfigBuilder newBuilder() {
        return I2CConfigBuilder.newInstance();
    }

    /**
     * I2C Device Identifier
     * To be able to identify unique I2C devices, an identifier is available which is based on the bus and device value.
     *
     * @return Unique I2C device identifier.
     */
    @Override
    default int getUniqueIdentifier() {
        return (bus() << 8) + device();
    }
}
