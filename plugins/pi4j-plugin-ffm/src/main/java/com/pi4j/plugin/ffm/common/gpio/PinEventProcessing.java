package com.pi4j.plugin.ffm.common.gpio;

import java.util.List;

/**
 * Event processing callback. Can be used as functional interface in streams.
 */
@FunctionalInterface
public interface PinEventProcessing {
    /**
     * Process the changed on the GPIO Pin.
     * WARNING: since the caller of this callback is heavily tight with linux poll, it is recommended to do processing as fast as possible in implementation part.
     * If there is any heavy processing call it is recommended to offload it into different thread.
     *
     * @param eventList list of detected events
     */
    void process(List<DetectedEvent> eventList);
}
