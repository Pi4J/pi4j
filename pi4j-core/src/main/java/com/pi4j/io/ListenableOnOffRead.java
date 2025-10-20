package com.pi4j.io;
/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OnOff.java
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

import java.util.*;
import java.util.function.Consumer;

/**
 * OnOffRead with simple functional interface listener support.
 * <p>
 * The purpose of this interface is to provide a simple DigitalInput abstraction that can be used to stand
 * in for a "real" digital input, e.g. when providing IO pins managed by IO expanders to drivers.
 * </p>
 *
 * @param <T> See OnOffRead.
 */
public interface ListenableOnOffRead<T> extends OnOffRead<T> {

    /**
     * Adds a boolean consumer as a listener to this object. This method name was chosen to avoid any ambiguity with
     * addListener in Digital.
     */
    Consumer<Boolean> addConsumer(Consumer<Boolean> listener);

    T removeConsumer(Consumer<Boolean> listener);

    /**
     * A simple implementation that will notify listeners for state-changing on()/off() (or setState()) calls.
     */
    final class Impl implements ListenableOnOffRead<Impl>, OnOff<Impl> {
        private List<Consumer<Boolean>> listeners = new ArrayList<>();
        private boolean state;

        public Impl() {
            this(false);
        }

        public Impl(boolean initialState) {
            this.state = initialState;
        }

        @Override
        public Impl on() throws IOException {
            return setState(true);
        }

        @Override
        public Impl off() throws IOException {
            return setState(false);
        }

        @Override
        public Impl setState(boolean newState) {
            if (state != newState) {
                state = newState;
                for (Consumer<Boolean> listener: listeners) {
                    listener.accept(newState);
                }
            }
            return this;
        }

        @Override
        public boolean isOn() {
            return state;
        }

        @Override
        public Consumer<Boolean> addConsumer(Consumer<Boolean> listener) {
            listeners.add(listener);
            return listener;
        }

        @Override
        public Impl removeConsumer(Consumer<Boolean> listener) {
            listeners.remove(listener);
            return this;
        }
    }
}
