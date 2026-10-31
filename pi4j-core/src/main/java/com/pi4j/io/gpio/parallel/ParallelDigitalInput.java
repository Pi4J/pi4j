package com.pi4j.io.gpio.parallel;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputBase;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * {@link DigitalInput} which delegates to a {@link ParallelPort} to perform IO
 */
public class ParallelDigitalInput extends DigitalInputBase implements DigitalInput {

    private final ParallelPort parallelPort;
    private final Map<DigitalStateChangeListener, DigitalEventDelegate> listenerMap = new HashMap<>();

    /**
     * Creates a digital input bound to the specified port
     *
     * @param config the digital configuration
     * @param parallelPort the delegate port
     * @param onClose a callback to be invoked on close
     */
    public ParallelDigitalInput(DigitalInputConfig config, ParallelPort parallelPort, Consumer<DigitalInput> onClose) {
        super(config, onClose);
        this.parallelPort = parallelPort;
    }

    @Override
    public void applyInitialState() {
        parallelPort.applyInitialState();
        super.applyInitialState();
    }

    @Override
    public DigitalState state() {
        return DigitalState.getState(parallelPort.read() != 0);
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

    @Override
    public void close() {
        super.close();
        parallelPort.close();
    }

    /**
     * Proxy to dispatch {@link com.pi4j.io.gpio.parallel.ParallelPort.PinStateChangedNotification}s as
     * {@link DigitalStateChangeEvent}s
     */
    private class DigitalEventDelegate implements ParallelPort.PinStateChangeListener {

        private final List<DigitalStateChangeListener> listeners;

        /**
         * @param listeners the digital listeners to be notified of events
         */
        public DigitalEventDelegate(DigitalStateChangeListener... listeners) {
            this.listeners = List.of(listeners);
        }

        @Override
        public void onPinStateChanged(ParallelPort.PinStateChangedNotification notification) {
            for (ParallelPort.PinStateChangedEvent event : notification.events()) {
                var stateChangeEvent = new DigitalStateChangeEvent<>(
                    ParallelDigitalInput.this,
                    event.newState()
                );

                listeners.forEach(listener -> listener.onDigitalStateChange(stateChangeEvent));
            }
        }
    }
}
