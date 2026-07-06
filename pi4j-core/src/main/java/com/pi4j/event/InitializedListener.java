package com.pi4j.event;

/**
 * {@link Listener} contract for receiving notifications when a Pi4J
 * {@link com.pi4j.context.Context} has completed initialization. Implementations are
 * registered with an {@link InitializedEventProducer} and notified through an
 * {@link EventManager}.
 */
public interface InitializedListener extends Listener {
    /**
     * Called once the Pi4J context has finished initializing.
     *
     * @param event the {@link InitializedEvent} describing the initialized context
     */
    void onInitialized(InitializedEvent event);
}
