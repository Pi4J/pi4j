/*
 *
 *
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  FILENAME      :  DigitalDebounceTestCase.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  #L%
 *
 *
 */

package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.digital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class DigitalDebounceCountTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(DigitalDebounceCountTestCase.class);

    private static final String TEST_NAME = "Digital Debounce";

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Digital Debounce Count test");

        DigitalOutput gpioOutTest = null;
        DigitalInput gpioInMonitor = null;

        try {
            // Initialize output
            gpioOutTest = createDigitalOutput(providerContext.getContext(), 22, DigitalState.LOW, DigitalState.LOW);
            Thread.sleep(100);
            if (gpioOutTest.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output has not the correct initial state");
            }

            // Initialize input
            gpioInMonitor = createDigitalInput(providerContext.getContext(), 27, PullResistance.PULL_DOWN, 3000L);
            Thread.sleep(100);
            DigitalDebounceCountTestCase.DataInGpioListener listener = new DigitalDebounceCountTestCase.DataInGpioListener();
            gpioInMonitor.addListener(listener);

            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Debounce is in microseconds, sleep in milliseconds

            // Change the output within debounce time should not be counted
            gpioOutTest.high();
            Thread.sleep(1);
            gpioOutTest.low();
            Thread.sleep(1);
            gpioOutTest.high();

            // We should now have high 1 low 0
            Thread.sleep(5);
            logger.info("Step 1: {}/{}", listener.getCountsHigh(), listener.getCountsLow());

            // Wait longer then debounce and set low
            Thread.sleep(5);
            gpioOutTest.low();

            // We should now have high 1 low 1
            Thread.sleep(5);
            logger.info("Step 2: {}/{}", listener.getCountsHigh(), listener.getCountsLow());

            // Wait longer then debounce and set high
            Thread.sleep(5);
            gpioOutTest.high();

            // We should now have high 2 low 1     
            Thread.sleep(5);
            logger.info("Step 3: {}/{}", listener.getCountsHigh(), listener.getCountsLow());       

            // Check the results
            var highs = listener.getCountsHigh();
            var lows = listener.getCountsLow();
            if (lows == 1 && highs == 2) {
                return new TestResult(TEST_NAME, true, "Correct state change counts detected");
            } else {
                return new TestResult(TEST_NAME, false, "Incorrect state change counts detected, expected high and low 2/1, but got " + highs + "/" + lows);
            }
        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        } finally {
            if (gpioInMonitor != null) {
                gpioInMonitor.close();
            }
            if (gpioOutTest != null) {
                gpioOutTest.close();
            }
        }
    }

    // Counts the changes in the input to low and high
    private static class DataInGpioListener implements DigitalStateChangeListener {
        AtomicInteger counterHigh = new AtomicInteger(0);
        AtomicInteger counterLow = new AtomicInteger(0);

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.HIGH) {
                counterHigh.getAndIncrement();
            }
            if (event.state() == DigitalState.LOW) {
                counterLow.getAndIncrement();
            }
        }

        public int getCountsHigh() {
            return counterHigh.get();
        }

        public int getCountsLow() {
            return counterLow.get();
        }
    }
}
