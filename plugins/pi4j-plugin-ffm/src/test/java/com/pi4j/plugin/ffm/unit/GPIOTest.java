package com.pi4j.plugin.ffm.unit;


import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock;
import com.pi4j.plugin.ffm.mocks.IoctlNativeMock;
import com.pi4j.plugin.ffm.mocks.PollNativeMock;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.lang.foreign.Arena;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class GPIOTest {
    private static Context pi4j0;
    private static Context pi4j1;
    private static Context pi4jNonExistent;

    private static final FileDescriptorNativeMock.FileDescriptorTestData GPIOCHIP_FILE =
        new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, "Test".getBytes());

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
        var lineInfoNonExistent = new IoctlNativeMock.IoctlTestData(LineInfo.class, (_) -> {
            throw new IllegalStateException();
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE); var _ = IoctlNativeMock.echo(lineInfoNonExistent)) {
            assertThrows(IllegalStateException.class, () -> pi4j1.digitalInput().create(99));
        }
    }

    @Test
    public void testInputNonExistent() {
        var lineInfoNonExistent = new IoctlNativeMock.IoctlTestData(LineInfo.class, (_) -> {
            throw new IllegalStateException();
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE); var _ = IoctlNativeMock.echo(lineInfoNonExistent)) {
            assertThrows(IllegalStateException.class, () -> pi4jNonExistent.digitalInput().create(0));
        }
    }

    @Test
    public void testInputCreate() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE); var _ = IoctlNativeMock.echo(lineInfoTestData)) {
            var pin = pi4j0.digitalInput().create(0);
            assertEquals(0, pin.address());
        }
    }

    @Test
    public void testInputState() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE); var _ = IoctlNativeMock.echo(lineInfoTestData)) {
            var pin = pi4j0.digitalInput().create(1);
            assertEquals(DigitalState.LOW, pin.state());
        }
    }

    @Test
    public void testInputEventProcessing() throws InterruptedException {
        var latch = new CountDownLatch(1);
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        var pollingCallback = new Function<InvocationOnMock, PollingData>() {
            @Override
            public PollingData apply(InvocationOnMock answer) {
                PollingData pollingData = answer.getArgument(0);
                return new PollingData(pollingData.fd(), pollingData.events(), (short) PollFlag.POLLIN);
            }
        };
        var pollingFile = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 42, "Test".getBytes(), (answer) -> {
            byte[] buffer = answer.getArgument(1);
            var lineEvent = new LineEvent(1, PinEvent.RISING.getValue(), 3, 4, 5);
            var memoryBuffer = Arena.ofAuto().allocate(LineEvent.LAYOUT);
            try {
                lineEvent.to(memoryBuffer);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            var lineBuffer = new byte[(int) LineEvent.LAYOUT.byteSize()];
            ByteBuffer.wrap(lineBuffer).put(memoryBuffer.asByteBuffer());
            System.arraycopy(lineBuffer, 0, buffer, 0, lineBuffer.length);
            return buffer;
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE, pollingFile);
             var _ = IoctlNativeMock.echo(lineInfoTestData);
             var _ = PollNativeMock.echo(pollingCallback)) {
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
        var lineInfoOccupied = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                lineInfo.offset(), 0,
                PinFlag.USED.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE); var _ = IoctlNativeMock.echo(lineInfoOccupied)) {
            assertThrows(IllegalStateException.class, () -> pi4j0.digitalInput().create(2));
        }
    }

    @Test
    public void testInputCustomConfig() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE); var _ = IoctlNativeMock.echo(lineInfoTestData)) {
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
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                lineInfo.offset(), 0,
                PinFlag.OUTPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE); var _ = IoctlNativeMock.echo(lineInfoTestData)) {
            var pin = pi4j0.digitalOutput().create(4);
            assertEquals(4, pin.address());
        }
    }

    @Test
    public void testOutputChangeState() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                lineInfo.offset(), 0,
                PinFlag.OUTPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE); var _ = IoctlNativeMock.echo(lineInfoTestData)) {
            var pin = pi4j0.digitalOutput().create(5);
            pin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, pin.state());
            pin.state(DigitalState.LOW);
            assertEquals(DigitalState.LOW, pin.state());
        }
    }

}
