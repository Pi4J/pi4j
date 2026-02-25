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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>Pi4J class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class Pi4J {

    private static final Logger logger = LoggerFactory.getLogger(Pi4J.class);
    private static BuildInfo buildInfo = null;

    // Private constructor
    private Pi4J() {
        // Hide constructor
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
        getBuildInfo().log();
        return ContextBuilder.newInstance();
    }

    /**
     * <p>Returns a new 'Context' instance which represents the Pi4J runtime
     * state and lifecycle.   This 'Context' instance will automatically
     * load all detected 'Platforms' and 'Providers' that are detected
     * in the application's class-path.</p>
     *
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

    /**
     * Record representing the Pi4J library build information.
     *
     * @param branch    The git branch name from which this build was created.
     * @param commitId  The git commit ID identifying the exact source revision.
     * @param version   The version string of the Pi4J library (e.g., "2.7.0").
     * @param timestamp The date and time when this build was produced.
     */
    public record BuildInfo(String branch, String commitId, String version, String timestamp) {
        public void log() {
            logger.info("Pi4J library build info:");
            logger.info("\tBranch: {}", branch);
            logger.info("\tCommit ID: {}", commitId);
            logger.info("\tVersion: {}", version);
            logger.info("\tTimestamp: {}", timestamp);
        }
    }

    /**
     * Reads the build info from the build.properties file or returns the already read info.
     *
     * @return {@link BuildInfo}
     */
    public static BuildInfo getBuildInfo() {
        if (buildInfo != null) {
            return buildInfo;
        }

        // Still need to load the build info
        try (InputStream is = Pi4J.class.getResourceAsStream("/build.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                buildInfo = new BuildInfo(
                    getProp(props, "git.branch"),
                    getProp(props, "git.commit.id"),
                    getProp(props, "build.version"),
                    getProp(props, "build.timestamp")
                );
            }
        } catch (IOException e) {
            logger.debug("Unable to load build properties", e);
            buildInfo = new BuildInfo("", "", "UNKNOWN", "");
        }
        return buildInfo;
    }

    /**
     * Helper to avoid null values from properties.
     *
     * @param props Properties read from build.properties file.
     * @param key   Property key.
     * @return String with property value or empty string if not found.
     */
    private static String getProp(Properties props, String key) {
        String value = props.getProperty(key);
        return value == null ? "" : value;
    }
}
