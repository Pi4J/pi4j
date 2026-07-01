package com.pi4j.io.pwm;

import com.pi4j.provider.Provider;

/**
 * Provider responsible for creating and managing {@link Pwm} I/O instances from a
 * {@link PwmConfig}. A concrete provider (extending {@link PwmProviderBase}) is
 * registered with the Pi4J runtime and supplies the platform-specific PWM
 * implementation; the convenience {@code create(...)} overloads here assemble a
 * configuration on the caller's behalf.
 */
public interface PwmProvider extends Provider<PwmProvider, Pwm, PwmConfig> {

    /**
     * Creates a new PWM instance from a configuration builder.
     *
     * @param builder a configured {@link PwmConfigBuilder}; its {@code build()} result is used
     * @param <T>     the concrete {@link Pwm} type produced by this provider
     * @return the newly created PWM instance
     */
    default <T extends Pwm> T create(PwmConfigBuilder builder) {
        return (T) create(builder.build());
    }

    /**
     * Creates a new PWM instance for the given chip and channel.
     *
     * @param chip    the PWM chip number (hardware PWM)
     * @param channel the PWM channel number on that chip
     * @param <T>     the concrete {@link Pwm} type produced by this provider
     * @return the newly created PWM instance
     */
    default <T extends Pwm> T create(Integer chip, Integer channel) {
        var config = Pwm.newConfigBuilder(context())
            .chip(chip)
            .channel(channel)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a new PWM instance for the given chip and channel with the supplied identifier.
     *
     * @param chip    the PWM chip number (hardware PWM)
     * @param channel the PWM channel number on that chip
     * @param id      the unique Pi4J identifier to assign to the instance
     * @param <T>     the concrete {@link Pwm} type produced by this provider
     * @return the newly created PWM instance
     */
    default <T extends Pwm> T create(Integer chip, Integer channel, String id) {
        var config = Pwm.newConfigBuilder(context())
            .chip(chip)
            .channel(channel)
            .id(id)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a new PWM instance for the given chip and channel with the supplied identifier and name.
     *
     * @param chip    the PWM chip number (hardware PWM)
     * @param channel the PWM channel number on that chip
     * @param id      the unique Pi4J identifier to assign to the instance
     * @param name    the human-readable name to assign to the instance
     * @param <T>     the concrete {@link Pwm} type produced by this provider
     * @return the newly created PWM instance
     */
    default <T extends Pwm> T create(Integer chip, Integer channel, String id, String name) {
        var config = Pwm.newConfigBuilder(context())
            .chip(chip)
            .channel(channel)
            .id(id)
            .name(name)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a new PWM instance for the given chip and channel with the supplied identifier, name and description.
     *
     * @param chip        the PWM chip number (hardware PWM)
     * @param channel     the PWM channel number on that chip
     * @param id          the unique Pi4J identifier to assign to the instance
     * @param name        the human-readable name to assign to the instance
     * @param description a free-form description of the instance
     * @param <T>         the concrete {@link Pwm} type produced by this provider
     * @return the newly created PWM instance
     */
    default <T extends Pwm> T create(Integer chip, Integer channel, String id, String name, String description) {
        var config = Pwm.newConfigBuilder(context())
            .chip(chip)
            .channel(channel)
            .id(id)
            .name(name)
            .description(description)
            .build();
        return (T) create(config);
    }
}
