package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;


/**
 * Base implementation of {@link DigitalOutput} that tracks the current {@link DigitalState} and applies the
 * configured initial and shutdown states. Concrete providers extend this class and override {@link #state(DigitalState)} to
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

}