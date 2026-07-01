package com.pi4j.event;

/**
 * {@link EventProducer} implemented by Pi4J types that emit {@link InitializedEvent}s
 * and let callers register {@link InitializedListener}s to be notified when the Pi4J
 * context has completed initialization. The listener-management methods are fluent,
 * returning the producer itself for chaining.
 *
 * @param <T> the concrete producer type returned for method chaining
 */
public interface InitializedEventProducer<T> extends EventProducer {
    /**
     * Removes all registered initialization listeners from this producer.
     *
     * @return this producer instance for method chaining
     */
    T removeAllInitializedListeners();

    /**
     * Registers one or more listeners to be notified when initialization completes.
     *
     * @param listener the {@link InitializedListener}(s) to register
     * @return this producer instance for method chaining
     */
    T addListener(InitializedListener ... listener);

    /**
     * Unregisters one or more previously registered initialization listeners.
     *
     * @param listener the {@link InitializedListener}(s) to remove
     * @return this producer instance for method chaining
     */
    T removeListener(InitializedListener ... listener);
}
