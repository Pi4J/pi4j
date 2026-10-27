package com.pi4j.io.gpio.digital;

import com.pi4j.event.EventDelegate;
import com.pi4j.event.EventManager;
import com.pi4j.io.IOBase;
import com.pi4j.io.IOConfig;

import java.util.function.Consumer;

/**
 * Abstract base implementation of {@link Digital}, providing the listener management and
 * state-change event dispatch shared by all digital I/O types. Subclasses such as
 * {@link DigitalInputBase} build on this to add their specific I/O behaviour, while the
 * generic {@link IOBase} supplies the common GPIO lifecycle support.
 *
 * @param <CONFIG_TYPE> the {@link IOConfig} type describing this instance
 */
public abstract class DigitalBase<CONFIG_TYPE extends IOConfig>
        extends IOBase<CONFIG_TYPE>
        implements Digital<CONFIG_TYPE>
{
    // internal listeners collection
    protected final EventManager<Digital, DigitalStateChangeListener, DigitalStateChangeEvent> stateChangeEventManager;

    /**
     * Creates a digital I/O instance bound to the given provider and configuration, and initializes
     * the internal event manager used to deliver {@link DigitalStateChangeEvent}s to listeners.
     *
     * @param config  the configuration describing this instance (pin, on-state, etc.)
     * @param onClose an optional close handler
     */
    protected DigitalBase(CONFIG_TYPE config, Consumer<?> onClose) {
        super(config, onClose);

        // create an event manager for digital state change events
        stateChangeEventManager  = new EventManager(this,
                (EventDelegate<DigitalStateChangeListener, DigitalStateChangeEvent>)
                        (listener, event) -> listener.onDigitalStateChange(event));
    }

    @Override
    public void addListener(DigitalStateChangeListener... listener) {
        stateChangeEventManager.add(listener);
    }

    @Override
    public void removeListener(DigitalStateChangeListener... listener) {
        stateChangeEventManager.remove(listener);
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
    public void close() {
        if (closed) {
            return;
        }
        stateChangeEventManager.clear();
        super.close();
    }
}
