package com.pi4j.io.gpio.digital;


import com.pi4j.context.Context;
import com.pi4j.io.Input;

/**
 * Represents a digital input pin that reads a HIGH/LOW logic level from a GPIO source, optionally with
 * a configured pull resistance and debounce interval. This is the read-only digital counterpart created
 * by a {@link DigitalInputProvider} and configured via {@link DigitalInputConfig}.
 */
public interface DigitalInput extends Digital<DigitalInput, DigitalInputConfig, DigitalInputProvider>, Input {
    /** Default debounce interval in microseconds applied to state-change detection. */
    long DEFAULT_DEBOUNCE = 10000;

    /**
     * Creates a new {@link DigitalInputConfigBuilder} for assembling a digital input configuration.
     *
     * @param context the Pi4J context (not required by the current implementation but kept for API consistency)
     * @return a new configuration builder instance
     */
    static DigitalInputConfigBuilder newConfigBuilder(Context context){
        return DigitalInputConfigBuilder.newInstance();
    }

    /**
     * Returns the pull resistance (pull-up, pull-down, or none) configured for this input.
     *
     * @return the configured {@link PullResistance}
     */
    default PullResistance pull() { return config().pull(); }

    /**
     * Returns a {@link PinReconfigurer} that can atomically release this GPIO line and
     * re-request it as a digital output or input with a new configuration.
     * <p>
     * Implementations that support runtime reconfiguration (e.g. the FFM backend) override
     * this method. All others throw {@link UnsupportedOperationException}.
     *
     * @return a reconfigurer for this pin
     * @throws UnsupportedOperationException if the backing implementation does not support reconfiguration
     */
    default PinReconfigurer reconfigure() {
        throw new UnsupportedOperationException(
            "reconfigure() is not supported by " + getClass().getSimpleName());
    }
}
