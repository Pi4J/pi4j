package com.pi4j.plugin.ffm.common.gpio;

/**
 * Immutable holder for a single GPIO line edge event read from the kernel after a successful {@code poll} on a
 * {@code /dev/gpiochipN} line-request file descriptor (mapping the kernel {@code struct gpio_v2_line_event}).
 * Instances are delivered to a {@link PinEventProcessing} callback.
 *
 * @param timestampInNanos kernel-supplied event timestamp in nanoseconds (best-effort estimate, from the realtime or HTE clock)
 * @param pinEvent         the detected edge type, {@link PinEvent#RISING} or {@link PinEvent#FALLING}
 * @param sequenceNumber   monotonically increasing event counter for the line since edge detection was enabled
 */
public record DetectedEvent(long timestampInNanos, PinEvent pinEvent, int sequenceNumber) {
}
