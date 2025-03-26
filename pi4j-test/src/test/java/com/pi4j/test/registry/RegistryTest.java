package com.pi4j.test.registry;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  RegistryTest.java
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
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.registry.Registry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(Lifecycle.PER_CLASS)
public class RegistryTest {

    private static final Logger logger = LoggerFactory.getLogger(RegistryTest.class);

    private Context pi4j;

    @BeforeAll
    public void beforeTest() throws Pi4JException {
        // initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build();
    }

    @AfterAll
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testFactoryRegistryAcquisition() throws Pi4JException {
        Registry registry = pi4j.registry();
        assertNotNull(registry);
        logger.info("-------------------------------------------------");
        logger.info("Pi4J I/O REGISTRY <acquired via factory accessor>");
        logger.info("-------------------------------------------------");
        registry.describe().print(System.out);
    }

    @Test
    public void testShutdownAndRecreate() throws Pi4JException {
        var inputConfig = DigitalInput.newConfigBuilder(pi4j)
                .id("DIN-3")
                .name("DIN-3")
                .address(3);

        // create a new input, then shutdown
        var input = pi4j.create(inputConfig);

        pi4j.shutdown(input.id());

        // shouldn't fail when recreating
        input = pi4j.create(inputConfig);

        // or shutting down
        pi4j.shutdown(input.id());
    }
}
