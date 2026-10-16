package com.pi4j.plugin.ffm.common.gpio;

/**
 * Immutable holder for a single GPIO line edge event read from the kernel after a successful {@code poll} on a
 * {@code /dev/gpiochipN} line-request file descriptor (mapping the kernel {@code struct gpio_v2_line_event}).
 * Instances are delivered to a {@link PinEventProcessing} callback.
 *
 * @param timestampInNanos kernel-supplied event timestamp in nanoseconds (best-effort estimate, from the realtime or HTE clock)
 * @param pinEvent         the detected edge type, {@link PinEvent#RISING} or {@link PinEvent#FALLING}
 * @param sequenceNumber   monotonically increasing event counter for the line since edge detection was enabled
 * @param offset           the line offset within the GPIO chip that generated this event
 */
/*
 The more fields we add to this record, the more we might consider simply exposing the {@link LineEvent} struct
  it represents. The utility of the abstraction degrades as we add more fields. **or** we could deserialise
  directly to the type natively understood by the API.
 */
public record DetectedEvent(long timestampInNanos, PinEvent pinEvent, int sequenceNumber, int offset) {
}
