package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.impl.DefaultDigitalOutputConfigBuilder;

/**
 * Fluent builder for assembling a {@link DigitalOutputConfig}, adding output-specific settings (pin address,
 * initial state and shutdown state) on top of the shared {@link DigitalConfigBuilder} options.
 */
public interface DigitalOutputConfigBuilder extends DigitalConfigBuilder<DigitalOutputConfigBuilder, DigitalOutputConfig> {

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
     * @deprecated the context argument is no longer required; use {@link #newInstance()} instead
     */
    @Deprecated
    static DigitalOutputConfigBuilder newInstance(Context context) {
        return DefaultDigitalOutputConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new digital output config builder.
     *
     * @return a new builder instance
     */
    static DigitalOutputConfigBuilder newInstance() {
        return DefaultDigitalOutputConfigBuilder.newInstance();
    }

}
