package com.pi4j.extension;

import com.pi4j.context.Context;
import com.pi4j.provider.Provider;

/**
 * Service handed to a {@link Plugin} during {@link Plugin#initialize(PluginService)}, exposing the
 * runtime {@link Context} and providing the entry point through which the plugin registers its
 * {@link Provider}s with Pi4J.
 */
public interface PluginService {
    /**
     * Returns the active Pi4J runtime context, giving the plugin access to configuration and the
     * registries it contributes to.
     *
     * @return the current Pi4J {@link Context}
     */
    Context context();

    /**
     * Registers one or more providers contributed by the plugin with the Pi4J runtime.
     *
     * @param provider the providers to register
     * @return this service instance, to allow chaining of registration calls
     */
    PluginService register(Provider ... provider);
}
