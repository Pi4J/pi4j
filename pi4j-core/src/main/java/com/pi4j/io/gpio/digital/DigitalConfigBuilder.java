package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalConfigBuilder.java
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

import com.pi4j.io.gpio.GpioConfigBuilder;

/**
 * Fluent builder contract for assembling a {@link DigitalConfig}, extending the generic
 * {@link GpioConfigBuilder} with the digital-specific BCM pin and on-state settings.
 *
 * @param <BUILDER_TYPE> the concrete builder type, used as the self-referencing return type for chaining
 * @param <CONFIG_TYPE> the {@link DigitalConfig} type produced by this builder
 */
public interface DigitalConfigBuilder<BUILDER_TYPE extends DigitalConfigBuilder, CONFIG_TYPE extends DigitalConfig>
    extends GpioConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * Sets the BCM (Broadcom) GPIO pin number for the configuration being built.
     *
     * @param address the BCM pin number
     * @return this builder for method chaining
     * @deprecated use {@link #bcm(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    default BUILDER_TYPE address(Integer address) {
        return bcm(address);
    }

    /**
     * Sets the BCM (Broadcom) GPIO pin number for the configuration being built.
     *
     * @param bcm the BCM pin number to bind the digital I/O instance to
     * @return this builder for method chaining
     */
    BUILDER_TYPE bcm(Integer bcm);

    /**
     * Sets the {@link DigitalState} to be treated as the logical "on" state for the configuration being built.
     *
     * @param state the state that should map to "on"
     * @return this builder for method chaining
     */
    BUILDER_TYPE onState(DigitalState state);
}
