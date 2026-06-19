package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalOutputBase.java
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
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Base implementation of {@link DigitalOutput} that tracks the current {@link DigitalState}, applies the
 * configured initial and shutdown states, and provides shared blocking/asynchronous {@code pulse} and
 * {@code blink} behaviour. Concrete providers extend this class and override {@link #state(DigitalState)} to
 * actually drive the hardware.
 */
public abstract class DigitalOutputBase extends DigitalBase<DigitalOutput, DigitalOutputConfig, DigitalOutputProvider> implements DigitalOutput {

    /** The current cached state of this output; {@link DigitalState#UNKNOWN} until first set. */
    protected DigitalState state = DigitalState.UNKNOWN;

    /**
     * Creates a new digital output bound to the given provider and configuration.
     *
     * @param provider the provider that created and manages this output instance
     * @param config   the configuration describing the pin address, initial state, shutdown state and identity
     */
    public DigitalOutputBase(DigitalOutputProvider provider, DigitalOutputConfig config) {
        super(provider, config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * After the base initialization completes, the output is driven to the configured initial state if
     * {@link DigitalOutputConfig#initialState()} is set.
     *
     * @throws InitializeException if base initialization fails or the initial state cannot be written
     */
    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        super.initialize(context);

        // update the value to the initial value if an initial value was configured
        if (config().initialState() != null) {
            try {
                state(config().initialState());
            } catch (IOException e) {
                throw new InitializeException(e);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the cached state and, only when the state actually changes, dispatches a
     * {@link DigitalStateChangeEvent} to any registered listeners or bindings.
     */
    @Override
    public DigitalOutput state(DigitalState state) throws IOException {

        if (!this.state.equals(state)) {
            this.state = state;
            if (this.hasListenersOrBindings()) {
                this.dispatch(new DigitalStateChangeEvent<>(this, this.state));
            }
        }
        return this;
    }

    @Override
    public DigitalOutput pulse(int interval, TimeUnit unit, DigitalState state, Callable<Void> callback) throws IOException {

        long millis = validateArguments(interval, unit);

        // start the pulse state
        this.state(state);

        // block the current thread for the pulse duration
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Pulse blocking thread interrupted.", e);
        }

        // end the pulse state
        this.state(DigitalState.getInverseState(state));

        // invoke callback if one was defined
        if (callback != null) {
            try {
                logger.info("Calling callback from blocking pulse() method");
                callback.call();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return this;
    }

    @Override
    public Future<?> pulseAsync(int interval, TimeUnit unit, DigitalState state, Callable<Void> callback) {
        validateArguments(interval, unit);
        return context().submitTask(() -> pulse(interval, unit, state, callback));
    }

    /**
     * This method will blink an output pin of the RPi according the given specifications.
     * The pin itself is created while creating a DigitalOutput configuration where one of
     * the parameters is an address (= a BCM pin number).
     *
     * @param delay    The toggle time.
     * @param duration The amount of times the output has to toggle.
     *                 <p>
     *                 Representation:
     *
     *                 <pre>
     *                   Output HIGH +-----+     +-----+     +-----+     +-----+     +-----+
     *                               |     |     |     |     |     |     |     |     |     |
     *                   Output LOW  +     +-----+     +-----+     +-----+     +-----+     +-----+
     *                               ^                                                           ^
     *                        start -┘                                                           └- stop
     *                                \___/ \___/
     *                                delay  delay
     *
     *                               \___________________________________________________________/
     *                                                        duration
     *                 </pre>
     *                 <p>
     *                 Example:
     *                 <p style = "margin-left: 100px">
     *                 Delay = 1 sec / duration = 5<br>
     *                 Output will be like so (suppose the initial state is set to HIGH):<br>
     *                 1 - 0 - 1 - 0 - 1 - 0 - 1 - 0 - 1 - 0 with each state lasting for 1 second.<br>
     *                 So, if you would connect a LED to the pin, you would see the LED switching<br>
     *                 on and off for 5 times.<br>
     *                 </p>
     *                 <p>
     *                 <b>Note: this is a blocking method!</b><br>
     *                 For as long as it takes to manipulate the output pin, the method will not return.<br>
     *                 <p>
     *                 In the example given above, it means the method will block for 10 seconds (5 times high for a second<br>
     *                 and 5 times low for a second), also for calling the callback function.
     *                 <p>
     *                 If you don't want the <code>blink()</code> method to block the calling thread, pls. use the
     *                 {@link #blinkAsync(int, int, java.util.concurrent.TimeUnit, com.pi4j.io.gpio.digital.DigitalState, java.util.concurrent.Callable) blinkAsync()} method instead.<br>
     *                 <p>
     * @param unit     The time unit used to calculate the delay.
     * @param state    The initial state of the pin.
     * @param callback The method to call, if any, once the blinking is done.
     * @return The DigitalOutputBase object itself.
     */
    @Override
    public DigitalOutput blink(int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback) {

        long millis = validateArguments(delay, duration, unit);

        this.state(state);

        for (int i = 0; i < ((duration * 2) - 1); i++) {
            // block the current thread for the pulse duration
            // if you don't want a blocking call, pls. use the blinkAsync() method instead.
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException("Pulse blocking thread interrupted. Exception message: [" + e.getMessage() + "].");
            }

            // toggle the pulse state
            toggle();
        }

        // invoke callback if one was defined
        if (callback != null) {
            try {
                logger.info("Calling callback from blocking blink() method");
                callback.call();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return this;
    }

    /**
     * This method is exactly the same as the blink() method, except that this method is <b>non-blocking</b> and returns a {@link Future} with which the action can be cancelled, or it can be detected if the task is complete
     * <p>
     * See the {@link #blink(int, int, java.util.concurrent.TimeUnit, com.pi4j.io.gpio.digital.DigitalState, java.util.concurrent.Callable) blink()}
     * method for a more detailed explanation on how the method works.
     *
     * @param delay    The toggle time.
     * @param duration The amount of times the output has to toggle.
     * @param unit     The time unit used to calculate the delay.
     * @param state    The initial state of the pin.
     * @param callback The method to call, if any, once the blinking is done.
     * @return A Future object that can be used to observe the end of the async blinking.
     */
    @Override
    public Future<?> blinkAsync(int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback) {
        validateArguments(delay, duration, unit);
        return context().submitTask(() -> blink(delay, duration, unit, state, callback));
    }

    @Override
    public DigitalState state() {
        return this.state;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Before delegating to the base shutdown logic, drives the output to the configured shutdown state if
     * {@link DigitalOutputConfig#shutdownState()} is set and not {@link DigitalState#UNKNOWN}.
     *
     * @throws ShutdownException if the shutdown state cannot be written or base shutdown fails
     */
    @Override
    public DigitalOutput shutdownInternal(Context context) throws ShutdownException {
        // set pin state to the shutdown state if a shutdown state is configured
        if (config().shutdownState() != null && config().shutdownState() != DigitalState.UNKNOWN) {
            try {
                state(config().shutdownState());
            } catch (IOException e) {
                throw new ShutdownException(e);
            }
        }
        return super.shutdownInternal(context);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Drives the output to the configured {@link DigitalConfig#onState()}, defaulting to
     * {@link DigitalState#HIGH} when none is configured.
     */
    @Override
    public DigitalOutput on() throws IOException {

        // the default ON state is HIGH
        DigitalState onState = DigitalState.HIGH;

        // get configured ON state
        if (config().onState() != null) {
            onState = config().onState();
        }

        // set the current state to the configured ON state
        return state(onState);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Drives the output to the inverse of the configured {@link DigitalConfig#onState()}, defaulting to
     * {@link DigitalState#LOW} when no on-state is configured.
     */
    @Override
    public DigitalOutput off() throws IOException {
        // the default OFF state is LOW
        DigitalState offState = DigitalState.LOW;

        // get configured ON state; then set OFF state to inverse of ON state
        if (config().onState() != null) {
            offState = DigitalState.getInverseState(config().onState());
        }

        // set the current state to the configured OFF state
        return state(offState);
    }


    ////////////////////////////////////////////////////////////////////////////////
    // Private section
    ////////////////////////////////////////////////////////////////////////////////


    /**
     * This method verifies the interval given and indirectly the time unit given.
     * The interval must be > 0, else an IllegalArgumentException is thrown.
     *
     * @param interval The output change interval.
     * @param unit     A time unit.
     * @return Number of milliseconds.
     */
    private long validateArguments(int interval, TimeUnit unit) {

        if (interval <= 0) {
            throw new IllegalArgumentException("A time interval of zero or less is not supported.");
        }

        return validateTimeUnit(interval, unit);
    }


    /**
     * This method verifies the interval and duration given and indirectly the time unit given.
     * Both the interval as well as the duration must be > 0, else an IllegalArgumentException is thrown.
     *
     * @param interval The output change interval.
     * @param duration The amount of times the output toggles.
     * @param unit     A time unit.
     * @return Number of milliseconds.
     */
    private long validateArguments(int interval, int duration, TimeUnit unit) {

        if (interval <= 0) {
            throw new IllegalArgumentException("A time interval of zero or less is not supported.");
        }

        if (duration <= 0) {
            throw new IllegalArgumentException("A time duration of zero or less is not supported.");
        }

        return validateTimeUnit(interval, unit);
    }


    /**
     * This method verifies the time unit given.
     * When an unsupported unit is encountered, an IllegalArgumentException is thrown.
     * Unsupported time units are:
     * - TimeUnit.NANOSECONDS
     * - TimeUnit.MICROSECONDS
     * - TimeUnit.DAYS
     *
     * @param unit A time unit.
     * @return Number of milliseconds.
     */
    private long validateTimeUnit(int interval, TimeUnit unit) {
        long millis;
        switch (unit) {
            case NANOSECONDS:
                throw new IllegalArgumentException("TimeUnit.NANOSECONDS is not supported.");
            case MICROSECONDS:
                throw new IllegalArgumentException("TimeUnit.MICROSECONDS is not supported.");
            case DAYS:
                throw new IllegalArgumentException("TimeUnit.DAYS is not supported.");
            default:
                millis = unit.toMillis(interval);
                break;
        }

        return millis;
    }
}