package com.pi4j.io;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOAddressConfigBuilder.java
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

import com.pi4j.config.BcmConfigBuilder;
import com.pi4j.provider.Provider;

/**
 * Builder contract for I/O configurations addressed by a Broadcom (BCM) GPIO pin number.
 * <p>
 * It combines the provider-selection capability of {@link IOConfigBuilder} with the BCM pin
 * addressing of {@link BcmConfigBuilder}, and is the basis for builders of pin-based I/O such as
 * digital input/output and PWM.
 *
 * @param <BUILDER_TYPE> the concrete builder type, returned for fluent method chaining
 * @param <CONFIG_TYPE>  the configuration type produced by this builder
 */
public interface IOBcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>
    extends IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    BcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * Selects the I/O provider to use, identified by its registered provider id.
     *
     * @param provider the provider id (e.g. a plugin's registered provider name)
     * @return this builder for method chaining
     */
    BUILDER_TYPE provider(String provider);

    /**
     * Selects the I/O provider to use by its implementing class.
     *
     * @param providerClass the {@link Provider} implementation class to resolve
     * @return this builder for method chaining
     */
    BUILDER_TYPE provider(Class<? extends Provider> providerClass);
}
