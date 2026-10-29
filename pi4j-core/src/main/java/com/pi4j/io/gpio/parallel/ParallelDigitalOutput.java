package com.pi4j.io.gpio.parallel;

import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputBase;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;

import java.util.function.Consumer;

/**
 * {@link DigitalOutput} which delegates to a {@link ParallelPort} to perform IO
 */
public class ParallelDigitalOutput extends DigitalOutputBase implements DigitalOutput {

    private final ParallelPort parallelPort;

    /**
     * Create a digital output bound to the specified port
     * @param config the digital configuration
     * @param parallelPort the delegate port
     * @param onClose a callback to be invoked on close
     */
    public ParallelDigitalOutput(DigitalOutputConfig config, ParallelPort parallelPort, Consumer<DigitalOutput> onClose) {
        super(config, onClose);
        this.parallelPort = parallelPort;
    }

    @Override
    public void applyInitialState() {
        parallelPort.applyInitialState();
        super.applyInitialState();
    }

    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        parallelPort.write(state == DigitalState.HIGH ? 1 : 0);
        return this;
    }


    @Override
    public DigitalState state() {
        return parallelPort.read() != 0 ? DigitalState.HIGH : DigitalState.LOW;
    }

    @Override
    public void close() {
        super.close();
        parallelPort.close();
    }
}
