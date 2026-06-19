package com.pi4j.io;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OnOffRead.java
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

/**
 * Read-only view of a two-state (on/off) I/O, exposing whether it is currently on or off.
 * It is the read half of {@link OnOff} and the basis for {@link ListenableOnOffRead}.
 *
 * @param <T> the concrete implementing type
 */
public interface OnOffRead<T> {
    /**
     * Returns the current state.
     *
     * @return {@code true} if currently on, {@code false} if off
     */
    boolean isOn();

    /**
     * Returns the inverse of {@link #isOn()}.
     *
     * @return {@code true} if currently off, {@code false} if on
     */
    default boolean isOff() { return !isOn(); };
}
