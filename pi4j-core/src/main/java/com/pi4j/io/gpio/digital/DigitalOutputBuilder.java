package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalOutputBuilder.java
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

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.impl.DefaultDigitalOutputBuilder;
import com.pi4j.platform.Platform;
import com.pi4j.provider.Provider;

/**
 * <p>DigitalOutputBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface DigitalOutputBuilder {

    /**
     * <p>id.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder id(String id);
    /**
     * <p>name.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder name(String name);
    /**
     * <p>description.</p>
     *
     * @param description a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder description(String description);
    /**
     * <p>address.</p>
     *
     * @param address a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder address(Integer address);
    /**
     * <p>shutdown.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder shutdown(DigitalState state);
    /**
     * <p>initial.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder initial(DigitalState state);

    /**
     * <p>platform.</p>
     *
     * @param platformId a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder platform(String platformId);
    /**
     * <p>platform.</p>
     *
     * @param platformClass a {@link java.lang.Class} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder platform(Class<? extends Platform> platformClass);
    /**
     * <p>provider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder provider(String providerId);
    /**
     * <p>provider.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    DigitalOutputBuilder provider(Class<? extends Provider> providerClass);

    /**
     * <p>newInstance.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    static DigitalOutputBuilder newInstance(Context context)  {
        return DefaultDigitalOutputBuilder.newInstance(context);
    }

    /**
     * <p>build.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     */
    DigitalOutput build();
}
