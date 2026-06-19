package com.pi4j.context.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultContext.java
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

import com.pi4j.boardinfo.model.BoardInfo;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.pi4j.context.ContextConfig;
import com.pi4j.event.*;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.LifecycleException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.extension.impl.DefaultPluginService;
import com.pi4j.extension.impl.PluginStore;
import com.pi4j.io.IO;
import com.pi4j.io.IOType;
import com.pi4j.provider.Provider;
import com.pi4j.provider.Providers;
import com.pi4j.registry.Registry;
import com.pi4j.util.ExecutorPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <p>DefaultContext class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultContext implements Context {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ContextConfig config = null;
    private BoardInfo boardInfo = null;

    private final MutableProviders mutableProviders = new MutableProviders(this);
    private final List<Plugin> plugins = new ArrayList<>();
    private final EventManager<Context, ShutdownListener, ShutdownEvent> shutdownEventManager =new EventManager(this,
        (EventDelegate<ShutdownListener, ShutdownEvent>) (listener, event) -> listener.onShutdown(event));
    private final EventManager<Context, InitializedListener, InitializedEvent> initializedEventManager = new EventManager(this,
        (EventDelegate<InitializedListener, InitializedEvent>) (listener, event) -> listener.onInitialized(event));

    private final ExecutorPool executorPool = new ExecutorPool();
    private final ExecutorService runtimeExecutor = this.executorPool.getExecutor("Pi4J.RUNTIME");
    private final MutableRegistry mutableRegistry = new MutableRegistry(this);

    private volatile boolean isShutdown = false;

    /**
     * <p>newInstance.</p>
     *
     * @param config a {@link com.pi4j.context.ContextConfig} object.
     * @return a {@link com.pi4j.context.Context} object.
     */
    public static Context newInstance(ContextConfig config) {
        return new DefaultContext(config);
    }

    /**
     * This constructor is protected to support special-case contexts bypassing providers and should not typically
     * be used / useful for user code.
     */
    protected DefaultContext(ContextConfig config) {
        logger.trace("new Pi4J runtime context initialized [config={}]", config);

        // validate config object exists
        if(config == null) {
            throw new LifecycleException("Unable to create new Pi4J runtime context; missing (ContextConfig) config object.");
        }

        // set context config member reference
        this.config = config;

        // listen for shutdown to properly clean up
        // TODO :: ADD PI4J INTERNAL SHUTDOWN CALLBACKS/EVENTS
        if (this.config().enableShutdownHook()) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    // shutdown Pi4J
                    if (!isShutdown)
                        shutdown();
                } catch (Exception e) {
                    logger.error("Failed to shutdown Pi4J runtime", e);
                }
            }, "pi4j-shutdown"));
        }

        // detect the board model
        this.boardInfo = BoardInfoHelper.current();
        logger.info("Detected board model: {}", boardInfo.getBoardModel().getLabel());
        logger.info("Running on: {}", boardInfo.getOperatingSystem());
        logger.info("With Java version: {}", boardInfo.getJavaInfo());

        // initialize runtime now
        logger.info("Initializing Pi4J context/runtime...");
        try {
            // clear plugins container
            plugins.clear();

            // container sets for providers to load
            Map<IOType, Provider> providers = new HashMap<>();

            // only attempt to load platforms and providers from the classpath if an auto detect option is enabled
            if (config.autoDetectPlatforms() || config.autoDetectProviders()) {

                // detect available Pi4J Plugins by scanning the classpath looking for plugin instances
                ServiceLoader<Plugin> serviceLoaderPlugins = ServiceLoader.load(Plugin.class);
                for (Plugin plugin : serviceLoaderPlugins) {
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
                        plugin.initialize(DefaultPluginService.newInstance(this, store));

                        // if auto-detect providers is enabled,
                        //    OR
                        // Detecting Mocks is enabled and this is a mock plugin
                        // then add any detected providers to the collection to load
                        if (config.autoDetectProviders() ||  (config.autoDetectMockPlugins() && plugin.isMock())) {
                            store.providers.forEach(provider -> addProvider(provider, providers));
                        }

                    } catch (Exception ex) {
                        // unable to initialize this provider instance
                        logger.error("unable to 'initialize()' plugin: [{}]; {}", plugin.getClass().getName(),
                            ex.getMessage(), ex);
                    }
                }
            }

            config().getProviders().forEach(provider -> {
                Provider replaced = providers.put(provider.getType(), provider);
                if (replaced != null) {
                    logger.info("Replacing auto detected provider {} {} with provider {} from context config",
                        replaced.getType(), replaced.getName(), provider.getName());
                }
            });

            // initialize all providers
            this.mutableProviders.initialize(providers.values());

        } catch (Exception e) {
            logger.error("failed to 'initialize(); '", e);
            throw new InitializeException(e);
        }

        logger.info("Pi4J context/runtime successfully initialized.");

        // notify initialized event listeners
        initializedEventManager.dispatch(new InitializedEvent(this));

        logger.debug("Pi4J runtime context successfully created & initialized.");
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
                        provider.getType() + " with name " + provider.getName() + " (" + provider.getId() + ") is already registered.");
                logger.info("Ignoring provider {} {} ({}) with priority {} as lower priority than {} which has priority {}",
                    provider.getType(), provider.getName(), provider.getId(), provider.getPriority(),
                    existingProvider.getName(), existingProvider.getPriority());
            } else {
                logger.info("Replacing provider {} {} ({}) with priority {} with provider {} ({}) with higher priority {}",
                    existingProvider.getType(), existingProvider.getName(), existingProvider.getId(), existingProvider.getPriority(),
                    provider.getName(), provider.getId(), provider.getPriority());
                providers.put(provider.getType(), provider);
            }
        }
    }

    @Override
    public ContextConfig config() { return this.config; }

    @Override
    public Providers providers() { return mutableProviders; }

    @Override
    public Registry registry() { return this.mutableRegistry; }

    @Override
    public BoardInfo boardInfo() { return this.boardInfo; }

    @Override
    public Future<?> submitTask(Runnable task) {
        return this.runtimeExecutor.submit(task);
    }

    @Override
    public Context shutdown() throws ShutdownException {
        // shutdown the runtime
        if (isShutdown) {
            logger.warn("Pi4J context/runtime is already shutdown.");
            return this;
        }

        isShutdown = true;
        logger.info("Shutting down Pi4J context/runtime...");

        // notify before shutdown event listeners (requires custom delegate to invoke appropriate listener method)
        shutdownEventManager.dispatch(new ShutdownEvent(this), ShutdownListener::beforeShutdown);

        try {
            // remove shutdown monitoring thread
            //java.lang.Runtime.getRuntime().removeShutdownHook(this.shutdownThread);

            // remove all I/O instances
            this.mutableRegistry.shutdown();

            // shutdown all providers
            this.mutableProviders.shutdown();

            // shutdown all plugins
            for (Plugin plugin : this.plugins) {
                try {
                    plugin.shutdown(this);
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
        shutdownEventManager.dispatch(new ShutdownEvent(this));

        // remove all shutdown event listeners
        this.shutdownEventManager.clear();

        return this;
	}

	@Override
	public <T extends IO> void shutdown(T instance) {
		mutableRegistry.shutdown(instance);
	}

	@Override
	public <T extends IO> T shutdown(String id) {
        T io = mutableRegistry.get(id);
		shutdown(io);
        return io;
	}

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public Future<Context> asyncShutdown() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                shutdown();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return this;
        });
    }

    @Override
    public Context addListener(ShutdownListener... listener) {
        shutdownEventManager.add(listener);
        return this;
    }

    @Override
    public Context removeListener(ShutdownListener... listener) {
        shutdownEventManager.remove(listener);
        return this;
    }

    @Override
    public Context removeAllShutdownListeners() {
        shutdownEventManager.clear();
        return this;
    }

    @Override
    public Context removeAllInitializedListeners() {
        initializedEventManager.clear();
        return this;
    }

    @Override
    public Context addListener(InitializedListener... listener) {
        initializedEventManager.add(listener);
        return this;
    }

    @Override
    public Context removeListener(InitializedListener... listener) {
        initializedEventManager.remove(listener);
        return this;
    }

    @Override
    public void register(IO instance) {
        mutableRegistry.register(instance);
    }
}
