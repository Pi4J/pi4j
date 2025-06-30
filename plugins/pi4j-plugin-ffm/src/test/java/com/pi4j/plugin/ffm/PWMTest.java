package com.pi4j.plugin.ffm;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock;
import com.pi4j.plugin.ffm.providers.pwm.PwmFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PWMTest {
    private static Context pi4j;

    @BeforeAll
    public static void setup() {
        pi4j = Pi4J.newContextBuilder()
            .add(new PwmFFMProviderImpl())
            .build();

    }

    @AfterAll
    public static void teardown() {
        pi4j.shutdown();
    }

    @Test
    public void testCreation() {
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm1/enable", 1, "Test".getBytes(), (_) -> "1".getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm1/duty_cycle", 2, "1".getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm1/polarity", 3, "normal".getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm1/period", 4, "1".getBytes());
        try (var _ = FileDescriptorNativeMock.echo(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .busNumber(1)
                .address(0)
                .pwmType(PwmType.HARDWARE)
                .build());
        }
    }

    @Test
    public void testOnOff() {
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm2/enable", 1, "Test".getBytes(), (_) -> "0".getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm2/duty_cycle", 2, "1".getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm2/polarity", 3, "normal".getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm2/period", 4, "1".getBytes());
        try (var _ = FileDescriptorNativeMock.echo(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            var pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .busNumber(2)
                .address(0)
                .pwmType(PwmType.HARDWARE)
                .build());
            pwm.on();
            assertTrue(pwm.isOn());
            pwm.off();
            assertTrue(pwm.isOff());
        }
    }

    @Test
    public void testChangeFrequency() {
        var pwmEnable = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm3/enable", 1, "Test".getBytes(), (_) -> "0".getBytes());
        var pwmDutyCycle = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm3/duty_cycle", 2, "1".getBytes());
        var pwmPolarity = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm3/polarity", 3, "normal".getBytes());
        var pwmPeriod = new FileDescriptorNativeMock.FileDescriptorTestData("/sys/class/pwm/pwmchip0/pwm3/period", 4, "1".getBytes());
        try (var _ = FileDescriptorNativeMock.echo(pwmEnable, pwmDutyCycle, pwmPolarity, pwmPeriod)) {
            var pwm = pi4j.pwm().create(PwmConfigBuilder.newInstance(pi4j)
                .busNumber(3)
                .address(0)
                .pwmType(PwmType.HARDWARE)
                .build());
            pwm.on();
            assertTrue(pwm.isOn());

            pwm.off();
            assertTrue(pwm.isOff());

            pwm.setFrequency(200);

            pwm.on();
            assertTrue(pwm.isOn());
        }
    }
}
