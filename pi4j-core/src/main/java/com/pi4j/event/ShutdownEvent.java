package com.pi4j.event;

import com.pi4j.context.Context;

/**
 * {@link Event} fired when a Pi4J {@link Context} is shutting down, and delivered to
 * registered {@link ShutdownListener}s via
 * {@link ShutdownListener#onShutdown(ShutdownEvent)} (with an optional pre-notification
 * through {@link ShutdownListener#beforeShutdown(ShutdownEvent)}). It carries the
 * context being shut down as its payload.
 */
public class ShutdownEvent implements Event {
    protected final Context context;

    /**
     * Creates a shutdown event for the given Pi4J context.
     *
     * @param context the Pi4J {@link Context} that is being shut down
     */
    public ShutdownEvent(Context context){
        this.context = context;
    }
}
