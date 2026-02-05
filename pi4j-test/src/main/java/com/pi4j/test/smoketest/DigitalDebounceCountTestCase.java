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

    private static long debounceTime = 1000L;

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Digital Debounce Count test, debounce time  " + debounceTime + "  ms");

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
            gpioInMonitor = createDigitalInput(providerContext.getContext(), 27, PullResistance.PULL_DOWN, debounceTime);
            Thread.sleep(100);
            DigitalDebounceCountTestCase.DataInGpioListener listener = new DigitalDebounceCountTestCase.DataInGpioListener();
            gpioInMonitor.addListener(listener);

            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Change the output
            for (int i = 0; i < 10; i++) {
                gpioOutTest.high();
                Thread.sleep(debounceTime * 2);
                gpioOutTest.low();
                Thread.sleep(debounceTime * 2);
            }

            // Check the results
            var lows = listener.getCountsLow();
            var highs = listener.getCountsHigh();
            if (lows == 10 && highs == 10) {
                return new TestResult(TEST_NAME, true, "Correct state change counts detected");
            } else {
                return new TestResult(TEST_NAME, false, "Incorrect state change counts detected, expected 10 high and 10 low, got" + highs + "/" + lows);
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

        Instant start;
        Instant end;
        long expectedTime = 0;

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
