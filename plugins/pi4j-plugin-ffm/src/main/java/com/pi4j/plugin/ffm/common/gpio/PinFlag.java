package com.pi4j.plugin.ffm.common.gpio;

/**
 * Bit flags describing or configuring a GPIO line, mirroring the kernel {@code GPIO_V2_LINE_FLAG_*} bits from
 * {@code <uapi/linux/gpio.h>}. Each constant's {@link #getValue()} is the corresponding bit, so flags are combined
 * with bitwise OR when building or interpreting a v2 line configuration.
 */
public enum PinFlag {
    /**
     * {@code GPIO_V2_LINE_FLAG_USED}: line is in use and not available for request.
     */
    USED(1),
    /**
     * {@code GPIO_V2_LINE_FLAG_ACTIVE_LOW}: line active state is physical low.
     */
    ACTIVE_LOW(1 << 1),
    /**
     * {@code GPIO_V2_LINE_FLAG_INPUT}: line is an input.
     */
    INPUT(1 << 2),
    /**
     * {@code GPIO_V2_LINE_FLAG_OUTPUT}: line is an output.
     */
    OUTPUT(1 << 3),
    /**
     * {@code GPIO_V2_LINE_FLAG_EDGE_RISING}: line detects rising (inactive-to-active) edges.
     */
    EDGE_RISING(1 << 4),
    /**
     * {@code GPIO_V2_LINE_FLAG_EDGE_FALLING}: line detects falling (active-to-inactive) edges.
     */
    EDGE_FALLING(1 << 5),
    /**
     * {@code GPIO_V2_LINE_FLAG_OPEN_DRAIN}: line is an open-drain output.
     */
    OPEN_DRAIN(1 << 6),
    /**
     * {@code GPIO_V2_LINE_FLAG_OPEN_SOURCE}: line is an open-source output.
     */
    OPEN_SOURCE(1 << 7),
    /**
     * {@code GPIO_V2_LINE_FLAG_BIAS_PULL_UP}: line has pull-up bias enabled.
     */
    BIAS_PULL_UP(1 << 8),
    /**
     * {@code GPIO_V2_LINE_FLAG_BIAS_PULL_DOWN}: line has pull-down bias enabled.
     */
    BIAS_PULL_DOWN(1 << 9),
    /**
     * {@code GPIO_V2_LINE_FLAG_BIAS_DISABLED}: line has bias disabled.
     */
    BIAS_DISABLED(1 << 10),
    /**
     * {@code GPIO_V2_LINE_FLAG_EVENT_CLOCK_REALTIME}: line events carry timestamps from the realtime clock.
     */
    EVENT_CLOCK_REALTIME(1 << 11),
    /**
     * {@code GPIO_V2_LINE_FLAG_EVENT_CLOCK_HTE}: line events carry timestamps from the hardware timestamping engine (HTE) subsystem.
     */
    EVENT_CLOCK_HTE(1 << 12);


    private final int value;

    /**
     * Associates the constant with its kernel flag bit.
     *
     * @param value the {@code GPIO_V2_LINE_FLAG_*} bit value for this flag
     */
    PinFlag(int value) {
        this.value = value;
    }

    /**
     * Returns the kernel flag bit represented by this constant.
     *
     * @return the {@code GPIO_V2_LINE_FLAG_*} bit value
     */
    public int getValue() {
        return value;
    }
}
