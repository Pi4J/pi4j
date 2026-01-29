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

public class DigitalDebounceTestCase  extends TestCase{

    private static final Logger logger = LoggerFactory.getLogger(DigitalOutputTestCase.class);

    private static final String TEST_NAME = "Digital Debounce";

    private static long debounceTime = 1000;

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Digital Debounce test, debounce time  "+ debounceTime + "  ms");

        DigitalOutput gpioOutTest = null;
        DigitalInput gpioInMonitor = null;
        DigitalOutputTestCase.TimeData tdResult;

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
            DigitalDebounceTestCase.DataInGpioListener listener = new DigitalDebounceTestCase.DataInGpioListener();
            gpioInMonitor.addListener(listener);

            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Change the output
            listener.startTiming(debounceTime);
            gpioOutTest.high();
            tdResult = listener.waitFiveSecondForPinChange();


            if (!tdResult.success) {
                return new TestResult("Digital Debounce debounce time  "+ debounceTime + "  ms\" " ,  false, "Listener triggered outside detection range after approximately " + tdResult.timeToChange.toNanos() + "ns");
            }

            // Check the expected input state
            DigitalState state = gpioInMonitor.state();
            if (state == DigitalState.HIGH) {
                return new TestResult(TEST_NAME, true, "Correct debounce time  " + debounceTime +" ms state detected in approximately " + tdResult.timeToChange.toNanos() + "ns");
            } else {
                return new TestResult(TEST_NAME, false, "Incorrect state:  debounce time  " + debounceTime + " ms " + state + "  after approximately " + tdResult.timeToChange.toNanos() + "ns");
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
        DigitalOutputTestCase.TimeData td = null;
        Instant start;
        Instant end;
        long expectedTime = 0;

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            end = Instant.now();
            Duration duration = Duration.between(start, end);
            td.timeToChange = duration;
            logger.debug("onDigitalStateChange fired duration " +td.timeToChange.toNanos() + "  ns");

            if ((td.timeToChange.toNanos()/1000 > expectedTime-300)&&(td.timeToChange.toNanos()/1000 < expectedTime+300))  {
                td.success = true;
            } else {
                td.success = false;
            }

        }


        public void startTiming(long expected) {
            expectedTime = expected;
            td = DigitalOutputTestCase.TimeData.createTimeData();
            start = Instant.now();

        }

        public DigitalOutputTestCase.TimeData waitFiveSecondForPinChange() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return td;
        }
    }

    public static class TimeData {
        boolean success;
        Duration timeToChange;

        private TimeData() {
            success = false;
            timeToChange = Duration.ofSeconds(0);
        }

        public static DigitalDebounceTestCase.TimeData createTimeData() {
            return new DigitalDebounceTestCase.TimeData();
        }
    }
}
