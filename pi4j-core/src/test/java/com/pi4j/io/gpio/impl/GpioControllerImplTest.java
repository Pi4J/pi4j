package com.pi4j.io.gpio.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  GpioControllerImplTest.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2021 Pi4J
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

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.exception.PinProviderException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the {@link GpioControllerImpl}.
 */
public class GpioControllerImplTest {
    @Before
    public void setUp() {
        GpioProvider provider = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(provider);
    }

    //@Test(expected = PinProviderException.class)
    @Test
    public void testProvisionPin() {
        GpioController controller = new GpioControllerImpl();
        controller.provisionPin(RaspiPin.GPIO_00, PinMode.DIGITAL_OUTPUT);
    }
}
