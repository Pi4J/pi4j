package com.pi4j.common;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;

import java.io.Closeable;

/**
 * Defines the managed startup and shutdown phases for Pi4J components such as I/O providers and platforms.
 * Components are brought online with {@link #initialize(Context)} and torn down with
 * close().
 *
 * @param <T> the self-type returned by the lifecycle methods, enabling fluent chaining on the implementing component
 */
public interface Lifecycle<T> extends Closeable {

    /**
     * Initializes this component against the given Pi4J context, acquiring any resources it needs to operate.
     *
     * @param context the active Pi4J {@link Context} this component is being initialized within
     * @return this component instance, initialized and ready for use
     * @throws com.pi4j.exception.InitializeException if an error occurs during initialization.
     */
    T initialize(Context context) throws InitializeException;

    @Override
    void close();
}
