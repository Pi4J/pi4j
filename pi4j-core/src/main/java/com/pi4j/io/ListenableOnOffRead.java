package com.pi4j.io;
import com.pi4j.io.exception.IOException;

import java.util.*;
import java.util.function.Consumer;

/**
 * An {@link OnOffRead} state that can be observed by registering listeners notified on state changes.
 * <p>
 * It provides a lightweight {@link com.pi4j.io.gpio.digital.DigitalInput} abstraction that can stand in
 * for a "real" digital input, for example when exposing I/O pins managed by I/O expanders to drivers.
 * A ready-made {@link Impl} implementation is supplied for that purpose.
 *
 * @param <T> the concrete type returned by the fluent operations; see {@link OnOffRead}
 */
public interface ListenableOnOffRead<T> extends OnOffRead<T> {

    /**
     * Registers a listener to be notified with the new on/off state whenever the state changes.
     * <p>
     * The name {@code addConsumer} (rather than {@code addListener}) was chosen to avoid ambiguity
     * with {@code addListener} in {@link com.pi4j.io.gpio.digital.Digital}.
     *
     * @param listener a consumer invoked with {@code true} when turned on and {@code false} when turned off
     * @return the same listener instance, so it can be retained for later removal
     */
    Consumer<Boolean> addConsumer(Consumer<Boolean> listener);

    /**
     * Removes a previously registered listener.
     *
     * @param listener the listener to remove
     * @return this instance for method chaining
     */
    T removeConsumer(Consumer<Boolean> listener);

    /**
     * A simple in-memory {@link ListenableOnOffRead} that also supports writing via {@link OnOff},
     * notifying registered listeners whenever a state-changing {@code on()}/{@code off()}/{@code setState()}
     * call actually changes the current state.
     */
    final class Impl implements ListenableOnOffRead<Impl>, OnOff<Impl> {
        private List<Consumer<Boolean>> listeners = new ArrayList<>();
        private boolean state;

        /**
         * Creates a new instance in the off state.
         */
        public Impl() {
            this(false);
        }

        /**
         * Creates a new instance with the given initial state.
         *
         * @param initialState {@code true} to start in the on state, {@code false} for off
         */
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
