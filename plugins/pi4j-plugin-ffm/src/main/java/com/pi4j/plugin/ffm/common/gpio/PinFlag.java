package com.pi4j.plugin.ffm.common.gpio;

/**
 * Pin Flag for configuring / reading configuration from GPIO.
 */
public enum PinFlag {
    /**
     * line is not available for request
     */
    USED(1),
    /**
     * line active state is physical low
     */
    ACTIVE_LOW(1 << 1),
    /**
     * line is an input
     */
    INPUT(1 << 2),
    /**
     * line is an output
     */
    OUTPUT(1 << 3),
    /**
     * line detects rising (inactive to active) edges
     */
    EDGE_RISING(1 << 4),
    /**
     * line detects rising (inactive to active) edges
     */
    EDGE_FALLING(1 << 5),
    /**
     * line is an open drain output
     */
    OPEN_DRAIN(1 << 6),
    /**
     * line is an open source output
     */
    OPEN_SOURCE(1 << 7),
    /**
     * line has pull-up bias enabled
     */
    BIAS_PULL_UP(1 << 8),
    /**
     * line has pull-down bias enabled
     */
    BIAS_PULL_DOWN(1 << 9),
    /**
     * line has bias disabled
     */
    BIAS_DISABLED(1 << 10),
    /**
     * line events contain REALTIME timestamps
     */
    EVENT_CLOCK_REALTIME(1 << 11),
    /**
     * line events contain timestamps from the hardware timestamping engine (HTE) subsystem
     */
    EVENT_CLOCK_HTE(1 << 12);


    private final int value;

    /**
     * Creates Pin Flag enum by given integer value.
     *
     * @param value integer value
     */
    PinFlag(int value) {
        this.value = value;
    }

    /**
     * Gets integer value of Pin Flag enum
     *
     * @return integer value
     */
    public int getValue() {
        return value;
    }
}
