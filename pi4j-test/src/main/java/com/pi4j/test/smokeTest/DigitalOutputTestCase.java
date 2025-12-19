package com.pi4j.test.smokeTest;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

public class DigitalOutputTestCase extends TestCase {

    private static final String TEST_NAME = "Digital Output";

    public static TestResult run(ProviderContext providerContext) {
        DigitalOutput gpioOutTest = null;
        DigitalInput gpioInMonitor = null;

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
            if (gpioInMonitor.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Change the output
            gpioOutTest.high();

            // Check the expected input state
            DigitalState state = gpioInMonitor.state();
            if (state == DigitalState.HIGH) {
                return new TestResult(TEST_NAME, true, "Correct state detected");
            } else {
                return new TestResult(TEST_NAME, false, "Incorrect state: " + state);
            }
        } catch (Exception e) {
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
}
