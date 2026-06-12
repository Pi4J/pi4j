package com.pi4j.test;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  About.java
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

import com.pi4j.exception.Pi4JException;
import com.pi4j.io.IOType;
import com.pi4j.provider.Provider;
import com.pi4j.test.smoketest.ProviderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>About class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class About {

    private static final Logger logger = LoggerFactory.getLogger(About.class);

    private final ProviderContext providerContext;

    public About(ProviderContext providerContext) {
        this.providerContext = providerContext;
    }

    /**
     * Logs and enumerates all providers within the provider context using
     * a default title.
     * <p>
     * This method invokes the overloaded {@code enumerateProviders(String title)}
     * with the default title, "PROVIDERS". It outputs detailed information about each
     * provider, including its name, ID, and type. The information is structured
     * and logged for debugging or analysis purposes.
     */
    public void enumerateProviders() {
        enumerateProviders("PROVIDERS");
    }

    /**
     * Logs and enumerates all providers within the provider context.
     * This method outputs detailed information about each provider, including its name,
     * ID, and type. The information is structured and prefixed by a given title.
     *
     * @param title a String representing the title to be logged before enumerating providers.
     */
    public void enumerateProviders(String title) {
        logger.info("=====================================================");
        logger.info(title);
        logger.info("=====================================================");
        for (Provider provider : providerContext.getContext().providers().all().values()) {
            logger.info("  {} [{}]; {}", provider.name(), provider.id(), provider.type());
        }
    }

    /**
     * Logs and enumerates all providers of the specified IO type within the provider context.
     * This method retrieves and iterates over all providers of the given IOType from the
     * provider context, logging their names, IDs, and types. It provides a structured view
     * of provider information to assist in debugging and analysis of the runtime environment.
     *
     * @param ioType an IOType object representing the type of providers to enumerate.
     */
    public void enumerateProviders(IOType ioType) {
        logger.info("=====================================================");
        logger.info("{} PROVIDERS", ioType);
        logger.info("=====================================================");
        for (var provider : providerContext.getContext().providers().all(ioType).values()) {
            logger.info("  {} [{}]; {}", provider.name(), provider.id(), provider.type());
        }
    }
}
