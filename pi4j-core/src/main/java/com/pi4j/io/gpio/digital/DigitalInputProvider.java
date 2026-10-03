package com.pi4j.io.gpio.digital;

import com.pi4j.provider.Provider;

/**
 * Legacy provider stub for creating {@link DigitalInput} instances.
 * @deprecated
 */
@Deprecated(since = "5.0")
public interface DigitalInputProvider extends Provider<DigitalInput, DigitalInputConfig> {

    /**
     * Creates a digital input from the configuration produced by the given builder.
     *
     * @param builder a configured {@link DigitalInputConfigBuilder}
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(DigitalInputConfigBuilder builder) {
        return (T) create(builder.build());
    }

    /**
     * Creates a digital input on the given BCM pin using default configuration.
     *
     * @param bcm the BCM (Broadcom) GPIO pin number
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(Integer bcm) {
        var config = DigitalInput.newConfigBuilder()
            .bcm(bcm)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital input on the given BCM pin with the given identifier.
     *
     * @param bcm the BCM (Broadcom) GPIO pin number
     * @param id the unique identifier to assign to the input
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(Integer bcm, String id) {
        var config = DigitalInput.newConfigBuilder()
            .bcm(bcm)
            .id(id)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital input on the given BCM pin with the given identifier and display name.
     *
     * @param bcm the BCM (Broadcom) GPIO pin number
     * @param id the unique identifier to assign to the input
     * @param name the human-readable name to assign to the input
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(Integer bcm, String id, String name) {
        var config = DigitalInput.newConfigBuilder()
            .bcm(bcm)
            .id(id)
            .name(name)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital input on the given BCM pin with the given identifier, display name, and description.
     *
     * @param bcm the BCM (Broadcom) GPIO pin number
     * @param id the unique identifier to assign to the input
     * @param name the human-readable name to assign to the input
     * @param description a free-form description of the input
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(Integer bcm, String id, String name, String description) {
        var config = DigitalInput.newConfigBuilder()
            .bcm(bcm)
            .id(id)
            .name(name)
            .description(description)
            .build();
        return (T) create(config);
    }
}
