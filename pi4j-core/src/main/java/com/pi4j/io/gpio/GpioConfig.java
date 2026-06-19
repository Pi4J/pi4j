package com.pi4j.io.gpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  GpioConfig.java
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

import com.pi4j.config.BcmConfig;
import com.pi4j.config.BusConfig;
import com.pi4j.config.Config;
import com.pi4j.io.IOConfig;

/**
 * Configuration contract for {@link Gpio} I/O instances. It combines the {@link IOConfig} identity and
 * lifecycle properties with the GPIO addressing model of {@link BcmConfig} (Broadcom pin number) and the
 * bus addressing of {@link BusConfig}, so a single configuration can fully describe a GPIO pin to its provider.
 *
 * @param <CONFIG_TYPE> the concrete configuration sub-type, returned by fluent accessors to enable type-safe chaining
 */
public interface GpioConfig<CONFIG_TYPE extends Config> extends BusConfig<CONFIG_TYPE>, BcmConfig<CONFIG_TYPE>, IOConfig<CONFIG_TYPE> {

    /**
     * Returns the unique identifier used to distinguish GPIO devices, derived from the configured
     * BCM pin number so that each physical GPIO pin maps to a distinct identifier.
     *
     * @return the BCM GPIO pin number serving as this device's unique identifier
     */
    @Override
    default int getUniqueIdentifier() {
        return bcm();
    }
}
