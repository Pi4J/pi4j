package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

@Fork(value = 1)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class I2CSMBusPerformanceTest {

    private Context pi4j;
    private I2C i2c;

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
        this.pi4j = Pi4J.newContextBuilder()
            .add(new FFMI2CProviderImpl())
            .build();
        this.i2c = pi4j.i2c().create(I2CConfigBuilder
            .newInstance(pi4j)
            .bus(99)
            .device(0x1C)
            .i2cImplementation(I2CImplementation.SMBUS));
    }

    @TearDown
    public void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();
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
        var writeBuffer = new byte[]{0x01, 0x02, 0x03};
        i2c.writeRegister(0xFF, writeBuffer);
        var readBuffer = new byte[3];
        i2c.readRegister(0xFF, readBuffer);
        if (!Arrays.equals(readBuffer, writeBuffer)) {
            throw new RuntimeException("Read buffer mismatch: read[" + Arrays.toString(readBuffer) + "]," +
                " write[" + Arrays.toString(writeBuffer) + "]");
        }
    }
}
