package com.pi4j.test.provider;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  ManualProvidersTest.java
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.pwm.PwmProvider;
import org.junit.jupiter.api.Test;

public class ManualProvidersTest {

    @Test
    public void testProviders() throws Exception {

        // create our own custom provider implementation classes
        PwmProvider pwmProvider = TestPwmProvider.newInstance();
        I2CProvider i2CProvider = TestI2CProvider.newInstance();

        // Initialize Pi4J with an empty context
        // An empty context disables AUTO-DETECT loading
        // which will not load any detected Pi4J extension
        // libraries (Platforms and Providers) from the class path
        // ...
        // add the custom providers to the Pi4J context
        Context pi4j = Pi4J.newContextBuilder().add(pwmProvider, i2CProvider).build();

        // ensure that the io collection in the Pi4J context is not NULL
        assertNotNull(pi4j.providers());

        // ensure that no io were detected/loaded into the Pi4J context
        assertEquals(2, pi4j.providers().all().size());

        // print out the detected Pi4J io libraries found on the class path
        System.out.println("2 CUSTOM PROVIDERS (added via API)");
        pi4j.providers().describe().print(System.out);

        // shutdown Pi4J runtime
        pi4j.shutdown();
    }
}
