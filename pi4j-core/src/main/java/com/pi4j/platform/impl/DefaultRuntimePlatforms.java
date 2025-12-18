package com.pi4j.platform.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultRuntimePlatforms.java
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
import com.pi4j.exception.ShutdownException;
import com.pi4j.platform.Platform;
import com.pi4j.platform.Platforms;
import com.pi4j.platform.exception.PlatformAlreadyExistsException;
import com.pi4j.platform.exception.PlatformException;
import com.pi4j.platform.exception.PlatformInitializeException;
import com.pi4j.platform.exception.PlatformNotFoundException;
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
public class DefaultRuntimePlatforms implements RuntimePlatforms {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Runtime runtime = null;
    protected Platform defaultPlatform = null;

    // all detected/available providers
    private Map<String, Platform> platforms = new ConcurrentHashMap<>();

    /**
     * <p>newInstance.</p>
     *
     * @param runtime a {@link com.pi4j.runtime.Runtime} object.
     * @return a {@link com.pi4j.platform.impl.RuntimePlatforms} object.
     * @throws com.pi4j.platform.exception.PlatformNotFoundException if any.
     */
    public static RuntimePlatforms newInstance(Runtime runtime) throws PlatformNotFoundException {
        return new DefaultRuntimePlatforms(runtime);
    }

    // private constructor
    private DefaultRuntimePlatforms(Runtime runtime) throws PlatformNotFoundException {
        // set local context reference
        this.runtime = runtime;
    }

    /**
     * {@inheritDoc}
     *
     * Get all platforms
     */
    @Override
    public Map<String, Platform> all(){
        return Collections.unmodifiableMap(this.platforms);
    }


