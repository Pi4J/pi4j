package com.pi4j.io.serial;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SerialBase.java
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
 * <p>Abstract SerialBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public abstract class SerialBase extends IOBase<Serial, SerialConfig, SerialProvider> implements Serial {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    protected boolean isOpen = false;

    /**
     * <p>Constructor for SerialBase.</p>
     *
     * @param provider a {@link com.pi4j.io.serial.SerialProvider} object.
     * @param config   a {@link com.pi4j.io.serial.SerialConfig} object.
     */
    public SerialBase(SerialProvider provider, SerialConfig config) {
        super(provider, config);
        logger.trace("created instance with config: {}", config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() {
        logger.trace("invoked 'open()'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        logger.trace("invoked 'closed()'");
        this.isOpen = false;
    }
}
