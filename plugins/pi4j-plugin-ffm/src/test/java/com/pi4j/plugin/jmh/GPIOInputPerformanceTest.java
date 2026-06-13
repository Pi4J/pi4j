package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
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
public class GPIOInputPerformanceTest {

    private Context pi4j;
    private DigitalInput pin;

    @Setup(Level.Trial)
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

        this.pi4j = Pi4J.newContextBuilder().add(new FFMDigitalInputProviderImpl()).setGpioChipName("gpiochip2").build();
        var config = DigitalInputConfigBuilder.newInstance(pi4j)
            .bcm(3)
            .debounce(99L, TimeUnit.MICROSECONDS)
            .pull(PullResistance.PULL_DOWN)
            .build();
        this.pin = pi4j.digitalInput().create(config);
    }

    @TearDown(Level.Trial)
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

//    @Benchmark
//    @Warmup(iterations = 3)
//    public void testFFMInputRoundTrip(Blackhole blackhole) {
//        blackhole.consume(pin.state());
//    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMInputWithListenerRoundTrip(Blackhole blackhole) {
        var listener = new DigitalStateChangeListener() {
            @Override
            public void onDigitalStateChange(DigitalStateChangeEvent event) {
                //blackhole.consume(event);
            }
        };
        pin.addListener(listener);
        blackhole.consume(pin.state());
        pin.removeListener(listener);
    }
}
