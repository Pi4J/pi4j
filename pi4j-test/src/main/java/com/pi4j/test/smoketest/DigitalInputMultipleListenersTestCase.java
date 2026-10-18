package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.digital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DigitalInputMultipleListenersTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(DigitalInputMultipleListenersTestCase.class);

    private static final String TEST_NAME = "Multiple Digital Listeners";

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting " + TEST_NAME + " test");

        try(DigitalOutput gpioOutTest = createDigitalOutput(providerContext.getContext(), 22, DigitalState.LOW, DigitalState.LOW);
            DigitalInput gpioInMonitor = createDigitalInput(providerContext.getContext(), 27, PullResistance.PULL_DOWN, 0)
        ) {
            if (gpioOutTest.state() != DigitalState.LOW) {
                return new TestResult(TEST_NAME, false, "Output has not the correct initial state");
            }

            // expect 4 events on 2 listeners, count 8 in total
            var latch = new CountDownLatch(8);

            var listener1 = new DigitalInputMultipleListenersTestCase.DataInGpioListener(latch::countDown);
            gpioInMonitor.addListener(listener1);

            var listener2 = new DigitalInputMultipleListenersTestCase.DataInGpioListener(latch::countDown);
            gpioInMonitor.addListener(listener2);

            gpioOutTest.setState(1);
            gpioOutTest.setState(0);
            gpioOutTest.setState(1);
            gpioOutTest.setState(0);

            // Assume timeout indicates that not all events were triggered
            if (!latch.await(500, TimeUnit.MILLISECONDS)) {
                return new TestResult(TEST_NAME, false, "Timed out waiting for all expected events");
            }

            if (listener1.counterLow.get() != listener2.counterLow.get()) {
                return new TestResult(TEST_NAME, false, "Mismatched falling events");
            }

            if (listener1.counterHigh.get() != listener2.counterHigh.get()) {
                return new TestResult(TEST_NAME, false, "Mismatched rising events");
            }

            gpioInMonitor.removeListener(listener1);
            gpioInMonitor.removeListener(listener2);

            // allow event watcher to terminate after removing listeners
            Thread.sleep(500);

            return new TestResult(TEST_NAME, true, "All events were received");
        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        }
    }

    // Counts the changes in the input to low and high
    private static class DataInGpioListener implements DigitalStateChangeListener {
        final AtomicInteger counterHigh = new AtomicInteger(0);
        final AtomicInteger counterLow = new AtomicInteger(0);
        private final Runnable onEvent;

        public DataInGpioListener(Runnable onEvent) {
            this.onEvent = onEvent;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.HIGH) {
                counterHigh.getAndIncrement();
            }
            if (event.state() == DigitalState.LOW) {
                counterLow.getAndIncrement();
            }
            onEvent.run();
        }
    }
}
