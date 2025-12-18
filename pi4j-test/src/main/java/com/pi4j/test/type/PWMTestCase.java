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

    public static TestResult run(ProviderContext providerContext) throws InterruptedException {
        // Create PWM
        var chip = PwmChipUtil.getPWMChip();
        var configPwm = Pwm
            .newConfigBuilder(providerContext.getContext())
            .channel(PWM_CHANNEL)
            .pwmType(PwmType.HARDWARE)
            .initial(50)
            .frequency(1)
            .chip(chip)
            .shutdown(0)
            .build();
        var pwm = providerContext.getContext().create(configPwm);

        // Create input to listen for "flashes"
        DigitalInput gpioInMonitor = createDigitalInput(providerContext.getContext(), 23, PullResistance.PULL_DOWN);
        gpioInMonitor.addListener(new DataInGpioListener());

        // Test
        pwm.on(50, 1);
        Thread.sleep(10000);  // wait 10 seconds while listener counts flashes
        pwm.off();

        if (pwmFlashes == 10) {
            return new TestResult("PWM", true, "Correct number of flashes detected");
        } else {
            return new TestResult("PWM", false, "Number of flashes is not correct: " + pwmFlashes);
        }
    }

    private static class DataInGpioListener implements DigitalStateChangeListener {
        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.HIGH) {
                logger.info("onDigitalStateChange Pin went High");
            } else if (event.state() == DigitalState.LOW) {
                logger.info("PWM flashed");
                pwmFlashes++;
            } else {
                logger.warn("Strange event state: {}", event.state());
            }
        }
    }
}
