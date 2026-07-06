package com.pi4j.plugin.ffm.common.gpio;

import java.util.List;

/**
 * Functional callback invoked by the native GPIO event loop whenever edge events are read from a watched line.
 * Implementations receive the {@link DetectedEvent}s decoded after a {@code poll}/{@code read} cycle on a
 * {@code /dev/gpiochipN} line file descriptor.
 */
@FunctionalInterface
public interface PinEventProcessing {
    /**
     * Handles a batch of edge events detected on a GPIO line.
     * <p>
     * WARNING: this callback runs on the thread driving the glibc {@code poll} loop, so it must return quickly;
     * offload any heavy work to a separate thread to avoid stalling further event detection.
     *
     * @param eventList the edge events detected in the most recent read, in the order reported by the kernel
     */
    void process(List<DetectedEvent> eventList);
}
