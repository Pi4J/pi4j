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
 * <p>DigitalConfigBuilder interface.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface DigitalConfigBuilder<BUILDER_TYPE extends DigitalConfigBuilder, CONFIG_TYPE extends DigitalConfig>
    extends GpioConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * @param address a {@link java.lang.Integer} object.
     * @return a BUILDER_TYPE object.
     * @deprecated use {@link #bcm(Integer)} bcm} instead.
     */
    @Deprecated(forRemoval = true)
    BUILDER_TYPE address(Integer address);

    /**
     * <p>bcm.</p>
     *
     * @param bcm a {@link java.lang.Integer} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE bcm(Integer bcm);

    /**
     * <p>pull.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link BUILDER_TYPE} object.
     */
    BUILDER_TYPE onState(DigitalState state);
}
