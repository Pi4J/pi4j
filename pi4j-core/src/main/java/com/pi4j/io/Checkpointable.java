package com.pi4j.io;

import com.pi4j.exception.LifecycleException;
import com.pi4j.exception.RestoreException;

/**
 * Implemented by {@link IO} instances that hold native resources (file descriptors) which must be released
 * before a CRaC (Coordinated Restore at Checkpoint) checkpoint and reacquired after a restore.
 * <p>
 * Unlike {@link IO#close()}, suspending keeps the Java object, its configuration, its listeners and its
 * cached state, so application code can keep using the same instance after the restore. The Pi4J context
 * calls these methods for every registered instance; applications don't need to call them directly.
 * <p>
 * Operations invoked on a suspended instance fail with a {@link com.pi4j.exception.Pi4JException}, so
 * applications should stop driving their I/O before triggering a checkpoint.
 *
 * @see <a href="https://docs.azul.com/crac/usage/integrating-crac">Integrating CRaC</a>
 */
public interface Checkpointable {

    /**
     * Releases all native resources held by this instance, while retaining its configuration and state.
     * Calling this on an already suspended or closed instance has no effect.
     *
     * @throws LifecycleException if the resources cannot be released
     */
    void suspend() throws LifecycleException;

    /**
     * Reacquires the native resources released by {@link #suspend()} and reapplies the retained
     * configuration and state. Calling this on an instance that isn't suspended has no effect.
     *
     * @throws RestoreException if the resources cannot be reacquired
     */
    void resume() throws RestoreException;
}
