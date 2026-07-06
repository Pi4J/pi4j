package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.digital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * https://github.com/Pi4J/pi4j/issues/622
 * <p>
 * In V4.0.0, the FFM plugin uses Virtual Threads to listen for input events.
 * But because native calls get "pinned", only the first four inputs work as they get linked to a CPU core.
 * This test creates 5 inputs and checks if the fifth receives input events.
 */
public class FiveDigitalInputsTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(FiveDigitalInputsTestCase.class);

    private static final String TEST_NAME = "Event on Fifth Digital Input";
    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(2);

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Fifth Digital Input test, expecting an event on the fifth input");

        DigitalOutput gpioOutControl = null;
        DataInGpioListener input1 = null;
        DataInGpioListener input2 = null;
        DataInGpioListener input3 = null;
        DataInGpioListener input4 = null;
        DataInGpioListener gpioInTest = null;

        try {
            // Initialize output
            gpioOutControl = createDigitalOutput(providerContext.getContext(), 26, DigitalState.LOW, DigitalState.LOW);
            Thread.sleep(100);
            if (gpioOutControl.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output has not the correct initial state");
            }

            // Initialize 4 inputs to fill up the available cores (4 on a Raspberry Pi 5)
            input1 = createInputListener(providerContext, 5);
            input2 = createInputListener(providerContext, 6);
            input3 = createInputListener(providerContext, 13);
            input4 = createInputListener(providerContext, 19);
            Thread.sleep(100);

            // Initialize 5th input, to validate a future fix
            gpioInTest = createInputListener(providerContext, 16);
            Thread.sleep(100);
            if (gpioInTest.getEvent() != null) {
                return new TestResult(TEST_NAME, false, "Input listener event should be null");
            }

            // Ignore any stale event that might have been emitted during startup.
            input1.clearEvent();
            input2.clearEvent();
            input3.clearEvent();
            input4.clearEvent();
            gpioInTest.clearEvent();

            // Change the output
            gpioOutControl.high();

            // Event delivery is asynchronous; wait with timeout to avoid race conditions.
            if (gpioInTest.awaitEvent(EVENT_TIMEOUT)) {
                return new TestResult(TEST_NAME, true, "The fifth listener received an event as expected");
            } else {
                return new TestResult(TEST_NAME, false, "The fifth listener did not receive an event before timeout " + EVENT_TIMEOUT);
            }
        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        } finally {
            if (gpioOutControl != null) {
                gpioOutControl.close();
            }
            if (gpioInTest != null) {
                gpioInTest.close();
            }
            if (input1 != null) {
                input1.close();
            }
            if (input2 != null) {
                input2.close();
            }
            if (input3 != null) {
                input3.close();
            }
            if (input4 != null) {
                input4.close();
            }
        }
    }

    private static DataInGpioListener createInputListener(ProviderContext providerContext, int bcm) {
        var input = createDigitalInput(providerContext.getContext(), bcm, PullResistance.PULL_DOWN, 0);
        var listener = new DataInGpioListener(input);
        input.addListener(listener);
        return listener;
    }

    private static class DataInGpioListener implements DigitalStateChangeListener {
        private final DigitalInput input;
        private DigitalStateChangeEvent event = null;

        public DataInGpioListener(DigitalInput input) {
            this.input = input;
        }

        @Override
        public synchronized void onDigitalStateChange(DigitalStateChangeEvent event) {
            this.event = event;
            notifyAll();
        }

        public synchronized DigitalStateChangeEvent getEvent() {
            return event;
        }

        public synchronized void clearEvent() {
            event = null;
        }

        public synchronized boolean awaitEvent(Duration timeout) throws InterruptedException {
            if (event != null) {
                return true;
            }

            final long timeoutNanos = timeout.toNanos();
            final long deadline = System.nanoTime() + timeoutNanos;
            long remainingNanos = timeoutNanos;

            while (event == null && remainingNanos > 0) {
                long millis = remainingNanos / 1_000_000;
                int nanos = (int) (remainingNanos % 1_000_000);
                wait(millis, nanos);
                remainingNanos = deadline - System.nanoTime();
            }

            return event != null;
        }

        public void close() {
            if (input != null) {
                input.close();
            }
        }
    }
}
