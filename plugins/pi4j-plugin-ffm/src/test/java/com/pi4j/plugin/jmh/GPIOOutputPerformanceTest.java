package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

@Fork(value = 1)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class GPIOOutputPerformanceTest {

    private Context pi4j;
    private DigitalOutput pin;

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

        this.pi4j = Pi4J.newContextBuilder().add(new FFMDigitalOutputProviderImpl()).setGpioChipName("gpiochip2").build();
        var config = DigitalOutputConfigBuilder.newInstance(pi4j)
            .bcm(5)
            .build();
        this.pin = pi4j.digitalOutput().create(config);
    }

    @TearDown
    public void tearDown() throws InterruptedException, IOException {
        pi4j.shutdown();
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
    public void testFFMOutputRoundTrip(Blackhole blackhole) {
        pin.state(DigitalState.HIGH);
        pin.state(DigitalState.LOW);
        if (!pin.state().equals(DigitalState.LOW)) {
            throw new RuntimeException("Invalid state");
        }
        blackhole.consume(pin.state());
    }
}
