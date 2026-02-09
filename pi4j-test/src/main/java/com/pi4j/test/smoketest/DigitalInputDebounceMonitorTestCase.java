/*
 *
 *
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  FILENAME      :  DigitalDebounceMonitorTestCase.java
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

public class DigitalInputDebounceMonitorTestCase extends TestCase {


    private static final Logger logger = LoggerFactory.getLogger(DigitalInputDebounceMonitorTestCase.class);

    private static final String TEST_NAME = "Digital Debounce Monitor";
    protected static DigitalOutput gpioOutLogic = null;

    public static TestResult run(ProviderContext providerContext) {
        logger.info(TEST_NAME);

        int debounceSetting = 0;
        DigitalOutput gpioOutTest = null;
        DigitalInput gpioInMonitor = null;
        try {
            // Initialize output
            gpioOutTest = createDigitalOutput(providerContext.getContext(), 12, DigitalState.LOW, DigitalState.LOW);
            Thread.sleep(100);
            if (gpioOutTest.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output Test has not the correct initial state");
            }
            gpioOutLogic = createDigitalOutput(providerContext.getContext(), 20, DigitalState.LOW, DigitalState.LOW);
            Thread.sleep(100);
            if (gpioOutLogic.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output Logic GPIO not the correct initial state");
            }
            DigitalInputDebounceMonitorTestCase.DataInGpioListener listener = new DigitalInputDebounceMonitorTestCase.DataInGpioListener();
            debounceSetting = waitForInput(logger, "Engage Logic Analyzer pin BCM12 and BCM20 \n Enter numeric debounce value, 0 to exit");

            while (debounceSetting > 0) {
                // Initialize input
                gpioInMonitor = createDigitalInput(providerContext.getContext(), 19, PullResistance.PULL_DOWN, debounceSetting);
                Thread.sleep(100);
                gpioInMonitor.addListener(listener);

                if (gpioInMonitor.state() != DigitalState.LOW) {
                    return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
                }

                logger.info("Engage Logic Analyzer pin BCM12 and BCM20");

                // Change the output within debounce time should not be counted
                gpioOutTest.high();
                Thread.sleep(3000);

                gpioOutTest.low();
                gpioInMonitor.removeListener(listener);
                gpioInMonitor.close();
                providerContext.getContext().shutdown(gpioInMonitor);
                gpioInMonitor = null;
                gpioOutLogic.low();
                debounceSetting = waitForInput(logger, "Engage Logic Analyzer pin BCM12 and BCM20 \n Enter numeric debounce value, to exit");
            }


            return new TestResult(TEST_NAME, true, "Manual monitoring completed");
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
            if (gpioOutLogic != null) {
                gpioOutLogic.close();
            }
        }
    }


    // Counts the changes in the input to low and high
    private static class DataInGpioListener implements DigitalStateChangeListener {

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            gpioOutLogic.high();

        }

    }
}

