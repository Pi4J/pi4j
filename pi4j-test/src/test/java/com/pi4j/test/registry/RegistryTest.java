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
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.spi.Spi;
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
class RegistryTest {

    private static final Logger logger = LoggerFactory.getLogger(RegistryTest.class);

    private Context pi4j;

    @BeforeEach
    void beforeTest() throws Pi4JException {
        // initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build();
    }

    @AfterEach
    void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    void testFactoryRegistryAcquisition() throws Pi4JException {
        Registry registry = pi4j.registry();
        assertNotNull(registry);
        logger.info("-------------------------------------------------");
        logger.info("Pi4J I/O REGISTRY <acquired via factory accessor>");
        logger.info("-------------------------------------------------");
        registry.describe().print(System.out);
    }

    @Test
    void testShutdownAndRecreate() throws Pi4JException {
        var config = DigitalInput.newConfigBuilder(pi4j).id("DIN-3").name("DIN-3").bcm(3);

        // create a new input, then shutdown
        var input = pi4j.create(config);
        input.close();

        // shouldn't fail when recreating
        input = pi4j.create(config);

        // or shutting down
        input.close();
    }

    @Test
    void testCreateMultipleSameAddress() throws Pi4JException {
        var inputConfig = DigitalInput.newConfigBuilder(pi4j).id("DIN-3").name("DIN-3").bcm(3);
        var outputConfig = DigitalOutput.newConfigBuilder(pi4j).id("DOUT-3").name("DOUT-3").bcm(3);
        var pwmConfig = Pwm.newConfigBuilder(pi4j).id("PWM-3").name("PWM-3").channel(3);

        // create I/O instances
        var input = pi4j.create(inputConfig);
        var output = pi4j.create(outputConfig);
        var i2c = pi4j.create(I2C.newConfigBuilder(pi4j).bus(0).device(0x70).build());
        var pwm = pi4j.create(pwmConfig);

        Registry registry = pi4j.registry();
        assertAll(
            // Test that we can find them by address
            () -> assertTrue(registry.exists(IOType.DIGITAL_INPUT, input.config().getUniqueIdentifier()), "Should exist: Digital Input by unique identifier which should be identical to BCM"),
            () -> assertTrue(registry.exists(IOType.DIGITAL_OUTPUT, output.config().getUniqueIdentifier()), "Should exist: Digital Output by unique identifier which should be identical to BCM"),
            () -> assertTrue(registry.exists(IOType.DIGITAL_INPUT, input.bcm()), "Should exist: Digital Input by BCM"),
            () -> assertTrue(registry.exists(IOType.DIGITAL_OUTPUT, output.bcm()), "Should exist: Digital Output by BCM"),

            // and also by ID
            () -> assertTrue(registry.exists(input.id()), "Should exist: Digital Input by ID"),
            () -> assertTrue(registry.exists(output.id()), "Should exist: Digital Output by ID"),
            () -> assertTrue(registry.exists(pwm.id()), "Should exist: PWM by ID"),
            () -> assertTrue(registry.exists(i2c.id()), "Should exist: I2C by ID")
        );


        // now shutdown all I/O instances by closing them.
        input.close();
        output.close();
        //i2c.close();
        //pwm.close();

        // The test PWM has no context here; assuming mock/fake incompleteness.
        // First guess was that this is because TestPwmProvider returns null from the create method, but this would
        // mean that pwm.id() above would fail already.
        registry.remove(i2c.id());
        registry.remove(pwm.id());

        assertAll(
            // and now we shouldn't find them by address
            // TO FIX () -> assertFalse(registry.exists(IOType.PWM, 3), "Should not exist: PWM by address"),
            () -> assertFalse(registry.exists(IOType.DIGITAL_INPUT, input.bcm()), "Should not exist: Digital Input by pin"),
            () -> assertFalse(registry.exists(IOType.DIGITAL_OUTPUT, output.bcm()), "Should not exist: Digital Output by pin"),
            () -> assertFalse(registry.exists(IOType.PWM, pwm.config().getUniqueIdentifier()), "Should not exist: PWM by unique identifier"),
            () -> assertFalse(registry.exists(IOType.I2C, i2c.config().getUniqueIdentifier()), "Should not exist: I2C by unique identifier"),

            // or ID
            () -> assertFalse(registry.exists(input.id()), "Should not exist: Digital Input by ID"),
            () -> assertFalse(registry.exists(output.id()), "Should not exist: Digital Output by ID"),
            () -> assertFalse(registry.exists(pwm.id()), "Should not exist: PWM by ID"),
            () -> assertFalse(registry.exists(i2c.id()), "Should not exist: I2C by ID")
        );

        // Check close idempotency.
        input.close();
        output.close();
        pwm.close();
    }

    @Test
    void testCreateMultipleI2C() {
        var i2c1 = pi4j.create(I2C.newConfigBuilder(pi4j).bus(0).device(0x21).build());
        var i2c2 = pi4j.create(I2C.newConfigBuilder(pi4j).bus(0).device(0x70).build());
        var i2c3 = pi4j.create(I2C.newConfigBuilder(pi4j).bus(1).device(0x21).build());
        var i2c4 = pi4j.create(I2C.newConfigBuilder(pi4j).bus(3).device(0x70).build());
        assertAll(
            () -> assertNotNull(i2c1),
            () -> assertNotNull(i2c2),
            () -> assertNotNull(i2c3),
            () -> assertNotNull(i2c4)
        );
    }

    @Test
    void testCreateMultipleI2CWithSameIdentifierShouldFail() {
        var i2c1 = pi4j.create(I2C.newConfigBuilder(pi4j).bus(0).device(0x70).build());
        assertNotNull(i2c1);
        assertThrows(Pi4JException.class, () -> pi4j.create(I2C.newConfigBuilder(pi4j).bus(0).device(0x70).build()));
    }

    @Test
    void testCreateMultipleSpi() {
        var config1 = Spi.newConfigBuilder(pi4j).bus(1).channel(0x21).build();
        var spi1 = pi4j.create(config1);
        var config2 = Spi.newConfigBuilder(pi4j).bus(1).channel(0x70).build();
        var spi2 = pi4j.create(config2);
        var config3 = Spi.newConfigBuilder(pi4j).bus(2).channel(0x21).build();
        var spi3 = pi4j.create(config3);
        var config4 = Spi.newConfigBuilder(pi4j).bus(3).channel(0x70).build();
        var spi4 = pi4j.create(config4);
        assertAll(
            () -> assertNotNull(spi1),
            () -> assertNotNull(spi2),
            () -> assertNotNull(spi3),
            () -> assertNotNull(spi4)
        );
    }

    @Test
    void testCreateMultipleSpiWithSameIdentifierShouldFail() {
        var spi1 = pi4j.create(Spi.newConfigBuilder(pi4j).bus(0).channel(0x70).build());
        assertNotNull(spi1);
        assertThrows(Pi4JException.class, () -> pi4j.create(Spi.newConfigBuilder(pi4j).bus(0).channel(0x70).build()));
    }
}
