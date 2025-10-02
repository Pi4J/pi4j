package com.pi4j.io;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOBase.java
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

import com.pi4j.common.Descriptor;
import com.pi4j.common.IdentityBase;
import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.provider.Provider;

import java.io.Closeable;

/**
 * <p>Abstract IOBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <CONFIG_TYPE>
 * @param <IO_TYPE>
 * @param <PROVIDER_TYPE>
 */
public abstract class IOBase<IO_TYPE extends IO, CONFIG_TYPE extends IOConfig, PROVIDER_TYPE extends Provider>
        extends IdentityBase implements IO<IO_TYPE,CONFIG_TYPE, PROVIDER_TYPE>, Closeable {

    protected CONFIG_TYPE config;
    protected PROVIDER_TYPE provider;
    private Context context;
    // close() requires idempotency.
    private boolean closed = false;

    /** {@inheritDoc} */
    @Override
    public PROVIDER_TYPE provider(){
        return this.provider;
    }

    /**
     * <p>Constructor for IOBase.</p>
     *
     * @param provider a PROVIDER_TYPE object.
     * @param config a CONFIG_TYPE object.
     */
    public IOBase(PROVIDER_TYPE provider, CONFIG_TYPE config){
        super();
        this.id = config.id();
        this.name = config.name();
        this.description = config.description();
        this.provider = provider;
        this.config = config;
    }

    /** {@inheritDoc} */
    @Override
    public IO_TYPE name(String name){
        this.name = name;
        return (IO_TYPE)this;
    }

    /** {@inheritDoc} */
    @Override
    public IO_TYPE description(String description){
        this.description = description;
        return (IO_TYPE)this;
    }

    /**
     * Closes the driver by calling this.context().shutdown(this.getId()), which in turn calls
     * the local shutdown() method here via DefaultRuntimeRegistry.remove().
     * <p>
     * Basically, for Pi4J, this constitutes an idempotent user convenience method for
     * this.context().shutdown(this.getId())
     * <p>
     * Subclasses should typically override the local shutdown method with implementation-specific shutdown
     * behaviour. Behaviour added here will not be triggered by context.shutdown()
     */
    @Override
    public void close() {
        // The null check accounts for contextless tests or somehow just closing without initializing
        // The closed check ensures idempotency, as required by the close method contract.
        if (this.context != null && !closed) {
            this.closed = true;
            this.context.shutdown(getId());
        }
    }

    /** {@inheritDoc} */
    @Override
    public CONFIG_TYPE config(){
        return this.config;
    }

    protected Context context() {
        return this.context;
    }

    /** {@inheritDoc} */
    @Override
    public IO_TYPE initialize(Context context) throws InitializeException {
        this.context = context;
        return (IO_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public IO_TYPE shutdown(Context context) throws ShutdownException {
        // Close is supposed to be idempotent. We interpret this here to include effective shutdowns by
        // other means, i.e. the infrastructure calling this method.
        this.closed = true;
        if (context != this.context) {
            throw new IllegalArgumentException("The context parameter and the local context don't match.");
        }
        return (IO_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public Descriptor describe() {
        return super.describe().category("IO");
    }
}
