package com.pi4j;

import com.pi4j.context.Context;
import com.pi4j.context.ContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Primary entry point for the Pi4J library. This utility class provides static factory methods for
 * obtaining a {@link Context}, the root object that holds the Pi4J runtime state and lifecycle and
 * from which all platforms, {@link com.pi4j.provider.Provider}s and I/O instances are accessed.
 * Applications typically start by calling {@link #newAutoContext()} or by configuring a
 * {@link ContextBuilder} obtained from {@link #newContextBuilder()}.
 */
public class Pi4J {

    private static final Logger logger = LoggerFactory.getLogger(Pi4J.class);
    private static final BuildInfo buildInfo = loadBuildInfo();

    // Private constructor
    private Pi4J() {
        // Hide constructor
    }

    /**
     * Returns a new {@link ContextBuilder} for assembling a customized {@link Context}. Use the builder
     * when the runtime needs explicit configuration, such as manually registering platforms and
     * {@link com.pi4j.provider.Provider}s or enabling auto-detection before the {@link Context} is built.
     *
     * @return a fresh {@link ContextBuilder} instance ready to be configured
     */
    public static ContextBuilder newContextBuilder() {
        logger.info("New context builder");
        buildInfo.log();
        return ContextBuilder.newInstance();
    }

    /**
     * Returns a new, fully initialized {@link Context} with auto-detection enabled. All platforms and
     * {@link com.pi4j.provider.Provider}s discovered on the application's class-path are automatically
     * loaded and registered. This is the most convenient way to bootstrap Pi4J for typical applications.
     *
     * @return an initialized {@link Context} populated with all auto-detected platforms and providers
     */
    public static Context newAutoContext() {
        logger.info("New auto context");
        return newContextBuilder().autoDetectProviders().build();
    }

    /**
     * Returns a new {@link Context} without any auto-detection. The resulting context contains no
     * platforms or {@link com.pi4j.provider.Provider}s by default; use this when the runtime should be
     * populated explicitly rather than by class-path discovery. For finer control over the contents,
     * build a context with {@link #newContextBuilder()} instead.
     *
     * @return an initialized but otherwise empty {@link Context}
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
        /**
         * Writes this build information to the Pi4J log at INFO level, listing the branch, commit ID,
         * version and timestamp. Invoked when a new context builder is created so the running Pi4J
         * version is recorded in application logs.
         */
        public void log() {
            logger.info("Pi4J library build info:");
            logger.info("\tBranch: {}", branch);
            logger.info("\tCommit ID: {}", commitId);
            logger.info("\tVersion: {}", version);
            logger.info("\tTimestamp: {}", timestamp);
        }
    }

    /**
     * Returns the build information for the Pi4J library in use, read once from the bundled
     * {@code build.properties} resource. When that resource is absent or unreadable, the returned
     * {@link BuildInfo} carries empty fields and a version of {@code "UNKNOWN"}.
     *
     * @return the {@link BuildInfo} describing this Pi4J build
     */
    public static BuildInfo getBuildInfo() {
        return buildInfo;
    }

    /**
     * Reads the build info from the build.properties file.
     */
    private static BuildInfo loadBuildInfo() {
        try (InputStream is = Pi4J.class.getResourceAsStream("/build.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                return new BuildInfo(
                    getProp(props, "git.branch"),
                    getProp(props, "git.commit.id"),
                    getProp(props, "build.version"),
                    getProp(props, "build.timestamp")
                );
            }
        } catch (IOException e) {
            logger.debug("Unable to load build properties", e);
        }
        return new BuildInfo("", "", "UNKNOWN", "");
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
