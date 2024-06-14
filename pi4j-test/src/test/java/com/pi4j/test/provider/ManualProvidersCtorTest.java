package com.pi4j.test.provider;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  ManualProvidersCtorTest.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.io.serial.SerialProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class ManualProvidersCtorTest {

    private Context pi4j;

    @BeforeAll
    public void beforeTest() {

        // create our own custom provider implementation classes
        PwmProvider pwmProvider = TestPwmProvider.newInstance();
        I2CProvider i2CProvider = TestI2CProvider.newInstance();
        SerialProvider serialProvider = TestSerialProvider.newInstance();

        // Initialize Pi4J with a manually configured context
        // ...
        // Explicitly add the test providers into the
        // context for testing
        pi4j = Pi4J.newContextBuilder().add(pwmProvider, i2CProvider, serialProvider).build();
    }

    @AfterAll
    public void afterTest() {
        try {
            if (this.pi4j != null)
                pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testProvidersNotNull() {
        // ensure that the io collection in the Pi4J context is not NULL
        assertNotNull(pi4j.providers());
    }

    @Test
    public void testProviderCount() {
        // ensure that only 3 providers were detected/loaded into the Pi4J context
        assertEquals(3 , pi4j.providers().all().size());

        // print out the detected Pi4J platforms
        pi4j.platforms().describe().print(System.out);

        // print out the detected Pi4J providers
        pi4j.providers().describe().print(System.out);
    }
}
