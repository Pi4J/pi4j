package com.pi4j.runtime.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultRuntime.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.context.ContextConfig;
import com.pi4j.event.*;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.extension.impl.DefaultPluginService;
import com.pi4j.extension.impl.PluginStore;
import com.pi4j.io.IOType;
import com.pi4j.platform.Platform;
import com.pi4j.platform.impl.DefaultRuntimePlatforms;
import com.pi4j.platform.impl.RuntimePlatforms;
import com.pi4j.provider.Provider;
import com.pi4j.provider.impl.DefaultRuntimeProviders;
import com.pi4j.provider.impl.RuntimeProviders;
import com.pi4j.registry.impl.DefaultRuntimeRegistry;
import com.pi4j.registry.impl.RuntimeRegistry;
import com.pi4j.runtime.Runtime;
import com.pi4j.runtime.RuntimeProperties;
import com.pi4j.util.ExecutorPool;
import com.pi4j.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <p>DefaultRuntime class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultRuntime implements Runtime {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Context context;
    private final RuntimeRegistry registry;
    private final RuntimeProviders providers;
    private final RuntimePlatforms platforms;
    private final RuntimeProperties properties;
    private final List<Plugin> plugins;
    private boolean isShutdown = false;
    private final EventManager<Runtime, ShutdownListener, ShutdownEvent> shutdownEventManager;
    private final EventManager<Runtime, InitializedListener, InitializedEvent> initializedEventManager;
    private final ExecutorPool executorPool;
    private final ExecutorService runtimeExecutor;

    /**
     * <p>newInstance.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     *
     * @return a {@link com.pi4j.runtime.Runtime} object.
     */
    public static Runtime newInstance(Context context) {
        return new DefaultRuntime(context);
    }

    // private constructor
    private DefaultRuntime(Context context) {

        // set local references
        this.context = context;
        plugins = new ArrayList<>();
        this.properties = DefaultRuntimeProperties.newInstance(context);
        this.registry = DefaultRuntimeRegistry.newInstance(this);
        this.providers = DefaultRuntimeProviders.newInstance(this);
        this.platforms = DefaultRuntimePlatforms.newInstance(this);

        this.shutdownEventManager = new EventManager(this,
            (EventDelegate<ShutdownListener, ShutdownEvent>) (listener, event) -> listener.onShutdown(event));
        this.initializedEventManager = new EventManager(this,
            (EventDelegate<InitializedListener, InitializedEvent>) (listener, event) -> listener.onInitialized(event));

        // initialize executor pool and runtime executor
        this.executorPool = new ExecutorPool();
        this.runtimeExecutor = this.executorPool.getExecutor("Pi4J.RUNTIME");

        logger.debug("Pi4J runtime context successfully created & initialized.'");

        // listen for shutdown to properly clean up
        // TODO :: ADD PI4J INTERNAL SHUTDOWN CALLBACKS/EVENTS
        if (this.context.config().enableShutdownHook()) {
            java.lang.Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    // shutdown Pi4J
                    if (!isShutdown)
                        shutdown();
                } catch (Exception e) {
                    logger.error("Failed to shutdown Pi4J runtime", e);
                }
            }, "pi4j-shutdown"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context context() {
        return this.context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeRegistry registry() {
        return this.registry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeProviders providers() {
        return this.providers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimePlatforms platforms() {
        return this.platforms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeProperties properties() {
        return this.properties;
    }

    @Override
    public Future<?> submitTask(Runnable task) {
        return this.runtimeExecutor.submit(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Runtime shutdown() throws ShutdownException {
        if (isShutdown) {
            logger.warn("Pi4J context/runtime is already shutdown.'");
            return this;
        }

        isShutdown = true;
        logger.info("Shutting down Pi4J context/runtime...");

        // notify before shutdown event listeners (requires custom delegate to invoke appropriate listener method)
        shutdownEventManager.dispatch(new ShutdownEvent(this.context), ShutdownListener::beforeShutdown);

        try {

            // remove shutdown monitoring thread
            //java.lang.Runtime.getRuntime().removeShutdownHook(this.shutdownThread);

            // remove all I/O instances
            this.registry.shutdown();

            // shutdown platforms
            this.platforms.shutdown();

            // shutdown all providers
            this.providers.shutdown();

            // shutdown all plugins
            for (Plugin plugin : this.plugins) {
                try {
                    plugin.shutdown(this.context);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            // shutdown executor pool
            this.executorPool.destroy();

        } catch (Exception e) {
            logger.error("failed to 'shutdown(); '", e);
            throw new ShutdownException(e);
        }

        logger.info("Pi4J context/runtime successfully shutdown. Dispatching shutdown event.");

        // notify shutdown event listeners
        shutdownEventManager.dispatch(new ShutdownEvent(this.context));

        // remove all shutdown event listeners
        this.shutdownEventManager.clear();

        return this;
    }

    @Override
    public Future<Context> asyncShutdown() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                shutdown();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return context;
        });
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Runtime initialize() throws InitializeException {
        logger.info("Initializing Pi4J context/runtime...");
        try {
            // clear plugins container
            plugins.clear();

            // container sets for providers and platforms to load
            Set<Platform> platforms = new HashSet<>();
            Map<IOType, Provider> providers = new HashMap<>();

            // only attempt to load platforms and providers from the classpath if an auto detect option is enabled
            ContextConfig config = context.config();
            if (config.autoDetectPlatforms() || config.autoDetectProviders()) {

                // detect available Pi4J Plugins by scanning the classpath looking for plugin instances
                ServiceLoader<Plugin> plugins = ServiceLoader.load(Plugin.class);
                for (Plugin plugin : plugins) {
                    if (plugin == null)
                        continue;

                    if (!config.autoDetectMockPlugins() && plugin.isMock()) {
                        logger.trace("Ignoring mock plugin: [{}] in classpath", plugin.getClass().getName());
                        continue;
                    }

                    logger.trace("detected plugin: [{}] in classpath; calling 'initialize()'",
                        plugin.getClass().getName());
                    try {
                        // add plugin to internal cache
                        this.plugins.add(plugin);

                        PluginStore store = new PluginStore();
                        plugin.initialize(DefaultPluginService.newInstance(this.context(), store));

                        // if auto-detect providers is enabled,
                        //    OR
                        // Detecting Mocks is enabled and this is a mock plugin
                        // then add any detected providers to the collection to load
                        if (config.autoDetectProviders() ||  (config.autoDetectMockPlugins() && plugin.isMock())) {
                            store.providers.forEach(provider -> addProvider(provider, providers));
                        }

                   
                        // if auto-detect platforms is enabled,
                        // then add any detected platforms to the collection to load
                        if (config.autoDetectPlatforms()) {
                            platforms.addAll(store.platforms);
                        }

                    } catch (Exception ex) {
                        // unable to initialize this provider instance
                        logger.error("unable to 'initialize()' plugin: [{}]; {}", plugin.getClass().getName(),
                            ex.getMessage(), ex);
                    }
                }
            }

            // now add the explicit platforms and providers
            platforms.addAll(context().config().getPlatforms());
            context().config().getProviders().forEach(provider -> {
                Provider replaced = providers.put(provider.getType(), provider);
                if (replaced != null) {
                    logger.info("Replacing auto detected provider {} {} with provider {} from context config",
                        replaced.getType(), replaced.getName(), provider.getName());
                }
            });

            // initialize I/O registry
            this.registry.initialize();

            // initialize all providers
            this.providers.initialize(providers.values());

            // initialize all platforms
            this.platforms.initialize(platforms);

            // now auto-load any defined I/O injection instances available in the context config
            try {
                // ensure that the auto-injection option is enabled for this context
                if (this.context().config().autoInject()) {

                    // get potential injection candidates
                    Map<String, String> candidates = PropertiesUtil.keysEndsWith(this.context().properties().all(),
                        "inject");

                    // iterate over injection candidate and determine if it is configured/enabled for injection and perform injection
                    for (String candidateKey : candidates.keySet()) {
                        try {
                            boolean candidateInject = Boolean.parseBoolean(
                                candidates.getOrDefault(candidateKey, "false"));
                            if (candidateInject) {
                                this.context().create(candidateKey);
                            }
                        } catch (Exception e) {
                            logger.error("FAILED TO AUTO-INJECT [{}]; {}'", candidateKey, e.getMessage(), e);
                            throw new InitializeException(e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        } catch (Exception e) {
            logger.error("failed to 'initialize(); '", e);
            throw new InitializeException(e);
        }

        logger.info("Pi4J context/runtime successfully initialized.");

        // notify initialized event listeners
        notifyInitListeners();

        return this;
    }

    /**
     * <p>Adds providers to the given collection, to later be used in the runtime after initialization.</p>
     * <p>This method validates the priority of a {@link Provider}, and guarantees, that we don't have multiple
     * providers for the same {@link IOType}</p>
     *
     * @param provider
     * @param providers
     */
    private void addProvider(Provider provider, Map<IOType, Provider> providers) {
        if (!providers.containsKey(provider.getType())) {
            providers.put(provider.getType(), provider);
        } else {
            Provider existingProvider = providers.get(provider.getType());
            if (provider.getPriority() <= existingProvider.getPriority()) {
                if (existingProvider.getName().equals(provider.getName()))
                    throw new InitializeException(
                        provider.getType() + " with name " + provider.getName() + " is already registered.");
                logger.info("Ignoring provider {} {} with priority {} as lower priority than {} which has priority {}",
                    provider.getType(), provider.getName(), provider.getPriority(), existingProvider.getName(),
                    existingProvider.getPriority());
            } else {
                logger.info("Replacing provider {} {} with priority {} with provider {} with higher priority {}",
                    existingProvider.getType(), existingProvider.getName(), existingProvider.getPriority(),
                    provider.getName(), provider.getPriority());
                providers.put(provider.getType(), provider);
            }
        }
    }

    private void notifyInitListeners() {
        initializedEventManager.dispatch(new InitializedEvent(this.context));
    }

    @Override
    public Runtime addListener(ShutdownListener... listener) {
        return shutdownEventManager.add(listener);
    }

    @Override
    public Runtime removeListener(ShutdownListener... listener) {
        return shutdownEventManager.remove(listener);
    }

    @Override
    public Runtime removeAllShutdownListeners() {
        return shutdownEventManager.clear();
    }

    @Override
    public Runtime removeAllInitializedListeners() {
        return initializedEventManager.clear();
    }

    @Override
    public Runtime addListener(InitializedListener... listener) {
        return initializedEventManager.add(listener);
    }

    @Override
    public Runtime removeListener(InitializedListener... listener) {
        return initializedEventManager.remove(listener);
    }
}
