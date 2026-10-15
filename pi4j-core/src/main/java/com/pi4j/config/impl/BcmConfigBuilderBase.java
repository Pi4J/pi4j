package com.pi4j.config.impl;

import com.pi4j.config.BcmConfig;
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;

/**
 * <p>Abstract AddressConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class BcmConfigBuilderBase<
    BUILDER_TYPE extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    CONFIG_TYPE extends Config
    >
    extends ConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected BcmConfigBuilderBase() {
    }

    @SuppressWarnings("unchecked")
    public BUILDER_TYPE bcm(Integer bcm) {
        this.properties.put(BcmConfig.BCM_KEY, bcm.toString());
        return (BUILDER_TYPE) this;
    }

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
}
