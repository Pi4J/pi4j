package com.pi4j;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Pi4J.java
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
import com.pi4j.context.ContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Pi4J class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class Pi4J {

    private static final Logger logger = LoggerFactory.getLogger(Pi4J.class);

    // private constructor
    private Pi4J() {
        // forbid object construction
    }

    /**
     * Returns a new 'ContextBuilder' instance to help create
     * a custom 'Context' which represents the Pi4J runtime
     * state and lifecycle.  The 'ContextBuilder' will allow
     * you to add custom 'Platforms' and 'Providers'.
     *
     * @return ContextBuilder
     */
    public static ContextBuilder newContextBuilder() {
        logger.info("New context builder");
        return ContextBuilder.newInstance();
    }

    /**
     * <p>Returns a new 'Context' instance which represents the Pi4J runtime
     * state and lifecycle.   This 'Context' instance will automatically
     * load all detected 'Platforms' and 'Providers' that are detected
     * in the application's class-path.</p>
     * @return Context
     */
    public static Context newAutoContext() {
        logger.info("New auto context");
        return newContextBuilder().autoDetect().build();
    }

    /**
     * Returns a new empty 'Context' instance which represents the Pi4J
     * runtime state and lifecycle.  This empty 'Context' will not contain
     * any 'Platforms' or 'Providers' by default.  The empty context
     * can be used if 'Platforms' and 'Providers' need to be added to the
     * runtime context.
     *
     * @return Context
     */
    public static Context newContext() {
        logger.info("New context");
        return newContextBuilder().build();
    }
}
