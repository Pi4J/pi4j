package com.pi4j.plugin.ffm.unit;

import com.pi4j.context.impl.CheckpointResource;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.RestoreException;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.ffm.FFMContext;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.gpio.structs.LineRequest;
import com.pi4j.plugin.ffm.common.gpio.structs.LineValues;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.spi.SpiTransferBuffer;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import com.pi4j.plugin.ffm.mocks.*;
import com.pi4j.plugin.ffm.providers.i2c.I2CFunctionality;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.foreign.Arena;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

/**
 * Verifies that FFM I/O instances release their file descriptors before a CRaC checkpoint and reacquire
 * them after a restore, driven through the context's {@link CheckpointResource} with mocked native calls.
 */
class CheckpointTest {

    private static final FileDescriptorNativeMock.FileDescriptorTestData GPIOCHIP_FILE =
        new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, ("Test").getBytes());

    private static final MockedStatic<FFMPermissionHelper> permissionHelperMock = PermissionHelperMock.echo();

    private static final IoctlNativeMock.IoctlTestData FREE_LINE_INFO = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
        LineInfo lineInfo = answer.getArgument(2);
        return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(),
            lineInfo.offset(), 0, PinFlag.INPUT.getValue(), new LineAttribute[0]);
    });

    private FFMContext pi4j;
    private CheckpointResource resource;

    @BeforeEach
    void setup() {
        pi4j = new FFMContext();
        resource = pi4j.checkpointResource();
    }

    @AfterEach
    void teardown() {
        pi4j.shutdown();
    }

    @AfterAll
    static void closeMocks() {
        permissionHelperMock.close();
    }

    @Test
    void testOutputRestoresLastState() throws Exception {
        var requests = new ArrayList<LineRequest>();
        var lineRequest = new IoctlNativeMock.IoctlTestData(LineRequest.class, (answer) -> {
            LineRequest request = answer.getArgument(2);
            requests.add(request);
            return new LineRequest(request.offsets(), request.consumer(), request.config(), request.numLines(), request.eventBufferSize(), 42);
        });
        try (var files = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(FREE_LINE_INFO, lineRequest)) {

            var pin = pi4j.create(DigitalOutputConfigBuilder.newInstance().bus(-1).bcm(5)
                .initial(DigitalState.HIGH).build());
            pin.state(DigitalState.LOW);

            resource.beforeCheckpoint(null);
            assertEquals(1, count(files, "close", 42));
            assertThrows(Pi4JException.class, () -> pin.state(DigitalState.HIGH));

            resource.afterRestore(null);
            assertEquals(2, requests.size());
            var attributes = requests.get(1).config().attrs();
            assertEquals(1, attributes.length);
            assertEquals(LineAttributeId.GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES.getValue(), attributes[0].attr().id());
            assertEquals(0L, attributes[0].attr().values());

            pin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, pin.state());
        }
    }

    @Test
    void testOutputRestoreFailsWhenLineWasClaimed() throws Exception {
        var claimed = new AtomicReference<>(false);
        var lineInfo = new IoctlNativeMock.IoctlTestData(LineInfo.class, (answer) -> {
            LineInfo info = answer.getArgument(2);
            var flags = claimed.get() ? PinFlag.USED.getValue() : PinFlag.INPUT.getValue();
            return new LineInfo(("Test").getBytes(), ("FFM-Test").getBytes(), info.offset(), 0, flags, new LineAttribute[0]);
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(lineInfo)) {

            var pin = pi4j.create(DigitalOutputConfigBuilder.newInstance().id("led").bus(-1).bcm(6).build());

            resource.beforeCheckpoint(null);
            claimed.set(true);
            var e = assertThrows(RestoreException.class, () -> resource.afterRestore(null));
            assertTrue(e.getMessage().contains("'led'"), e.getMessage());
            assertTrue(e.getMessage().contains("[6]"), e.getMessage());

            // the pin stays unusable, but can still be closed
            assertThrows(Pi4JException.class, () -> pin.state(DigitalState.HIGH));
        }
    }

    @Test
    void testParallelOutputRestoresLastValue() throws Exception {
        var requests = new ArrayList<LineRequest>();
        var lineRequest = new IoctlNativeMock.IoctlTestData(LineRequest.class, (answer) -> {
            LineRequest request = answer.getArgument(2);
            requests.add(request);
            return new LineRequest(request.offsets(), request.consumer(), request.config(), request.numLines(), request.eventBufferSize(), 42);
        });
        // remembers the last written values, so reading back returns them like the kernel does
        var lastValues = new AtomicReference<>(0L);
        var lineValues = new IoctlNativeMock.IoctlTestData(LineValues.class, (answer) -> {
            LineValues values = answer.getArgument(2);
            if (answer.<Long>getArgument(1) == Command.getGpioV2SetValuesIoctl()) {
                lastValues.set(values.bits());
                return values;
            }
            return new LineValues(lastValues.get(), values.mask());
        });
        try (var _ = FileDescriptorNativeMock.setup(GPIOCHIP_FILE);
             var _ = IoctlNativeMock.setup(FREE_LINE_INFO, lineRequest, lineValues)) {

            var port = pi4j.create(ParallelPortConfigBuilder.newInstance().id("port").bus(-1).bcm(10, 11, 12)
                .initialDirection(ParallelPort.Direction.OUTPUT).build());
            port.write(5);

            resource.beforeCheckpoint(null);
            assertThrows(Pi4JException.class, () -> port.write(2));

            resource.afterRestore(null);
            assertEquals(2, requests.size());
            var config = requests.get(1).config();
            assertNotEquals(0, config.flags() & PinFlag.OUTPUT.getValue());
            assertEquals(1, config.attrs().length);
            assertEquals(LineAttributeId.GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES.getValue(), config.attrs()[0].attr().id());
            assertEquals(5L, config.attrs()[0].attr().values());
            assertEquals(0b111L, config.attrs()[0].mask());
            assertEquals(5, port.read());
        }
    }

    @Test
    void testInputRestartsEventWatchers() throws Exception {
        var events = new CountDownLatch[]{new CountDownLatch(1)};
        var pollingCallback = (java.util.function.Function<org.mockito.invocation.InvocationOnMock, PollingData>) (answer) -> {
            PollingData pollingData = answer.getArgument(0);
            return new PollingData(pollingData.fd(), pollingData.events(), (short) PollFlag.POLLIN);
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
             var _ = IoctlNativeMock.setup(FREE_LINE_INFO);
             var _ = PollNativeMock.setup(pollingCallback)) {

            var pin = pi4j.create(DigitalInputConfigBuilder.newInstance().bus(-1).bcm(7).debounce(0L).build());
            pin.addListener(_ -> events[0].countDown());
            assertTrue(events[0].await(5, TimeUnit.SECONDS));

            resource.beforeCheckpoint(null);
            assertThrows(Pi4JException.class, pin::state);

            events[0] = new CountDownLatch(1);
            resource.afterRestore(null);
            assertTrue(events[0].await(5, TimeUnit.SECONDS), "no events after restore");
            assertEquals(DigitalState.LOW, pin.state());
        }
    }

    @Test
    void testSpiReopensAndReconfigures() throws Exception {
        var transfer = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            return new SpiTransferBuffer(buffer.getTxBuffer(), buffer.getTxBuffer(), buffer.getTxBuffer().length);
        });
        try (var files = FileDescriptorNativeMock.setup();
             var _ = IoctlNativeMock.setup(transfer)) {

            var spi = pi4j.create(SpiConfigBuilder.newInstance().bus(SpiBus.BUS_0).channel(3).mode(0).baud(50_000).build());
            assertEquals(1, count(files, "open", "/dev/spidev0.3"));

            resource.beforeCheckpoint(null);
            assertThrows(Pi4JException.class, () -> spi.write((byte) 0x01));

            resource.afterRestore(null);
            assertEquals(2, count(files, "open", "/dev/spidev0.3"));
            spi.write((byte) 0x01);
        }
    }

    @Test
    @EnabledOnOs(LINUX)
    void testI2CReopensBus() throws Exception {
        var i2cFile = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/i2c-", 1, ("Test").getBytes());
        var functionalities = I2CFunctionality.I2C_FUNC_I2C.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_BYTE.getValue()
            | I2CFunctionality.I2C_FUNC_SMBUS_BYTE_DATA.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_WORD_DATA.getValue();
        try (var files = FileDescriptorNativeMock.setup(i2cFile);
             var _ = IoctlNativeMock.i2c(functionalities);
             var _ = SMBusNativeMock.setup()) {

            var i2c = pi4j.create(I2CConfigBuilder.newInstance().bus(3).device(0x1C).i2cImplementation(I2CImplementation.SMBUS));

            resource.beforeCheckpoint(null);
            assertThrows(Pi4JException.class, () -> i2c.write((byte) 0x1C));

            resource.afterRestore(null);
            assertEquals(2, count(files, "open", "/dev/i2c-3"));
            assertEquals(1, i2c.write((byte) 0x1C));
        }
    }

    @Test
    void testPwmReopensAndReappliesSettings() throws Exception {
        var path = "/sys/class/pwm/pwmchip0/pwm3";
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/enable", 1, ("1").getBytes(), (_) -> ("0").getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/duty_cycle", 2, ("1").getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/polarity", 3, ("normal").getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/period", 4, ("1").getBytes());
        try (var files = FileDescriptorNativeMock.setup(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            var pwm = pi4j.create(PwmConfigBuilder.newInstance(pi4j).pwmType(PwmType.HARDWARE).chip(0).channel(3).build());
            pwm.on();
            assertEquals(1, count(files, "write", 1));

            resource.beforeCheckpoint(null);
            assertEquals(1, count(files, "close", 1));
            assertThrows(Pi4JException.class, pwm::off);

            resource.afterRestore(null);
            assertEquals(2, count(files, "open", path + "/enable"));
            // the PWM was on, so it is enabled again with its settings
            assertEquals(2, count(files, "write", 1));
            assertTrue(pwm.isOn());
            pwm.off();
        }
    }

    /**
     * Counts the invocations of a {@link FileDescriptorNative} method whose first argument matches, across
     * all mocks constructed so far.
     */
    private static long count(MockedConstruction<FileDescriptorNative> files, String method, Object firstArgument) {
        return files.constructed().stream()
            .flatMap(mock -> Mockito.mockingDetails(mock).getInvocations().stream())
            .filter(invocation -> invocation.getMethod().getName().equals(method))
            .filter(invocation -> firstArgument.equals(invocation.getArgument(0)))
            .count();
    }
}
