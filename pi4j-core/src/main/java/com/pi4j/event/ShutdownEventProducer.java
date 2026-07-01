package com.pi4j.event;

/**
 * {@link EventProducer} implemented by Pi4J types that emit {@link ShutdownEvent}s and
 * let callers register {@link ShutdownListener}s to be notified when the Pi4J context
 * is shutting down. The listener-management methods are fluent, returning the producer
 * itself for chaining.
 *
 * @param <T> the concrete producer type returned for method chaining
 */
public interface ShutdownEventProducer<T> extends EventProducer {
    /**
     * Removes all registered shutdown listeners from this producer.
     *
     * @return this producer instance for method chaining
     */
    T removeAllShutdownListeners();

    /**
     * Registers one or more listeners to be notified when the context is shutting down.
     *
     * @param listener the {@link ShutdownListener}(s) to register
     * @return this producer instance for method chaining
     */
    T addListener(ShutdownListener ... listener);

    /**
     * Unregisters one or more previously registered shutdown listeners.
     *
     * @param listener the {@link ShutdownListener}(s) to remove
     * @return this producer instance for method chaining
     */
    T removeListener(ShutdownListener ... listener);
}
