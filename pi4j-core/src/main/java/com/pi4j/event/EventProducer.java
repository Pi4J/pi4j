package com.pi4j.event;

/**
 * Marker interface implemented by Pi4J types that emit {@link Event}s and therefore
 * allow {@link Listener}s to be registered against them. Concrete producer contracts
 * such as {@link InitializedEventProducer} and {@link ShutdownEventProducer} extend
 * this interface with the listener-management methods specific to each event type.
 */
public interface EventProducer {
    // MARKER INTERFACE
}
