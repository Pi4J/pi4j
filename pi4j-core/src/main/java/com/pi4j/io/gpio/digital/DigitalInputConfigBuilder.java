package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.util.StringUtil;

import java.util.concurrent.TimeUnit;

/**
 * Fluent builder for assembling a {@link DigitalInputConfig}, extending {@link IOConfigBuilder}
 * with the input-specific pull resistance, debounce, onState, and BCM pin settings.
 */
public class DigitalInputConfigBuilder extends IOConfigBuilder<DigitalInputConfigBuilder, DigitalInputConfig> {

    private Integer bcm = null;
    private Integer bus = 0;
    private PullResistance pull = PullResistance.OFF;
    private Long debounce = DigitalInput.DEFAULT_DEBOUNCE;
    private DigitalState onState = DigitalState.HIGH;

    /**
     * Default constructor for creating a new digital input configuration builder.
     */
    public DigitalInputConfigBuilder() {
    }

    /**
     * Creates a new digital input config builder instance.
     *
     * @param context the Pi4J context
     * @return a new builder instance
     * @deprecated use DigitalInput.newConfigBuilder() instead.
     */
    @Deprecated(since = "5.0")
    public static DigitalInputConfigBuilder newInstance(Context context) {
        return new DigitalInputConfigBuilder();
    }

    /**
     * Internal method that creates a new digital input config builder instance. Please don't call this method
     * directly; use DigitalInput.newConfigBuilder() instead.
     *
     * @return a new builder instance
     */
    public static DigitalInputConfigBuilder newInstance() {
        return new DigitalInputConfigBuilder();
    }

    /**
     * Sets the Broadcom (BCM) GPIO pin number this I/O is bound to.
     *
     * @param bcm the BCM GPIO pin number
     * @return this builder for method chaining
     */
    public DigitalInputConfigBuilder bcm(Integer bcm) {
        this.bcm = bcm;
        return this;
    }

    /**
     * Sets the bus number on which the GPIO device is addressed.
     *
     * @param bus the bus number to associate with the configured GPIO device
     * @return this builder instance for method chaining
     */
    public DigitalInputConfigBuilder bus(int bus) {
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
    public DigitalInputConfigBuilder address(Integer address) {
        return bcm(address);
    }

    /**
     * Sets the pull resistance (pull-up, pull-down, or none) to apply to the input pin.
     *
     * @param value the desired {@link PullResistance}
     * @return this builder for method chaining
     */
    public DigitalInputConfigBuilder pull(PullResistance value) {
        this.pull = value != null ? value : PullResistance.OFF;
        return this;
    }

    /**
     * Sets the debounce interval, in microseconds, used to filter spurious state transitions.
     *
     * @param microseconds the debounce interval in microseconds
     * @return this builder for method chaining
     * @see com.pi4j.io.gpio.digital.DigitalInput#DEFAULT_DEBOUNCE DEFAULT_DEBOUNCE
     */
    public DigitalInputConfigBuilder debounce(Long microseconds) {
        this.debounce = microseconds;
        return this;
    }

    /**
     * Sets the debounce interval expressed in the given time unit, converting it internally to microseconds.
     *
     * @param interval the debounce interval value
     * @param units the {@link TimeUnit} in which {@code interval} is expressed
     * @return this builder for method chaining
     */
    public DigitalInputConfigBuilder debounce(Long interval, TimeUnit units) {
        return debounce(units != null && interval != null ? units.toMicros(interval) : interval);
    }

    /**
     * Sets the {@link DigitalState} to be treated as the logical "on" state for the configuration being built.
     *
     * @param state the state that should map to "on"
     * @return this builder for method chaining
     */
    public DigitalInputConfigBuilder onState(DigitalState state) {
        this.onState = state != null ? state : DigitalState.HIGH;
        return this;
    }

    @Override
    public DigitalInputConfig build() {
        String defaultId = this.bcm != null ? "DIN-" + this.bcm : "DIN";
        String id = StringUtil.setIfNullOrEmpty(this.id, defaultId, true);
        String name = StringUtil.setIfNullOrEmpty(this.name, id, true);
        String description = StringUtil.setIfNullOrEmpty(this.description, id, true);

        return new DigitalInputConfig(
            id,
            name,
            description,
            this.bcm,
            this.bus != null ? this.bus : 0,
            this.pull != null ? this.pull : PullResistance.OFF,
            this.debounce,
            this.onState != null ? this.onState : DigitalState.HIGH
        );
    }
}
