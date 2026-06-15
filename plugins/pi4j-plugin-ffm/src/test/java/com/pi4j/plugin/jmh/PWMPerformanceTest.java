package com.pi4j.plugin.jmh;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.BaseSetup;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

@Fork(value = 1)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class PWMPerformanceTest extends BaseSetup {

    private Context pi4j;
    private Pwm pwm;

    @Setup
    public void setup() throws InterruptedException, IOException {
        setup("pwm");
        this.pi4j = Pi4J.newContextBuilder().add(new FFMPwmProviderImpl()).build();
        var config = PwmConfigBuilder.newInstance(pi4j)
            .pwmType(PwmType.HARDWARE)
            .chip(0)
            .channel(0)
            .build();
        this.pwm = pi4j.pwm().create(config);
    }

    @TearDown
    public void tearDown() throws InterruptedException, IOException {
        pi4j.shutdown();
        tearDown("pwm");
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testPWMRoundTrip() {
        pwm.setFrequency(500);
        pwm.setDutyCycle(5);
        pwm.on();
        pwm.off();
        pwm.on();
        pwm.setFrequency(10_000);
        pwm.setDutyCycle(10);
        pwm.off();
    }
}
