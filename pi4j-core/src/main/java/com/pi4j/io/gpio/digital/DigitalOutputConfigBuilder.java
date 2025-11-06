package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalOutputConfigBuilder.java
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
import com.pi4j.io.gpio.digital.impl.DefaultDigitalOutputConfigBuilder;

/**
 * <p>DigitalOutputConfigBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface DigitalOutputConfigBuilder extends DigitalConfigBuilder<DigitalOutputConfigBuilder, DigitalOutputConfig> {

    DigitalOutputConfigBuilder bcm(Integer bcm);

    /**
     * <p>shutdown.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder} object.
     */
    DigitalOutputConfigBuilder shutdown(DigitalState state);

    /**
     * <p>initial.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder} object.
     */
    DigitalOutputConfigBuilder initial(DigitalState state);

    /**
     * <p>newInstance.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder} object.
     */
    static DigitalOutputConfigBuilder newInstance(Context context) {
        return DefaultDigitalOutputConfigBuilder.newInstance(context);
    }

}
