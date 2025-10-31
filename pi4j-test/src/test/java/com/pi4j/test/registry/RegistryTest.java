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
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.registry.Registry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class RegistryTest {

    private static final Logger logger = LoggerFactory.getLogger(RegistryTest.class);

    private Context pi4j;

    @BeforeEach
    public void beforeTest() throws Pi4JException {
        // initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build();
    }

    @AfterEach
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
        var inputConfig = DigitalInput.newConfigBuilder(pi4j).id("DIN-3").name("DIN-3").pin(3);

        // create a new input, then shutdown
        var input = pi4j.create(inputConfig);

        input.close();

        // shouldn't fail when recreating
        input = pi4j.create(inputConfig);

        // or shutting down
        input.close();
    }

    @Test
    public void testCreateMultipleSameAddress() throws Pi4JException {
        var inputConfig = DigitalInput.newConfigBuilder(pi4j).id("DIN-3").name("DIN-3").pin(3);
        var outputConfig = DigitalOutput.newConfigBuilder(pi4j).id("DOUT-3").name("DOUT-3").pin(3);
        var pwmConfig = Pwm.newConfigBuilder(pi4j).id("PWM-3").name("PWM-3").channel(3);

        // create I/O instances
        var input = pi4j.create(inputConfig);
        var output = pi4j.create(outputConfig);
        var pwm = pi4j.create(pwmConfig);

        Registry registry = pi4j.registry();
        assertAll(
            // Test that we can find them by address
            () -> assertTrue(registry.exists(IOType.PWM, pwm.getChannel()), "Should exist: PWM by address"),
            () -> assertTrue(registry.exists(IOType.DIGITAL_INPUT, input.pin()), "Should exist: Digital Input by pin"),
            () -> assertTrue(registry.exists(IOType.DIGITAL_OUTPUT, output.pin()), "Should exist: Digital Output by pin"),

            // and also by ID
            () -> assertTrue(registry.exists(pwm.id()), "Should exist: PWM by ID"),
            () -> assertTrue(registry.exists(input.id()), "Should exist: Digital Input by ID"),
            () -> assertTrue(registry.exists(output.id()), "Should exist: Digital Output by ID")
        );

        // but we shouldn't find them by other types
//        assertFalse(registry.exists(IOType.ANALOG_INPUT, output.address()));
//        assertFalse(registry.exists(IOType.ANALOG_OUTPUT, output.address()));

        // now shutdown all I/O instances by closing them.
        input.close();
        output.close();

        // The test PWM has no context here; assuming mock/fake incompleteness.
        // First guess was that this is because TestPwmProvider returns null from the create method, but this would
        // mean that pwm.id() above would fail already.
        registry.remove(pwm.id());

        assertAll(
            // and now we shouldn't find them by address
            // TO FIX () -> assertFalse(registry.exists(IOType.PWM, 3), "Should not exist: PWM by address"),
            () -> assertFalse(registry.exists(IOType.DIGITAL_INPUT, 3), "Should not exist: Digital Input by pin"),
            () -> assertFalse(registry.exists(IOType.DIGITAL_OUTPUT, 3), "Should not exist: Digital Output by pin"),

            // or ID
            () -> assertFalse(registry.exists(pwm.id()), "Should not exist: PWM by ID"),
            () -> assertFalse(registry.exists(input.id()), "Should not exist: Digital Input by ID"),
            () -> assertFalse(registry.exists(output.id()), "Should not exist: Digital Output by ID")
        );

        // Check close idempotency.
        input.close();
        output.close();
        pwm.close();
    }
}
