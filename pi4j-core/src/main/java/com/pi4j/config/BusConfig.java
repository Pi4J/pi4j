package com.pi4j.config;

/**
 * Configuration contract for I/O instances that communicate over a numbered hardware bus, such as
 * I2C, SPI and PWM. The {@code bus} value selects which of the platform's buses the I/O uses.
 *
 * @param <CONFIG_TYPE> the concrete configuration sub-type, returned by fluent accessors to enable type-safe chaining
 */
public interface BusConfig<CONFIG_TYPE extends Config> extends Config<CONFIG_TYPE> {
    /**
     * Property key under which the bus number is stored in the configuration properties map.
     */
    String BUS_KEY = "bus";

    /**
     * Returns the hardware bus number this I/O is assigned to.
     *
     * @return the bus number, or {@code null} if not configured
     */
    Integer bus();

    /**
     * Returns the hardware bus number this I/O is assigned to.
     *
     * @return the bus number, or {@code null} if not configured
     */
    default Integer getBus() {
        return this.bus();
    }
}
