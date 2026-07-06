package com.pi4j.event;

/**
 * {@link Listener} contract for receiving notifications when a Pi4J
 * {@link com.pi4j.context.Context} is shutting down. Implementations are registered
 * with a {@link ShutdownEventProducer} and notified through an {@link EventManager}.
 */
public interface ShutdownListener extends Listener {
    /**
     * Called when the Pi4J context is being shut down.
     *
     * @param event the {@link ShutdownEvent} describing the context being shut down
     */
    void onShutdown(ShutdownEvent event);

    /**
     * Called immediately before {@link #onShutdown(ShutdownEvent)}, allowing the
     * listener to perform any preparatory work ahead of the actual shutdown
     * notification. The default implementation does nothing.
     *
     * @param event the {@link ShutdownEvent} describing the context being shut down
     */
    default void beforeShutdown(ShutdownEvent event) { };
}
