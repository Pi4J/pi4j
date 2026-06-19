package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalInput.java
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
import com.pi4j.io.Input;

/**
 * Represents a digital input pin that reads a HIGH/LOW logic level from a GPIO source, optionally with
 * a configured pull resistance and debounce interval. This is the read-only digital counterpart created
 * by a {@link DigitalInputProvider} and configured via {@link DigitalInputConfig}.
 */
public interface DigitalInput extends Digital<DigitalInput, DigitalInputConfig, DigitalInputProvider>, Input {
    /** Default debounce interval in microseconds applied to state-change detection. */
    long DEFAULT_DEBOUNCE = 10000;

    /**
     * Creates a new {@link DigitalInputConfigBuilder} for assembling a digital input configuration.
     *
     * @param context the Pi4J context (not required by the current implementation but kept for API consistency)
     * @return a new configuration builder instance
     */
    static DigitalInputConfigBuilder newConfigBuilder(Context context){
        return DigitalInputConfigBuilder.newInstance();
    }

    /**
     * Returns the pull resistance (pull-up, pull-down, or none) configured for this input.
     *
     * @return the configured {@link PullResistance}
     */
    default PullResistance pull() { return config().pull(); }
}
