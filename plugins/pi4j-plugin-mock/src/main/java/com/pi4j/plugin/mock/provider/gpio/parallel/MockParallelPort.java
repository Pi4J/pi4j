package com.pi4j.plugin.mock.provider.gpio.parallel;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.MaskUtils;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortBase;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Placeholder mock implementation of a {@link ParallelPort}
 */
public class MockParallelPort
    extends ParallelPortBase
    implements ParallelPort {

    private final AtomicInteger value;

    /**
     * Creates a new GPIO I/O instance bound to the given provider and configuration.
     *
     * @param context  the context of this instance
     * @param config   the {@link ParallelPortConfig} describing this I/O, including its BCM pin numbers
     */
    public MockParallelPort(Context context, ParallelPortConfig config) {
        super(context, config);
        this.value = new AtomicInteger((int) (config.initialValue() & MaskUtils.packed(config.mask())));
    }

    /**
     * Allow the backing value to be set directly for testing purposes.
     * @param value the value to set
     */
    public void mockValue(int value) {
        // don't raise events for output ports
        if (this.getDirection() == Direction.OUTPUT) {
            return;
        }

        var changeEvents = eventsForChange(this, value, this.value.get());
        handleWrite(value);
        changeEvents.forEach(events::dispatch);
    }

    @Override
    protected void handleWrite(int value) {
        this.value.set(value);
    }

    @Override
    protected int handleRead() {
        return value.get();
    }

    /**
     * Calculates the list of pin state change events based on the difference between the current and previous values.
     * @param source the port to attach to the event
     * @param value the value being set
     * @param previous the previous value
     * @return the list of pin state change events
     */
    static List<PinStateChangedEvent> eventsForChange(ParallelPort source, int value, int previous) {
        var diff = value ^ previous;
        return Arrays.stream(MaskUtils.offsets(diff))
            .mapToObj(offset -> new PinStateChangedEvent(
                source,
                offset,
                (((1 << offset) & value) == 0) ? DigitalState.LOW : DigitalState.HIGH)
            )
            .sorted(Comparator.comparingInt(PinStateChangedEvent::offset))
            .toList();
    }
}
