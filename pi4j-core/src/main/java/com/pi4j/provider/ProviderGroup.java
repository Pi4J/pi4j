package com.pi4j.provider;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ProviderGroup.java
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

import java.util.Map;

import com.pi4j.common.Describable;
import com.pi4j.common.Descriptor;
import com.pi4j.io.IOType;
import com.pi4j.provider.exception.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>ProviderGroup class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <T>
 */
public class ProviderGroup<T extends Provider> implements Describable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private IOType type = null;
    private Providers providers;

    /**
     * Default Constructor
     *
     * @param type a {@link com.pi4j.io.IOType} object.
     * @param providers a {@link com.pi4j.provider.Providers} object.
     */
    public ProviderGroup(Providers providers, IOType type){
        this.providers = providers;
        this.type = type;
    }
    private Map<String, T> all() throws ProviderException {
        return providers.all(type);
    }

    /**
     * <p>get.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @return a T object.
     * @throws com.pi4j.provider.exception.ProviderException if any.
     */
    public T get(String providerId) throws ProviderException {
        return providers.get(providerId, type);
    }

    /**
     * <p>exists.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @return a boolean.
     * @throws com.pi4j.provider.exception.ProviderException if any.
     */
    public boolean exists(String providerId) throws ProviderException {
        return providers.exists(providerId, type);
    }

    /** {@inheritDoc} */
    @Override
    public Descriptor describe() {
        Descriptor descriptor = Descriptor.create()
                .category("PROVIDER GROUP")
                .name("Provider Group")
                .type(this.getClass());
        Map<String, T> all = null;
        try {
            all = all();
            all.forEach((id, provider)->{
                descriptor.add(provider.describe());
            });
        } catch (ProviderException e) {
            logger.error(e.getMessage(), e);
        }
        return descriptor;
    }
}
