package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

@Fork(value = 1, warmups = 1)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class I2CPerformanceTest {

    @Setup
    public void setup() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources/i2c-setup.sh");
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
        var result = setupScript.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
            fail("Failed to setup I2C Test: \n" + errorOutput + "\n" +
                "Probably you need to add the I2C bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
        }
    }

    @TearDown
    public void shutdown() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources/i2c-clean.sh");
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
        var result = setupScript.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
            fail("Failed to cleanup I2C Test: \n" + errorOutput + "\n" +
                "Probably you need to add the I2C bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
        }
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMSMBusRoundTrip() {
        var pi4j = Pi4J.newContextBuilder()
            .add(new I2CFFMProviderImpl())
            .build();
        var i2c = pi4j.i2c().create(1, 0x1C);
        var writeBuffer = new byte[] {0x01, 0x02, 0x03};
        i2c.writeRegister(0xFF, writeBuffer);
        var readBuffer = new byte[3];
        i2c.readRegister(0xFF, readBuffer);
        pi4j.shutdown();
    }
}
