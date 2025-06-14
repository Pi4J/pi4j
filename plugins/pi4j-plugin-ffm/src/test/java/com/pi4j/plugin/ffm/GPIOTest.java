package com.pi4j.plugin.ffm;


import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.pi4j.plugin.ffm.MockHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class GPIOTest {
    private static Context pi4j0;
    private static Context pi4j1;
    private static Context pi4jNonExistent;

    @BeforeAll
    public static void setup() {
        pi4j0 = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl(), new DigitalOutputFFMProviderImpl())
            // set gpio chip name to null for testing purpose (we need any available device in the system)
            .setGpioChipName("null")
            .build();
        pi4j1 = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl())
            // set gpio chip name to null for testing purpose (we need any available device in the system)
            .setGpioChipName("null")
            .build();
        pi4jNonExistent = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl())
            .setGpioChipName("gpiochip99")
            .build();
    }

    @AfterAll
    public static void teardown() {
        pi4j0.shutdown();
        pi4j1.shutdown();
        pi4jNonExistent.shutdown();
    }


    @Test
    public void testInputUnavailable() {
        try (var _ = createFileMock(); var _ = createIoctlMock()) {
            assertThrows(IllegalStateException.class, () -> pi4j1.digitalInput().create(99));
        }
    }

    @Test
    public void testInputNonExistent() {
        try (var _ = createFileMock(); var _ = createIoctlMock()) {
            assertThrows(IllegalStateException.class, () -> pi4jNonExistent.digitalInput().create(0));
        }
    }

    @Test
    public void testInputCreate() {
        try (var _ = createFileMock(); var _ = createIoctlMock()) {
            var pin = pi4j0.digitalInput().create(0);
            assertEquals(0, pin.address());
        }
    }

    @Test
    public void testInputState() {
        try (var _ = createFileMock(); var _ = createIoctlMock()) {
            var pin = pi4j0.digitalInput().create(1);
            assertEquals(DigitalState.LOW, pin.state());
        }
    }


    @Test
    public void testInputEventProcessing() throws InterruptedException {
        var latch = new CountDownLatch(1);
        try (var _ = createDigitalInputFileMock(); var _ = createIoctlMock(); var _ = createPollMock()) {
            var pin = pi4j0.digitalInput().create(7);
            assertEquals(DigitalState.LOW, pin.state());
            var passed = new AtomicBoolean(false);
            pin.addListener(event -> {
                passed.set(event.state() == DigitalState.HIGH);
                latch.countDown();
            });
            assertTrue(latch.await(5, TimeUnit.SECONDS));
            assertTrue(passed.get());
        }
    }


    @Test
    public void testInputIsOccupied() {
        try (var _ = createFileMock(); var _ = createIoctlMock()) {
            assertThrows(IllegalStateException.class, () -> pi4j0.digitalInput().create(2));
        }
    }

    @Test
    public void testInputCustomConfig() {
        try (var _ = createFileMock(); var _ = createIoctlMock()) {
            var config = DigitalInputConfigBuilder.newInstance(pi4j0)
                .address(3)
                .debounce(99L, TimeUnit.MICROSECONDS)
                .pull(PullResistance.PULL_DOWN)
                .build();
            var pin = pi4j0.digitalInput().create(config);
            assertEquals(99, pin.config().debounce());
            assertEquals(3, pin.address());
            assertEquals(PullResistance.PULL_DOWN, pin.pull());
        }
    }

    @Test
    public void testOutputCreate() {
        try (var _ = createFileMock(); var _ = createIoctlMock()) {
            var pin = pi4j0.digitalOutput().create(4);
            assertEquals(4, pin.address());
        }
    }

    @Test
    public void testOutputChangeState() {
        try (var _ = createFileMock(); var _ = createIoctlMock()) {
            var pin = pi4j0.digitalOutput().create(5);
            pin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, pin.state());
            pin.state(DigitalState.LOW);
            assertEquals(DigitalState.LOW, pin.state());
        }
    }

}
