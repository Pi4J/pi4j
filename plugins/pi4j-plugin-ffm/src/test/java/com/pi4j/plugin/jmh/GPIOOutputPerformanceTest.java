package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.BaseSetup;
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
public class GPIOOutputPerformanceTest extends BaseSetup {

    private Context pi4j;
    private DigitalOutput pin;

    @Setup
    public void setup() throws InterruptedException, IOException {
        setup("gpio");
        this.pi4j = Pi4J.newContextBuilder().add(new FFMDigitalOutputProviderImpl()).build();
        var config = DigitalOutputConfigBuilder.newInstance()
            .bus(97)
            .bcm(5)
            .build();
        this.pin = pi4j.digitalOutput().create(config);
    }

    @TearDown
    public void tearDown() throws InterruptedException, IOException {
        pi4j.shutdown();
        tearDown("gpio");
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
