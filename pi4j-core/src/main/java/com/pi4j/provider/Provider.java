package com.pi4j.provider;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Provider.java
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
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.extension.Extension;
import com.pi4j.io.IO;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.IOException;
import com.pi4j.util.PropertiesUtil;

import java.util.Map;

/**
 * <p>Provider interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <CONFIG_TYPE>
 * @param <IO_TYPE>
 * @param <PROVIDER_TYPE>
 */
public interface Provider<PROVIDER_TYPE extends Provider, IO_TYPE extends IO, CONFIG_TYPE extends Config> extends Extension<PROVIDER_TYPE> {

    Context context();

    /**
     * <p>create.</p>
     *
     * @param config a CONFIG_TYPE object.
     * @return a IO_TYPE object.
     */
    IO_TYPE create(CONFIG_TYPE config);

    /**
     * <p>type.</p>
     *
     * @return a {@link com.pi4j.io.IOType} object.
     */
    default IOType type() { return IOType.getByProviderClass(this.getClass()); }
    /**
     * <p>getType.</p>
     *
     * @return a {@link com.pi4j.io.IOType} object.
     */
    default IOType getType() { return type(); }

    /**
     * Returns the priority for this provider, defaults to 0
     *
     * @return an integer
     */
    default int getPriority() {
        return 0;
    }

    /**
     * <p>isType.</p>
     *
     * @param type a {@link com.pi4j.io.IOType} object.
     * @return a boolean.
     */
    default boolean isType(IOType type) { return this.type().isType(type); }

    /** {@inheritDoc} */
    @Override
    default Descriptor describe() {
        Descriptor descriptor = Extension.super.describe();
        //descriptor.category(this.type().name());
        descriptor.category("PROVIDER");
        return descriptor;
    }

    default IO_TYPE create(String id) {
        // validate context
        if(context() == null) throw new IOException("Unable to create IO instance; this provider has not been 'initialized()' with a Pi4J context.");

        // resolve inheritable properties from the context based on the provided 'id' for this IO instance
        Map<String,String> inheritedProperties = PropertiesUtil.subProperties(context().properties().all(), id);

        // create IO instance
        ConfigBuilder builder = type().newConfigBuilder(context());
        builder.id(id);
        builder.load(inheritedProperties);
        return (IO_TYPE)create((CONFIG_TYPE) builder.build());
    }
}
