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

import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.IOType;
import com.pi4j.platform.Platform;
import com.pi4j.provider.Provider;
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

    /**
     * <p>Constructor for About.</p>
     */
    public About() {
    }

    /**
     * <p>enumerateProviders.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     */
    public void enumerateProviders(Context context) {
        enumerateProviders(context,"PROVIDERS");
    }
    /**
     * <p>enumerateProviders.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @param title a {@link java.lang.String} object.
     */
    public void enumerateProviders(Context context, String title) {
        logger.info("=====================================================");
        logger.info(title);
        logger.info("=====================================================");
        for (Provider provider : context.providers().all().values()) {
            logger.info("  {} [{}]; {}", provider.name(), provider.id(), provider.type());
        }
    }

    /**
     * <p>enumerateProviders.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @param ioType a {@link com.pi4j.io.IOType} object.
     */
    public void enumerateProviders(Context context, IOType ioType) {
        logger.info("=====================================================");
        logger.info("{} PROVIDERS", ioType);
        logger.info("=====================================================");
        for(var provider : context.providers().all(ioType).values()){
            logger.info("  {} [{}]; {}", provider.name(), provider.id(), provider.type());
        }
    }

    /**
     * <p>enumeratePlatforms.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     */
    public void enumeratePlatforms(Context context) {
        logger.info("=====================================================");
        logger.info("PLATFORMS");
        logger.info("=====================================================");
        for (Platform platform : context.platforms().all().values()) {
            logger.info("  {} [{}]; {}", platform.name(), platform.id(), platform.getDescription());
        }
    }

    /**
     * <p>describeDeafultPlatform.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @throws Pi4JException if any.
     */
    public void describeDefaultPlatform(Context context) throws Pi4JException {
        logger.info("=====================================================");
        logger.info("DEFAULT (RUNTIME) PLATFORM ");
        logger.info("=====================================================");
        if (context.platform() == null) {
            logger.warn("  NOT DEFINED");
        } else {
            logger.info("  {}} [{}}]", context.platform().name(), context.platform().id());
            context.platform().describe().print(System.out);
        }
    }

//    public void enumeratePlatformProviders() throws Pi4JException {
//        logger.info("=====================================================");
//        logger.info("PLATFORM PROVIDERS");
//        logger.info("=====================================================");
//        for(var provider : Pi4J.platform().providers().values()){
//            logger.info("  " + provider.name() + "[" + provider.id() + "]; " + provider.type());
//        }
//    }
}
