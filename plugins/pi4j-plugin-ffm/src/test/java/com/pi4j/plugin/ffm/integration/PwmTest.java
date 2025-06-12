package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.plugin.ffm.providers.pwm.PwmFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
public class PwmTest {
    private static Context pi4j;
    private static Pwm pwm;

    @BeforeAll
    public static void setup() {
        pi4j = Pi4J.newContextBuilder()
            .add(new PwmFFMProviderImpl())
            .build();
        pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j).busNumber(1).address(0));
    }

    @AfterAll
    public static void shutdown() {
        pi4j.shutdown();
    }

    @Test
    public void testPwm() {
        pwm.on();
        pwm.off();
        pwm.setFrequency(500);
        pwm.on();
    }

}
