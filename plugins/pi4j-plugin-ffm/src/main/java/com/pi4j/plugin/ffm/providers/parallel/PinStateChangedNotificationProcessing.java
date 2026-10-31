package com.pi4j.plugin.ffm.providers.parallel;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEventProcessing;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import com.pi4j.plugin.ffm.providers.gpio.EventWatcher;

import java.util.List;
import java.util.function.IntUnaryOperator;

/**
 * {@link PinEventProcessing} to consume {@link LineEvent}s and emit {@link ParallelPort.PinStateChangedNotification}s
 */
final class PinStateChangedNotificationProcessing implements PinEventProcessing {

    private final ParallelPort eventSource;
    private final ParallelPort.PinStateChangeListener listener;
    private final IntUnaryOperator offsetMappingOperation;
    /**
     * Array to track the latest sequence number of each line as provided by {@link LineEvent#lineSeqno()}
     */
    private final int[] sequenceNumbers = new int[32];

    /**
     * @param eventSource the device generating events
     * @param listener the callback to invoke when events are processed
     * @param offsetMappingOperation pin offset remapping operation used when user offsets are not mapped sequentially
     */
    public PinStateChangedNotificationProcessing(
        ParallelPort eventSource,
        ParallelPort.PinStateChangeListener listener,
        IntUnaryOperator offsetMappingOperation
    ) {
        this.eventSource = eventSource;
        this.listener = listener;
        this.offsetMappingOperation = offsetMappingOperation;
    }

    /**
     * Handle the events detected by the {@link EventWatcher} and dispatch them to the registered listeners.
     *
     * @param eventList the list of line events detected
     */
    @Override
    public void process(List<LineEvent> eventList) {
        if (!eventList.isEmpty()) {
            listener.onPinStateChanged(eventsAsNotification(eventList));
        }
    }

    /**
     * Convert a list of {@link LineEvent} to a {@link ParallelPort.PinStateChangedNotification}
     *
     * @param eventList the list of detected events
     * @return a {@link ParallelPort.PinStateChangedNotification} representing the state changes
     */
    private ParallelPort.PinStateChangedNotification eventsAsNotification(List<LineEvent> eventList) {
        boolean eventsMissing = false;

        ParallelPort.PinStateChangedEvent[] changeEvents = new ParallelPort.PinStateChangedEvent[eventList.size()];
        for (int i = 0; i < eventList.size(); i++) {
            LineEvent event = eventList.get(i);
            changeEvents[i] = eventAsChangeEvent(event);

            int offset = event.offset();
            int sequenceNumber = event.lineSeqno();
            int lastSequenceNumber = sequenceNumbers[offset];
            eventsMissing |= (sequenceNumber - lastSequenceNumber > 1);
            sequenceNumbers[offset] = sequenceNumber;
        }

        return new ParallelPort.PinStateChangedNotification(eventSource, eventsMissing, changeEvents);
    }

    /**
     * Map a {@link LineEvent} to a {@link ParallelPort.PinStateChangedEvent}
     *
     * @param event the detected event
     * @return a {@link ParallelPort.PinStateChangedEvent} representing the state change
     */
    private ParallelPort.PinStateChangedEvent eventAsChangeEvent(LineEvent event) {
        // convert the hardware offset to the logical user-specified offset
        int offset = offsetMappingOperation.applyAsInt(event.offset());
        // represent the change type by its resulting digital state value
        var state = event.pinEvent() == PinEvent.RISING
            ? DigitalState.HIGH
            : DigitalState.LOW;

        return new ParallelPort.PinStateChangedEvent(offset, state);
    }
}
