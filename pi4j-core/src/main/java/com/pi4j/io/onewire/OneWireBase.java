package com.pi4j.io.onewire;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OneWireBase.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for managing 1-Wire communication.
 *
 * <p>This abstract class provides core functionality and lifecycle management
 * for 1-Wire communication interfaces within the Pi4J library. It is intended
 * to be extended by specific 1-Wire implementations.</p>
 *
 * <p>The class integrates with the Pi4J I/O framework and ensures
 * consistent behavior across all supported 1-Wire providers.</p>
 */
public abstract class OneWireBase extends IOBase<OneWire, OneWireConfig, OneWireProvider> implements OneWire {

    /** Logger instance for debugging and tracing operations. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructs a new {@code OneWireBase} instance.
     *
     * @param provider the {@link OneWireProvider} used to manage 1-Wire communication.
     * @param config the {@link OneWireConfig} specifying configuration settings for this instance.
     */
    public OneWireBase(OneWireProvider provider, OneWireConfig config) {
        super(provider, config);
        logger.trace("Created OneWireBase instance with config: {}", config);
    }
}
