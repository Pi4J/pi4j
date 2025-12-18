package com.pi4j.io.gpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  GpioBase.java
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

import com.pi4j.io.IOBase;
import com.pi4j.provider.Provider;

/**
 * <p>Abstract GpioBase class.</p>
 *
 * @param <CONFIG_TYPE>
 * @param <IO_TYPE>
 * @param <PROVIDER_TYPE>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public abstract class GpioBase<IO_TYPE extends Gpio<IO_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
    CONFIG_TYPE extends GpioConfig<CONFIG_TYPE>, PROVIDER_TYPE extends Provider>
    extends IOBase<IO_TYPE, CONFIG_TYPE, PROVIDER_TYPE>
    implements Gpio<IO_TYPE, CONFIG_TYPE, PROVIDER_TYPE> {

    /**
     * <p>Constructor for GpioBase.</p>
     *
     * @param provider a PROVIDER_TYPE object.
     * @param config   a CONFIG_TYPE object.
     */
    public GpioBase(PROVIDER_TYPE provider, CONFIG_TYPE config) {
        super(provider, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // include ID
        result.append("@");
        result.append(this.id());

        // include NAME
        if (this.name() != null && !this.name().isEmpty()) {
            result.append(" \"");
            result.append(this.name());
        }

        return result.toString();
    }
}
