package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

public class DigitalInputTestCase extends TestCase {

    private static final String TEST_NAME = "Digital Input";

    public static TestResult run(ProviderContext providerContext) {
        DigitalOutput gpioOutControl = null;
        DigitalInput gpioInTest = null;

        try {
            // Initialize output
            gpioOutControl = createDigitalOutput(providerContext.getContext(), 26, DigitalState.LOW, DigitalState.LOW);
            Thread.sleep(100);
            if (gpioOutControl.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output has not the correct initial state");
            }

            // Initialize input
            gpioInTest = createDigitalInput(providerContext.getContext(), 16, PullResistance.PULL_DOWN);
            Thread.sleep(100);
            if (gpioInTest.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Input has not the correct initial state");
            }

            // Change the output
            gpioOutControl.high();

            // Check the expected input state
            var state = gpioInTest.state();
            if (state == DigitalState.HIGH) {
                return new TestResult(TEST_NAME, true, "Correct state detected");
            } else {
                return new TestResult(TEST_NAME, false, "Incorrect state: " + state);
            }
        } catch (Exception e) {
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        } finally {
            if (gpioOutControl != null) {
                gpioOutControl.close();
            }
            if (gpioInTest != null) {
                gpioInTest.close();
            }
        }
    }
}
