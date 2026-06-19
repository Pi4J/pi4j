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
import com.pi4j.io.gpio.Gpio;

/**
 * Base contract for a two-state (HIGH/LOW) digital I/O instance in Pi4J, such as a
 * {@link DigitalInput} or digital output. Extends the generic {@link Gpio} contract with
 * {@link DigitalState} access and adds listener registration for state-change events, while
 * {@link ListenableOnOffRead} provides the on/off abstraction shared with simpler I/O types.
 *
 * @param <DIGITAL_TYPE> the concrete digital I/O type, used as the self-referencing return type for fluent methods
 * @param <CONFIG_TYPE> the {@link DigitalConfig} type describing this instance
 * @param <PROVIDER_TYPE> the {@link DigitalProvider} type that created this instance
 */
public interface Digital<DIGITAL_TYPE extends Digital<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
    CONFIG_TYPE extends DigitalConfig<CONFIG_TYPE>,
    PROVIDER_TYPE extends DigitalProvider>
    extends Gpio<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
    ListenableOnOffRead<DIGITAL_TYPE> {

    /**
     * Returns the current logic level of this digital I/O instance.
     *
     * @return the present {@link DigitalState} (typically HIGH or LOW)
     */
    DigitalState state();

    /**
     * Returns the BCM (Broadcom) GPIO pin number this instance is bound to, as supplied by its configuration.
     *
     * @return the configured BCM pin number, or {@code null} if none was configured
     */
    default Integer bcm() {
        return config().bcm();
    }

    /**
     * Registers one or more listeners to be notified whenever this instance's {@link DigitalState} changes.
     *
     * @param listener one or more {@link DigitalStateChangeListener} instances to register
     * @return this instance for method chaining
     */
    DIGITAL_TYPE addListener(DigitalStateChangeListener... listener);

    /**
     * Unregisters one or more previously added state-change listeners.
     *
     * @param listener one or more {@link DigitalStateChangeListener} instances to remove
     * @return this instance for method chaining
     */
    DIGITAL_TYPE removeListener(DigitalStateChangeListener... listener);

    /**
     * Tests whether this instance's current state equals the given {@link DigitalState}.
     *
     * @param state the state to compare against
     * @return {@code true} if the current state matches {@code state}
     */
    default boolean equals(DigitalState state) {
        return this.state().equals(state);
    }

    /**
     * Tests whether this instance's current state equals the state derived from the given number,
     * where a non-zero value maps to HIGH and zero maps to LOW.
     *
     * @param state the numeric value to interpret as a {@link DigitalState}
     * @return {@code true} if the current state matches the derived state
     */
    default boolean equals(Number state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * Tests whether this instance's current state equals the state derived from the given boolean,
     * where {@code true} maps to HIGH and {@code false} maps to LOW.
     *
     * @param state the boolean value to interpret as a {@link DigitalState}
     * @return {@code true} if the current state matches the derived state
     */
    default boolean equals(boolean state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * Tests whether this instance's current state equals the state derived from the given byte,
     * where a non-zero value maps to HIGH and zero maps to LOW.
     *
     * @param state the byte value to interpret as a {@link DigitalState}
     * @return {@code true} if the current state matches the derived state
     */
    default boolean equals(byte state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * Tests whether this instance's current state equals the state derived from the given short,
     * where a non-zero value maps to HIGH and zero maps to LOW.
     *
     * @param state the short value to interpret as a {@link DigitalState}
     * @return {@code true} if the current state matches the derived state
     */
    default boolean equals(short state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * Tests whether this instance's current state equals the state derived from the given int,
     * where a non-zero value maps to HIGH and zero maps to LOW.
     *
     * @param state the int value to interpret as a {@link DigitalState}
     * @return {@code true} if the current state matches the derived state
     */
    default boolean equals(int state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * Tests whether this instance's current state equals the state derived from the given long,
     * where a non-zero value maps to HIGH and zero maps to LOW.
     *
     * @param state the long value to interpret as a {@link DigitalState}
     * @return {@code true} if the current state matches the derived state
     */
    default boolean equals(long state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * Tests whether this instance's current state equals the state derived from the given float,
     * where a non-zero value maps to HIGH and zero maps to LOW.
     *
     * @param state the float value to interpret as a {@link DigitalState}
     * @return {@code true} if the current state matches the derived state
     */
    default boolean equals(float state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * Tests whether this instance's current state equals the state derived from the given double,
     * where a non-zero value maps to HIGH and zero maps to LOW.
     *
     * @param state the double value to interpret as a {@link DigitalState}
     * @return {@code true} if the current state matches the derived state
     */
    default boolean equals(double state) {
        return equals(DigitalState.getState(state));
    }

    /**
     * Convenience test for whether the current state is HIGH.
     *
     * @return {@code true} if the current {@link DigitalState} is HIGH
     */
    default boolean isHigh() {
        return this.state().isHigh();
    }

    /**
     * Convenience test for whether the current state is LOW.
     *
     * @return {@code true} if the current {@link DigitalState} is LOW
     */
    default boolean isLow() {
        return this.state().isLow();
    }
}
