package com.pi4j.config;

/**
 * Configuration contract for I/O instances that are addressed by a device number, for example a
 * specific peripheral on a bus. The {@code device} value selects which device the I/O targets.
 *
 * @param <CONFIG_TYPE> the concrete configuration sub-type, returned by fluent accessors to enable type-safe chaining
 */
public interface DeviceConfig<CONFIG_TYPE extends Config> extends Config<CONFIG_TYPE> {

    /**
     * Property key under which the device number is stored in the configuration properties map.
     */
    String DEVICE_KEY = "device";

    /**
     * Returns the device number this I/O is assigned to.
     *
     * @return the device number, or {@code null} if not configured
     */
    Integer device();

    /**
     * Returns the device number this I/O is assigned to.
     *
     * @return the device number, or {@code null} if not configured
     */
    default Integer getDevice() {
        return this.device();
    }
}
