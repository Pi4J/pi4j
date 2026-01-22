package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.digital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class DigitalOutputTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(DigitalOutputTestCase.class);

    private static final String TEST_NAME = "Digital Output";

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Digital Output test");

        DigitalOutput gpioOutTest = null;
        DigitalInput gpioInMonitor = null;
        TimeData tdResult;

        try {
            // Initialize output
            gpioOutTest = createDigitalOutput(providerContext.getContext(), 24, DigitalState.LOW, DigitalState.LOW);
            Thread.sleep(100);
            if (gpioOutTest.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output has not the correct initial state");
            }

            // Initialize input
            gpioInMonitor = createDigitalInput(providerContext.getContext(), 25, PullResistance.PULL_DOWN);
            Thread.sleep(100);
            DigitalOutputTestCase.DataInGpioListener listener = new DataInGpioListener();
            gpioInMonitor.addListener(listener);

            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Change the output
            listener.startTiming();
            gpioOutTest.high();
            tdResult = listener.waitOneSecondForPinChange();


            if (!tdResult.success) {
                return new TestResult("Digital Output", false, "Listener failed any detection");
            }

            // Check the expected input state
            DigitalState state = gpioInMonitor.state();
            if (state == DigitalState.HIGH) {
                return new TestResult(TEST_NAME, true, "Correct state detected in approximately " + tdResult.timeToChange.toNanos() + "ns");
            } else {
                return new TestResult(TEST_NAME, false, "Incorrect state: " + state);
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
        TimeData td = null;
        Instant start;
        Instant end;

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            end = Instant.now();
            Duration duration = Duration.between(start, end);

            td.timeToChange = duration;
            td.success = true;

        }


        public void startTiming() {
            td = TimeData.createTimeData();
            start = Instant.now();

        }

        public TimeData waitOneSecondForPinChange() {
            try {
                Thread.sleep(1000);
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

        public static DigitalOutputTestCase.TimeData createTimeData() {
            return new DigitalOutputTestCase.TimeData();
        }
    }
}
