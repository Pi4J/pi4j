package com.pi4j.test.runtime;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  RuntimeTest.java
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

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.event.ShutdownEvent;
import com.pi4j.event.ShutdownListener;
import com.pi4j.exception.Pi4JException;
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
        pi4j.describe().print(System.out);

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
