package com.pi4j.provider;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ProviderBase.java
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

import com.pi4j.config.Config;
import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.LifecycleException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.ExtensionBase;
import com.pi4j.io.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <p>Abstract ProviderBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <CONFIG_TYPE>
 * @param <IO_TYPE>
 * @param <PROVIDER_TYPE>
 */
public abstract class ProviderBase<PROVIDER_TYPE extends Provider, IO_TYPE extends IO, CONFIG_TYPE extends Config>
        extends ExtensionBase<PROVIDER_TYPE>
        implements Provider<PROVIDER_TYPE, IO_TYPE, CONFIG_TYPE> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Context context;

    /**
     * <p>Constructor for ProviderBase.</p>
     */
    public ProviderBase(){
        super();
    }

    /**
     * <p>Constructor for ProviderBase.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public ProviderBase(String id){
        super(id);
    }

    /**
     * <p>Constructor for ProviderBase.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public ProviderBase(String id, String name){
        super(id, name);
    }

    /** {@inheritDoc} */
    @Override
    public PROVIDER_TYPE initialize(Context context) throws InitializeException {
        this.context = context;
        return (PROVIDER_TYPE)this;
    }

    /** {@inheritDoc} */
    @Override
    public PROVIDER_TYPE shutdownInternal(Context context) throws ShutdownException {

        // TODO :: ABSTRACT PROVIDER IO INSTANCE SHUTDOWN VIA PROXY IMPL

        // perform a shutdown on each digital I/O instance that is tracked in the internal cache
        Map<String, IO> instances = context.registry().allByProvider(this.id(), IO.class);
        instances.forEach((address, instance)->{
            try {
                instance.shutdownInternal(context);
            } catch (LifecycleException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return (PROVIDER_TYPE)this;
    }

    /** {@inheritDoc} */
    @Override
    public Context context(){
        return this.context;
    }
}
