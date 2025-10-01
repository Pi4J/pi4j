package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Digital.java
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

import com.pi4j.io.ListenableOnOffRead;
import com.pi4j.io.binding.Bindable;
import com.pi4j.io.binding.DigitalBinding;
import com.pi4j.io.gpio.Gpio;

/**
 * <p>Digital interface.</p>
 *
 * @param <CONFIG_TYPE>
 * @param <DIGITAL_TYPE>
 * @param <PROVIDER_TYPE>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Digital<DIGITAL_TYPE extends Digital<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
    CONFIG_TYPE extends DigitalConfig<CONFIG_TYPE>,
    PROVIDER_TYPE extends DigitalProvider>
    extends Gpio<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
    ListenableOnOffRead<DIGITAL_TYPE>,
    Bindable<DIGITAL_TYPE, DigitalBinding> {

    /**
     * <p>state.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    DigitalState state();

    default Integer pin() {
        return config().pin();
    }

    /**
     * <p>addListener.</p>
     *
     * @param listener a {@link DigitalStateChangeListener} object.
     * @return a DIGITAL_TYPE object.
     */
    DIGITAL_TYPE addListener(DigitalStateChangeListener... listener);

    /**
     * <p>removeListener.</p>
     *
     * @param listener a {@link DigitalStateChangeListener} object.
     * @return a DIGITAL_TYPE object.
     */
    DIGITAL_TYPE removeListener(DigitalStateChangeListener... listener);

    /**
     * <p>equals.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a boolean.
     */
    default boolean equals(DigitalState state) {
        return this.state().equals(state);
    }

    /**
     * <p>equals.</p>
     *
     * @param state a {@link java.lang.Number} object.
     * @return a boolean.
     */
    default boolean equals(Number state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * <p>equals.</p>
     *
     * @param state a boolean.
     * @return a boolean.
     */
    default boolean equals(boolean state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * <p>equals.</p>
     *
     * @param state a byte.
     * @return a boolean.
     */
    default boolean equals(byte state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * <p>equals.</p>
     *
     * @param state a short.
     * @return a boolean.
     */
    default boolean equals(short state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * <p>equals.</p>
     *
     * @param state a int.
     * @return a boolean.
     */
    default boolean equals(int state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * <p>equals.</p>
     *
     * @param state a long.
     * @return a boolean.
     */
    default boolean equals(long state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * <p>equals.</p>
     *
     * @param state a float.
     * @return a boolean.
     */
    default boolean equals(float state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * <p>equals.</p>
     *
     * @param state a double.
     * @return a boolean.
     */
    default boolean equals(double state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * <p>isHigh.</p>
     *
     * @return a boolean.
     */
    default boolean isHigh() {
        return this.state().isHigh();
    }

    /**
     * <p>isLow.</p>
     *
     * @return a boolean.
     */
    default boolean isLow() {
        return this.state().isLow();
    }
}
