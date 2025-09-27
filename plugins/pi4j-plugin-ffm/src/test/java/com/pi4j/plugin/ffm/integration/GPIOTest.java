package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
@Disabled
public class GPIOTest {
    private static final String IN_CONTAINER = System.getenv("IN_CONTAINER");

    private static Context pi4j0;
    private static Context pi4j1;
    private static Context pi4jNonExistent;

    @BeforeAll
    public static void setup() throws IOException, InterruptedException {
        if (IN_CONTAINER == null || !IN_CONTAINER.equals("true")) {
            var scriptPath = Paths.get("src/test/resources/gpio-setup.sh");
            var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
            var result = setupScript.waitFor();
            if (result != 0) {
                var username = System.getProperty("user.name");
                var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
                fail("Failed to setup GPIO Test:\n" + errorOutput + "\n" +
                    "Probably you need to add the GPIO Simulator bash script to sudoers file " +
                    "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
            }
        }
        pi4j0 = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl(), new DigitalOutputFFMProviderImpl())
            .setGpioChipName("gpiochip0")
            .build();
        pi4j1 = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl())
            .setGpioChipName("gpiochip1")
            .build();
        pi4jNonExistent = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl())
            .setGpioChipName("gpiochip99")
            .build();
    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j0.shutdown();
        pi4j1.shutdown();
        pi4jNonExistent.shutdown();
        if (IN_CONTAINER == null || !IN_CONTAINER.equals("true")) {
            var scriptPath = Paths.get("src/test/resources/gpio-clean.sh");
            var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
            var result = setupScript.waitFor();
            if (result != 0) {
                var username = System.getProperty("user.name");
                var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
                fail("Failed to setup GPIO Test:\n" + errorOutput + "\n" +
                    "Probably you need to add the GPIO Simulator bash script to sudoers file " +
                    "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
            }
        }
    }

    @Test
    public void testInputUnavailable() {
        assertThrows(Pi4JException.class, () -> pi4j1.digitalInput().create(99));
    }

    @Test
    public void testInputNonExistent() {
        assertThrows(IllegalStateException.class, () -> pi4jNonExistent.digitalInput().create(0));
    }

    @Test
    public void testInputCreate() {
        var pin = pi4j0.digitalInput().create(0);
        assertEquals(0, pin.address());
    }

    @Test
    public void testInputState() {
        var pin = pi4j0.digitalInput().create(1);
        assertEquals(DigitalState.LOW, pin.state());
    }

    @Test
    public void testInputIsOccupied() {
        assertThrows(IllegalStateException.class, () -> pi4j0.digitalInput().create(2));
    }

    @Test
    public void testInputCustomConfig() {
        var config = DigitalInputConfigBuilder.newInstance(pi4j0)
            .address(3)
            .debounce(99L, TimeUnit.MICROSECONDS)
            .pull(PullResistance.PULL_DOWN)
            .build();
        var pin = pi4j0.digitalInput().create(config);
        assertEquals(99, pin.config().debounce());
        assertEquals(3, pin.address());
        assertEquals(PullResistance.PULL_DOWN, pin.pull());
    }

    @Test
    public void testOutputCreate() {
        var pin = pi4j0.digitalOutput().create(4);
        assertEquals(4, pin.address());
    }

    @Test
    public void testOutputChangeState() throws InterruptedException {
        var pin = pi4j0.digitalOutput().create(5);
        pin.state(DigitalState.HIGH);
        assertEquals(DigitalState.HIGH, pin.state());
        pin.state(DigitalState.LOW);
        assertEquals(DigitalState.LOW, pin.state());
    }

    @Test
    public void testOutputCustomConfig() {
        var config = DigitalOutputConfigBuilder.newInstance(pi4j0)
            .address(4)
            .initial(DigitalState.HIGH)
            .build();
        var pin = pi4j0.digitalOutput().create(config);
        assertEquals(DigitalState.HIGH, pin.config().initialState());
        assertEquals(4, pin.address());
    }
}
