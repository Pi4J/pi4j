package com.pi4j.io;

/**
 * Root contract for all Pi4J I/O configuration objects. An {@code IOConfig} carries the common
 * identity properties ({@code id}, {@code name}, {@code description}) shared by every I/O, exposes
 * accessors, and validates its own required values.
 */
public interface IOConfig {

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
