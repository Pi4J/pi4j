package com.pi4j.test.type;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.test.ProviderContext;
import com.pi4j.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalInputTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(DigitalInputTestCase.class);

    public static TestResult run(ProviderContext providerContext) {
        DigitalOutput gpioOutControl = createDigitalOutput(providerContext.getContext(), 26, DigitalState.LOW, DigitalState.LOW);
        DigitalInput gpioInTest = createDigitalInput(providerContext.getContext(), 16, PullResistance.PULL_DOWN);

        // Validate monitor is LOW, then set test control state HIGH
        if (gpioInTest.state() == DigitalState.LOW) {
            gpioOutControl.high();
        }

        DigitalState state = gpioInTest.state();

        if (state == DigitalState.HIGH) {
            return new TestResult("Digital Input", true, "Correct state detected");
        } else {
            return new TestResult("Digital Input", false, "Incorrect state: " + state);
        }
    }
}
