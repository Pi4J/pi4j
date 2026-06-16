package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.plugin.BaseSetup;
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
public class I2CDirectPerformanceTest extends BaseSetup {

    private Context pi4j;
    private I2C i2c;

    @Setup
    public void setup() throws InterruptedException, IOException {
        setup("i2c");
        this.pi4j = Pi4J.newContextBuilder()
            .add(new FFMI2CProviderImpl())
            .build();
        this.i2c = pi4j.i2c().create(I2CConfigBuilder.newInstance()
            .bus(99)
            .device(0x1C)
            .i2cImplementation(I2CImplementation.DIRECT));
    }

    @TearDown
    public void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();
        tearDown("i2c");
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testI2CDirectRoundTrip() {
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
