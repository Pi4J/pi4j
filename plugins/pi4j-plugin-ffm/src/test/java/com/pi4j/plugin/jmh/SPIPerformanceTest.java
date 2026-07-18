package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.BaseSetup;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

@Fork(value = 1)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SPIPerformanceTest extends BaseSetup {

    private Context pi4j;
    private Spi spi;

    @Setup
    public void setup() throws InterruptedException, IOException {
        setup("spi");
        this.pi4j = Pi4J.newContextBuilder()
            .add(new FFMSpiProviderImpl())
            .build();
        var config = SpiConfigBuilder.newInstance()
            .bus(SpiBus.BUS_6)
            .channel(0)
            .mode(0)
            .baud(50_000)
            .build();
        this.spi = pi4j.spi().create(config);
    }

    @TearDown
    public void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();
        tearDown("spi");
    }


    private static final Random random = new Random();
    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMWriteReadRoundTrip() {
        var str = String.valueOf(random.nextInt(1, 1024));
        var writeBuffer = str.getBytes();
        var readBuffer = new byte[str.length()];
        spi.transfer(writeBuffer, readBuffer);
        if (!Arrays.equals(readBuffer, writeBuffer)) {
            throw new RuntimeException("Read buffer mismatch: read[" + Arrays.toString(readBuffer) + "], write[" + Arrays.toString(writeBuffer) + "]");
        }
    }
}
