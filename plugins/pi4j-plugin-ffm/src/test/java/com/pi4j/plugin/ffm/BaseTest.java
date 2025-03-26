package com.pi4j.plugin.ffm;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import org.junit.jupiter.api.Test;

public class BaseTest {

    @Test
    public void test() {
        //var pi4j = Pi4J.newAutoContext();
        var pi4j = Pi4J.newContextBuilder()
            .add(new FFMDigitalInputProviderImpl(), new FFMDigitalOutputProviderImpl(), new FFMI2CProviderImpl(),
                new FFMPwmProviderImpl(), new FFMSpiProviderImpl())
            .setGpioChipName("gpiochip0").build();
        pi4j.providers().describe().print(System.out);
        var rst = pi4j.digitalInput().create(1);
        var pwr = pi4j.digitalOutput().create(2, "123");
        pwr.state(DigitalState.UNKNOWN);
        System.out.println(pwr.state() + " " + rst.state());
        //System.out.println(pi4j.registry().all());
        pi4j.shutdown();
    }
}
