package com.pi4j.test.runtime;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.event.ShutdownEvent;
import com.pi4j.event.ShutdownListener;
import com.pi4j.exception.Pi4JException;
import com.pi4j.test.Slf4jStreamBridge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class RuntimeTest {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeTest.class);
    private boolean beforeShutdownEventFired = false;
    private boolean afterShutdownEventFired = false;

    @Test
    public void testRuntimeShutdownEvents() throws Pi4JException {

        // initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        Context pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build();

        logger.info("-------------------------------------------------");
        logger.info("Pi4J CONTEXT <acquired via factory accessor>");
        logger.info("-------------------------------------------------");
        var ps = Slf4jStreamBridge.createPrintStream(logger);
        pi4j.describe().print(ps);

        // add shutdown listener
        pi4j.addListener(new ShutdownListener() {

            @Override
            public void beforeShutdown(ShutdownEvent event) {
                logger.info("Pi4J RUNTIME EVENT --> BEFORE SHUTDOWN EVENT");
                beforeShutdownEventFired = true;
            }

            @Override
            public void onShutdown(ShutdownEvent event) {
                logger.info("Pi4J RUNTIME EVENT --> (AFTER) SHUTDOWN EVENT");
                afterShutdownEventFired = true;
            }
        });

        // perform shutdown
        pi4j.shutdown();

        // test to ensure both "before" and "after" shutdown events have been fired
        assertTrue(beforeShutdownEventFired, "Before (pre) shutdown event [ShutdownListener.beforeShutdown(ShutdownEvent)] was not fired");
        assertTrue(afterShutdownEventFired, "After (post) shutdown event [ShutdownListener.onShutdown(ShutdownEvent)] was not fired");
    }
}
