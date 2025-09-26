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
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;

import java.util.*;
import java.util.function.Consumer;

/**
 * OnOffRead with simple functional interface listener support.
 * <p>
 * The purpose of this interface is to provide a simple DigitalInput abstraction that can be used to stand
 * in for a "real" digital input, e.g. when providing IO pins managed by IO expanders to drivers.
 * </p>
 */
public interface ListenableOnOffRead<T> extends OnOffRead<T> {

    void addListener(Consumer<Boolean> listener);
    void removeListener(Consumer<Boolean> listener);

    static ListenableOnOffRead<DigitalInput> wrap(DigitalInput digitalInput) {
        return new ListenableOnOffRead<>() {
            Map<Consumer<Boolean>, DigitalStateChangeListener> listeners = new HashMap<>();
            @Override
            public void addListener(Consumer<Boolean> listener) {
                DigitalStateChangeListener wrapper = new DigitalStateChangeListener() {
                    @Override
                    public void onDigitalStateChange(DigitalStateChangeEvent event) {
                        listener.accept(event.state().equals(true));
                    }
                };
                digitalInput.addListener(wrapper);
                listeners.put(listener, wrapper);
            }

            @Override
            public void removeListener(Consumer<Boolean> listener) {
                DigitalStateChangeListener wrapper = listeners.remove(listener);
                if (wrapper != null) {
                    digitalInput.removeListener(wrapper);
                }
            }

            @Override
            public boolean isOn() {
                return digitalInput.isOn();
            }
        };
    }

    /**
     * A simple implementation that will notify listeners for state-changing on()/off() (or setState()) calls.
     */
    class Impl<T> implements ListenableOnOffRead<T>, OnOff<T> {
        private List<Consumer<Boolean>> listeners = new ArrayList<>();
        private boolean state;

        public Impl() {
            this(false);
        }

        public Impl(boolean initialState) {
            this.state = initialState;
        }

        @Override
        public T on() throws IOException {
            if (!state) {
                state = true;
                for (Consumer<Boolean> listener: listeners) {
                    listener.accept(true);
                }
            }
            return (T) this;
        }

        @Override
        public T off() throws IOException {
            if (!state) {
                state = false;
                for (Consumer<Boolean> listener: listeners) {
                    listener.accept(false);
                }
            }
            return (T) this;
        }

        @Override
        public boolean isOn() {
            return state;
        }

        @Override
        public void addListener(Consumer<Boolean> listener) {
            listeners.add(listener);
        }

        @Override
        public void removeListener(Consumer<Boolean> listener) {
            listeners.remove(listener);
        }
    }
}
