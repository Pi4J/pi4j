package com.pi4j.config;

/**
 * Builder contract for assembling a {@link BcmConfig}, i.e. the configuration of an I/O that is
 * addressed by a Broadcom (BCM) GPIO pin number.
 *
 * @param <BUILDER_TYPE> the concrete builder sub-type, returned by setters to enable type-safe chaining
 * @param <CONFIG_TYPE> the configuration type produced by {@link #build()}
 */
public interface BcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    /**
     * Sets the target pin using its legacy "address" name.
     *
     * @param address the BCM GPIO pin number
     * @return this builder instance for method chaining
     * @deprecated use {@link #bcm(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    BUILDER_TYPE address(Integer address);

    /**
     * Sets the Broadcom (BCM) GPIO pin number this I/O is bound to.
     *
     * @param bcm the BCM GPIO pin number
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE bcm(Integer bcm);
}
