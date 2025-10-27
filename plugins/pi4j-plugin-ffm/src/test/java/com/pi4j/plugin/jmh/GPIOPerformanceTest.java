package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
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
        var scriptPath = Paths.get("src/test/resources/gpio-setup.sh");
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
        var result = setupScript.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            fail("Failed to setup GPIO Test. Probably you need to add the GPIO Simulator bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
        }

    }

    @TearDown
    public void tearDown() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources/gpio-clean.sh");
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
        var result = setupScript.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            fail("Failed to cleanup GPIO Test. Probably you need to add the GPIO Simulator bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
        }
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMInputRoundTrip() {
        var pi4j = Pi4J.newContextBuilder().add(new DigitalInputFFMProviderImpl()).setGpioChipName("gpiochip0").build();
        var config = DigitalInputConfigBuilder.newInstance(pi4j)
            .address(0)
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
        var pi4j = Pi4J.newContextBuilder().add(new DigitalInputFFMProviderImpl()).setGpioChipName("gpiochip0").build();
        var config = DigitalInputConfigBuilder.newInstance(pi4j)
            .address(0)
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
        var pi4j = Pi4J.newContextBuilder().add(new DigitalOutputFFMProviderImpl()).setGpioChipName("gpiochip0").build();
        var config = DigitalOutputConfigBuilder.newInstance(pi4j)
            .address(0)
            .build();
        var pin = pi4j.digitalOutput().create(config);
        pin.state();
        pi4j.shutdown();
    }

//    @Benchmark
//    @Warmup(iterations = 3)
//    public void testLinuxFsInputRoundTrip() {
//        var pi4j = Pi4J.newContextBuilder().add(new LinuxFsDigitalInputProviderImpl("/sys/class/gpio/")).setGpioChipName("gpiochip0").build();
//        var config = DigitalInputConfigBuilder.newInstance(pi4j)
//            .address(512)
//            .debounce(99L, TimeUnit.MICROSECONDS)
//            .pull(PullResistance.PULL_DOWN)
//            .build();
//        var pin = pi4j.digitalInput().create(config);
//        pin.state();
//        pi4j.shutdown();
//    }
//
//    @Benchmark
//    @Warmup(iterations = 3)
//    public void testLinuxFsInputWithListenerRoundTrip() {
//        var pi4j = Pi4J.newContextBuilder().add(new LinuxFsDigitalInputProviderImpl("/sys/class/gpio/")).setGpioChipName("gpiochip0").build();
//        var config = DigitalInputConfigBuilder.newInstance(pi4j)
//            .address(512)
//            .debounce(99L, TimeUnit.MICROSECONDS)
//            .pull(PullResistance.PULL_DOWN)
//            .build();
//        var pin = pi4j.digitalInput().create(config);
//        pin.addListener(DigitalStateChangeEvent::state);
//        pin.state();
//        pi4j.shutdown();
//    }
//
//    @Benchmark
//    @Warmup(iterations = 3)
//    public void testLinuxFsOutputRoundTrip() {
//        var pi4j = Pi4J.newContextBuilder().add(new LinuxFsDigitalOutputProviderImpl("/sys/class/gpio/")).setGpioChipName("gpiochip0").build();
//        var config = DigitalOutputConfigBuilder.newInstance(pi4j)
//            .address(512)
//            .build();
//        var pin = pi4j.digitalOutput().create(config);
//        pin.state();
//        pi4j.shutdown();
//    }
//
//    @Benchmark
//    @Warmup(iterations = 3)
//    public void testGpioDInputRoundTrip() throws InterruptedException {
//        var pi4j = Pi4J.newContextBuilder().add(new GpioDDigitalInputProviderImpl()).setGpioChipName("gpiochip0").build();
//        var config = DigitalInputConfigBuilder.newInstance(pi4j)
//            .address(0)
//            .debounce(99L, TimeUnit.MICROSECONDS)
//            .pull(PullResistance.PULL_DOWN)
//            .build();
//        var pin = pi4j.create(config);
//        pin.state();
//        pi4j.shutdown();
//    }
//
//    @Benchmark
//    @Warmup(iterations = 3)
//    public void testGpioDInputWithListenerRoundTrip() {
//        var pi4j = Pi4J.newContextBuilder().add(new GpioDDigitalInputProviderImpl()).setGpioChipName("gpiochip0").build();
//        var config = DigitalInputConfigBuilder.newInstance(pi4j)
//            .address(0)
//            .debounce(99L, TimeUnit.MICROSECONDS)
//            .pull(PullResistance.PULL_DOWN)
//            .build();
//        var pin = pi4j.create(config);
//        pin.addListener(DigitalStateChangeEvent::state);
//        pin.state();
//        pi4j.shutdown();
//    }
//
//    @Benchmark
//    @Warmup(iterations = 3)
//    public void testGpioDOutputRoundTrip() {
//        var pi4j = Pi4J.newContextBuilder().add(new GpioDDigitalOutputProviderImpl()).setGpioChipName("gpiochip0").build();
//        var config = DigitalOutputConfigBuilder.newInstance(pi4j)
//            .address(0)
//            .build();
//        var pin = pi4j.create(config);
//        pin.state();
//        pi4j.shutdown();
//    }
}
