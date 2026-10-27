package com.pi4j.io.gpio.digital;

import java.util.function.Consumer;

/**
 * Abstract base class for {@link DigitalInput} implementations, specializing {@link DigitalBase}
 * with the digital-input type parameters. Provider-specific subclasses extend this to supply the
 * actual hardware or expander read behaviour.
 */
public abstract class DigitalInputBase extends DigitalBase<DigitalInputConfig> implements DigitalInput {
    /**
     * Creates a digital input bound to the given provider and configuration.
     *
     * @param config  the configuration describing this input (pin, pull resistance, debounce, etc.)
     * @param onClose an optional close handler
     */
    protected DigitalInputBase(DigitalInputConfig config, Consumer<?> onClose){
        super(config, onClose);
    }


    @Override
    public Consumer<Boolean> addConsumer(Consumer<Boolean> listener) {
        DigitalState on = config().onState();
        addListener(new ConsumerAdapter(listener, on != null ? on : DigitalState.HIGH));
        return listener;
    }

    @Override
    public void removeConsumer(Consumer<Boolean> listener) {
        stateChangeEventManager.remove(
            (candidate) -> (candidate instanceof ConsumerAdapter)
                && ((ConsumerAdapter) candidate).consumer == listener);
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
