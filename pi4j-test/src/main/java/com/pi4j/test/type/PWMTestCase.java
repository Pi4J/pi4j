package com.pi4j.test.type;

import com.pi4j.boardinfo.util.PwmChipUtil;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.test.ProviderContext;
import com.pi4j.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PWMTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(PWMTestCase.class);

    private static final int PWM_CHANNEL = 2;
    private static int pwmFlashes = 0;

    public static TestResult run(ProviderContext providerContext, int frequency, int dutyCycle, int expected) throws InterruptedException {
        // Create PWM
        var chip = PwmChipUtil.getPWMChip();
        var configPwm = Pwm
            .newConfigBuilder(providerContext.getContext())
            .channel(PWM_CHANNEL)
            .pwmType(PwmType.HARDWARE)
            .initial(dutyCycle)
            .frequency(frequency)
            .chip(chip)
            .shutdown(0)
            .build();
        var pwm = providerContext.getContext().create(configPwm);

        if (pwm.getFrequency() != frequency || pwm.getDutyCycle() != dutyCycle) {
            return new TestResult("PWM " + frequency + " " + dutyCycle, false, "The expected initial values are not correct");
        }

        // Create input to listen for "flashes"
        DigitalInput gpioInMonitor = createDigitalInput(providerContext.getContext(), 23, PullResistance.PULL_DOWN);
        gpioInMonitor.addListener(new DataInGpioListener());

        // Test
        logger.info("Starting to count flashes, please wait...");

        pwm.on(dutyCycle, frequency);
        Thread.sleep(10000);  // wait 10 seconds while listener counts flashes
        pwm.off();

        logger.info("Number of flashes counted: {}", pwmFlashes);

        // Cleanup
        gpioInMonitor.close();
        pwm.close();
        providerContext.getContext().registry().remove(pwm.id());

        if (pwmFlashes == expected) {
            return new TestResult("PWM " + frequency + " " + dutyCycle, true, "Correct number of flashes detected");
        } else {
            return new TestResult("PWM " + frequency + " " + dutyCycle, false, "Number of flashes is not correct: " + pwmFlashes + "/" + expected);
        }
    }

    private static class DataInGpioListener implements DigitalStateChangeListener {
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
    }
}
