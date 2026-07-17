package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.BaseSetup;
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
public class GPIOInputPerformanceTest extends BaseSetup {

    private Context pi4j;
    private DigitalInput pin;

    @Setup(Level.Trial)
    public void setup() throws InterruptedException, IOException {
        setup("gpio");

        this.pi4j = Pi4J.newContextBuilder().add(new FFMDigitalInputProviderImpl()).build();
        var config = DigitalInputConfigBuilder.newInstance()
            .bus(97)
            .bcm(3)
            .debounce(99L, TimeUnit.MICROSECONDS)
            .pull(PullResistance.PULL_DOWN)
            .build();
        this.pin = pi4j.digitalInput().create(config);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws InterruptedException, IOException {
        pi4j.shutdown();
        tearDown("gpio");
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFFMInputRoundTrip(Blackhole blackhole) {
        blackhole.consume(pin.state());
    }

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
