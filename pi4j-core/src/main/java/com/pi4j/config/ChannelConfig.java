package com.pi4j.config;

/**
 * Configuration contract for I/O instances that are addressed by a channel number within a bus,
 * such as SPI and PWM. The {@code channel} value selects a specific line on the bus identified by
 * the accompanying {@link BusConfig}.
 *
 * @param <CONFIG_TYPE> the concrete configuration sub-type, returned by fluent accessors to enable type-safe chaining
 */
public interface ChannelConfig<CONFIG_TYPE extends Config> extends Config<CONFIG_TYPE> {
    /**
     * Property key under which the channel number is stored in the configuration properties map.
     */
    String CHANNEL_KEY = "channel";

    /**
     * Returns the channel number this I/O is assigned to within its bus.
     *
     * @return the channel number, or {@code null} if not configured
     */
    Integer channel();

    /**
     * Returns the channel number this I/O is assigned to within its bus.
     *
     * @return the channel number, or {@code null} if not configured
     */
    default Integer getChannel() {
        return this.channel();
    }
}
