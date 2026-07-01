package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.event.EventDelegate;
import com.pi4j.event.EventManager;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.GpioBase;

import java.util.function.Consumer;

/**
 * Abstract base implementation of {@link Digital}, providing the listener management and
 * state-change event dispatch shared by all digital I/O types. Subclasses such as
 * {@link DigitalInputBase} build on this to add their specific I/O behaviour, while the
 * generic {@link GpioBase} supplies the common GPIO lifecycle support.
 *
 * @param <DIGITAL_TYPE> the concrete digital I/O type, used as the self-referencing return type for fluent methods
 * @param <CONFIG_TYPE> the {@link DigitalConfig} type describing this instance
 * @param <PROVIDER_TYPE> the {@link DigitalProvider} type that created this instance
 */
public abstract class DigitalBase<DIGITAL_TYPE extends Digital<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
        CONFIG_TYPE extends DigitalConfig<CONFIG_TYPE>,
        PROVIDER_TYPE extends DigitalProvider>
        extends GpioBase<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>
        implements Digital<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>
{
    // internal listeners collection
    protected final EventManager<DIGITAL_TYPE, DigitalStateChangeListener, DigitalStateChangeEvent> stateChangeEventManager;

    /**
     * Creates a digital I/O instance bound to the given provider and configuration, and initializes
     * the internal event manager used to deliver {@link DigitalStateChangeEvent}s to listeners.
     *
     * @param provider the {@link DigitalProvider} responsible for this instance's underlying I/O
     * @param config the configuration describing this instance (pin, on-state, etc.)
     */
    public DigitalBase(PROVIDER_TYPE provider, CONFIG_TYPE config){
        super(provider,config);

        // create an event manager for digital state change events
        stateChangeEventManager  = new EventManager(this,
                (EventDelegate<DigitalStateChangeListener, DigitalStateChangeEvent>)
                        (listener, event) -> listener.onDigitalStateChange(event));
    }

    @Override
    public Consumer<Boolean> addConsumer(Consumer<Boolean> listener) {
        addListener(new ConsumerAdapter(listener, config().onState() != null ? config().onState() : DigitalState.HIGH));
        return listener;
    }

    @Override
    public DIGITAL_TYPE removeConsumer(Consumer<Boolean> listener) {
        stateChangeEventManager.remove(
            (candidate) -> (candidate instanceof ConsumerAdapter)
                && ((ConsumerAdapter) candidate).consumer == listener);
        return (DIGITAL_TYPE) this;
    }

    @Override
    public DIGITAL_TYPE addListener(DigitalStateChangeListener... listener) {
        stateChangeEventManager.add(listener);
        return (DIGITAL_TYPE)this;
    }

    @Override
    public DIGITAL_TYPE removeListener(DigitalStateChangeListener... listener) {
        stateChangeEventManager.remove(listener);
        return (DIGITAL_TYPE)this;
    }

    /**
     * Indicates whether any state-change listeners (including consumer adapters) are currently registered.
     *
     * @return {@code true} if at least one listener is registered
     */
    public boolean hasListenersOrBindings() {
        return stateChangeEventManager.hasListeners();
    }


    /**
     * Delivers a state-change event to all registered listeners; intended to be called by subclasses
     * when the underlying I/O reports a transition.
     *
     * @param event the {@link DigitalStateChangeEvent} describing the new state and its source
     */
    protected void dispatch(DigitalStateChangeEvent event){
        stateChangeEventManager.dispatch(event);
    }

    @Override
    public DIGITAL_TYPE shutdownInternal(Context context) throws ShutdownException {
        // remove all listeners
        stateChangeEventManager.clear();

        // return this instance
        return (DIGITAL_TYPE) this;
    }

    @Override
    public boolean isOn() {
        // the default ON state is HIGH
        DigitalState onState = DigitalState.HIGH;

        // get configured ON state
        if(config().onState() != null){
            onState = config().onState();
        }

        // return TRUE if the current state matches the configured ON state
        return state().equals(onState);
    }

    private static class ConsumerAdapter implements DigitalStateChangeListener {
        private final Consumer<Boolean> consumer;
        private final DigitalState onState;

        private ConsumerAdapter(Consumer<Boolean> consumer, DigitalState onState) {
            this.consumer = consumer;
            this.onState = onState;
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            consumer.accept(event.state().equals(onState));
        }
    }
}
