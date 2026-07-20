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
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.gpio.structs.LineRequest;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import com.pi4j.plugin.ffm.mocks.*;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;

import java.lang.foreign.Arena;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class GPIOTest {
    private static Context pi4j0;
    private static Context pi4j1;
    private static Context pi4jNonExistent;

    private static final FileDescriptorNativeMock.FileDescriptorTestData GPIOCHIP_FILE =
        new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, ("Test").getBytes());

    private static final MockedStatic<FFMPermissionHelper> permissionHelperMock = PermissionHelperMock.echo();

    @BeforeAll
    public static void setup() {
        pi4j0 = Pi4J.newContextBuilder()
            .add(new FFMDigitalInputProviderImpl(), new FFMDigitalOutputProviderImpl())
            .build();
        pi4j1 = Pi4J.newContextBuilder()
            .add(new FFMDigitalInputProviderImpl())
            .build();
        pi4jNonExistent = Pi4J.newContextBuilder()
            .add(new FFMDigitalInputProviderImpl())
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
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoNonExistent)) {

            var builder = DigitalInputConfigBuilder.newInstance().bus(-1)
                .bcm(99).build();
            assertThrows(IllegalStateException.class, () -> pi4j1.digitalInput().create(builder));
        }
    }

    @Test
    public void testInputNonExistent() {
        var lineInfoNonExistent = new IoctlNativeMock.IoctlTestData(LineInfo.class, (_) -> {
            throw new IllegalStateException();
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoNonExistent)) {

            var builder = DigitalInputConfigBuilder.newInstance().bus(-1)
                .bcm(0).build();
            assertThrows(IllegalStateException.class, () -> pi4jNonExistent.digitalInput().create(builder));
        }
    }

    @Test
    public void testInputCreate() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var builder = DigitalInputConfigBuilder.newInstance().bus(-1)
                .bcm(0).build();
            var pin = pi4j0.digitalInput().create(builder);
            assertEquals(0, pin.bcm());
        }
    }

    @Test
    public void testInputState() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var builder = DigitalInputConfigBuilder.newInstance().bus(-1)
                .bcm(1).build();
            var pin = pi4j0.digitalInput().create(builder);
            assertEquals(DigitalState.LOW, pin.state());
        }
    }

    @Test
    public void testInputEventProcessing() throws InterruptedException {
        var latch = new CountDownLatch(1);
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
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
        var pollingFile = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 42, ("Test").getBytes(), (answer) -> {
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
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE, pollingFile);
             var _ = IoctlNativeMock.setup(lineInfoTestData);
             var _ = PollNativeMock.setup(pollingCallback)) {
            var builder = DigitalInputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(7)
                .debounce(0L)
                .build();
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
    public void testInputEventProcessingWithZeroTimestampLSB() throws InterruptedException {
        // Regression test: a GPIO event whose timestamp_ns is divisible by 256 has a zero LSB.
        // Previously the EventWatcher only checked buf[i] == 0 (the LSB), which incorrectly
        // skipped valid events and caused RISING events to be silently dropped.
        var latch = new CountDownLatch(1);
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
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
        // Use a timestamp whose LSB is 0 (i.e. divisible by 256); this exposed the bug.
        // fd=42 must match the chipFileDescriptor returned by IoctlNativeMock for LineRequest.
        long timestampWithZeroLSB = 256L;
        var pollingFile = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 42, ("Test").getBytes(), (answer) -> {
            byte[] buffer = answer.getArgument(1);
            var lineEvent = new LineEvent(timestampWithZeroLSB, PinEvent.RISING.getValue(), 3, 4, 5);
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
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE, pollingFile);
             var _ = IoctlNativeMock.setup(lineInfoTestData);
             var _ = PollNativeMock.setup(pollingCallback)) {
            var builder = DigitalInputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(17)
                .debounce(0L)
                .build();
            var pin = pi4j0.digitalInput().create(builder);
            assertEquals(DigitalState.LOW, pin.state());
            var passed = new AtomicBoolean(false);
            pin.addListener(event -> {
                passed.set(event.state() == DigitalState.HIGH);
                latch.countDown();
            });
            assertTrue(latch.await(5, TimeUnit.SECONDS), "Event with zero-LSB timestamp was not dispatched");
            assertTrue(passed.get());
        }
    }

    @Test
    public void testInputEventProcessingWithDebounce() throws InterruptedException {
        var latch = new CountDownLatch(1);
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        var pollingCallback = new Function<InvocationOnMock, PollingData>() {
            private int callCount = 0;

            @Override
            public PollingData apply(InvocationOnMock answer) {
                PollingData pollingData = answer.getArgument(0);
                callCount++;
                // Return event on first call, then timeout (null) on subsequent calls
                if (callCount == 1) {
                    return new PollingData(pollingData.fd(), pollingData.events(), (short) PollFlag.POLLIN);
                }
                return null;
            }
        };
        var pollingFile = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 42, ("Test").getBytes(), (answer) -> {
            byte[] buffer = answer.getArgument(1);
            long timestampNs = System.nanoTime();
            var lineEvent = new LineEvent(timestampNs, PinEvent.RISING.getValue(), 3, 4, 5);
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
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE, pollingFile);
             var _ = IoctlNativeMock.setup(lineInfoTestData);
             var _ = PollNativeMock.setup(pollingCallback)) {
            var builder = DigitalInputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(8)
                .debounce(50L) // 50ms debounce
                .build();
            var pin = pi4j0.digitalInput().create(builder);
            assertEquals(DigitalState.LOW, pin.state());
            var passed = new AtomicBoolean(false);
            pin.addListener(event -> {
                passed.set(event.state() == DigitalState.HIGH);
                latch.countDown();
            });
            // Event should be dispatched after debounce timeout
            assertTrue(latch.await(5, TimeUnit.SECONDS));
            assertTrue(passed.get());
        }
    }

    @Test
    public void testInputIsOccupied() {
        var lineInfoOccupied = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.USED.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoOccupied)) {

            var builder = DigitalInputConfigBuilder.newInstance().bus(-1)
                .bcm(2).build();
            assertThrows(IllegalStateException.class, () -> pi4j0.digitalInput().create(builder));
        }
    }

    @Test
    public void testInputCustomConfig() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var config = DigitalInputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(3)
                .debounce(99L, TimeUnit.MICROSECONDS)
                .pull(PullResistance.PULL_DOWN)
                .build();
            var pin = pi4j0.digitalInput().create(config);
            assertEquals(99, pin.config().debounce());
            assertEquals(3, pin.bcm());
            assertEquals(PullResistance.PULL_DOWN, pin.pull());
        }
    }

    @Test
    public void testOutputCreate() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.OUTPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var builder = DigitalOutputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(4)
                .build();
            var pin = pi4j0.digitalOutput().create(builder);
            assertEquals(4, pin.bcm());
        }
    }

    @Test
    public void testOutputChangeState() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.OUTPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var builder = DigitalOutputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(5)
                .build();
            var pin = pi4j0.digitalOutput().create(builder);
            pin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, pin.state());
            pin.state(DigitalState.LOW);
            assertEquals(DigitalState.LOW, pin.state());
        }
    }

    @Test
    public void testOutputInitialStateHigh() {
        var capturedRequest = new java.util.concurrent.atomic.AtomicReference<LineRequest>();
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.OUTPUT.getValue(),
                new LineAttribute[0]);
        });
        var lineRequestTestData = new IoctlNativeMock.IoctlTestData(LineRequest.class, (answer) -> {
            LineRequest lineRequest = answer.getArgument(2);
            capturedRequest.set(lineRequest);
            return new LineRequest(lineRequest.offsets(), lineRequest.consumer(), lineRequest.config(),
                lineRequest.numLines(), lineRequest.eventBufferSize(), 42);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData, lineRequestTestData)) {

            var builder = DigitalOutputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(9)
                .initial(DigitalState.HIGH)
                .build();
            var pin = pi4j0.digitalOutput().create(builder);

            assertEquals(DigitalState.HIGH, pin.state());

            var request = capturedRequest.get();
            assertNotNull(request, "LineRequest was not sent to the kernel");
            var config = request.config();
            assertEquals(1, config.numAttrs(), "Initial state should add a single output-values attribute");
            var attribute = config.attrs()[0];
            assertEquals(LineAttributeId.GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES.getValue(), attribute.attr().id());
            // bit 0 corresponds to the single requested line at index 0 in the offsets array
            assertEquals(1L, attribute.mask());
            assertEquals(1L, attribute.attr().values(), "HIGH initial state should set the line's value bit");
        }
    }

    @Test
    public void testOutputInitialStateLowPassedToKernel() {
        var capturedRequest = new java.util.concurrent.atomic.AtomicReference<LineRequest>();
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.OUTPUT.getValue(),
                new LineAttribute[0]);
        });
        var lineRequestTestData = new IoctlNativeMock.IoctlTestData(LineRequest.class, (answer) -> {
            LineRequest lineRequest = answer.getArgument(2);
            capturedRequest.set(lineRequest);
            return new LineRequest(lineRequest.offsets(), lineRequest.consumer(), lineRequest.config(),
                lineRequest.numLines(), lineRequest.eventBufferSize(), 42);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData, lineRequestTestData)) {

            var builder = DigitalOutputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(10)
                .initial(DigitalState.LOW)
                .build();
            var pin = pi4j0.digitalOutput().create(builder);

            assertEquals(DigitalState.LOW, pin.state());

            var request = capturedRequest.get();
            assertNotNull(request, "LineRequest was not sent to the kernel");
            var config = request.config();
            assertEquals(1, config.numAttrs(), "Initial state should add a single output-values attribute");
            var attribute = config.attrs()[0];
            assertEquals(LineAttributeId.GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES.getValue(), attribute.attr().id());
            assertEquals(1L, attribute.mask());
            assertEquals(0L, attribute.attr().values(), "LOW initial state should clear the line's value bit");
        }
    }

    @Test
    public void testOutputWithoutInitialStateHasNoAttributes() {
        var capturedRequest = new java.util.concurrent.atomic.AtomicReference<LineRequest>();
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.OUTPUT.getValue(),
                new LineAttribute[0]);
        });
        var lineRequestTestData = new IoctlNativeMock.IoctlTestData(LineRequest.class, (answer) -> {
            LineRequest lineRequest = answer.getArgument(2);
            capturedRequest.set(lineRequest);
            return new LineRequest(lineRequest.offsets(), lineRequest.consumer(), lineRequest.config(),
                lineRequest.numLines(), lineRequest.eventBufferSize(), 42);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData, lineRequestTestData)) {

            var builder = DigitalOutputConfigBuilder.newInstance()
                .bus(-1)
                .bcm(11)
                .build();
            pi4j0.digitalOutput().create(builder);

            var request = capturedRequest.get();
            assertNotNull(request, "LineRequest was not sent to the kernel");
            assertEquals(0, request.config().numAttrs(),
                "Without an initial state no output-values attribute should be sent");
        }
    }

    @Test
    public void testApi() {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData);
             var _ = BoardInfoMock.setup(BoardModel.MODEL_4_B)) {

            BoardInfoHelper.reinitialize();
            var mockingBoard = Pi4JApi.board(RaspberryPi.Model4B.class);
            var pin = mockingBoard.input(5);
            assertEquals(5, pin.bcm());
        }
    }

    @Test
    public void testReconfigureInputToOutput() throws Exception {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0, PinFlag.INPUT.getValue(), new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var inputConfig = DigitalInputConfigBuilder.newInstance().bus(-1).bcm(30).build();
            var inputPin = pi4j0.digitalInput().create(inputConfig);
            assertEquals(DigitalState.LOW, inputPin.state());

            var outputConfig = DigitalOutputConfigBuilder.newInstance().bus(-1).bcm(30).build();
            var outputPin = inputPin.reconfigure().digitalOutput().create(outputConfig);
            assertNotNull(outputPin);
            assertEquals(30, outputPin.bcm());

            outputPin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, outputPin.state());
        }
    }

    @Test
    public void testReconfigureOutputToInput() throws Exception {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0, PinFlag.OUTPUT.getValue(), new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var outputConfig = DigitalOutputConfigBuilder.newInstance().bus(-1).bcm(31).build();
            var outputPin = pi4j0.digitalOutput().create(outputConfig);
            outputPin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, outputPin.state());

            var inputConfig = DigitalInputConfigBuilder.newInstance().bus(-1).bcm(31).build();
            var inputPin = outputPin.reconfigure().digitalInput().create(inputConfig);
            assertNotNull(inputPin);
            assertEquals(31, inputPin.bcm());
            // The mock ioctl returns the LineValues argument unchanged (bits=0) → LOW
            assertEquals(DigitalState.LOW, inputPin.state());
        }
    }

    @Test
    public void testReconfigureInputToOutputReusingConfig() throws Exception {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0, PinFlag.INPUT.getValue(), new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var inputConfig = DigitalInputConfigBuilder.newInstance().bus(-1).bcm(32).build();
            var inputPin = pi4j0.digitalInput().create(inputConfig);
            assertEquals(DigitalState.LOW, inputPin.state());

            // no explicit builder — reuse the current pin's BCM/bus addressing
            var outputPin = inputPin.reconfigure().digitalOutput().create();
            assertNotNull(outputPin);
            assertEquals(32, outputPin.bcm());
            assertEquals(inputPin.config().bus(), outputPin.config().bus());

            outputPin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, outputPin.state());
        }
    }

    @Test
    public void testReconfigureOutputToInputReusingConfig() throws Exception {
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0, PinFlag.OUTPUT.getValue(), new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfoTestData)) {

            var outputConfig = DigitalOutputConfigBuilder.newInstance().bus(-1).bcm(33).build();
            var outputPin = pi4j0.digitalOutput().create(outputConfig);
            outputPin.state(DigitalState.HIGH);

            // no explicit builder — reuse the current pin's BCM/bus addressing
            var inputPin = outputPin.reconfigure().digitalInput().create();
            assertNotNull(inputPin);
            assertEquals(33, inputPin.bcm());
            assertEquals(outputPin.config().bus(), inputPin.config().bus());
            assertEquals(DigitalState.LOW, inputPin.state());
        }
    }

    @Test
    public void testMoreThanFourInputsReceiveEvents() throws InterruptedException {
        // Regression test for the carrier-thread-pinning bug:
        //
        // Before the fix, EventWatcher used virtual threads. Virtual threads that call a
        // blocking native method (poll()) are *pinned* to a ForkJoinPool carrier thread
        // for the duration of the call. The carrier pool size defaults to the number of
        // available CPU cores (4 on a Raspberry Pi 4). Once 4 pins are listening, all
        // carrier threads are pinned and the 5th+ EventWatcher can never be scheduled.
        //
        // This test makes each watcher's first poll() call block until ALL numPins
        // watchers are simultaneously blocked, then releases them. That directly mirrors
        // the real scenario: concurrent blocking native poll() calls.
        //
        // With the old virtual-thread code on a <=4-core machine the test would time out
        // at allWatchersBlockingLatch because the 5th watcher can never enter poll().
        // With the fixed platform-daemon-thread code every watcher runs on its own OS
        // thread, all 5 block simultaneously, and the test completes.
        int numPins = 5;

        // Latch that counts down once per watcher that reaches (and blocks inside) poll()
        var allWatchersBlockingLatch = new CountDownLatch(numPins);
        // Released by the test thread once all watchers are simultaneously blocking
        var releaseWatchersLatch = new CountDownLatch(1);
        // Counts down as each pin's listener fires the first HIGH event
        var eventsDeliveredLatch = new CountDownLatch(numPins);

        var pinReceivedEvent = new AtomicBoolean[numPins];
        for (int i = 0; i < numPins; i++) {
            pinReceivedEvent[i] = new AtomicBoolean(false);
        }
        var lineInfoTestData = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo lineInfo = answer.getArgument(2);
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
                lineInfo.offset(), 0,
                PinFlag.INPUT.getValue(),
                new LineAttribute[0]);
        });

        // Each of the first numPins poll() calls (one per watcher thread) blocks until
        // the test thread sees all watchers are blocked and releases them.  Subsequent
        // calls return immediately so the loop can continue delivering events.
        var blockedWatcherCount = new AtomicInteger(0);
        var pollingCallback = new Function<InvocationOnMock, PollingData>() {
            @Override
            public PollingData apply(InvocationOnMock answer) {
                PollingData pollingData = answer.getArgument(0);
                if (blockedWatcherCount.incrementAndGet() <= numPins) {
                    // Signal: this watcher is now blocked in poll(), simulating the real
                    // blocking native syscall that pins a virtual-thread carrier thread.
                    allWatchersBlockingLatch.countDown();
                    try {
                        releaseWatchersLatch.await(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                return new PollingData(pollingData.fd(), pollingData.events(), (short) PollFlag.POLLIN);
            }
        };
        var pollingFile = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 42, ("Test").getBytes(), (answer) -> {
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
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE, pollingFile);
             var _ = IoctlNativeMock.setup(lineInfoTestData);
             var _ = PollNativeMock.setup(pollingCallback)) {
            List<Object> pins = new ArrayList<>();
            for (int i = 0; i < numPins; i++) {
                var builder = DigitalInputConfigBuilder.newInstance()
                    .bus(-1)
                    .bcm(20 + i)
                    .debounce(0L)
                    .build();
                var pin = pi4j0.digitalInput().create(builder);
                final int pinIndex = i;
                pin.addListener(event -> {
                    if (event.state() == DigitalState.HIGH && pinReceivedEvent[pinIndex].compareAndSet(false, true)) {
                        eventsDeliveredLatch.countDown();
                    }
                });
                pins.add(pin);
            }

            // All numPins watcher threads must reach poll() simultaneously.
            // With virtual threads on <=4 cores this assertion would time out because the
            // 5th+ watcher is never scheduled while the first 4 pin the carrier pool.
            assertTrue(allWatchersBlockingLatch.await(5, TimeUnit.SECONDS),
                (numPins - allWatchersBlockingLatch.getCount()) + " of " + numPins +
                    " watcher threads reached poll(). " +
                    "Carrier thread pinning may have prevented remaining watchers from being scheduled.");

            // Release all watchers so they return from poll() and process the event.
            releaseWatchersLatch.countDown();

            // All numPins pins must deliver a state change event.
            assertTrue(eventsDeliveredLatch.await(5, TimeUnit.SECONDS),
                "Only " + (numPins - eventsDeliveredLatch.getCount()) + " of " + numPins +
                    " digital inputs delivered a state change event after poll() was released.");
        }
    }
}
