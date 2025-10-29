package com.pi4j.test.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  DigitalInputOnTest.java
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
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class DigitalInputOnTest {

    private static final Logger logger = LoggerFactory.getLogger(DigitalInputOnTest.class);

    private Context pi4j;

    @BeforeEach
    public void beforeTest() throws Pi4JException {
        // Initialize Pi4J with MOCK digital iput provider
        pi4j = Pi4J.newContextBuilder().add(MockDigitalInputProvider.newInstance()).build();
    }

    @AfterEach
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testIsOnDefault() {

        // create GPIO digital input config
        var config = DigitalInput.newConfigBuilder(pi4j)
            .id("test-input")
            .name("Test Digital Input")
            .pin(1)
            .build();

        // create GPIO digital input instance
        var input = pi4j.din().create(config);

        // set MOCK state to HIGH
        MockDigitalInput mockInput = (MockDigitalInput) input;
        mockInput.mockState(DigitalState.HIGH);

        // ensure input is ON and not OFF
        assertTrue(input.isOn());
        assertFalse(input.isOff());
    }

    @Test
    public void testIsOnHigh() {

        // create GPIO digital input config
        var config = DigitalInput.newConfigBuilder(pi4j)
            .id("test-input")
            .name("Test Digital Input")
            .onState(DigitalState.HIGH)
            .pin(1)
            .build();

        // create GPIO digital input instance
        var input = pi4j.din().create(config);

        // set MOCK state to HIGH
        MockDigitalInput mockInput = (MockDigitalInput) input;
        mockInput.mockState(DigitalState.HIGH);

        // ensure input is ON and not OFF
        assertTrue(input.isOn());
        assertFalse(input.isOff());
    }

    @Test
    public void testIsOnLow() {

        // create GPIO digital input config
        var config = DigitalInput.newConfigBuilder(pi4j)
            .id("test-input")
            .name("Test Digital Input")
            .onState(DigitalState.LOW)
            .pin(1)
            .build();

        // create GPIO digital input instance
        var input = pi4j.din().create(config);

        // set MOCK state to LOW
        MockDigitalInput mockInput = (MockDigitalInput) input;
        mockInput.mockState(DigitalState.LOW);

        // ensure input is ON and not OFF
        assertTrue(input.isOn());
        assertFalse(input.isOff());
    }
}
