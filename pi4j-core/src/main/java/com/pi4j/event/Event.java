package com.pi4j.event;

/**
 * Marker interface implemented by all Pi4J event types that are dispatched to
 * {@link Listener} implementations through an {@link EventManager}. Concrete events
 * such as {@link InitializedEvent} and {@link ShutdownEvent} carry the contextual
 * data delivered to listeners.
 */
public interface Event {
    // MARKER INTERFACE
}
