package com.pi4j.plugin.ffm.unit;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock;
import com.pi4j.plugin.ffm.mocks.PermissionHelperMock;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmHardware;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PWMTest {
    private static Context pi4j;

    private static final MockedStatic<FFMPermissionHelper> permissionHelperMock = PermissionHelperMock.echo();

    @BeforeAll
    public static void setup() {
        pi4j = Pi4J.newContextBuilder()
            .add(new FFMPwmProviderImpl())
            .build();

    }

    @AfterAll
    public static void teardown() {
        pi4j.shutdown();
        permissionHelperMock.close();
    }

    @Test
    public void testCreation() {
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm0/enable", 1, ("Test").getBytes(), (_) -> ("1").getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm0/duty_cycle", 2, ("1").getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm0/polarity", 3, ("normal").getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm0/period", 4, ("1").getBytes());
        try (var _ = FileDescriptorNativeMock.setup(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {

            pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .pwmType(PwmType.HARDWARE)
                .chip(0)
                .channel(0)
                .build());
        }
    }

    @Test
    public void testOnOff() {
        var chip = 0;
        var channel = 2;
        var path = "/sys/class/pwm/pwmchip" + chip + "/pwm" + channel;
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/enable", 1, ("Test").getBytes(), (_) -> ("0").getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/duty_cycle", 2, ("1").getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/polarity", 3, ("normal").getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/period", 4, ("1").getBytes());
        try (var _ = FileDescriptorNativeMock.setup(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            var pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .pwmType(PwmType.HARDWARE)
                .chip(chip)
                .channel(channel)
                .build());
            pwm.on();
            assertTrue(pwm.isOn());
            pwm.off();
            assertTrue(pwm.isOff());

            pi4j.shutdown(pwm.id());
        }
    }

    @Test
    public void testChangeFrequency() {
        var chip = 0;
        var channel = 2;
        var path = "/sys/class/pwm/pwmchip" + chip + "/pwm" + channel;
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/enable", 1, ("Test").getBytes(), (_) -> ("0").getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/duty_cycle", 2, ("1").getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/polarity", 3, ("normal").getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/period", 4, ("1").getBytes());

        try (var _ = FileDescriptorNativeMock.setup(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            var pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .pwmType(PwmType.HARDWARE)
                .chip(chip)
                .channel(channel)
                .build());

            pwm.on();
            assertTrue(pwm.isOn());

            pwm.off();
            assertTrue(pwm.isOff());

            pwm.setFrequency(200);

            pwm.on();
            assertTrue(pwm.isOn());

            pi4j.shutdown(pwm.id());
        }
    }

    /**
     * on() must run entirely on the persistent descriptors opened during initialize(): no open()/close()
     * of the attribute files, no access() polling and no attribute read-back on the hot path. Each write
     * must be preceded by a rewind (lseek to offset 0), and the recomputed period must be written as a
     * decimal value (default 100Hz -> 10_000_000ns).
     */
    @Test
    public void testOnDoesNotPollAccessOrReadBack() {
        var chip = 0;
        var channel = 2;
        var path = "/sys/class/pwm/pwmchip" + chip + "/pwm" + channel;
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/enable", 1, ("Test").getBytes(), (_) -> ("0").getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/duty_cycle", 2, ("1").getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/polarity", 3, ("normal").getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/period", 4, ("1").getBytes());

        try (var construction = FileDescriptorNativeMock.setup(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            var pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .pwmType(PwmType.HARDWARE)
                .chip(chip)
                .channel(channel)
                .build());

            var fdMock = construction.constructed().getLast();
            clearInvocations(fdMock);

            pwm.on();

            // persistent descriptors: on() neither opens nor closes attribute files
            verify(fdMock, never()).open(anyString(), anyInt());
            verify(fdMock, never()).close(anyInt());
            // permission checks cached during initialize(); none on the hot path
            verify(fdMock, never()).access(anyString(), anyInt());
            // no read-back of the current period (or any attribute) during on()
            verify(fdMock, never()).read(anyInt(), any(byte[].class), anyInt());
            // each write is preceded by a rewind to offset 0
            verify(fdMock, atLeastOnce()).lseek(anyInt(), eq(0L), eq(0 /* SEEK_SET */));
            // period recomputed from the default 100Hz and written as ASCII decimal nanoseconds
            verify(fdMock).write(eq(4), aryEq("10000000".getBytes(StandardCharsets.US_ASCII)));
            // enable written last
            verify(fdMock).write(eq(1), aryEq("1".getBytes(StandardCharsets.US_ASCII)));

            pi4j.shutdown(pwm.id());
        }
    }

    /**
     * Shutting the PWM down must release every persistent attribute descriptor opened during
     * initialize() (enable/period/duty_cycle/polarity -> fds 1..4).
     */
    @Test
    public void testShutdownClosesPersistentDescriptors() {
        var chip = 0;
        var channel = 2;
        var path = "/sys/class/pwm/pwmchip" + chip + "/pwm" + channel;
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/enable", 1, ("Test").getBytes(), (_) -> ("0").getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/duty_cycle", 2, ("1").getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/polarity", 3, ("normal").getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/period", 4, ("1").getBytes());

        try (var construction = FileDescriptorNativeMock.setup(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            var pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .pwmType(PwmType.HARDWARE)
                .chip(chip)
                .channel(channel)
                .build());

            var fdMock = construction.constructed().getLast();
            clearInvocations(fdMock);

            pi4j.shutdown(pwm.id());

            verify(fdMock).close(eq(1));
            verify(fdMock).close(eq(2));
            verify(fdMock).close(eq(3));
            verify(fdMock).close(eq(4));
        }
    }

    /**
     * off() must be a single enable=0 write, with no access() polling and no read-back.
     */
    @Test
    public void testOffOnlyWritesEnable() {
        var chip = 0;
        var channel = 2;
        var path = "/sys/class/pwm/pwmchip" + chip + "/pwm" + channel;
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/enable", 1, ("Test").getBytes(), (_) -> ("1").getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/duty_cycle", 2, ("1").getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/polarity", 3, ("normal").getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData(path + "/period", 4, ("1").getBytes());

        try (var construction = FileDescriptorNativeMock.setup(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            var pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .pwmType(PwmType.HARDWARE)
                .chip(chip)
                .channel(channel)
                .build());
            // enable file reports "1", so the instance starts on and off() will act
            assertTrue(pwm.isOn());

            var fdMock = construction.constructed().getLast();
            clearInvocations(fdMock);

            pwm.off();

            verify(fdMock, never()).access(anyString(), anyInt());
            verify(fdMock, never()).read(anyInt(), any(byte[].class), anyInt());
            verify(fdMock).write(eq(1), aryEq("0".getBytes(StandardCharsets.US_ASCII)));
            assertTrue(pwm.isOff());

            pi4j.shutdown(pwm.id());
        }
    }

    /**
     * The parser must read values wider than {@link Integer#MAX_VALUE} (frequencies below ~0.47Hz yield
     * periods > 2^31 ns), ignore NUL/newline padding, and reject non-numeric content.
     */
    @Test
    public void testGetLongContentParsing() throws Exception {
        var parse = longContentMethod();

        // NUL-padded fixed-size buffer, as returned by a sysfs read
        assertEquals(1L, parse.invoke(null, (Object) "1\0\0\0\0\0\0\0\0\0".getBytes(StandardCharsets.US_ASCII)));
        // newline-terminated, as sysfs attributes are written
        assertEquals(500L, parse.invoke(null, (Object) "500\n".getBytes(StandardCharsets.US_ASCII)));
        // value that overflows int32 (would have been corrupted by the old Integer.parseInt path)
        assertEquals(3_000_000_000L, parse.invoke(null, (Object) "3000000000\n".getBytes(StandardCharsets.US_ASCII)));

        var noDigits = assertThrows(InvocationTargetException.class,
            () -> parse.invoke(null, (Object) "normal\0".getBytes(StandardCharsets.US_ASCII)));
        assertInstanceOf(IllegalArgumentException.class, noDigits.getCause());

        var nullBytes = assertThrows(InvocationTargetException.class, () -> parse.invoke(null, (Object) null));
        assertInstanceOf(IllegalArgumentException.class, nullBytes.getCause());
    }

    private static Method longContentMethod() throws NoSuchMethodException {
        var method = FFMPwmHardware.class.getDeclaredMethod("getLongContent", byte[].class);
        method.setAccessible(true);
        return method;
    }
}
