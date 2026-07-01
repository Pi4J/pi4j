package com.pi4j.io.gpio.digital;

/**
 * Provider responsible for creating and managing {@link DigitalOutput} instances for a particular hardware
 * platform or device. It specializes {@link DigitalProvider} for the output direction and adds convenience
 * factory methods that build the {@link DigitalOutputConfig} from a BCM pin number and optional identity.
 */
public interface DigitalOutputProvider extends DigitalProvider<DigitalOutputProvider, DigitalOutput, DigitalOutputConfig> {

    /**
     * Creates a digital output from a pre-populated configuration builder.
     *
     * @param builder the configuration builder describing the output
     * @param <T>     the concrete {@link DigitalOutput} type returned by this provider
     * @return the new digital output instance
     */
    default <T extends DigitalOutput> T create(DigitalOutputConfigBuilder builder) {
        return (T) create(builder.build());
    }

    /**
     * Creates a digital output for the given BCM pin using default settings.
     *
     * @param bcm the Broadcom GPIO pin number
     * @param <T> the concrete {@link DigitalOutput} type returned by this provider
     * @return the new digital output instance
     */
    default <T extends DigitalOutput> T create(Integer bcm) {
        var config = DigitalOutput.newConfigBuilder()
            .bcm(bcm)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital output for the given BCM pin with the supplied identifier.
     *
     * @param bcm the Broadcom GPIO pin number
     * @param id  the unique I/O identifier
     * @param <T> the concrete {@link DigitalOutput} type returned by this provider
     * @return the new digital output instance
     */
    default <T extends DigitalOutput> T create(Integer bcm, String id) {
        var config = DigitalOutput.newConfigBuilder()
            .id(id)
            .bcm(bcm)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital output for the given BCM pin with the supplied identifier and name.
     *
     * @param bcm  the Broadcom GPIO pin number
     * @param id   the unique I/O identifier
     * @param name the human-readable name
     * @param <T>  the concrete {@link DigitalOutput} type returned by this provider
     * @return the new digital output instance
     */
    default <T extends DigitalOutput> T create(Integer bcm, String id, String name) {
        var config = DigitalOutput.newConfigBuilder()
            .bcm(bcm)
            .id(id)
            .name(name)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital output for the given BCM pin with the supplied identifier, name and description.
     *
     * @param bcm         the Broadcom GPIO pin number
     * @param id          the unique I/O identifier
     * @param name        the human-readable name
     * @param description the free-form description
     * @param <T>         the concrete {@link DigitalOutput} type returned by this provider
     * @return the new digital output instance
     */
    default <T extends DigitalOutput> T create(Integer bcm, String id, String name, String description) {
        var config = DigitalOutput.newConfigBuilder()
            .bcm(bcm)
            .id(id)
            .name(name)
            .description(description)
            .build();
        return (T) create(config);
    }
}
