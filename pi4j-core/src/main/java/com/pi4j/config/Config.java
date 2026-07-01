package com.pi4j.config;

import java.util.Map;

/**
 * Root contract for all Pi4J I/O configuration objects. A {@code Config} carries the common
 * identity properties ({@code id}, {@code name}, {@code description}) shared by every I/O, exposes
 * the raw underlying property map, and validates its own required values. I/O-specific
 * configurations (such as {@link BcmConfig}, {@link BusConfig} or {@link ChannelConfig}) extend
 * this interface, and instances are typically assembled with a {@link ConfigBuilder}.
 *
 * @param <CONFIG_TYPE> the concrete configuration sub-type
 */
public interface Config<CONFIG_TYPE> {
    /**
     * Property key under which the configuration identifier is stored in the properties map.
     */
    String ID_KEY = "id";
    /**
     * Property key under which the configuration name is stored in the properties map.
     */
    String NAME_KEY = "name";
    /**
     * Property key under which the configuration description is stored in the properties map.
     */
    String DESCRIPTION_KEY = "description";

    /**
     * Returns the underlying raw configuration properties backing this instance.
     *
     * @return an unmodifiable map of raw property keys to their string values
     */
    Map<String, String> properties();

    /**
     * Returns the unique identifier of this configuration.
     *
     * @return the configuration id, or {@code null} if none was set
     */
    String id();

    /**
     * Returns the human-readable name of this configuration.
     *
     * @return the configuration name, or {@code null} if none was set
     */
    String name();

    /**
     * Returns the human-readable description of this configuration.
     *
     * @return the configuration description, or {@code null} if none was set
     */
    String description();

    /**
     * Validates that this configuration contains all required values, throwing if any are missing
     * or invalid. Called before the configured I/O is created.
     */
    void validate();

    /**
     * Returns the unique identifier of this configuration.
     *
     * @return the configuration id, or {@code null} if none was set
     */
    default String getId() {
        return this.id();
    }

    /**
     * Returns the human-readable name of this configuration.
     *
     * @return the configuration name, or {@code null} if none was set
     */
    default String getName() {
        return this.name();
    }

    /**
     * Returns the human-readable description of this configuration.
     *
     * @return the configuration description, or {@code null} if none was set
     */
    default String getDescription() {
        return this.description();
    }

    /**
     * Each IO Type has to provide a unique identifier.
     * Depending on the type of device:
     *
     * <ul>
     *     <li>GPIO: bcm</li>
     *     <li>I2C: bus+address</li>
     *     <li>SPI: bus+channel</li>
     *     <li>PWM: bus+channel</li>
     * </ul>
     *
     * @return Unique identifier.
     */
    int getUniqueIdentifier();
}
