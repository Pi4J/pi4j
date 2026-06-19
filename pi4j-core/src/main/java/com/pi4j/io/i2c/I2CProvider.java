package com.pi4j.io.i2c;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CProvider.java
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

import com.pi4j.provider.Provider;

/**
 * Provider responsible for creating and managing {@link I2C} device instances for a given platform. A concrete
 * provider is registered with the Pi4J {@link com.pi4j.context.Context} and produces {@link I2C} instances from an
 * {@link I2CConfig}; the convenience {@code create} overloads here build the config for common cases.
 */
public interface I2CProvider extends Provider<I2CProvider, I2C, I2CConfig> {

    /**
     * Builds the supplied configuration and creates the corresponding I2C device instance.
     *
     * @param builder the configuration builder describing the bus, device address and options
     * @param <T>     the concrete {@link I2C} type produced by this provider
     * @return a new I2C device instance bound to the built configuration
     */
    default <T extends I2C> T create(I2CConfigBuilder builder) {
        return (T)create(builder.build());
    }

    /**
     * Creates an I2C device instance for the given bus and device address.
     *
     * @param bus    the I2C bus number the device is attached to
     * @param device the 7-bit device (slave) address on that bus
     * @param <T>    the concrete {@link I2C} type produced by this provider
     * @return a new I2C device instance
     */
    default <T extends I2C> T create(Integer bus, Integer device) {
        var config = I2C.newConfigBuilder(context())
                .bus(bus)
                .device(device)
                .build();
        return (T)create(config);
    }

    /**
     * Creates an I2C device instance for the given bus and device address, with a custom identifier.
     *
     * @param bus    the I2C bus number the device is attached to
     * @param device the 7-bit device (slave) address on that bus
     * @param id     the unique identifier to assign to the created device instance
     * @param <T>    the concrete {@link I2C} type produced by this provider
     * @return a new I2C device instance
     */
    default <T extends I2C> T create(Integer bus, Integer device, String id) {
        var config = I2C.newConfigBuilder(context())
                .bus(bus)
                .device(device)
                .id(id)
                .build();
        return (T)create(config);
    }

    /**
     * Creates an I2C device instance for the given bus and device address, with a custom identifier and name.
     *
     * @param bus    the I2C bus number the device is attached to
     * @param device the 7-bit device (slave) address on that bus
     * @param id     the unique identifier to assign to the created device instance
     * @param name   the human-readable display name to assign to the created device instance
     * @param <T>    the concrete {@link I2C} type produced by this provider
     * @return a new I2C device instance
     */
    default <T extends I2C> T create(Integer bus, Integer device, String id, String name) {
        var config = I2C.newConfigBuilder(context())
                .bus(bus)
                .device(device)
                .id(id)
                .name(name)
                .build();
        return (T)create(config);
    }

    /**
     * Creates an I2C device instance for the given bus and device address, with a custom identifier, name and
     * description.
     *
     * @param bus         the I2C bus number the device is attached to
     * @param device      the 7-bit device (slave) address on that bus
     * @param id          the unique identifier to assign to the created device instance
     * @param name        the human-readable display name to assign to the created device instance
     * @param description a free-form description of the created device instance
     * @param <T>         the concrete {@link I2C} type produced by this provider
     * @return a new I2C device instance
     */
    default <T extends I2C> T create(Integer bus, Integer device, String id, String name, String description) {
        var config = I2C.newConfigBuilder(context())
                .bus(bus)
                .device(device)
                .id(id)
                .name(name)
                .description(description)
                .build();
        return (T)create(config);
    }

}
