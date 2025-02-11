package com.pi4j.context;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Context.java
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

import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.boardinfo.model.BoardInfo;
import com.pi4j.boardinfo.model.JavaInfo;
import com.pi4j.boardinfo.model.OperatingSystem;
import com.pi4j.common.Describable;
import com.pi4j.common.Descriptor;
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.event.InitializedEventProducer;
import com.pi4j.event.ShutdownEventProducer;
import com.pi4j.exception.ShutdownException;
import com.pi4j.internal.IOCreator;
import com.pi4j.internal.ProviderProvider;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.exception.IOInvalidIDException;
import com.pi4j.io.exception.IONotFoundException;
import com.pi4j.io.exception.IOShutdownException;
import com.pi4j.platform.Platform;
import com.pi4j.platform.Platforms;
import com.pi4j.platform.exception.PlatformNotFoundException;
import com.pi4j.provider.Provider;
import com.pi4j.provider.Providers;
import com.pi4j.provider.exception.ProviderInterfaceException;
import com.pi4j.provider.exception.ProviderNotFoundException;
import com.pi4j.registry.Registry;
import com.pi4j.util.PropertiesUtil;
import com.pi4j.util.StringUtil;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * <p>Context interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Context extends Describable, IOCreator, ProviderProvider, InitializedEventProducer<Context>,
    ShutdownEventProducer<Context> {

    /**
     * <p>config.</p>
     *
     * @return a {@link com.pi4j.context.ContextConfig} object.
     */
    ContextConfig config();

    /**
     * <p>properties.</p>
     *
     * @return a {@link com.pi4j.context.ContextProperties} object.
     */
    ContextProperties properties();

    /**
     * <p>providers.</p>
     *
     * @return a {@link com.pi4j.provider.Providers} object.
     */
    Providers providers();

    /**
     * <p>registry.</p>
     *
     * @return a {@link com.pi4j.registry.Registry} object.
     */
    Registry registry();

    /**
     * <p>platforms.</p>
     *
     * @return a {@link com.pi4j.platform.Platforms} object.
     */
    Platforms platforms();

    /**
     * Submits the given task for async execution
     *
     * @param task the task to execute asynchronously
     * @return the task to cancel later
     */
    Future<?> submitTask(Runnable task);

    /**
     * <p>shutdown.</p>
     *
     * @return a {@link com.pi4j.context.Context} object.
     * @throws com.pi4j.exception.ShutdownException if an error occurs during shutdown.
     */
    Context shutdown() throws ShutdownException;

    /**
     * @return {@link Future} of {@link Context}
     */
    Future<Context> asyncShutdown();

    /**
     * @return Flag indicating if the context has been shutdown
     */
    boolean isShutdown();

    // ------------------------------------------------------------------------
    // PLATFORM ACCESSOR METHODS
    // ------------------------------------------------------------------------

    /**
     * <p>platform.</p>
     *
     * @param <P> a P object.
     * @return a P object.
     */
    default <P extends Platform> P platform() {
        return platforms().getDefault();
    }

    /**
     * <p>platform.</p>
     *
     * @param <P> a P object.
     * @return a P object.
     */
    default <P extends Platform> P getPlatform() {
        return this.platform();
    }

    /**
     * <p>platform.</p>
     *
     * @param <P> a P object.
     * @return a P object.
     */
    default <P extends Platform> P getDefaultPlatform() {
        return this.platform();
    }

    /**
     * <p>platform.</p>
     *
     * @param <P> a P object.
     * @return a P object.
     */
    default <P extends Platform> P defaultPlatform() {
        return this.platform();
    }

    /**
     * <p>platform.</p>
     *
     * @param id  Id of the platform.
     * @param <P> the platform type
     * @return a P object.
     * @throws PlatformNotFoundException if platform specified by {@code id} is not found.
     */
    default <P extends Platform> P platform(String id) throws PlatformNotFoundException {
        return (P) this.platforms().get(id);
    }

    /**
     * <p>platform.</p>
     *
     * @param id  Id of the platform.
     * @param <P> the platform type
     * @return a P object.
     * @throws PlatformNotFoundException if platform specified by {@code id} is not found.
     */
    default <P extends Platform> P getPlatform(String id) throws PlatformNotFoundException {
        return this.platform(id);
    }

    /**
     * <p>platform.</p>
     *
     * @param id Id of the platform.
     * @return a P object.
     * @throws PlatformNotFoundException if platform specified by {@code id} is not found.
     */
    default boolean hasPlatform(String id) throws PlatformNotFoundException {
        return this.platforms().exists(id);
    }

    /**
     * <p>platform.</p>
     *
     * @param platformClass a P object.
     * @param <P>           the platform type
     * @return a P object.
     * @throws PlatformNotFoundException if platform specified by {@code platformClass} is not found.
     */
    default <P extends Platform> P platform(Class<P> platformClass) throws PlatformNotFoundException {
        return (P) this.platforms().get(platformClass);
    }

    /**
     * <p>platform.</p>
     *
     * @param platformClass a P object.
     * @param <P>           the platform type
     * @return a P object.
     * @throws PlatformNotFoundException if platform specified by {@code platformClass} is not found.
     */
    default <P extends Platform> P getPlatform(Class<P> platformClass) throws PlatformNotFoundException {
        return platform(platformClass);
    }

    /**
     * <p>Has platforms.</p>
     *
     * @param platformClass a P object.
     * @return boolean
     * @throws PlatformNotFoundException if platform specified by {@code platformClass} is not found.
     */
    default boolean hasPlatform(Class<? extends Platform> platformClass) throws PlatformNotFoundException {
        return platforms().exists(platformClass);
    }

    // ------------------------------------------------------------------------
    // PROVIDER ACCESSOR METHODS
    // ------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    default <T extends Provider> T provider(String providerId) throws ProviderNotFoundException {
        return (T) providers().get(providerId);
    }

    /**
     * {@inheritDoc}
     */
    default <T extends Provider> T provider(String providerId, Class<T> providerClass)
        throws ProviderNotFoundException {
        return (T) providers().get(providerId);
    }

    /**
     * {@inheritDoc}
     */
    default boolean hasProvider(String providerId) {
        try {
            return providers().exists(providerId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    default <T extends Provider> boolean hasProvider(IOType ioType) {
        return providers().exists(ioType);
    }

    /**
     * {@inheritDoc}
     */
    default <T extends Provider> boolean hasProvider(Class<T> providerClass) {
        return providers().exists(providerClass);
    }

    /**
     * {@inheritDoc}
     */
    default <T extends Provider> T provider(Class<T> providerClass)
        throws ProviderNotFoundException, ProviderInterfaceException {
        // return the default provider for this type from the default platform
        if (platform() != null && platform().hasProvider(providerClass))
            return platform().provider(providerClass);

        // return the default provider for this type (outside of default platform)
        if (providers().exists(providerClass))
            return providers().get(providerClass);

        // provider not found
        throw new ProviderNotFoundException(providerClass);
    }

    /**
     * {@inheritDoc}
     */
    default <T extends Provider> T provider(IOType ioType) throws ProviderNotFoundException {
        // return the default provider for this type from the default platform
        if (platform() != null && platform().hasProvider(ioType))
            return platform().provider(ioType);

        // return the default provider for this type (outside of default platform)
        if (providers().exists(ioType))
            return providers().get(ioType);

        // provider not found
        throw new ProviderNotFoundException(ioType);
    }

    // ------------------------------------------------------------------------
    // BOARD INFO ACCESSOR METHODS
    // ------------------------------------------------------------------------

    /**
     * Return the BoardInfo containing more info about the
     * {@link BoardModel}, {@link OperatingSystem}, and {@link JavaInfo}.
     *
     * @return {@link BoardInfo}
     */
    BoardInfo boardInfo();

    // ------------------------------------------------------------------------
    // I/O INSTANCE ACCESSOR/CREATOR METHODS
    // ------------------------------------------------------------------------

    @Override
    default <I extends IO> I create(IOConfig config, IOType ioType) {

        // create by explicitly configured IO <PLATFORM> from IO config
        String platformId = config.platform();
        if (StringUtil.isNotNullOrEmpty(platformId)) {
            // resolve the platform and use it to create the IO instance
            Platform platform = this.platforms().get(platformId);
            return platform.create(config, ioType);
        }

        // create by explicitly configured IO <PROVIDER> from IO config
        String providerId = config.provider();
        if (StringUtil.isNotNullOrEmpty(providerId)) {
            // resolve the provider and use it to create the IO instance
            Provider provider = this.providers().get(providerId, ioType);
            return (I) provider.create(config);
        }

        // get implicitly defined provider (defined by IO type)
        // (this is the default or platform defined provider for this particular IO type)
        if (ioType != null) {
            // resolve the provider and use it to create the IO instance
            Provider provider = this.provider(ioType);
            return (I) provider.create(config);
        }

        // unable to resolve the IO type and thus unable to create I/O instance
        throw new IOException("This IO instance [" + config.id() + "] could not be created because it does not define one of the following: 'PLATFORM', 'PROVIDER', or 'I/O TYPE'.");
    }

    @Override
    default <T extends IO> T create(String id) {
        Provider provider = null;

        // resolve inheritable properties from the context based on the provided 'id' for this IO instance
        Map<String, String> inheritedProperties = PropertiesUtil.subProperties(this.properties().all(), id);

        // create by explicitly configured IO <PLATFORM> from IO inheritable properties
        if (inheritedProperties.containsKey("platform")) {
            // resolve the platform and use it to create the IO instance
            String platformId = inheritedProperties.get("platform");
            Platform platform = this.platforms().get(platformId);
            return platform.create(id);
        }

        // create by explicitly configured IO <PROVIDER> from IO config
        if (inheritedProperties.containsKey("provider")) {
            String providerId = inheritedProperties.get("provider");
            // resolve the provider and use it to create the IO instance
            provider = this.providers().get(providerId);
        }

        // create by IO TYPE
        // (use platform provider if one if available for this IO type)
        if (provider == null && inheritedProperties.containsKey("type")) {
            IOType ioType = IOType.parse(inheritedProperties.get("type"));
            provider = provider(ioType);
        }

        // validate resolved provider
        if (provider == null) {
            // unable to resolve the IO type and thus unable to create I/O instance
            throw new IOException("This IO instance [" + id +
                "] could not be created because it does not define one of the following: 'PLATFORM', 'PROVIDER', or 'I/O TYPE'.");
        }

        // create IO instance using the provided ID and resolved inherited properties
        ConfigBuilder builder = provider.type().newConfigBuilder(this);
        builder.id(id);
        builder.load(inheritedProperties);
        return (T) provider.create((Config) builder.build());
    }

    @Override
    default <T extends IO> T create(String id, IOType ioType) {
        Provider provider = null;

        // resolve inheritable properties from the context based on the provided 'id' for this IO instance
        Map<String, String> inheritedProperties = PropertiesUtil.subProperties(this.properties().all(), id);

        // create by explicitly configured IO <PLATFORM> from IO inheritable properties
        if (inheritedProperties.containsKey("platform")) {
            // resolve the platform and use it to create the IO instance
            String platformId = inheritedProperties.get("platform");
            Platform platform = this.platforms().get(platformId);
            return platform.create(id, ioType);
        }

        // create by explicitly configured IO <PROVIDER> from IO config
        if (inheritedProperties.containsKey("provider")) {
            String providerId = inheritedProperties.get("provider");
            // resolve the provider and use it to create the IO instance
            provider = this.providers().get(providerId, ioType);

            // validate IO type from resolved provider
            if (!ioType.isType(provider.type())) {
                throw new IOException("This IO instance [" + id +
                    "] could not be created because the resolved provider [" + providerId +
                    "] does not match the required I/O TYPE [" + ioType.name() + "]");
            }
        }

        // create by IO TYPE
        // (use platform provider if one if available for this IO type)
        provider = provider(ioType);

        // validate resolved provider
        if (provider == null) {
            throw new ProviderNotFoundException(ioType);
        }

        // create IO instance
        ConfigBuilder builder = provider.type().newConfigBuilder(this);
        builder.id(id);
        builder.load(inheritedProperties);
        return (T) provider.create((Config) builder.build());
    }

    /**
     * shutdown and unregister a created IO.
     *
     * @param <T> the IO Type
     * @param id  the IO id
     * @return the IO which was shutdown or null if it wasn't created/registered
     * @throws IONotFoundException  if the IO was not registered
     * @throws IOInvalidIDException if the ID is invalid
     * @throws IOShutdownException  if an error occured while shuting down the IO
     */
    <T extends IO> T shutdown(String id) throws IOInvalidIDException, IONotFoundException, IOShutdownException;

    // ------------------------------------------------------------------------
    // I/O INSTANCE ACCESSORS
    // ------------------------------------------------------------------------

    default boolean hasIO(String id) throws IOInvalidIDException, IONotFoundException {
        return registry().exists(id);
    }

    default <T extends IO> T io(String id) throws IOInvalidIDException, IONotFoundException {
        return registry().get(id);
    }

    default <T extends IO> T io(String id, Class<T> ioClass) throws IOInvalidIDException, IONotFoundException {
        return registry().get(id, ioClass);
    }

    default <T extends IO> T getIO(String id) throws IOInvalidIDException, IONotFoundException {
        return io(id);
    }

    default <T extends IO> T getIO(String id, Class<T> ioClass) throws IOInvalidIDException, IONotFoundException {
        return io(id, ioClass);
    }

    // ------------------------------------------------------------------------
    // DESCRIPTOR
    // ------------------------------------------------------------------------

    /**
     * <p>describe.</p>
     *
     * @return a {@link com.pi4j.common.Descriptor} object.
     */
    default Descriptor describe() {
        Descriptor descriptor = Descriptor.create().category("CONTEXT").name("Runtime Context").type(this.getClass());

        descriptor.add(registry().describe());
        descriptor.add(platforms().describe());
        descriptor.add(providers().describe());
        descriptor.add(properties().describe());
        return descriptor;
    }
}
