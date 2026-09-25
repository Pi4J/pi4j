package com.pi4j.plugin.mock.provider.gpio.parallel;

import com.pi4j.io.gpio.MaskUtils;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortBase;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.gpio.parallel.ParallelPortProvider;

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
     * @param provider the {@link ParallelPortProvider} that creates and backs this I/O instance
     * @param config   the {@link ParallelPortConfig} describing this I/O, including its BCM pin numbers
     */
    public MockParallelPort(ParallelPortProvider provider, ParallelPortConfig config) {
        super(provider, config);
        this.value = new AtomicInteger((int) (config.initialValue() & MaskUtils.packed(config.mask())));
    }

    /**
     * Allow the backing value to be set directly for testing purposes.
     * @param value the value to set
     */
    public void mockValue(int value) {
        handleWrite(value);
        fireEventWithValue(value);
    }

    @Override
    protected void handleWrite(int value) {
        this.value.set(value);
    }

    @Override
    protected int handleRead() {
        return value.get();
    }
}
