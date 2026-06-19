package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalConfig.java
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

import com.pi4j.config.Config;
import com.pi4j.io.gpio.GpioConfig;

/**
 * Configuration contract for a {@link Digital} I/O instance, extending the generic {@link GpioConfig}
 * with the BCM pin assignment and the {@link DigitalState} that is considered the logical "on" state.
 *
 * @param <CONFIG_TYPE> the concrete configuration type, used as the self-referencing builder/return type
 */
public interface DigitalConfig<CONFIG_TYPE extends Config> extends GpioConfig<CONFIG_TYPE> {

    /**
     * Property key under which the on-state value is stored in a configuration map.
     */
    String ON_STATE_KEY = "onstate";

    /**
     * Returns the BCM (Broadcom) GPIO pin number this I/O instance is bound to.
     *
     * @return the configured BCM pin number, or {@code null} if none was set
     */
    Integer bcm();

    /**
     * Returns the {@link DigitalState} that is treated as the logical "on" state for this instance,
     * used by on/off convenience methods to map a physical level to an on/off meaning.
     *
     * @return the configured on-state, or {@code null} if none was set (defaulting to HIGH)
     */
    DigitalState onState();

    /**
     * Bean-style accessor equivalent to {@link #onState()}.
     *
     * @return the configured on-state, or {@code null} if none was set
     */
    default DigitalState getOnState() {
        return this.onState();
    }
}
