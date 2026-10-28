package com.pi4j.plugin.ffm.providers.parallel;

import com.pi4j.io.gpio.digital.DigitalBase;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputBase;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.io.gpio.parallel.ParallelPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ParallelDigitalInput extends DigitalInputBase implements DigitalInput {

    private final ParallelPort parallelPort;
    private final Map<DigitalStateChangeListener, DigitalEventDelegate> listenerMap = new HashMap<>();

    /**
     * Creates a digital input bound to the given provider and configuration.
     *
     * @param config  the configuration describing this input (pin, pull resistance, debounce, etc.)
     */
    public ParallelDigitalInput(DigitalInputConfig config, ParallelPort parallelPort, Consumer<DigitalInput> onClose) {
        super(config, onClose);
        this.parallelPort = parallelPort;
    }

    @Override
    public void applyInitialState() {
        super.applyInitialState();
        parallelPort.applyInitialState();
    }

    @Override
    public DigitalState state() {
        return DigitalState.getState((parallelPort.read() & 1 << config.bcm()) != 0);
    }

    @Override
    public void addListener(DigitalStateChangeListener... listener) {
        var listenerDelegate = new DigitalEventDelegate(listener);
        for (DigitalStateChangeListener digitalStateChangeListener : listener) {
            listenerMap.put(digitalStateChangeListener, listenerDelegate);
        }
        parallelPort.addListener(listenerDelegate);
    }

    @Override
    public void removeListener(DigitalStateChangeListener... listener) {
        for (DigitalStateChangeListener l : listener) {
            DigitalEventDelegate delegate = listenerMap.remove(l);
            if (delegate != null) {
                parallelPort.removeListener(delegate);
            }
        }
    }

    private class DigitalEventDelegate implements ParallelPort.PinStateChangeListener {

        private final List<DigitalStateChangeListener> listeners;

        public DigitalEventDelegate(DigitalStateChangeListener... listeners) {
            this.listeners = List.of(listeners);
        }

        @Override
        public void onPinStateChanged(ParallelPort.PinStateChangedNotification notification) {
            for (ParallelPort.PinStateChangedEvent event : notification.events()) {
                var stateChangeEvent = new DigitalStateChangeEvent<>(
                    ParallelDigitalInput.this,
                    DigitalState.getState((event.offset() & 1 << config.bcm()) != 0)
                );

                listeners.forEach(listener -> listener.onDigitalStateChange(stateChangeEvent));
            }
        }
    }
}
