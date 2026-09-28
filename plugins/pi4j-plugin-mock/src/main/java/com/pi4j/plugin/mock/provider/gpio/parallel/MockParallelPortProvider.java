package com.pi4j.plugin.mock.provider.gpio.parallel;

import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.gpio.parallel.ParallelPortProvider;
import com.pi4j.provider.ProviderBase;

/**
 * Placeholder mock implementation of a {@link ParallelPortProvider}
 */
public class MockParallelPortProvider
    extends ProviderBase<ParallelPortProvider, ParallelPort, ParallelPortConfig>
    implements ParallelPortProvider {

    public MockParallelPortProvider() {
        super("mock-gpio-port", "Mock GPIO Port");
    }

    @Override
    public MockParallelPort create(ParallelPortConfig config) {
        return new MockParallelPort(this, config);
    }
}
