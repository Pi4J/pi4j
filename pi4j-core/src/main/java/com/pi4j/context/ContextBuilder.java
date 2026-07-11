package com.pi4j.context;

import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.config.Builder;
import com.pi4j.context.impl.DefaultContext;
import com.pi4j.exception.Pi4JException;
import com.pi4j.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Fluent builder used to configure and create a Pi4J {@link Context}. It accumulates settings such as
 * the default platform, auto-detection behaviour for platforms and providers, the shutdown hook, manually
 * added {@link Provider}s and user properties, and finally produces either a {@link ContextConfig} (via
 * {@link #toConfig()}) or a fully initialized {@link Context} (via {@link #build()}). Obtain an instance
 * with {@link #newInstance()}.
 *
 * @see Context
 * @see ContextConfig
 */
public class ContextBuilder implements Builder<Context> {
    protected Logger logger = LoggerFactory.getLogger(ContextBuilder.class);

    // auto detection flags
    protected boolean autoDetectMockPlugins = !BoardInfoHelper.runningOnRaspberryPi();
    protected boolean autoDetectProviders = false;
    protected boolean enableShutdownHook = false;

    // default platform identifier
    protected String defaultPlatformId = null;

    // extensibility modules
    protected Collection<Provider> providers = Collections.synchronizedList(new ArrayList<>());

    // properties
    protected Map<String,String> properties = Collections.synchronizedMap(new HashMap<>());

    // Add a field to store the GPIO chip name
    protected String gpioChipName;

    /**
     * Private Constructor
     */
    protected ContextBuilder() {
        // forbid object construction
    }

    public static ContextBuilder newInstance(){
        return new ContextBuilder();
    }

    /**
     * Adds one or more providers to be registered in the resulting context.
     *
     * @param provider the providers to add
     * @return this builder instance for method chaining
     */
    public ContextBuilder add(Provider... provider) {
        if(provider != null && provider.length > 0)
            this.providers.addAll(List.of(provider));
        return this;
    }

    /**
     * Enables auto-detection of mock plugins on the classpath, primarily useful for testing without real
     * hardware.
     *
     * @return this builder instance for method chaining
     */
    public ContextBuilder autoDetectMockPlugins() {
        this.autoDetectMockPlugins = true;
        return this;
    }

    /**
     * Enables auto-detection of provider implementations available on the classpath.
     *
     * @return this builder instance for method chaining
     */
    public ContextBuilder autoDetectProviders() {
        this.autoDetectProviders = true;
        return this;
    }

    /**
     * Disables auto-detection of provider implementations on the classpath.
     *
     * @return this builder instance for method chaining
     */
    public ContextBuilder noAutoDetectProviders() {
        this.autoDetectProviders = false;
        return this;
    }

    /**
     * Enables registration of a JVM shutdown hook that automatically shuts the context down when the JVM
     * terminates.
     *
     * @return this builder instance for method chaining
     */
    public ContextBuilder enableShutdownHook() {
        this.enableShutdownHook = true;
        return this;
    }

    public ContextBuilder disableShutdownHook() {
        this.enableShutdownHook = false;
        return this;
    }

    public ContextBuilder setGpioChipName(String chipName) {
        this.gpioChipName = chipName;
        return this;
    }

    /**
     * Enables or disables the JVM shutdown hook depending on the given flag.
     *
     * @param enableShutdownHook {@code true} to register the shutdown hook, {@code false} to skip it
     * @return this builder instance for method chaining
     */
    public ContextBuilder setShutdownHook(boolean enableShutdownHook) {
        if (enableShutdownHook)
            return enableShutdownHook();
        else
            return disableShutdownHook();
    }

    /**
     * Builds an immutable configuration snapshot from the current builder state, without creating a context.
     *
     * @return a {@link ContextConfig} reflecting this builder's settings
     */
    public ContextConfig toConfig() {

        // create a new context configuration object
        return new ContextConfig() {
            private final Collection<Provider> providers = Collections.unmodifiableCollection(
                new ArrayList<>(ContextBuilder.this.providers));
            private final boolean autoDetectMockPlugins = ContextBuilder.this.autoDetectMockPlugins;
            private final boolean enableShutdownHook = ContextBuilder.this.enableShutdownHook;
            private final boolean autoDetectProviders = ContextBuilder.this.autoDetectProviders;

            @Override
            public Collection<Provider> providers() {
                return providers;
            }

            @Override
            public boolean autoDetectMockPlugins() {
                return autoDetectMockPlugins;
            }

            @Override
            public boolean enableShutdownHook() {
                return enableShutdownHook;
            }

            @Override
            public boolean autoDetectProviders() {
                return autoDetectProviders;
            }
        };
    }

    @Override
    public Context build() throws Pi4JException {
        logger.trace("invoked 'build()'");

        // create new context
        Context context = DefaultContext.newInstance(this.toConfig());

        // return the newly created context
        logger.debug("Pi4J successfully created and initialized a new runtime 'Context'.'");
        return context;
    }
}
