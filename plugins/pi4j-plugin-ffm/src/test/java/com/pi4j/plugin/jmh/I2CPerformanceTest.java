package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
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
        var scriptPath = Paths.get("src/test/resources/").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.getAbsolutePath() + "/i2c-setup.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to setup I2C Test: \n" + errorOutput + "\n" +
                "Probably you need to add the I2C bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }
    }

    @TearDown
    public void shutdown() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources/").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.getAbsolutePath() + "/i2c-clean.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to cleanup I2C Test: \n" + errorOutput + "\n" +
                "Probably you need to add the I2C bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testSMBusRoundTrip() {
        var pi4j = Pi4J.newContextBuilder()
            .add(new FFMI2CProviderImpl())
            .build();
        var i2c = pi4j.i2c().create(I2CConfigBuilder
            .newInstance(pi4j)
            .bus(99)
            .device(0x1C)
            .i2cImplementation(I2CImplementation.SMBUS));
        var writeBuffer = new byte[]{0x01, 0x02, 0x03};
        i2c.writeRegister(0xFF, writeBuffer);
        var readBuffer = new byte[3];
        i2c.readRegister(0xFF, readBuffer);
        pi4j.shutdown();
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testI2CDirectRoundTrip() {
        var pi4j = Pi4J.newContextBuilder()
            .add(new FFMI2CProviderImpl())
            .build();
        var i2c = pi4j.i2c().create(I2CConfigBuilder
            .newInstance(pi4j)
            .bus(99)
            .device(0x1C)
            .i2cImplementation(I2CImplementation.DIRECT));
        var writeBuffer = new byte[]{0x01, 0x02, 0x03};
        i2c.writeRegister(0xFF, writeBuffer);
        var readBuffer = new byte[3];
        i2c.readRegister(0xFF, readBuffer);
        pi4j.shutdown();
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testI2CFileRoundTrip() {
        var pi4j = Pi4J.newContextBuilder()
            .add(new FFMI2CProviderImpl())
            .build();
        var i2c = pi4j.i2c().create(I2CConfigBuilder
            .newInstance(pi4j)
            .bus(99)
            .device(0x1C)
            .i2cImplementation(I2CImplementation.FILE));
        var writeBuffer = new byte[]{0x01, 0x02, 0x03};
        i2c.writeRegister(0xFF, writeBuffer);
        var readBuffer = new byte[3];
        i2c.readRegister(0xFF, readBuffer);
        pi4j.shutdown();
    }
}
