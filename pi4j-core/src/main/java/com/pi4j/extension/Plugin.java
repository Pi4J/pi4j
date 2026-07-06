package com.pi4j.extension;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;

/**
 * A loadable Pi4J component, typically discovered on the classpath, that contributes one or more
 * {@link com.pi4j.provider.Provider}s to the runtime. The runtime invokes {@link #initialize(PluginService)}
 * during startup, passing a {@link PluginService} through which the plugin registers its providers,
 * and {@link #shutdown(Context)} during teardown.
 */
public interface Plugin  {
    /**
     * Initializes this plugin, allowing it to register its providers and any other contributions
     * via the supplied {@link PluginService}. Called once by the runtime during startup.
     *
     * @param service the service used to access the {@link Context} and register providers
     * @throws InitializeException if the plugin fails to initialize or register its contributions
     */
    void initialize(PluginService service) throws InitializeException;

    /**
     * Releases any resources held by this plugin during runtime shutdown. The default implementation
     * does nothing; plugins with resources to clean up should override it.
     *
     * @param context the active Pi4J runtime context being shut down
     * @throws ShutdownException if an error occurs while shutting down the plugin
     */
    default void shutdown(Context context) throws ShutdownException {
        // do nothing <optional override>
    }

    /**
     * Indicates whether this plugin provides mock (simulated) implementations rather than real
     * hardware access. Mock plugins are intended for testing and should generally not be used in
     * production. The default implementation returns {@code false}.
     *
     * @return {@code true} if this is a mock plugin, otherwise {@code false}
     */
    default boolean isMock() {
        return false;
    }
}
