package com.pi4j.plugin.ffm.unit;


import com.pi4j.Pi4J;
import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.plugin.ffm.api.Pi4JApi;
import com.pi4j.plugin.ffm.api.RaspberryPi;
import com.pi4j.plugin.ffm.common.PermissionHelper;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import com.pi4j.plugin.ffm.mocks.*;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
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

    private static final MockedStatic<PermissionHelper> permissionHelperMock = PermissionHelperMock.echo();

    @BeforeAll
    public static void setup() {
        pi4j0 = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl(), new DigitalOutputFFMProviderImpl())
            .build();
        pi4j1 = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl())
            .build();
        pi4jNonExistent = Pi4J.newContextBuilder()
            .add(new DigitalInputFFMProviderImpl())
            .build();
    }

    @AfterAll
    public static void teardown() {
        pi4j0.shutdown();
        pi4j1.shutdown();
        pi4jNonExistent.shutdown();
        permissionHelperMock.close();
    }


    @Test
    public void testInputUnavailable() {
        var lineInfoNonExistent = new IoctlNativeMock.IoctlTestData(LineInfo.class, (_) -> {
            throw new IllegalStateException();
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoNonExistent)) {

            var builder = DigitalInputConfigBuilder.newInstance(pi4j1).bus(-1)
                .pin(99).build();
            assertThrows(IllegalStateException.class, () -> pi4j1.digitalInput().create(builder));
        }
    }

    @Test
    public void testInputNonExistent() {
        var lineInfoNonExistent = new IoctlNativeMock.IoctlTestData(LineInfo.class, (_) -> {
            throw new IllegalStateException();
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoNonExistent)) {

            var builder = DigitalInputConfigBuilder.newInstance(pi4jNonExistent).busNumber(-1)
                .pin(0).build();
            assertThrows(IllegalStateException.class, () -> pi4jNonExistent.digitalInput().create(builder));
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
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoTestData)) {

            var builder = DigitalInputConfigBuilder.newInstance(pi4j0).bus(-1)
                .pin(0).build();
            var pin = pi4j0.digitalInput().create(builder);
            assertEquals(0, pin.pin());
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
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoTestData)) {

            var builder = DigitalInputConfigBuilder.newInstance(pi4j0).bus(-1)
                .pin(1).build();
            var pin = pi4j0.digitalInput().create(builder);
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

            var builder = DigitalInputConfigBuilder.newInstance(pi4j0).bus(-1)
                .pin(7).build();
            var pin = pi4j0.digitalInput().create(builder);
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
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoOccupied)) {

            var builder = DigitalInputConfigBuilder.newInstance(pi4j0).bus(-1)
                .pin(2).build();
            assertThrows(IllegalStateException.class, () -> pi4j0.digitalInput().create(builder));
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
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoTestData)) {

            var config = DigitalInputConfigBuilder.newInstance(pi4j0)
                .bus(-1)
                .pin(3)
                .debounce(99L, TimeUnit.MICROSECONDS)
                .pull(PullResistance.PULL_DOWN)
                .build();
            var pin = pi4j0.digitalInput().create(config);
            assertEquals(99, pin.config().debounce());
            assertEquals(3, pin.pin());
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
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoTestData)) {

            var builder = DigitalOutputConfigBuilder.newInstance(pi4j0).bus(-1)
                .pin(4).build();
            var pin = pi4j0.digitalOutput().create(builder);
            assertEquals(4, pin.pin());
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
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoTestData)) {

            var builder = DigitalOutputConfigBuilder.newInstance(pi4j0).bus(-1)
                .pin(5).build();
            var pin = pi4j0.digitalOutput().create(builder);
            pin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, pin.state());
            pin.state(DigitalState.LOW);
            assertEquals(DigitalState.LOW, pin.state());
        }
    }

    @Test
    public void testApi() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.echo(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.echo(lineInfoTestData);
             var _ = BoardInfoMock.echo(BoardModel.MODEL_4_B)) {

            BoardInfoHelper.reinitialize();
            var mockingBoard = Pi4JApi.board(RaspberryPi.Model4B.class);
            var pin = mockingBoard.input(5);
            assertEquals(5, pin.pin());
        }
    }

}
