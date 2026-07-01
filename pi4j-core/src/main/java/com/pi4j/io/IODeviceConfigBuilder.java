package com.pi4j.io;

import com.pi4j.config.DeviceConfigBuilder;
import com.pi4j.provider.Provider;

/**
 * Builder contract for I/O configurations addressed by a bus/device pair.
 * <p>
 * It combines the provider-selection capability of {@link IOConfigBuilder} with the bus and device
 * addressing of {@link DeviceConfigBuilder}, and is the basis for builders of bus-attached I/O such
 * as I2C and SPI.
 *
 * @param <BUILDER_TYPE> the concrete builder type, returned for fluent method chaining
 * @param <CONFIG_TYPE>  the configuration type produced by this builder
 */
public interface IODeviceConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>
        extends IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
                DeviceConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
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
