package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PWMTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(PWMTestCase.class);

    public static TestResult run(ProviderContext providerContext, int frequency, int dutyCycle, int expected) {
        return run(providerContext, 0, 2, PwmType.HARDWARE, frequency, dutyCycle, expected);
    }

    public static TestResult run(ProviderContext providerContext, int chip, int channel, PwmType pwmType, int frequency, int dutyCycle, int expected) {
        logger.info("Starting PWM test");

        Pwm pwm = null;
        DigitalInput gpioInMonitor = null;

        var testName = "PWM (freq: " + frequency + ", dc: " + dutyCycle + ")";

        try {
            // Initialize PWM
            var configPwm = Pwm
                .newConfigBuilder(providerContext.getContext())
                .chip(chip)
                .channel(channel)
                .pwmType(pwmType)
                .initial(dutyCycle)
                .frequency(frequency)
                .shutdown(0)
                .build();
            pwm = providerContext.getContext().create(configPwm);

            Thread.sleep(100);

            if (pwm.getFrequency() != frequency || pwm.getDutyCycle() != dutyCycle) {
                return new TestResult(testName, false, "The expected initial values are not correct");
            }

            // Create input to listen for "flashes"
            gpioInMonitor = createDigitalInput(providerContext.getContext(), 23, PullResistance.PULL_DOWN);
            var flashListener = new DataInGpioListener();
            gpioInMonitor.addListener(flashListener);
            Thread.sleep(100);
            // Since the initial value is non zero, it is possible that
            // the PWM output could be low or high, no validation to be done here

            // Test
            logger.info("Starting to count flashes, please wait...");
            pwm.on(dutyCycle, frequency);
            Thread.sleep(10_000);  // wait 10 seconds while listener counts flashes
            pwm.off();
            logger.info("Number of flashes counted: {}", flashListener.getPwmFlashes());

            // Check counted flashes versus expected
            if (flashListener.getPwmFlashes() == expected) {
                return new TestResult(testName, true, "Correct number of flashes detected");
            } else {
                return new TestResult(testName, false, "Number of flashes is not correct: "
                    + flashListener.getPwmFlashes() + "/" + expected);
            }
        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(testName, false, "Test failure: " + e.getMessage());
        } finally {
            if (pwm != null) {
                pwm.close();
                providerContext.getContext().registry().remove(pwm.id());
            }
            if (gpioInMonitor != null) {
                gpioInMonitor.close();
            }
        }
    }

    private static class DataInGpioListener implements DigitalStateChangeListener {
        private int pwmFlashes = 0;

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.HIGH) {
                logger.debug("onDigitalStateChange Pin went High");
            } else if (event.state() == DigitalState.LOW) {
                logger.debug("PWM flashed");
                pwmFlashes++;
            } else {
                logger.warn("Strange event state: {}", event.state());
            }
        }

        public int getPwmFlashes() {
            return pwmFlashes;
        }
    }
}
