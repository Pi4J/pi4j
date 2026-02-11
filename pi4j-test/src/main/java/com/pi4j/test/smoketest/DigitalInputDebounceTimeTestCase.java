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

    private static long debounceTime = 1000;

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Digital Debounce Time test, debounce time  " + debounceTime + "  ms");

        DigitalOutput gpioOutTest = null;
        DigitalInput gpioInMonitor = null;
        DigitalInputDebounceTimeTestCase.TimeEventData tdResult;

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
            DigitalInputDebounceTimeTestCase.DataInGpioListener listener = new DigitalInputDebounceTimeTestCase.DataInGpioListener();
            gpioInMonitor.addListener(listener);

            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Change the output
            gpioOutTest.high();
            listener.startTiming(debounceTime);

            // The event should be detected immediately, but adding a sleep here in case something is delaying the change
            Thread.sleep(10_000);

            // Check the results
            if (listener.getResult().success) {
                return new TestResult(TEST_NAME, true, "Correct debounce time " + debounceTime
                    + "ms state detected in approximately " + listener.getResult().timeToChange.toNanos() / 1000 + "ms");
            } else if (listener.getResult().eventOccurred) {
                return new TestResult(TEST_NAME, false, "Debounce  time " + debounceTime
                    + "ms, event occurred but outside the limits after "
                    + listener.getResult().timeToChange.toNanos() / 1000 + "ms");
            } else {
                return new TestResult(TEST_NAME, false, "No event detected");
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

    // Prior to driving the output pin high.  the monitor input pin listener
    // is set to track the NS until the pin changes. The test code immediately drives the output pin
    // and then requests the monitor listener to wait a second, then return the data logged when
    // the event fired.
    private static class DataInGpioListener implements DigitalStateChangeListener {
        private TimeEventData result = new TimeEventData();
        private Instant start;
        private Instant end;
        private long expectedTime = 0;

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            end = Instant.now();
            Duration duration = Duration.between(start, end);
            result.timeToChange = duration;
            logger.debug("onDigitalStateChange fired duration " + result.timeToChange.toNanos() + "  ns");
            result.eventOccurred = true;
            if ((result.timeToChange.toNanos() / 1000 > expectedTime - 300) && (result.timeToChange.toNanos() / 1000 < expectedTime + 300)) {
                result.success = true;
            } else {
                result.success = false;
            }
        }

        public void startTiming(long expected) {
            expectedTime = expected;
            start = Instant.now();
        }

        public TimeEventData getResult() {
            return result;
        }
    }

    public static class TimeEventData {
        boolean success;
        boolean eventOccurred;
        Duration timeToChange;

        public TimeEventData() {
            success = false;
            eventOccurred = false;
            timeToChange = Duration.ofSeconds(0);
        }
    }
}
