package com.pi4j.event;

/**
 * Functional strategy used by an {@link EventManager} to deliver a single event to a
 * single listener. Implementations typically invoke the appropriate callback method on
 * the listener (for example {@link InitializedListener#onInitialized}), allowing the
 * generic {@link EventManager} to remain agnostic of the concrete listener and event
 * types.
 *
 * @param <LISTENER_TYPE> the listener type that receives the event
 * @param <EVENT_TYPE> the event type being delivered
 */
public interface EventDelegate<LISTENER_TYPE, EVENT_TYPE> {
    /**
     * Delivers the given event to the given listener, invoking the listener callback
     * that corresponds to the event type.
     *
     * @param listener the listener to notify
     * @param event the event to deliver to the listener
     */
    void dispatch(LISTENER_TYPE listener, EVENT_TYPE event);
}
