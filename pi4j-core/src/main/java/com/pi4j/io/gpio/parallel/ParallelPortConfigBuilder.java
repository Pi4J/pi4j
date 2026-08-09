package com.pi4j.io.gpio.parallel;

import com.pi4j.config.Config;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.impl.IOConfigBuilderBase;

/**
 * Builder for {@link ParallelPortConfig}
 */
public class ParallelPortConfigBuilder extends IOConfigBuilderBase<ParallelPortConfigBuilder, ParallelPortConfig> {

    private Integer bus = 0;
    private int bcmMask = 0;
    private ParallelPort.Direction initialDirection = ParallelPort.Direction.INPUT;
    private Integer initialValue = 0;
    private Integer shutdownValue = 0;
    private PullResistance pull = PullResistance.OFF;
    private Long debounce = 0L;

    /**
     * Specify the GPIO chip
     * @param bus the GPIO chip number
     * @return this builder instance
     */
    public ParallelPortConfigBuilder bus(int bus) {
        this.bus = bus;
        return this;
    }

    /**
     * Add a BCM pin mapping. This can be called multiple times to add multiple BCM pin mappings.
     * @param bcm the BCM pin mapping
     * @return this builder instance
     */
    public ParallelPortConfigBuilder bcm(int bcm) {
        this.bcmMask |= (1 << bcm);
        return this;
    }

    /**
     * Specify the initial direction
     * @param initialDirection the initial direction
     * @return this builder instance
     */
    public ParallelPortConfigBuilder initialDirection(ParallelPort.Direction initialDirection) {
        this.initialDirection = initialDirection;
        return this;
    }

    /**
     * Specify the initial value when the port is intialised as OUTPUT
     * @param initialValue the initial value
     * @return this builder instance
     */
    public ParallelPortConfigBuilder initialValue(int initialValue) {
        this.initialValue = initialValue;
        return this;
    }

    /**
     * Specify the value to be applied on shutdown when the port is intialised as OUTPUT
     * @param shutdownValue the shutdown value
     * @return this builder instance
     */
    public ParallelPortConfigBuilder shutdownValue(int shutdownValue) {
        this.shutdownValue = shutdownValue;
        return this;
    }

    /**
     * Specify the pull resistance when the port is intialised as INPUT
     * @param pull the pull resistance
     * @return this builder instance
     */
    public ParallelPortConfigBuilder pull(PullResistance pull) {
        this.pull = pull;
        return this;
    }

    /**
     * Specify the debounce time when the port is intialised as INPUT
     * @param debounce the debounce time
     * @return this builder instance
     */
    public ParallelPortConfigBuilder debounce(long debounce) {
        this.debounce = debounce;
        return this;
    }

    /**
     * Construct the {@link ParallelPortConfig} instance
     * @return the constructed {@link ParallelPortConfig} instance
     */
    @Override
    public ParallelPortConfig build() {
        if (bcmMask == 0) {
            throw new IllegalArgumentException("BCM pins must be specified");
        }
        if (initialDirection == null) {
            throw new IllegalArgumentException("Initial direction must be specified");
        }
        return new ParallelPortConfig(
            this.id(),
            this.properties.get(Config.NAME_KEY),
            this.properties.get(Config.DESCRIPTION_KEY),
            this.bus,
            this.bcmMask,
            this.pull,
            this.debounce,
            this.initialValue,
            this.shutdownValue,
            this.initialDirection
        );
    }

    /**
     * Provided mainly for compatibility. A constructor with the same signature as other config builders
     * @return a new builder instance
     */
    public static ParallelPortConfigBuilder newInstance() {
        return new ParallelPortConfigBuilder();
    }
}
