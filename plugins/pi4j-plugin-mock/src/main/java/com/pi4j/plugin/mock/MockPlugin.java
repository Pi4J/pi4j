package com.pi4j.plugin.mock;

import com.pi4j.context.Context;
import com.pi4j.context.ContextConfig;
import com.pi4j.extension.Plugin;

/**
 * Pi4J {@link Plugin} that contributes the in-memory, hardware-free Mocks, allowing Pi4J to
 * run without real GPIO/I2C/SPI/PWM hardware (for example in unit tests).
 */
public class MockPlugin implements Plugin {

    @Override
    public Context createContext(ContextConfig config) {
        return new MockContext(config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always returns {@code true}, marking this plugin and its providers as mock (non-hardware)
     * implementations.
     */
    @Override
    public boolean isMock() {
        return true;
    }
}
