package com.pi4j.plugin.ffm.common.gpio;


/**
 * GPIO line edge event types that can be subscribed to and reported back on a pin state change. The integer values
 * correspond to the kernel {@code GPIOEVENT_EVENT_*}/{@code GPIOEVENT_REQUEST_*} bits in {@link GpioConstants}.
 *
 * @see PinEventProcessing
 * @see com.pi4j.plugin.ffm.common.gpio.structs.LineEvent
 */
public enum PinEvent {
    /**
     * If the state was LOW and changed to HIGH.
     */
    RISING(1),
    /**
     * If the state was HIGH and changed to LOW.
     */
    FALLING(2),
    /**
     * Either edge: the state changed in either direction (combines {@link #RISING} and {@link #FALLING}).
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
     * Resolves the constant whose {@link #getValue()} matches the given integer.
     *
     * @param value the kernel event value to look up
     * @return the matching {@link PinEvent} constant
     * @throws java.util.NoSuchElementException if no constant has the given value
     */
    public static PinEvent getByValue(int value) {
        return switch (value) {
            case 1 -> RISING;
            case 2 -> FALLING;
            case 3 -> BOTH;
            default -> throw new IllegalArgumentException("Invalid PinEvent value: " + value);
        };
    }
}
