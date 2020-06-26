package com.pi4j.test.registry;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  RegistryGetIoInstancesByProvider.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2020 Pi4J
 * %%
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class RegistryGetIoInstancesByProvider {

    public static final int PIN_ADDRESS_1 = 1;
    public static final String PIN_ID_1 = "my-custom-pin-1";

    public static final int PIN_ADDRESS_2 = 2;
    public static final String PIN_ID_2 = "my-custom-pin-2";

    private Context pi4j;

    @BeforeAll
    public void beforeTest() throws Exception {

        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");

        // Initialize Pi4J with a manually configured context
        // Disable AUTO-DETECT loading to prevent automatic
        // loading of any detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        // ...
        // Explicitly add single provider implementation
        // into the context for testing
        // ...
        // Also, inject this class instance into the Pi4J context
        // for annotation processing and dependency injection
        pi4j = Pi4J.newContextBuilder()
                .add(MockDigitalInputProvider.newInstance())
                .build().inject(this);

        // create simple I/O instances
        DigitalInput input1 = pi4j.din().create(PIN_ADDRESS_1, PIN_ID_1);
        DigitalInput input2 = pi4j.din().create(PIN_ADDRESS_2, PIN_ID_2);
    }

    @AfterAll
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testGetIoInstancesByProviderClass() throws Pi4JException {

        // attempt to get I/O instance from registry
        var retrieved = pi4j.registry().allByProvider(DigitalInputProvider.class);

        // verify the retrieved I/O instance is the same count we registered
        assertEquals(2, retrieved.size(), "The I/O instances retrieved from registry is not a match.");
    }

    @Test
    public void testGetIoInstancesByProviderType() throws Pi4JException {

        // attempt to get I/O instance from registry
        var retrieved = pi4j.registry().allByIoType(IOType.DIGITAL_INPUT);

        // verify the retrieved I/O instance is the same count we registered
        assertEquals(2, retrieved.size(), "The I/O instances retrieved from registry is not a match.");
    }

    @Test
    public void testGetIoInstancesByProviderId() throws Pi4JException {

        // attempt to get I/O instance from registry
        var retrieved = pi4j.registry().allByProvider(MockDigitalInputProvider.ID);

        // verify the retrieved I/O instance is the same count we registered
        assertEquals(2, retrieved.size(), "The I/O instances retrieved from registry is not a match.");
    }

    @Test
    public void testGetIoInstancesFromInvalidProvider() throws Pi4JException {

        // attempt to get I/O instance from registry
        var retrieved = pi4j.registry().allByIoType(IOType.ANALOG_INPUT);

        // verify the retrieved I/O instance is ZERO
        assertEquals(0, retrieved.size(), "No I/O instances should have retrieved from registry using this IO type.");
    }

}
