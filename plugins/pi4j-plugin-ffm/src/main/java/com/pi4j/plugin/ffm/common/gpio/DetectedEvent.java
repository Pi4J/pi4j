package com.pi4j.plugin.ffm.common.gpio;

/**
 * Class-holder of detected pin events, which is filled upon successful call of glibc poll.
 *
 * @param timestampInNanos timestampInNanos in nanoseconds (best effort estimate)
 * @param pinEvent         event type detected (rising of falling edge)
 * @param sequenceNumber   number of event since detecting started on the pin
 */
public record DetectedEvent(long timestampInNanos, PinEvent pinEvent, int sequenceNumber) {
}
