package com.pi4j.io;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IO.java
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

import com.pi4j.common.Describable;
import com.pi4j.common.Identity;
import com.pi4j.common.Lifecycle;
import com.pi4j.provider.Provider;

/**
 * <p>IO interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <CONFIG_TYPE>
 * @param <IO_TYPE>
 * @param <PROVIDER_TYPE>
 */
public interface IO<IO_TYPE extends IO, CONFIG_TYPE extends IOConfig, PROVIDER_TYPE extends Provider>
        extends Describable, Lifecycle, Identity {

    /**
     * <p>config.</p>
     *
     * @return a CONFIG_TYPE object.
     */
    CONFIG_TYPE config();

    /**
     * <p>type.</p>
     *
     * @return a {@link com.pi4j.io.IOType} object.
     */
    default IOType type() { return IOType.getByIOClass(this.getClass()); }

    // TODO :: RECONCILE IDENTITY PROPERTIES BETWEEN IO INSTANCE AND UNDERLYING CONFIG; PROBABLY NEED TO REMOVE THESE SETTERS
    /**
     * <p>name.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a IO_TYPE object.
     */
    IO_TYPE name(String name);

    /**
     * <p>description.</p>
     *
     * @param description a {@link java.lang.String} object.
     * @return a IO_TYPE object.
     */
    IO_TYPE description(String description);

    /**
     * <p>provider.</p>
     *
     * @return a PROVIDER_TYPE object.
     */
    PROVIDER_TYPE provider();
}
