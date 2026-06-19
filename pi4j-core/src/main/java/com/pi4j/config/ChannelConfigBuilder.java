package com.pi4j.config;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ChannelConfigBuilder.java
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
 * Builder contract for assembling a {@link ChannelConfig}, i.e. the configuration of an I/O that
 * is addressed by a channel number within a bus (such as SPI or PWM).
 *
 * @param <BUILDER_TYPE> the concrete builder sub-type, returned by setters to enable type-safe chaining
 * @param <CONFIG_TYPE> the configuration type produced by {@link #build()}
 */
public interface ChannelConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    /**
     * Sets the channel number this I/O is assigned to within its bus.
     *
     * @param channel the channel number
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE channel(Integer channel);
}
