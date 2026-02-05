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

public class DigitalDebounceTimeTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(DigitalDebounceTimeTestCase.class);

    private static final String TEST_NAME = "Digital Debounce";

    private static long debounceTime = 1000;

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Digital Debounce Time test, debounce time  " + debounceTime + "  ms");

        DigitalOutput gpioOutTest = null;
        DigitalInput gpioInMonitor = null;
        DigitalDebounceTimeTestCase.TimeEventData tdResult;

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
            DigitalDebounceTimeTestCase.DataInGpioListener listener = new DigitalDebounceTimeTestCase.DataInGpioListener();
            gpioInMonitor.addListener(listener);

            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Change the output
            listener.startTiming(debounceTime);
            gpioOutTest.high();
            tdResult = listener.waitTenSecondForPinChange();


            // Check the results
            if (tdResult.success) {
                return new TestResult(TEST_NAME, true, "Correct debounce time  " + debounceTime + " ms state detected in approximately " + tdResult.timeToChange.toNanos() / 1000 + "ms");
            } else if (tdResult.eventOccurred) {
                return new TestResult(TEST_NAME, false, "Debounce  time " + debounceTime + "ms , event occurred : " + tdResult.eventOccurred + " but outside limits after  approximately " + tdResult.timeToChange.toNanos() / 1000 + "ms");
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
    // is set to track the NS until the pin changes.  The test code immediately drives the output pin
    // and then request the monitor listener wait a second, then return the data logged when
    // the event fired.
    private static class DataInGpioListener implements DigitalStateChangeListener {
        DigitalDebounceTimeTestCase.TimeEventData td = null;
        Instant start;
        Instant end;
        long expectedTime = 0;

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            end = Instant.now();
            Duration duration = Duration.between(start, end);
            td.timeToChange = duration;
            logger.debug("onDigitalStateChange fired duration " + td.timeToChange.toNanos() + "  ns");
            td.eventOccurred = true;
            if ((td.timeToChange.toNanos() / 1000 > expectedTime - 300) && (td.timeToChange.toNanos() / 1000 < expectedTime + 300)) {
                td.success = true;
            } else {
                td.success = false;
            }

        }


        public void startTiming(long expected) {
            expectedTime = expected;
            td = DigitalDebounceTimeTestCase.TimeEventData.createTimeEventData();
            start = Instant.now();

        }

        public DigitalDebounceTimeTestCase.TimeEventData waitTenSecondForPinChange() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return td;
        }
    }

    public static class TimeEventData {
        boolean success;
        boolean eventOccurred;
        Duration timeToChange;

        private TimeEventData() {
            success = false;
            eventOccurred = false;
            timeToChange = Duration.ofSeconds(0);
        }

        public static DigitalDebounceTimeTestCase.TimeEventData createTimeEventData() {
            return new DigitalDebounceTimeTestCase.TimeEventData();
        }
    }
}
