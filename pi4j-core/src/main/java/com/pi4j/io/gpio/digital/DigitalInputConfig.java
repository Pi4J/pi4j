package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalInputConfig.java
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

/**
 * Configuration contract for a {@link DigitalInput}, extending {@link DigitalConfig} with the input's
 * pull resistance and debounce interval. Instances are typically assembled through a
 * {@link DigitalInputConfigBuilder}.
 */
public interface DigitalInputConfig extends DigitalConfig<DigitalInputConfig> {

    /** Property key under which the pull resistance value is stored in a configuration map. */
    String PULL_RESISTANCE_KEY = "pull";
    /** Property key under which the debounce interval is stored in a configuration map. */
    String DEBOUNCE_RESISTANCE_KEY = "debounce";

    /**
     * Returns the pull resistance (pull-up, pull-down, or none) applied to the input pin.
     *
     * @return the configured {@link PullResistance}
     */
    PullResistance pull();

    /**
     * Bean-style accessor equivalent to {@link #pull()}.
     *
     * @return the configured {@link PullResistance}
     */
    default PullResistance getPull(){
        return pull();
    }

    /**
     * Returns the debounce interval, in microseconds, used to filter spurious state transitions.
     *
     * @return the configured debounce interval in microseconds, or {@code null} if unset
     */
    Long debounce();

    /**
     * Bean-style accessor equivalent to {@link #debounce()}.
     *
     * @return the configured debounce interval in microseconds, or {@code null} if unset
     */
    default Long getDebounce(){ return debounce(); }

    /**
     * Creates a new {@link DigitalInputConfigBuilder}.
     *
     * @param context the Pi4J context
     * @return a new configuration builder instance
     * @deprecated As of version 5, please use {@link #newBuilder()} instead.
     */
    @Deprecated
    static DigitalInputConfigBuilder newBuilder(Context context)  {
        return DigitalInputConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new {@link DigitalInputConfigBuilder} for assembling a digital input configuration.
     *
     * @return a new configuration builder instance
     */
    static DigitalInputConfigBuilder newBuilder()  {
        return DigitalInputConfigBuilder.newInstance();
    }
}
