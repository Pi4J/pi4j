package com.pi4j.config;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BcmConfig.java
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

/**
 * Configuration contract for I/O instances that are addressed by a Broadcom (BCM) GPIO pin number,
 * such as digital inputs/outputs and PWM pins. The {@code bcm} value identifies the physical SoC
 * GPIO pin the I/O is bound to.
 *
 * @param <CONFIG_TYPE> the concrete configuration sub-type, returned by fluent accessors to enable type-safe chaining
 */
public interface BcmConfig<CONFIG_TYPE extends Config> extends Config<CONFIG_TYPE> {
    /**
     * Property key under which the legacy "address" value is stored in the configuration properties map.
     *
     * @deprecated use {@link BcmConfig#BCM_KEY} instead.
     * <p>
     * Since "address" has lead to many confusions while configuring IOs,
     * this value is deprecated and will be removed in a future release.
     * Use the correct config related to the IO type.
     */
    @Deprecated(forRemoval = true)
    String ADDRESS_KEY = "address";

    /**
     * Returns the configured pin number under its legacy "address" name.
     *
     * @return the BCM GPIO pin number, or {@code null} if not configured
     * @deprecated use {@link #bcm()} instead.
     * <p>
     * Since "address" has lead to many confusions while configuring IOs,
     * this value is deprecated and will be removed in a future release.
     * Use the correct config related to the IO type.
     */
    @Deprecated(forRemoval = true)
    Integer address();

    /**
     * Returns the configured pin number under its legacy "address" name.
     *
     * @return the BCM GPIO pin number, or {@code null} if not configured
     * @deprecated use {@link #getBcm()} instead.
     * <p>
     * Since "address" has lead to many confusions while configuring IOs,
     * this value is deprecated and will be removed in a future release.
     * Use the correct config related to the IO type.
     */
    @Deprecated(forRemoval = true)
    default Integer getAddress() {
        return this.address();
    }

    /**
     * Property key under which the BCM pin number is stored in the configuration properties map.
     */
    String BCM_KEY = "bcm";

    /**
     * Returns the Broadcom (BCM) GPIO pin number this I/O is bound to.
     *
     * @return the BCM GPIO pin number, or {@code null} if not configured
     */
    Integer bcm();

    /**
     * Returns the Broadcom (BCM) GPIO pin number this I/O is bound to.
     *
     * @return the BCM GPIO pin number, or {@code null} if not configured
     */
    default Integer getBcm() {
        return this.bcm();
    }
}
