package com.pi4j.test.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  DigitalOutputOnTest.java
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
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class DigitalOutputOnTest {

    private static final Logger logger = LoggerFactory.getLogger(DigitalOutputOnTest.class);

    private Context pi4j;

    @BeforeEach
    public void beforeTest() throws Pi4JException {
        // Initialize Pi4J with MOCK digital output provider
        pi4j = Pi4J.newContextBuilder().add(MockDigitalOutputProvider.newInstance()).build();
    }

    @AfterEach
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testIsOnDefault() {

        // create GPIO digital output config
        var config = DigitalOutput.newConfigBuilder(pi4j)
            .id("test-output")
            .name("Test Digital Output")
            .pin(1)
            .build();

        // create GPIO digital output instance
        var output = pi4j.dout().create(config);

        // set output to HIGH
        output.high();

        // ensure output is HIGH
        assertEquals(DigitalState.HIGH, output.state());

        // ensure output is ON and not OFF
        assertTrue(output.isOn());
        assertFalse(output.isOff());
    }

    @Test
    public void testIsOnHigh() {

        // create GPIO digital output config
        var config = DigitalOutput.newConfigBuilder(pi4j)
            .id("test-output")
            .name("Test Digital Output")
            .pin(1)
            .onState(DigitalState.HIGH)
            .build();

        // create GPIO digital output instance
        var output = pi4j.dout().create(config);

        // set output to HIGH
        output.high();

        // ensure output is HIGH
        assertEquals(DigitalState.HIGH, output.state());

        // ensure output is ON and not OFF
        assertTrue(output.isOn());
        assertFalse(output.isOff());
    }

    @Test
    public void testIsOnLow() {

        // create GPIO digital output config
        var config = DigitalOutput.newConfigBuilder(pi4j)
            .id("test-output")
            .name("Test Digital Output")
            .onState(DigitalState.LOW)
            .pin(1)
            .build();

        // create GPIO digital output instance
        var output = pi4j.dout().create(config);

        // set output to LOW
        output.low();

        // ensure output is LOW
        assertEquals(DigitalState.LOW, output.state());

        // ensure output is ON and not OFF
        assertTrue(output.isOn());
        assertFalse(output.isOff());
    }

    @Test
    public void testOnDefault() {

        // create GPIO digital output config
        var config = DigitalOutput.newConfigBuilder(pi4j)
            .id("test-output")
            .name("Test Digital Output")
            .pin(1)
            .build();

        // create GPIO digital output instance
        var output = pi4j.dout().create(config);

        // set output to ON
        output.on();

        // ensure output is HIGH
        assertEquals(DigitalState.HIGH, output.state());

        // ensure output is ON and not OFF
        assertTrue(output.isOn());
        assertFalse(output.isOff());
    }

    @Test
    public void testOnHigh() {

        // create GPIO digital output config
        var config = DigitalOutput.newConfigBuilder(pi4j)
            .id("test-output")
            .name("Test Digital Output")
            .pin(1)
            .onState(DigitalState.HIGH)
            .build();

        // create GPIO digital output instance
        var output = pi4j.dout().create(config);

        // set output to ON
        output.on();

        // ensure output is HIGH
        assertEquals(DigitalState.HIGH, output.state());

        // ensure output is ON and not OFF
        assertTrue(output.isOn());
        assertFalse(output.isOff());
    }

    @Test
    public void testOnLow() {

        // create GPIO digital output config
        var config = DigitalOutput.newConfigBuilder(pi4j)
            .id("test-output")
            .name("Test Digital Output")
            .pin(1)
            .onState(DigitalState.LOW)
            .build();

        // create GPIO digital output instance
        var output = pi4j.dout().create(config);

        // set output to ON
        output.on();

        // ensure output is LOW
        assertEquals(DigitalState.LOW, output.state());

        // ensure output is ON and not OFF
        assertTrue(output.isOn());
        assertFalse(output.isOff());
    }
}
