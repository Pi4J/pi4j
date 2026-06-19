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
 */
public interface DigitalInputConfigBuilder extends DigitalConfigBuilder<DigitalInputConfigBuilder, DigitalInputConfig> {

    DigitalInputConfigBuilder bcm(Integer bcm);

    DigitalInputConfigBuilder pull(PullResistance value);

    /**
     * @see com.pi4j.io.gpio.digital.DigitalInput#DEFAULT_DEBOUNCE DEFAULT_DEBOUNCE
     */
    DigitalInputConfigBuilder debounce(Long microseconds);

    DigitalInputConfigBuilder debounce(Long interval, TimeUnit units);

    @Deprecated
    static DigitalInputConfigBuilder newInstance(Context context) {
        return DefaultDigitalInputConfigBuilder.newInstance(context);
    }

    static DigitalInputConfigBuilder newInstance() {
        return DefaultDigitalInputConfigBuilder.newInstance();
    }
}
