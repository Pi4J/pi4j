package com.pi4j.plugin.ffm.detect.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardOverviewTest {

    @Test
    public void detectedProfileProvidesHintForEverySubsystem() {
        var profile = BoardOverview.detect();
        assertNotNull(profile.name());
        assertFalse(profile.name().isBlank());
        for (var subsystem : HWInterfaces.values()) {
            var hint = profile.enableHint(subsystem);
            assertNotNull(hint);
            assertFalse(hint.isBlank(), subsystem.name());
        }
    }

    @Test
    public void raspberryPiProfileUsesDtparam() {
        var profile = BoardOverview.forName("Raspberry Pi");
        assertEquals("Raspberry Pi", profile.name());
        assertTrue(profile.enableHint(HWInterfaces.SPI).contains("dtparam=spi=on"));
        assertTrue(profile.enableHint(HWInterfaces.SPI).contains("config.txt"));
    }

    @Test
    public void overlayProfilesUseTheRightMechanism() {
        assertTrue(BoardOverview.forName("Armbian").enableHint(HWInterfaces.SPI).contains("overlays="));
        assertTrue(BoardOverview.forName("Armbian").enableHint(HWInterfaces.SPI).contains("armbianEnv.txt"));
        assertTrue(BoardOverview.forName("Radxa").enableHint(HWInterfaces.SPI).contains("rsetup"));
        assertTrue(BoardOverview.forName("NVIDIA Jetson").enableHint(HWInterfaces.SPI).contains("jetson-io"));
        assertTrue(BoardOverview.forName("Hardkernel ODROID").enableHint(HWInterfaces.I2C).contains("config.ini"));
        assertTrue(BoardOverview.forName("Khadas").enableHint(HWInterfaces.PWM).contains("env.txt"));
        assertTrue(BoardOverview.forName("Libre Computer").enableHint(HWInterfaces.SPI).contains("ldto"));
    }

    @Test
    public void detectMatchesVendorFromDeviceTreeCompatible() {
        assertEquals("Radxa", BoardOverview.detect("", List.of("radxa,rock-5b", "rockchip,rk3588")).name());
        assertEquals("NVIDIA Jetson", BoardOverview.detect("", List.of("nvidia,p3450-0000", "nvidia,tegra210")).name());
        assertEquals("Hardkernel ODROID", BoardOverview.detect("", List.of("hardkernel,odroid-n2", "amlogic,g12b")).name());
        // A bare SoC vendor is not a board vendor — fall through to the generic profile.
        assertEquals("Generic Linux", BoardOverview.detect("", List.of("rockchip,rk3399")).name());
        // Model-string fallback when 'compatible' carries no known board vendor.
        assertTrue(BoardOverview.detect("Pine64 RockPro64", List.of()).name().contains("Pine64"));
    }

    @Test
    public void detectFoldsModelIntoName() {
        var profile = BoardOverview.detect("ROCK 5B", List.of("radxa,rock-5b", "rockchip,rk3588"));
        assertTrue(profile.name().contains("Radxa"), profile.name());
        assertTrue(profile.name().contains("ROCK 5B"), profile.name());
    }
}
