package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.digital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalProfilerTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(DigitalProfilerTestCase.class);

    private static final String TEST_NAME = "Digital Output Profiling";
    private static final int ITERATIONS = 5_000_000;

    public static TestResult run(ProviderContext providerContext) {
        logger.info(TEST_NAME + " test");

        try(var gpioOutTest = createDigitalOutput(providerContext.getContext(), 24, DigitalState.LOW, DigitalState.LOW);
            var gpioInMonitor = createDigitalInput(providerContext.getContext(), 25, PullResistance.PULL_DOWN, 0);
        ) {
            logger.info("Using digital output of type: {}", gpioOutTest.getClass().getSimpleName());
            logger.info("Using digital input of type: {}", gpioInMonitor.getClass().getSimpleName());

            if (gpioOutTest.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output has not the correct initial state");
            }

            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            for (var i = 0; i < ITERATIONS; i++) {
                gpioOutTest.high();
                if (gpioInMonitor.state() != DigitalState.HIGH) {
                    return new TestResult(TEST_NAME, false, String.format("Should be high after %d iterations", i));
                }
                gpioOutTest.low();
                if (gpioInMonitor.state() != DigitalState.LOW) {
                    return new TestResult(TEST_NAME, false, String.format("Should be low after %d iterations", i));
                }
            }

            return new TestResult(TEST_NAME, true, "Done");
        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        }
    }
}
