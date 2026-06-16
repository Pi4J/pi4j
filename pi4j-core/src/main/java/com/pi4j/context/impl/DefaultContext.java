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
import com.pi4j.event.InitializedListener;
import com.pi4j.event.ShutdownListener;
import com.pi4j.exception.LifecycleException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.IO;
import com.pi4j.provider.Providers;
import com.pi4j.provider.impl.DefaultProviders;
import com.pi4j.registry.Registry;
import com.pi4j.runtime.Runtime;
import com.pi4j.runtime.impl.DefaultRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * <p>DefaultContext class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultContext implements Context {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Runtime runtime = null;
    private ContextConfig config = null;
    private Providers providers = null;
    private BoardInfo boardInfo = null;

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

        // create internal runtime state instance  (READ-ONLY ACCESS OBJECT)
        this.runtime = DefaultRuntime.newInstance(this);

        // create API accessible providers instance  (READ-ONLY ACCESS OBJECT)
        this.providers = DefaultProviders.newInstance(this.runtime.providers());

        // detect the board model
        this.boardInfo = BoardInfoHelper.current();
        logger.info("Detected board model: {}", boardInfo.getBoardModel().getLabel());
        logger.info("Running on: {}", boardInfo.getOperatingSystem());
        logger.info("With Java version: {}", boardInfo.getJavaInfo());

        // initialize runtime now
        this.runtime.initialize();

        logger.debug("Pi4J runtime context successfully created & initialized.'");
    }

    /** {@inheritDoc} */
    @Override
    public ContextConfig config() { return this.config; }

    /** {@inheritDoc} */
    @Override
    public Providers providers() { return providers; }

    /** {@inheritDoc} */
    @Override
    public Registry registry() { return this.runtime.registry(); }

    /** {@inheritDoc} */
    @Override
    public BoardInfo boardInfo() { return this.boardInfo; }

    /** {@inheritDoc} */
    @Override
    public Future<?> submitTask(Runnable task) {
        return this.runtime.submitTask(task);
    }

    /** {@inheritDoc} */
    @Override
    public Context shutdown() throws ShutdownException {
        // shutdown the runtime
        this.runtime.shutdown();
        return this;
	}

	@Override
	public <T extends IO> void shutdown(T instance) {
		runtime.remove(instance);
	}
	@Override
	public <T extends IO> T shutdown(String id) {
		return runtime.remove(runtime.registry().get(id));
	}

    @Override
    public boolean isShutdown() {
        return this.runtime.isShutdown();
    }

    @Override
    public Future<Context> asyncShutdown() {
        return this.runtime.asyncShutdown();
    }

    @Override
    public Context addListener(ShutdownListener... listener) {
        runtime.addListener(listener);
        return this;
    }

    @Override
    public Context removeListener(ShutdownListener... listener) {
        runtime.removeListener(listener);
        return this;
    }

    @Override
    public Context removeAllShutdownListeners() {
        runtime.removeAllShutdownListeners();
        return this;
    }

    @Override
    public Context removeAllInitializedListeners() {
        this.runtime.removeAllInitializedListeners();
        return this;
    }

    @Override
    public Context addListener(InitializedListener... listener) {
        this.runtime.addListener(listener);
        return this;
    }

    @Override
    public Context removeListener(InitializedListener... listener) {
        this.runtime.removeListener(listener);
        return this;
    }
}
