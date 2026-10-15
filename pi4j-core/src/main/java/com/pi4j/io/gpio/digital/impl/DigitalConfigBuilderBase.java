package com.pi4j.io.gpio.digital.impl;

import com.pi4j.io.gpio.digital.DigitalConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.impl.IOBcmConfigBuilderBase;

/**
 * <p>Abstract DigitalConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class DigitalConfigBuilderBase<
    BUILDER_TYPE extends com.pi4j.io.IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> & com.pi4j.config.ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    CONFIG_TYPE extends DigitalConfig
    >
    extends IOBcmConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements com.pi4j.io.IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>, com.pi4j.config.ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DigitalConfigBuilderBase() {
    }

    @SuppressWarnings("unchecked")
    public BUILDER_TYPE onState(DigitalState state) {
        this.properties.put(DigitalConfig.ON_STATE_KEY, state.toString());
        return (BUILDER_TYPE) this;
    }

    /**
     * Sets the bus number on which the GPIO device is addressed.
     *
     * @param bus the bus number to associate with the configured GPIO device
     * @return this builder instance for method chaining
     */
    public abstract BUILDER_TYPE bus(int bus);

    /**
     * Sets the target pin using its legacy "address" name.
     *
     * @param address the BCM GPIO pin number
     * @return this builder instance for method chaining
     * @deprecated use {@link #bcm(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public BUILDER_TYPE address(Integer address) {
        return bcm(address);
    }

    /**
     * Sets the Broadcom (BCM) GPIO pin number this I/O is bound to.
     *
     * @param bcm the BCM GPIO pin number
     * @return this builder instance for method chaining
     */
    public abstract BUILDER_TYPE bcm(Integer bcm);
}
