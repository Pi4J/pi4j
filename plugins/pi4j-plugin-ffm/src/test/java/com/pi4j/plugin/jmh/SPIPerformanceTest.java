package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.ffm.providers.spi.SpiFFMProviderImpl;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

@Fork(value = 1, warmups = 1)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SPIPerformanceTest {

    @Setup
    public static void setup() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources/spi-setup.sh");
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
        var result = setupScript.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
            var o = new String(setupScript.getInputStream().readAllBytes());
            fail(o + "\n" + "Failed to setup SPI Test: \n" + errorOutput + "\n" +
                "Probably you need to add the SPI bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
        }


    }

    @TearDown
    public static void shutdown() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources/spi-clean.sh");
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
        var result = setupScript.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
            fail("Failed to cleanup SPI Test: \n" + errorOutput + "\n" +
                "Probably you need to add the SPI bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
        }
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMWriteReadRoundTrip() {
        var pi4j = Pi4J.newContextBuilder()
            .add(new SpiFFMProviderImpl())
            .build();
        var config = SpiConfigBuilder.newInstance(pi4j).bus(SpiBus.BUS_0).address(0).mode(0).baud(50_000).build();
        var spi = pi4j.spi().create(config);
        spi.write("Test".getBytes());
        var buffer = new byte[4];
        spi.read(buffer);
        pi4j.shutdown();
    }

//    @Benchmark
//    @Warmup(iterations = 3)
//    public void testLinuxFsWriteReadRoundTrip() {
//        var pi4j = Pi4J.newContextBuilder()
//            .add(new LinuxFsSpiProviderImpl())
//            .build();
//        var config = SpiConfigBuilder.newInstance(pi4j).bus(SpiBus.BUS_0).address(0).mode(0).baud(50_000).build();
//        var spi = pi4j.spi().create(config);
//        spi.write("Test".getBytes());
//        var buffer = new byte[4];
//        spi.read(buffer);
//        pi4j.shutdown();
//    }
}
