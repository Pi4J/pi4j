package com.pi4j.platform;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PlatformBase.java
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
import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.ExtensionBase;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.IOException;
import com.pi4j.provider.Provider;
import com.pi4j.provider.exception.ProviderException;
import com.pi4j.provider.exception.ProviderInterfaceException;
import com.pi4j.provider.exception.ProviderNotFoundException;
import com.pi4j.util.PropertiesUtil;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Abstract PlatformBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <PLATFORM>
 */
public abstract class PlatformBase<PLATFORM extends Platform>
        extends ExtensionBase<Platform>
        implements Platform {

    protected Context context = null;
    protected Map<IOType, Provider> providers = new ConcurrentHashMap<>();
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>Constructor for PlatformBase.</p>
     */
    public PlatformBase(){
        super();
    }

    /**
     * <p>Constructor for PlatformBase.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public PlatformBase(String id){
        super(id);
    }

    /**
     * <p>Constructor for PlatformBase.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public PlatformBase(String id, String name){
        super(id, name);
    }

    /**
     * <p>Constructor for PlatformBase.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     */
    public PlatformBase(String id, String name, String description){
        super(id, name, description);
    }

    /** {@inheritDoc} */
    @Override
    public Map<IOType, Provider> providers() {
        return Collections.unmodifiableMap(this.providers);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Provider> T provider(Class<T> providerClass) throws ProviderNotFoundException, ProviderInterfaceException {

        if(!providerClass.isInterface()){
            logger.warn("Provider type [{}] requested; this is not an 'Interface'" +
                        "and may not return a valid provider or may not be able to cast to the concrete class.",
                        providerClass.getName());
        }

        for (Provider p : providers.values()) {
            if (providerClass.isAssignableFrom(p.getClass()))
                return (T) p;
        }

        if (providerClass.isInterface()) {
            throw new ProviderNotFoundException(providerClass);
        } else {
            throw new ProviderInterfaceException(providerClass);
        }
    }

    /** {@inheritDoc} */
    @Override
    public <I extends IO>I create(IOConfig config, IOType ioType) {

        // create by explicitly configured IO <PROVIDER> from IO config
        String providerId = config.provider();
        if(StringUtil.isNotNullOrEmpty(providerId)) {
            // resolve the provider and use it to create the IO instance
            Provider provider = this.provider(providerId);
            return (I)provider.create(config);
        }

        // get implicitly defined provider (defined by IO type) for this platform
        // (this is the platform defined provider for this particular IO type)
        if(hasProvider(ioType)){
            Provider provider = this.provider(ioType);
            if(provider == null) {
                throw new ProviderNotFoundException(ioType);
            }
            // create IO instance
            return (I)provider.create(config);
        } else {
            throw new ProviderNotFoundException(ioType);
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IO>T create(String id) {
        Provider provider = null;

        // validate context
        if(this.context == null) throw new IllegalStateException("Unable to create IO instance; this platform has not been 'initialized()' with a Pi4J context.");

        // resolve inheritable properties from the context based on the provided 'id' for this IO instance
        Map<String,String> inheritedProperties = PropertiesUtil.subProperties(this.context.properties().all(), id);

        // create by explicitly configured IO <PROVIDER> from IO config
        if(inheritedProperties.containsKey("provider")){
            String providerId = inheritedProperties.get("provider");
            // resolve the provider and use it to create the IO instance
            provider = this.provider(providerId);
        }

        // create by IO TYPE
        // (use platform provider if one if available for this IO type)
        if(provider == null && inheritedProperties.containsKey("type")){
            IOType ioType = IOType.parse(inheritedProperties.get("type"));
            provider = provider(ioType);
        }

        // validate resolved provider
        if(provider == null) {
            // unable to resolve the IO type and thus unable to create I/O instance
            throw new IOException("This IO instance [" + id +
                    "] could not be created because it does not define one of the following: 'PROVIDER', or 'I/O TYPE'.");
        }

        // create IO instance
        ConfigBuilder builder = provider.type().newConfigBuilder(this.context);
        builder.id(id);
        builder.load(inheritedProperties);
        return (T)provider.create((Config) builder.build());
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IO>T create(String id, IOType ioType) {
        Provider provider = null;

        // validate context
        if(this.context == null) throw new IllegalStateException("Unable to create IO instance; this platform has not been 'initialized()' with a Pi4J context.");

        // resolve inheritable properties from the context based on the provided 'id' for this IO instance
        Map<String,String> inheritedProperties = PropertiesUtil.subProperties(this.context.properties().all(), id);

        // create by explicitly configured IO <PROVIDER> from IO config
        if(inheritedProperties.containsKey("provider")){
            String providerId = inheritedProperties.get("provider");
            // resolve the provider and use it to create the IO instance
            provider = this.provider(providerId);

            // validate IO type from resolved provider
            if(!ioType.isType(provider.type())){
                throw new IOException("This IO instance [" + id +
                        "] could not be created because the resolved provider [" + providerId +
                        "] does not match the required I/O TYPE [" + ioType.name() + "]");
            }
        }

        // create by IO TYPE
        // (use platform provider if one if available for this IO type)
        provider = provider(ioType);

        // validate resolved provider
        if(provider == null) {
            throw new ProviderNotFoundException(ioType);
        }

        // create IO instance
        ConfigBuilder builder = provider.type().newConfigBuilder(this.context);
        builder.id(id);
        builder.load(inheritedProperties);
        return (T)provider.create((Config) builder.build());
    }

    /** {@inheritDoc} */
    @Override
    public abstract int priority();

    /** {@inheritDoc} */
    @Override
    public abstract boolean enabled(Context context);

    /** {@inheritDoc} */
    @Override
    public PLATFORM initialize(Context context) throws InitializeException {
        this.context = context;
        String[] provIds = getProviders();
        for (String providerId : provIds) {
            if (!context.providers().exists(providerId))
                continue;
            try {
                addProvider(context, providerId);
            } catch (ProviderException e) {
                throw new InitializeException(e.getMessage());
            }
        }
        return (PLATFORM) this;
    }

    /** {@inheritDoc} */
    @Override
    public PLATFORM shutdownInternal(Context context) throws ShutdownException {
        return (PLATFORM)this;
    }

    /**
     * <p>Getter for the field <code>providers</code>.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    protected abstract String[] getProviders();

    /**
     * <p>addProvider.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @param providerId a {@link java.lang.String} object.
     * @throws com.pi4j.provider.exception.ProviderException if any.
     */
    protected void addProvider(Context context, String providerId) throws ProviderException {
        var provider = context.providers().get(providerId);
        this.providers.put(IOType.getByProviderClass(provider.getClass()), provider);
    }
}
