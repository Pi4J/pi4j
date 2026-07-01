package com.pi4j.event;

/**
 * Marker interface implemented by all Pi4J event listeners. Listeners are registered
 * with an {@link EventManager}, which dispatches {@link Event} instances to them.
 * Specialized listener contracts such as {@link InitializedListener} and
 * {@link ShutdownListener} extend this interface.
 */
public interface Listener {
    // MARKER INTERFACE
}
