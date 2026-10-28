package com.pi4j.plugin.ffm.providers.parallel;

import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalBase;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputBase;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;

import java.util.function.Consumer;

public class ParallelDigitalOutput extends DigitalOutputBase implements DigitalOutput {

    private final ParallelPort parallelPort;

    public ParallelDigitalOutput(DigitalOutputConfig config, ParallelPort parallelPort, Consumer<DigitalOutput> onClose) {
        super(config, onClose);
        this.parallelPort = parallelPort;
    }

    @Override
    public void applyInitialState() {
        super.applyInitialState();
        parallelPort.applyInitialState();
    }

    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        parallelPort.write(state == DigitalState.HIGH ? 1 << config.bcm() : 0);
        return this;
    }


    @Override
    public DigitalState state() {
        return (parallelPort.read() & 1 << config.bcm()) != 0 ? DigitalState.HIGH : DigitalState.LOW;
    }

    @Override
    public void close() {
        parallelPort.close();
    }
}
