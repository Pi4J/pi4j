package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmPreset;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.BaseSetup;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.mocks.PermissionHelperMock;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
public class PwmTest extends BaseSetup {

    private static Context pi4j;
    private static Pwm pwm;

    @BeforeAll
    public static void setup() throws InterruptedException, IOException {
        setup("pwm");

        pi4j = Pi4J.newContextBuilder()
            .add(new FFMPwmProviderImpl())
            .build();
        pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
            .pwmType(PwmType.HARDWARE)
            .chip(findMockPwmChip())
            .channel(0)
            .build());
    }

    /**
     * Finds the pwmchip number assigned to the pwm-mock driver. The kernel PWM core allocates the
     * pwmchip id dynamically (lowest free id), so on boards that already expose real PWM chips
     * (e.g. a Raspberry Pi 5) the mock does not land on pwmchip0. Each /sys/class/pwm/pwmchipN has
     * a 'device' symlink back to its parent platform device, so we match the one owned by pwm-mock.
     */
    private static int findMockPwmChip() throws IOException {
        var pwmClass = Paths.get("/sys/class/pwm");
        try (var chips = Files.newDirectoryStream(pwmClass, "pwmchip*")) {
            for (var chip : chips) {
                var device = chip.resolve("device");
                if (Files.exists(device) && device.toRealPath().toString().contains("pwm-mock")) {
                    return Integer.parseInt(chip.getFileName().toString().substring("pwmchip".length()));
                }
            }
        }
        throw new IllegalStateException("Could not find a pwm-mock pwmchip under " + pwmClass);
    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();
        tearDown("pwm");
    }

    @Test
    public void testPwm() {
        pwm.on();
        pwm.off();
        pwm.setFrequency(500);
        pwm.setDutyCycle(5);
        pwm.on();
        pwm.off();
        pwm.on();
        pwm.setFrequency(10_000);
        pwm.setDutyCycle(10);
        pwm.off();
    }

    @Test
    public void testPwmChangeValues() {
        pwm.on();
        pwm.setFrequency(500);
        pwm.setDutyCycle(50);
        assertEquals(500, pwm.actualFrequency());
        assertEquals(50, pwm.dutyCycle());
        pwm.off();
    }

}
