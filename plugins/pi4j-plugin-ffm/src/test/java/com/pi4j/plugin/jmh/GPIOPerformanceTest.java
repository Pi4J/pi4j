package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

@Fork(value = 1, warmups = 1)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GPIOPerformanceTest {

    @Setup
    public void setup() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath + "/gpio-setup.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to setup GPIO Test:\n" + errorOutput + "\n" +
                "Probably you need to add the GPIO Simulator bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }
    }

    @TearDown
    public void tearDown() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath + "/gpio-clean.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to setup GPIO Test:\n" + errorOutput + "\n" +
                "Probably you need to add the GPIO Simulator bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMInputRoundTrip() {
        var pi4j = Pi4J.newContextBuilder().add(new FFMDigitalInputProviderImpl()).setGpioChipName("gpiochip0").build();
        var config = DigitalInputConfigBuilder.newInstance(pi4j)
            .bcm(0)
            .debounce(99L, TimeUnit.MICROSECONDS)
            .pull(PullResistance.PULL_DOWN)
            .build();
        var pin = pi4j.digitalInput().create(config);
        pin.state();
        pi4j.shutdown();
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMInputWithListenerRoundTrip() {
        var pi4j = Pi4J.newContextBuilder().add(new FFMDigitalInputProviderImpl()).setGpioChipName("gpiochip0").build();
        var config = DigitalInputConfigBuilder.newInstance(pi4j)
            .bcm(0)
            .debounce(99L, TimeUnit.MICROSECONDS)
            .pull(PullResistance.PULL_DOWN)
            .build();
        var pin = pi4j.digitalInput().create(config);
        pin.addListener(DigitalStateChangeEvent::state);
        pin.state();
        pi4j.shutdown();
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMOutputRoundTrip() {
        var pi4j = Pi4J.newContextBuilder().add(new FFMDigitalOutputProviderImpl()).setGpioChipName("gpiochip0").build();
        var config = DigitalOutputConfigBuilder.newInstance(pi4j)
            .bcm(0)
            .build();
        var pin = pi4j.digitalOutput().create(config);
        pin.state();
        pi4j.shutdown();
    }
}
