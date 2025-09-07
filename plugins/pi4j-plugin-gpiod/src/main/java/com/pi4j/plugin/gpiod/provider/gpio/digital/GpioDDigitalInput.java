package com.pi4j.plugin.gpiod.provider.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.library.gpiod.internal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * <p>PiGpioDigitalOutput class.</p>
 *
 * @author Alexander Liggesmeyer (<a href="https://alexander.liggesmeyer.net/">https://alexander.liggesmeyer.net/</a>)
 * @version $Id: $Id
 */
public class GpioDDigitalInput extends DigitalInputBase implements DigitalInput {
    private static final Logger logger = LoggerFactory.getLogger(GpioDDigitalInput.class);
    private static final long inputMaxWaitNs = 10 * 1000 * 1000; // 10 ms
    private final GpioLine line;
    private final long debounceNs;
    private CountDownLatch inputListenerRunning;
    private volatile boolean running;
    private Future<?> inputListener;

    /**
     * <p>Constructor for GpioDDigitalInput.</p>
     *
     * @param line     a {@link com.pi4j.library.gpiod.internal.GpioLine} object.
     * @param provider a {@link DigitalInputProvider} object.
     * @param config   a {@link DigitalInputConfig} object.
     */
    public GpioDDigitalInput(GpioLine line, DigitalInputProvider provider, DigitalInputConfig config) {
        super(provider, config);
        this.line = line;
        if (config.getDebounce() == 0) {
            this.debounceNs = 0;
        } else {
            // Convert microseconds to nanoseconds
            this.debounceNs = 1000 * config.getDebounce();
        }
    }

    @Override
    public DigitalInput initialize(Context context) throws InitializeException {
        try {
            if (this.line.getDirection() == LineDirection.OUTPUT)
                GpioDContext.getInstance().closeLine(this.line);

            switch (this.config.getPull()) {
                case PULL_UP:
                    this.line.requestBothEdgeEventsFlags(this.config.getId(), LineRequestFlag.BIAS_PULL_UP.getVal());
                    break;
                case PULL_DOWN:
                    this.line.requestBothEdgeEventsFlags(this.config.getId(), LineRequestFlag.BIAS_PULL_DOWN.getVal());
                    break;
                case OFF:
                    this.line.requestBothEdgeEventsFlags(this.config.getId(), LineRequestFlag.BIAS_DISABLE.getVal());
                    break;
            }
        } catch (GpioDException e) {
            throw new InitializeException("Failed to initialize input " + this.id, e);
        }
        super.initialize(context);

        this.inputListenerRunning = new CountDownLatch(1);
        this.running = true;
        this.inputListener = context.submitTask(this::monitorLineEvents);
        return this;
    }

    @Override
    public DigitalInput shutdown(Context context) throws ShutdownException {
        super.shutdown(context);
        if (this.inputListener != null)
            shutdownInputListener();
        this.line.close();
        return this;
    }

    private void shutdownInputListener() {
        if (this.inputListenerRunning == null || this.inputListenerRunning.getCount() == 0)
            return;
        if (this.inputListener.isDone())
            return;

        this.running = false;
        if (!this.inputListener.cancel(true))
            logger.error("Failed to cancel input listener!");

        try {
            if (!this.inputListenerRunning.await(5, TimeUnit.SECONDS)) {
                throw new IllegalArgumentException("Input listener didn't stop in 5s");
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted, while waiting for input listener to stop");
        }

        logger.info("Shutdown input listener for {}", this.id);
    }

    @Override
    public DigitalState state() {
        return DigitalState.getState(this.line.getValue());
    }

    private void monitorLineEvents() {
        GpioDContext gpioDContext = GpioDContext.getInstance();
        DigitalState lastState = null;
        GpioLineEvent lineEvent = GpioDContext.getInstance().openLineEvent();

        try {
            while (this.running) {
                long debounceNs = this.debounceNs;
                // We have to use this function before calling eventRead() directly, since native methods can't be interrupted.
                // eventRead() is blocking and prevents thread interrupt while running
                while (!this.line.eventWait(inputMaxWaitNs)) {
                    if (!this.running)
                        return;
                }

                this.line.eventRead(lineEvent);
                long currentTime = System.nanoTime();

                // Perform debouncing
                // If the event is too new to be sure that it is debounced then ...
                while (lineEvent.getTimeNs() + debounceNs >= currentTime) {
                    if (!this.running)
                        return;

                    // ... wait for remaining debounce time and watch out for new event(s)
                    if (this.line.eventWait(
                        Math.min(inputMaxWaitNs, lineEvent.getTimeNs() + debounceNs - currentTime))) {
                        // Repeat if a second event occurred withing debounce interval
                        this.line.eventRead(lineEvent);
                    }

                    currentTime = System.nanoTime();
                }

                // Apply event only if the new state is not the same as the last state.
                DigitalState newState = DigitalState.getState(lineEvent.getType() == LineEvent.RISING_EDGE);
                if (lastState != newState) {
                    lastState = newState;
                    this.dispatch(new DigitalStateChangeEvent<>(this, newState));
                }
            }
        } finally {
            if (lineEvent != null)
                gpioDContext.closeLineEvent(lineEvent);
            // plain read is safe, guaranteed to see the initialised value because submitting an executor task is a memory barrier
            this.inputListenerRunning.countDown();
        }
    }
}
