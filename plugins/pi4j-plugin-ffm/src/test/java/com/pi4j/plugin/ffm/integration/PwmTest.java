package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.ffm.providers.pwm.PwmFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
public class PwmTest {
    //private static final MockedStatic<PermissionHelper> permissionHelperMock = PermissionHelperMock.echo();

    private static Context pi4j;
    private static Pwm pwm;

    @BeforeAll
    public static void setup() throws IOException, InterruptedException {
        var scriptPath = Paths.get("src/test/resources/").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.getAbsolutePath() + "/pwm-setup.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to setup PWM Test: \n" + errorOutput + "\n" +
                "Probably you need to add the PWM bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }

        pi4j = Pi4J.newContextBuilder()
            .add(new PwmFFMProviderImpl())
            .build();
        pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
            .busNumber(1)
            .address(0)
            .pwmType(PwmType.HARDWARE)
            .build());
    }

    @AfterAll
    public static void shutdown() throws IOException, InterruptedException {
        pi4j.shutdown();
        //permissionHelperMock.close();

        var scriptPath = Paths.get("src/test/resources/").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.getAbsolutePath() + "/pwm-clean.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to cleanup PWM Test: \n" + errorOutput + "\n" +
                "Probably you need to add the PWM bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }
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
