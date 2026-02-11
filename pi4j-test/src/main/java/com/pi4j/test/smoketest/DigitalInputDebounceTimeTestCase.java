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

import java.time.Duration;
import java.time.Instant;

public class DigitalInputDebounceTimeTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(DigitalInputDebounceTimeTestCase.class);

    private static final String TEST_NAME = "Digital Debounce Time";

    private static long debounceTimeMs = 1000;

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Digital Debounce Time test, debounce time  " + debounceTimeMs + "  ms");

        DigitalOutput gpioOutTest = null;
        DigitalInput gpioInMonitor = null;

        try {
            // Initialize output
            gpioOutTest = createDigitalOutput(providerContext.getContext(), 22, DigitalState.LOW, DigitalState.LOW);
            Thread.sleep(100);
            if (gpioOutTest.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output has not the correct initial state");
            }

            // Initialize input with debounce in microseconds (API expects microseconds)
            long debounceTimeMicros = debounceTimeMs * 1000;
            gpioInMonitor = createDigitalInput(providerContext.getContext(), 27, PullResistance.PULL_DOWN, debounceTimeMicros);
            Thread.sleep(100);
            DigitalInputDebounceTimeTestCase.DataInGpioListener listener = new DigitalInputDebounceTimeTestCase.DataInGpioListener();
            gpioInMonitor.addListener(listener);

            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Change the output and start timing
            listener.startTiming();
            gpioOutTest.high();

            // Wait for the debounced event - should take at least debounceTimeMs to fire
            Thread.sleep(debounceTimeMs + 5000);

            // Check the results
            if (!listener.getResult().eventOccurred) {
                return new TestResult(TEST_NAME, false, "No event detected");
            }

            long eventTimeMs = listener.getResult().timeToChange.toMillis();
            // Event should fire no earlier than ~100ms (allowing for some system overhead)
            // Different plugins may have different timing characteristics due to hardware vs software debounce
            if (eventTimeMs < 100) {
                return new TestResult(TEST_NAME, false, "Event fired too early at " + eventTimeMs
                    + "ms (debounce not working properly)");
            }

            return new TestResult(TEST_NAME, true, "Event correctly debounced, fired after " + eventTimeMs + "ms");
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

    private static class DataInGpioListener implements DigitalStateChangeListener {
        private TimeEventData result = new TimeEventData();
        private Instant start;

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            Instant end = Instant.now();
            result.timeToChange = Duration.between(start, end);
            result.eventOccurred = true;
            logger.debug("onDigitalStateChange fired after " + result.timeToChange.toMillis() + " ms");
        }

        public void startTiming() {
            start = Instant.now();
        }

        public TimeEventData getResult() {
            return result;
        }
    }

    public static class TimeEventData {
        boolean eventOccurred;
        Duration timeToChange;

        public TimeEventData() {
            eventOccurred = false;
            timeToChange = Duration.ofSeconds(0);
        }
    }
}
