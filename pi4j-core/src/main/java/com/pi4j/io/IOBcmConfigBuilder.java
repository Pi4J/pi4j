package com.pi4j.io;

import com.pi4j.config.BcmConfigBuilder;
import com.pi4j.provider.Provider;

/**
 * Builder contract for I/O configurations addressed by a Broadcom (BCM) GPIO pin number.
 * <p>
 * It combines the provider-selection capability of {@link IOConfigBuilder} with the BCM pin
 * addressing of {@link BcmConfigBuilder}, and is the basis for builders of pin-based I/O such as
 * digital input/output and PWM.
 *
 * @param <BUILDER_TYPE> the concrete builder type, returned for fluent method chaining
 * @param <CONFIG_TYPE>  the configuration type produced by this builder
 */
public interface IOBcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>
    extends IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    BcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * Selects the I/O provider to use, identified by its registered provider id.
     *
     * @param provider the provider id (e.g. a plugin's registered provider name)
     * @return this builder for method chaining
     */
    BUILDER_TYPE provider(String provider);

    /**
     * Selects the I/O provider to use by its implementing class.
     *
     * @param providerClass the {@link Provider} implementation class to resolve
     * @return this builder for method chaining
     */
    BUILDER_TYPE provider(Class<? extends Provider> providerClass);
}