    /** {@inheritDoc} */
    @Override
    public boolean exists(String platformId) {
        // return true if the managed platform map contains the given platform-id
        if(platforms.containsKey(platformId)){
            return true;
        }

        // additionally attempt to resolve the platform by its class name
        try {
            Class platformClass = Class.forName(platformId);
            if (platformClass != null && Platform.class.isAssignableFrom(platformClass)) {
                for(Platform platform : platforms.values()){
                    if(platformClass.isInstance(platform)) {
                        return true;
                    }
                }
            }
        } catch (ClassNotFoundException e){}

        // if not found, attempt to resolve platform by class name
        for (Platform platform : platforms.values()) {
            if(platform.getClass().getName().equalsIgnoreCase(platformId)){
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public Platform get(String platformId) throws PlatformNotFoundException {
        // return the platform instance from the managed platform map that contains the given platform-id
        if(platforms.containsKey(platformId)){
            return platforms.get(platformId);
        }

        // additionally attempt to resolve the platform by its class name
        try {
            Class platformClass = Class.forName(platformId);
            if (platformClass != null && Platform.class.isAssignableFrom(platformClass)) {
                for(Platform platform : platforms.values()){
                    if(platformClass.isInstance(platform)) {
                        return platform;
                    }
                }
            }
        } catch (ClassNotFoundException e){}

        throw new PlatformNotFoundException(platformId);
    }


    /** {@inheritDoc} */
    @Override
    public Platform defaultPlatform() {
        return this.defaultPlatform;
    }

    /** {@inheritDoc} */
    @Override
    public RuntimePlatforms shutdown() throws ShutdownException {
        logger.trace("invoked 'shutdown();'");

        ShutdownException shutdownException = null;
        // iterate over all providers and invoke the shutdown method on each
        var platformIds = platforms.keySet();
        for(var platformId : platformIds){
            try {
                remove(platformId);
            } catch (PlatformException e) {
                shutdownException = new ShutdownException(platformId, e);
            }
        }

        // clear all platforms
        platforms.clear();

        // throw exception if
        if(shutdownException != null) throw shutdownException;

        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RuntimePlatforms initialize(Collection<Platform> platforms) throws InitializeException {

        // iterate over all defined platforms and initialize each
        if(platforms != null && !platforms.isEmpty()) {
            logger.trace("adding platforms: [count={}]", platforms.size());
            for (Platform platform : platforms) {
                if (platform != null) {
                    logger.trace("platform: [id={}; name={}; class={}]",
                            platform.id(), platform.name(), platform.getClass().getName());
                    try {
                        // add platform instance
                        add(platform);
                    } catch (Exception ex) {
                        // unable to initialize this platform instance
                        logger.error("unable to 'initialize()' platform: [id={}; name={}]; {}",
                                platform.id(), platform.name(), ex.getMessage());
                        continue;
                    }
                }
            }
        }

        logger.debug("platforms loaded [{}]", this.platforms.size());

        // set default platform
        if(runtime.context().config().hasDefaultPlatform() &&
                this.platforms.containsKey(runtime.context().config().getDefaultPlatform())) {
            this.defaultPlatform = this.platforms.get(runtime.context().config().getDefaultPlatform());
        }
        return this;
    }


    private <T extends Platform> Platforms add(T ... platform) throws PlatformAlreadyExistsException, PlatformInitializeException {
        return add(List.of(platform));
    }

    private <T extends Platform > Platforms add(Collection<T> platform) throws PlatformAlreadyExistsException, PlatformInitializeException {
        logger.trace("invoked 'add()' platform [count={}]", platform.size());

        // iterate the given platform array
        for(var platformInstance : platform) {

            logger.info("adding platform to managed platform map [id={}; name={}; priority={}; class={}]",
                    platformInstance.id(), platformInstance.name(), platformInstance.priority(), platformInstance.getClass().getName());

            // perform check to see if this platform reports as enabled/supported
            if(!platformInstance.enabled(this.runtime.context())){
                logger.trace("not adding platform [id={}; name={}; class={}] as it reports as [DISABLED] on this system;",
                        platformInstance.id(), platformInstance.name(), platformInstance.getClass().getName());
                continue;
            }

            // ensure requested platform id does not already exist in the managed set
            if (exists(platformInstance.id())) {
                throw new PlatformAlreadyExistsException(platformInstance.id());
            }

            // attempt to initialize the new platform instance
            initializePlatform(platformInstance);

            // add new platform to managed set
            platforms.put(platformInstance.id(), platformInstance);

            // set system wide default platform
            if(defaultPlatform == null) {
                this.defaultPlatform = platformInstance;

                logger.debug("default platform is now [id={}; name={}; class={}]",
                        defaultPlatform.id(), defaultPlatform.name(), defaultPlatform.getClass().getName());

            }
            else{
                if(platformInstance.priority() > this.defaultPlatform.priority()){
                    this.defaultPlatform = platformInstance;
                    logger.debug("default platform is now [id={}; name={}; class={}; weight={}]",
                            defaultPlatform.id(), defaultPlatform.name(), defaultPlatform.getClass().getName(), defaultPlatform.priority());
                }
            }
        }

        return this;
    }

    private <T extends Platform> void remove(String platformId) throws PlatformNotFoundException, ShutdownException {
        logger.trace("invoked 'remove() platform' [id={}]", platformId);

        // ensure requested platform id does exist in the managed set
        if(!platforms.containsKey(platformId)){
            logger.warn("unable to remove platform [id={}]; id not found in managed platform map.", platformId);
            throw new PlatformNotFoundException(platformId);
        }

        // get existing platform instance
        Platform oldPlatform = platforms.get(platformId);

        // attempt to shutdown old platform instance
        shutdownPlatform(oldPlatform);

        // remove platform from managed set
        var removedPlatform = platforms.remove(platformId);
        if(removedPlatform != null) {
            logger.debug("removed platform from managed platform map [id={}; name={}; class={}]",
                    removedPlatform.id(), removedPlatform.name(), removedPlatform.getClass().getName());
        }
    }

    private void initializePlatform(Platform platform) throws PlatformInitializeException {

        // ensure the platform object is valid
        if(platform == null) return;

        // attempt to initialize the platform instance
        try {
            logger.trace("calling 'initialize' platform [id={}; name={}; class={}]",
                    platform.id(), platform.name(), platform.getClass().getName());
            platform.initialize(runtime.context());
        } catch (Exception e) {
            logger.error("unable to 'initialize()' platform: [id={}; name={}]; {}",
                    platform.id(), platform.name(), e.getMessage());
            logger.error(e.getMessage(), e);
            throw new PlatformInitializeException(platform.id(), e);
        }
    }

    private void shutdownPlatform(Platform platform) throws ShutdownException {
        // ensure the platform object is valid
        if(platform == null) return;

        // attempt to shutdown the platform instance
        try {
            logger.trace("calling 'shutdown' platform [id={}; name={}; class={}]",
                    platform.id(), platform.name(), platform.getClass().getName());
            platform.shutdownInternal(runtime.context());
        } catch (Exception e) {
            logger.error("unable to 'shutdown()' platform: [id={}; name={}]; {}",
                    platform.id(), platform.name(), e.getMessage());
            logger.error(e.getMessage(), e);
            throw new ShutdownException(e.getMessage(), e);
        }
    }
}
