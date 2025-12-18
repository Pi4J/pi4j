package com.pi4j.test.type;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.test.ProviderContext;
import com.pi4j.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalOutputTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(DigitalOutputTestCase.class);

    public static TestResult run(ProviderContext providerContext) {
        DigitalOutput gpioOutTest = createDigitalOutput(providerContext.getContext(), 24, DigitalState.LOW, DigitalState.LOW);
        DigitalInput gpioInMonitor = createDigitalInput(providerContext.getContext(), 25, PullResistance.PULL_DOWN);

        // Validate monitor is LOW, test control state HIGH
        if (gpioInMonitor.state() == DigitalState.LOW) {
            gpioOutTest.high();
        }

        DigitalState state = gpioInMonitor.state();

        if (state == DigitalState.HIGH) {
            return new TestResult("Digital Output", true, "Correct state detected");
        } else {
            return new TestResult("Digital Output", false, "Incorrect state: " + state);
        }
    }
}
