package com.pi4j.event;

/**
 * Convenience {@link Listener} that combines the {@link ShutdownListener} and
 * {@link InitializedListener} contracts, allowing a single object to be registered for
 * both initialization and shutdown notifications of a Pi4J context.
 */
public class ComplexListener implements ShutdownListener, InitializedListener {

    @Override
    public void onInitialized(InitializedEvent event) {

    }

    @Override
    public void onShutdown(ShutdownEvent event) {

    }
}
