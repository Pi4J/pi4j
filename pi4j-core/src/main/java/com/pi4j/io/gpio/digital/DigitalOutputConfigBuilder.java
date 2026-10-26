package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.util.StringUtil;

/**
 * Fluent builder for assembling a {@link DigitalOutputConfig}, adding output-specific settings (pin address,
 * initial state, shutdown state, and on-state) on top of the shared builder options.
 */
public class DigitalOutputConfigBuilder extends IOConfigBuilder<DigitalOutputConfigBuilder, DigitalOutputConfig> {

    private Integer bcm = null;
    private Integer bus = 0;
    private DigitalState shutdownState = null;
    private DigitalState initialState = null;
    private DigitalState onState = DigitalState.HIGH;

    /**
     * Default constructor for creating a new digital output configuration builder.
     */
    public DigitalOutputConfigBuilder() {
    }

    /**
     * Creates a new digital output config builder bound to the given Pi4J context.
     *
     * @param context the Pi4J runtime context
     * @return a new builder instance
     * @deprecated the context argument is no longer required; use DigitalOutput.newConfigBuilder() instead.
     */
    @Deprecated(since = "5.0")
    public static DigitalOutputConfigBuilder newInstance(Context context) {
        return new DigitalOutputConfigBuilder();
    }

    /**
     * Internal method creating a new digital output config builder.
     * Please use DigitalOutput.newConfigBuilder() instead.
     *
     * @return a new builder instance
     */
    public static DigitalOutputConfigBuilder newInstance() {
        return new DigitalOutputConfigBuilder();
    }

    /**
     * Sets the Broadcom (BCM) GPIO pin number the output controls.
     *
     * @param bcm the Broadcom GPIO pin number
     * @return this builder for method chaining
     */
    public DigitalOutputConfigBuilder bcm(Integer bcm) {
        this.bcm = bcm;
        return this;
    }

    /**
     * Sets the bus number on which the GPIO device is addressed.
     *
     * @param bus the bus number to associate with the configured GPIO device
     * @return this builder instance for method chaining
     */
    public DigitalOutputConfigBuilder bus(int bus) {
        this.bus = bus;
        return this;
    }

    /**
     * Sets the target pin using its legacy "address" name.
     *
     * @param address the BCM GPIO pin number
     * @return this builder instance for method chaining
     * @deprecated use {@link #bcm(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public DigitalOutputConfigBuilder address(Integer address) {
        return bcm(address);
    }

    /**
     * Sets the state the output is driven to when Pi4J shuts down.
     *
     * @param state the shutdown state to apply
     * @return this builder for method chaining
     */
    public DigitalOutputConfigBuilder shutdown(DigitalState state) {
        this.shutdownState = state;
        return this;
    }

    /**
     * Sets the state the output is driven to immediately after it is initialized.
     *
     * @param state the initial state to apply
     * @return this builder for method chaining
     */
    public DigitalOutputConfigBuilder initial(DigitalState state) {
        this.initialState = state;
        return this;
    }

    /**
     * Sets the {@link DigitalState} to be treated as the logical "on" state for the configuration being built.
     *
     * @param state the state that should map to "on"
     * @return this builder for method chaining
     */
    public DigitalOutputConfigBuilder onState(DigitalState state) {
        this.onState = state != null ? state : DigitalState.HIGH;
        return this;
    }

    @Override
    public DigitalOutputConfig build() {
        String defaultId = this.bcm != null ? "DOUT-" + this.bcm : "DOUT";
        String id = StringUtil.setIfNullOrEmpty(this.id, defaultId, true);
        String name = StringUtil.setIfNullOrEmpty(this.name, id, true);
        String description = StringUtil.setIfNullOrEmpty(this.description, id, true);

        return new DigitalOutputConfig(
            id,
            name,
            description,
            this.bcm,
            this.bus != null ? this.bus : 0,
            this.initialState,
            this.shutdownState,
            this.onState != null ? this.onState : DigitalState.HIGH
        );
    }
}
