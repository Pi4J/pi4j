package com.pi4j.plugin.ffm.common.gpio;


import java.util.Arrays;

/**
 * Events, that you can subscribe and receive callback on the GPIO Pin state change.
 *
 * @see PinEventProcessing
 */
public enum PinEvent {
    /**
     * If the state was LOW and changed to HIGH.
     */
    RISING(1),
    /**
     * If the state was HIGH and changed to LOW.
     */
    FALLING(1 << 1),
    /**
     * if the state is changed.
     */
    BOTH((1) | (1 << 1));

    private final int value;

    /**
     * Creates PinEvent enum.
     *
     * @param value integer value of pin event
     */
    PinEvent(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value of pin event.
     *
     * @return integer value of pin event
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets pin event enum by given integer value.
     *
     * @param value given integer value
     * @return pin event
     */
    public static PinEvent getByValue(int value) {
        return Arrays.stream(values()).filter(v -> v.getValue() == value).findFirst().orElseThrow();
    }
}
