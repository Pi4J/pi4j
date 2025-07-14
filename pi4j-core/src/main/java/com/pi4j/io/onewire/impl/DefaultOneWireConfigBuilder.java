package com.pi4j.io.onewire.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultOneWireConfigBuilder.java
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

import com.pi4j.context.Context;
import com.pi4j.io.onewire.OneWireConfig;
import com.pi4j.io.onewire.OneWireConfigBuilder;
import com.pi4j.io.impl.IOConfigBuilderBase;

/**
 * <p>DefaultOneWireConfigBuilder class.</p>
 * This class is used to build and configure 1-Wire device configurations.
 */
public class DefaultOneWireConfigBuilder
    extends IOConfigBuilderBase<OneWireConfigBuilder, OneWireConfig>
    implements OneWireConfigBuilder {

    /**
     * PRIVATE CONSTRUCTOR
     * This constructor ensures that the builder can only be created through the static method newInstance().
     */
    protected DefaultOneWireConfigBuilder(Context context){
        super(context);
    }

    /**
     * Static method to create a new instance of DefaultOneWireConfigBuilder.
     *
     * @param context The Pi4J context used for this configuration.
     * @return A new instance of OneWireConfigBuilder.
     */
    public static OneWireConfigBuilder newInstance(Context context) {
        return new DefaultOneWireConfigBuilder(context);
    }

    /** {@inheritDoc} */
    @Override
    public OneWireConfig build() {
        // Return a new OneWireConfig instance with the resolved properties
        return new DefaultOneWireConfig(getResolvedProperties());
    }

    /** {@inheritDoc} */
    @Override
    public OneWireConfigBuilder device(String device) {
        // Set the device ID for 1-Wire devices in the configuration properties
        this.properties.put(OneWireConfig.DEVICE_KEY, device);
        return this;
    }

    /**
     * Additional configuration options specific to 1-Wire devices can be added here.
     * For example, you might add methods to set 1-Wire device-specific parameters.
     */
}
