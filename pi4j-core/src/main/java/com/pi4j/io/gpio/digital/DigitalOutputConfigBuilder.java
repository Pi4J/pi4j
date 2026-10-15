package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.impl.DefaultDigitalOutputConfigBuilder;

/**
 * Fluent builder for assembling a {@link DigitalOutputConfig}, adding output-specific settings (pin address,
 * initial state and shutdown state) on top of the shared {@link DigitalOutputConfigBuilder} options.
 */
public interface DigitalOutputConfigBuilder extends com.pi4j.io.IOConfigBuilder<DigitalOutputConfigBuilder, DigitalOutputConfig>, com.pi4j.config.ConfigBuilder<DigitalOutputConfigBuilder, DigitalOutputConfig> {

    /**
     * Sets the BCM pin number the output controls.
     *
     * @param bcm the Broadcom GPIO pin number
     * @return this builder for method chaining
     */
    DigitalOutputConfigBuilder bcm(Integer bcm);

    /**
     * Sets the state the output is driven to when Pi4J shuts down.
     *
     * @param state the shutdown state to apply
     * @return this builder for method chaining
     */
    DigitalOutputConfigBuilder shutdown(DigitalState state);

    /**
     * Sets the state the output is driven to immediately after it is initialized.
     *
     * @param state the initial state to apply
     * @return this builder for method chaining
     */
    DigitalOutputConfigBuilder initial(DigitalState state);

    /**
     * Creates a new digital output config builder bound to the given Pi4J context.
     *
     * @param context the Pi4J runtime context
     * @return a new builder instance
     * @deprecated the context argument is no longer required; use DigitalOutput.newConfigBuilder() instead.
     */
    @Deprecated(since="5.0")
    static DigitalOutputConfigBuilder newInstance(Context context) {
        return DefaultDigitalOutputConfigBuilder.newInstance(context);
    }

    /**
     * Internal method creating a new digital output config builder.
     * Please use DigitalOutput.newConfigBuilder() instead.
     *
     * @return a new builder instance
     */
    static DigitalOutputConfigBuilder newInstance() {
        return DefaultDigitalOutputConfigBuilder.newInstance();
    }

    /**
     * Sets the {@link DigitalState} to be treated as the logical "on" state for the configuration being built.
     *
     * @param state the state that should map to "on"
     * @return this builder for method chaining
     */
    DigitalOutputConfigBuilder onState(DigitalState state);

    /**
     * Sets the bus number on which the GPIO device is addressed.
     *
     * @param bus the bus number to associate with the configured GPIO device
     * @return this builder instance for method chaining
     */
    DigitalOutputConfigBuilder bus(int bus);

    /**
     * Sets the target pin using its legacy "address" name.
     *
     * @param address the BCM GPIO pin number
     * @return this builder instance for method chaining
     * @deprecated use {@link #bcm(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    default DigitalOutputConfigBuilder address(Integer address) {
        return bcm(address);
    }
}
