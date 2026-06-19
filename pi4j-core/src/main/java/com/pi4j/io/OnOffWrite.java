package com.pi4j.io;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OnOffWrite.java
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

import com.pi4j.io.exception.IOException;

/**
 * Write side of a binary on/off I/O capability, switching a device or signal between its
 * two logical states. It is combined with its read counterpart in {@link OnOff} and is
 * implemented by binary outputs such as {@link com.pi4j.io.gpio.digital.DigitalOutput}.
 *
 * @param <T> the concrete I/O type returned by the state-changing methods, enabling fluent
 *            method chaining on the implementing instance
 */
public interface OnOffWrite<T> {
    /**
     * Switches this I/O to its "on" (active/high) state.
     *
     * @return this instance for method chaining
     * @throws IOException if the underlying I/O operation fails
     */
    T on() throws IOException;

    /**
     * Switches this I/O to its "off" (inactive/low) state.
     *
     * @return this instance for method chaining
     * @throws IOException if the underlying I/O operation fails
     */
    T off() throws IOException;

    /**
     * Sets the state from a boolean, switching on when {@code true} and off when {@code false}.
     *
     * @param state {@code true} to switch on, {@code false} to switch off
     * @return this instance for method chaining
     * @throws IOException if the underlying I/O operation fails
     */
    @SuppressWarnings("unchecked")
    default T setState(boolean state) throws IOException {
        if (state) {
            on();
        } else {
            off();
        }
        return (T) this;
    }
}
