package com.pi4j.context.impl;

import com.pi4j.boardinfo.model.BoardInfo;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.pi4j.context.ContextConfig;
import com.pi4j.event.*;
import com.pi4j.exception.LifecycleException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.registry.Registry;
import com.pi4j.util.ExecutorPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class DefaultContext implements Context {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultContext.class);

    private final ContextConfig config;
    private BoardInfo boardInfo = null;

    private final EventManager<Context, ShutdownListener, ShutdownEvent> shutdownEventManager =new EventManager(this,
        (EventDelegate<ShutdownListener, ShutdownEvent>) (listener, event) -> listener.onShutdown(event));
    private final EventManager<Context, InitializedListener, InitializedEvent> initializedEventManager = new EventManager(this,
        (EventDelegate<InitializedListener, InitializedEvent>) (listener, event) -> listener.onInitialized(event));

    private final ExecutorPool executorPool = new ExecutorPool();
    private final ExecutorService runtimeExecutor = this.executorPool.getExecutor("Pi4J.RUNTIME");
    private final MutableRegistry mutableRegistry = new MutableRegistry(this);

    private volatile boolean isShutdown = false;

    public static Context newInstance(ContextConfig config) {
        if (!config.autoDetectProviders() &&
            !config.autoDetectMockPlugins()) {
            throw new IllegalArgumentException("Can't create a new instance without at least one auto-detection type enabled.");
        }

        // detect available Pi4J Plugins by scanning the classpath looking for plugin instances
        ServiceLoader<Plugin> serviceLoaderPlugins = ServiceLoader.load(Plugin.class);
        for (Plugin plugin : serviceLoaderPlugins) {
            if (plugin == null) continue;

            if (!config.autoDetectMockPlugins() && plugin.isMock()) {
                logger.trace("Ignoring mock plugin: [{}] in classpath", plugin.getClass().getName());
                continue;
            }
            if (!config.autoDetectProviders() && !plugin.isMock()) {
                logger.trace("Ignoring non-mock plugin: [{}] in classpath", plugin.getClass().getName());
                continue;
            }

            logger.trace("detected plugin: [{}] in classpath; calling 'initialize()'", plugin.getClass().getName());

            try {
                return plugin.createContext(config);
            } catch (Exception ex) {
                // unable to initialize this provider instance
                logger.error("unable to 'initialize()' plugin: [{}]; {}", plugin.getClass().getName(),
                    ex.getMessage(), ex);
            }
        }

        throw new IllegalStateException("No plugin found matching the provided context configuration");
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

        logger.info("Pi4J context/runtime successfully initialized.");

        // notify initialized event listeners
        initializedEventManager.dispatch(new InitializedEvent(this));

        logger.debug("Pi4J runtime context successfully created & initialized.");
    }

    @Override
    public ContextConfig config() { return this.config; }

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

    @Override
    public <I extends IO<?, ?>> I create(IOConfig config, IOType type) {
        I io = (I) createImpl(config, type);
        register(io);
        return io;
    }

    /**
     * Context implementations must override this method to crate IO instances. Registration is handled by
     * the caller.
     */
    protected abstract IO<?,?> createImpl(IOConfig config, IOType type);
}
