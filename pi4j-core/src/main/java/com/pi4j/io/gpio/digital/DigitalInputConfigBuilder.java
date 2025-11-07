package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalInputConfigBuilder.java
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
import com.pi4j.io.gpio.digital.impl.DefaultDigitalInputConfigBuilder;

import java.util.concurrent.TimeUnit;

/**
 * <p>DigitalInputConfigBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface DigitalInputConfigBuilder extends DigitalConfigBuilder<DigitalInputConfigBuilder, DigitalInputConfig> {

    DigitalInputConfigBuilder bcm(Integer bcm);

    /**
     * <p>pull.</p>
     *
     * @param value a {@link com.pi4j.io.gpio.digital.PullResistance} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalInputConfigBuilder} object.
     */
    DigitalInputConfigBuilder pull(PullResistance value);

    /**
     * <p>debounce.</p>
     *
     * @param microseconds a {@link java.lang.Long} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalInputConfigBuilder} object.
     * @see com.pi4j.io.gpio.digital.DigitalInput#DEFAULT_DEBOUNCE DEFAULT_DEBOUNCE
     */
    DigitalInputConfigBuilder debounce(Long microseconds);

    /**
     * <p>debounce.</p>
     *
     * @param interval a {@link java.lang.Long} object.
     * @param units    a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalInputConfigBuilder} object.
     */
    DigitalInputConfigBuilder debounce(Long interval, TimeUnit units);

    /**
     * <p>newInstance.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.gpio.digital.DigitalInputConfigBuilder} object.
     */
    static DigitalInputConfigBuilder newInstance(Context context) {
        return DefaultDigitalInputConfigBuilder.newInstance(context);
    }
}
