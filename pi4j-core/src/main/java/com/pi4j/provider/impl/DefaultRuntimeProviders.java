package com.pi4j.provider.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultRuntimeProviders.java
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

import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.analog.AnalogInputProvider;
import com.pi4j.io.gpio.analog.AnalogOutputProvider;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.io.serial.SerialProvider;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.provider.Provider;
import com.pi4j.provider.ProviderGroup;
import com.pi4j.provider.Providers;
import com.pi4j.provider.exception.ProviderAlreadyExistsException;
import com.pi4j.provider.exception.ProviderInitializeException;
import com.pi4j.provider.exception.ProviderNotFoundException;
import com.pi4j.runtime.Runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * </p>
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultRuntimeProviders implements RuntimeProviders {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRuntimeProviders.class);

    private Runtime runtime = null;

    // all detected/available providers
    private Map<String, Provider> providers = new ConcurrentHashMap<>();

    private ProviderGroup<AnalogInputProvider> _analogInput = new ProviderGroup<>(this, IOType.ANALOG_INPUT);
    private ProviderGroup<AnalogOutputProvider> _analogOutput = new ProviderGroup<>(this, IOType.ANALOG_OUTPUT);
    private ProviderGroup<DigitalInputProvider> _digitalInput = new ProviderGroup<>(this, IOType.DIGITAL_INPUT);
    private ProviderGroup<DigitalOutputProvider> _digitalOutput = new ProviderGroup<>(this, IOType.DIGITAL_OUTPUT);
    private ProviderGroup<PwmProvider> _pwm = new ProviderGroup<>(this, IOType.PWM);
    private ProviderGroup<SpiProvider> _spi = new ProviderGroup<>(this, IOType.SPI);
    private ProviderGroup<I2CProvider> _i2c = new ProviderGroup<>(this, IOType.I2C);
    private ProviderGroup<SerialProvider> _serial = new ProviderGroup<>(this, IOType.SERIAL);

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<AnalogInputProvider> analogInput() { return _analogInput; }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<AnalogOutputProvider> analogOutput() { return _analogOutput; }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<DigitalInputProvider> digitalInput() { return _digitalInput; }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<DigitalOutputProvider> digitalOutput() { return _digitalOutput; }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<PwmProvider> pwm() { return _pwm; }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<SpiProvider> spi() { return _spi; }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<I2CProvider> i2c() { return _i2c; }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<SerialProvider> serial() { return _serial; }

    // static singleton instance
    /**
     * <p>newInstance.</p>
     *
     * @param runtime a {@link com.pi4j.runtime.Runtime} object.
     * @return a {@link com.pi4j.provider.impl.RuntimeProviders} object.
     */
    public static RuntimeProviders newInstance(Runtime runtime){
        return new DefaultRuntimeProviders(runtime);
    }

    // private constructor
    private DefaultRuntimeProviders(Runtime runtime) {
        // set local runtime reference
        this.runtime = runtime;
    }

    /**
     * {@inheritDoc}
     *
     * Get all providers
     */
    @Override
    public Map<String, Provider> all(){
        return Collections.unmodifiableMap(this.providers);
    }

    /**
     * {@inheritDoc}
     *
     * Get all providers of a specified io class type.
     */
    @Override
    public <T extends Provider> Map<String, T> all(Class<T> providerClass) {

        if(!providerClass.isInterface()){
            logger.warn("Provider type [{}] requested; this is not an 'Interface' and make not return a valid " +
                        "provider or may not be able to cast to the concrete class.",
                providerClass.getName());
        }

        // create a map <io-id, io-instance> of providers that extend of the given io class
        var result = new ConcurrentHashMap<String, T>();
        providers.values().stream().forEach(p -> {
            if (providerClass.isInstance(p)) {
                result.put(p.id(), (T) p);
            }
        });
        return Collections.unmodifiableMap(result);
    }

    /**
     * {@inheritDoc}
     *
     * Get all providers of a specified io class type.
     */
    @Override
    public <T extends Provider> Map<String, T> all(IOType ioType) {

        // create a map <io-id, io-instance> of providers that match the given ProviderType
        var result = new ConcurrentHashMap<String, T>();
        providers.values().stream().filter(provider -> provider.isType(ioType)).forEach(provider -> {
            result.put(provider.id(), (T) provider);
        });
        return Collections.unmodifiableMap(result);
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(String providerId) {

        // return true if the managed io map contains the given io-id
        if(providers.containsKey(providerId)){
            return true;
        }
        // additionally attempt to resolve the provider by its class name
        try {
            Class providerClass = Class.forName(providerId);
            if (providerClass != null && Provider.class.isAssignableFrom(providerClass)) {
                for(Provider provider : providers.values()){
                    if(providerClass.isInstance(provider)) {
                        return true;
                    }
                }
            }
        } catch (ClassNotFoundException e){}

        // provider not found by 'id' or class name
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Provider get(String providerId) throws ProviderNotFoundException {

        // return the io instance from the managed io map that contains the given io-id
        if(providers.containsKey(providerId)){
            return providers.get(providerId);
        }

        // additionally attempt to resolve the provider by its class name
        try {
            Class providerClass = Class.forName(providerId);
            if (providerClass != null && Provider.class.isAssignableFrom(providerClass)) {
                for(Provider provider : providers.values()){
                    if(providerClass.isInstance(provider)) {
                        return provider;
                    }
                }
            }
        } catch (ClassNotFoundException e){}

        // provider not found by 'id' or class name
        throw new ProviderNotFoundException(providerId);
    }

    private <T extends Provider> Providers add(T ... provider) throws ProviderInitializeException, ProviderAlreadyExistsException {
        return add(List.of(provider));
    }

    private <T extends Provider> Providers add(Collection<T> provider) throws ProviderAlreadyExistsException, ProviderInitializeException {
        logger.trace("invoked 'add()' provider [count={}]", provider.size());

        // iterate the given provider collection
        for(var providerInstance : provider) {
			if (providerInstance == null)
				continue;

			logger.trace("adding provider to managed io map [id={}; name={}; class={}]",
					providerInstance.id(), providerInstance.name(), providerInstance.getClass().getName());

			// ensure requested io id does not already exist in the managed set
			if (exists(providerInstance.id())) {
				throw new ProviderAlreadyExistsException(providerInstance.id());
			}

			// attempt to initialize the new io instance
			initializeProvider(providerInstance);

			//                logger.info("INTERFACES :: " + ReflectionUtil.getAllInterfaces(providerInstance));
			//                logger.info("CLASSES :: " + ReflectionUtil.getAllClasses(providerInstance));

			// add new io to managed set
			providers.put(providerInstance.id(), providerInstance);

			logger.debug("added io to managed provider map [id={}; name={}; class={}]",
					providerInstance.id(), providerInstance.name(), providerInstance.getClass().getName());
		}

        return this;
    }

    private void initializeProvider(Provider provider) throws ProviderInitializeException {

        // ensure the io object is valid
        if(provider == null) return;

        // attempt to initialize the io instance
        try {
            logger.trace("initializing provider [id={}; name={}; class={}]",
                    provider.id(), provider.name(), provider.getClass().getName());
            provider.initialize(runtime.context());
        } catch (Exception e) {
            logger.error("unable to 'initialize()' provider: [id={}; name={}]; {}",
                    provider.id(), provider.name(), e.getMessage());
            logger.error(e.getMessage(), e);
            throw new ProviderInitializeException(provider.id(), e);
        }
    }

    private void shutdownProvider(Provider provider) throws ShutdownException {

        // ensure the io object is valid
        if(provider == null) return;

        // attempt to shutdown the io instance
        try {
            logger.trace("calling 'shutdown()' provider [id={}; name={}; class={}]",
                    provider.id(), provider.name(), provider.getClass().getName());
            provider.shutdownInternal(runtime.context());
        } catch (ShutdownException e) {
            logger.error("unable to 'shutdown()' provider: [id={}; name={}]; {}",
                    provider.id(), provider.name(), e.getMessage());
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private <T extends Provider> void remove(String providerId) throws ProviderNotFoundException, ShutdownException {
        logger.trace("invoked 'remove() provider' [id={}]", providerId);

        // get existing io instance
        var oldProvider = get(providerId);

        // attempt to shutdown old io instance
        shutdownProvider(oldProvider);

        // remove from managed set
        var removedProvider = providers.remove(providerId);
        if(removedProvider != null) {
            logger.debug("removed provider from managed provider map [id={}; name={}; class={}]",
                    removedProvider.id(), removedProvider.name(), removedProvider.getClass().getName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public RuntimeProviders shutdown() throws ShutdownException {
        logger.trace("invoked providers 'shutdown();'");
        ShutdownException shutdownException = null;

        // iterate over all providers and invoke the shutdown method on each
        var providerIds = providers.keySet();
        for(var providerId : providerIds){
            try {
                remove(providerId);
            } catch (Pi4JException e) {
                shutdownException = new ShutdownException(e);
            }
        }

        // clear all providers
        providers.clear();

        // throw exception if
        if(shutdownException != null) throw shutdownException;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RuntimeProviders initialize(Collection<Provider> providers) throws InitializeException {

        // iterate over all defined platforms and initialize each
        if(providers != null && !providers.isEmpty()) {
            logger.trace("adding providers: [count={}]", providers.size());
            for (Provider provider : providers) {
                if (provider != null) {
                    logger.trace("adding provider: [id={}; name={}; class={}]",
                            provider.id(), provider.name(), provider.getClass().getName());
                    try {
                        // add provider instance
                        add(provider);
                    } catch (Exception ex) {
                        // unable to initialize this provider instance
                        logger.error("unable to 'initialize()' provider: [id={}; name={}]; {}",
                                provider.id(), provider.name(), ex.getMessage());
                        continue;
                    }
                }
            }
        }

        logger.debug("providers loaded [{}]", this.providers.size());
        return this;
    }
}
