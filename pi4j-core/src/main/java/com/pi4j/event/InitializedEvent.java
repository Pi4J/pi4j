package com.pi4j.event;

import com.pi4j.context.Context;

/**
 * {@link Event} fired when a Pi4J {@link Context} has completed initialization, and
 * delivered to registered {@link InitializedListener}s via
 * {@link InitializedListener#onInitialized(InitializedEvent)}. It carries the
 * initialized context as its payload.
 */
public class InitializedEvent implements Event {
    protected final Context context;

    /**
     * Creates an initialization event for the given Pi4J context.
     *
     * @param context the Pi4J {@link Context} that has just been initialized
     */
    public InitializedEvent(Context context){
        this.context = context;
    }
}
