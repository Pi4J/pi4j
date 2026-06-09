package com.pi4j.plugin.ffm.detect;

import com.pi4j.plugin.ffm.detect.model.DetectedHardware;
import com.pi4j.plugin.ffm.detect.model.DetectedHardware.Source;
import com.pi4j.plugin.ffm.detect.model.DetectedHardware.State;
import com.pi4j.plugin.ffm.detect.model.DetectionReport;
import com.pi4j.plugin.ffm.detect.model.GpioLine;
import com.pi4j.plugin.ffm.detect.model.HWInterfaces;
import com.pi4j.plugin.ffm.detect.probe.sysfs.SysfsBusScanner;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the detection orchestrator and its domain model. Deterministic and hardware-free: the
 * end-to-end {@link HardwareDetector#detect()} sweep is run only as a smoke test that must degrade
 * gracefully on a machine with no GPIO/I2C/SPI/PWM controllers.
 */
public class HardwareDetectorTest {

    @Test
    public void serialSupportIsRemoved() {
        assertThrows(IllegalArgumentException.class, () -> HWInterfaces.valueOf("SERIAL"));
        assertEquals(EnumSet.of(HWInterfaces.GPIO, HWInterfaces.I2C, HWInterfaces.SPI, HWInterfaces.PWM),
            EnumSet.allOf(HWInterfaces.class));
        assertTrue(HWInterfaces.byDtGenericName("serial").isEmpty());
    }

    @Test
    public void subsystemMatchesNodesAndDtNames() {
        assertTrue(HWInterfaces.I2C.matchesDevNode("i2c-1"));
        assertFalse(HWInterfaces.I2C.matchesDevNode("spidev0.0"));
        assertTrue(HWInterfaces.GPIO.matchesDevNode("gpiochip0"));
        assertEquals(HWInterfaces.SPI, HWInterfaces.byDtGenericName("spi").orElseThrow());
        assertTrue(HWInterfaces.byDtGenericName("nonsense").isEmpty());
    }

    @Test
    public void reportQueriesAndRendering() {
        var lines = List.of(new GpioLine(0, 27, "ID_SD", true), new GpioLine(2, 3, "GPIO2", false));
        var gpio = DetectedHardware.active(HWInterfaces.GPIO, "/dev/gpiochip0", "pinctrl-rp1",
            "name=gpiochip0, lines=54", lines);
        var spi = new DetectedHardware(HWInterfaces.SPI, Source.DEVICE_TREE, State.DISABLED,
            "spi@7e204000", "brcm,bcm2835-spi", "[status=disabled]", "dtparam=spi=on", List.of());
        var report = new DetectionReport("Raspberry Pi", "6.8.0-52-generic", List.of(gpio, spi), true);

        assertEquals(List.of(spi), report.of(HWInterfaces.SPI));
        assertEquals(EnumSet.of(HWInterfaces.GPIO), report.activeSubsystems());

        var text = report.renderSummery();
        assertTrue(text.contains("Raspberry Pi"), text); // board name in the header
        assertTrue(text.contains("6.8.0-52-generic"), text); // kernel version in the header
        assertTrue(text.contains("/dev/gpiochip0"), text);
        assertTrue(text.contains("pinctrl-rp1"), text);   // driver column
        assertTrue(text.contains("lines=54"), text);      // description detail
        assertTrue(text.contains("dtparam=spi=on"), text); // enable hint shown only for the non-active controller
    }

    @Test
    public void scannersHandleEmptyRequest() {
        assertTrue(new SysfsBusScanner().scan(EnumSet.noneOf(HWInterfaces.class)).isEmpty());
    }

    @Test
    public void detectIsHardwareSafeAndSerialFree() {
        var report = new HardwareDetector().detect();

        assertNotNull(report);
        assertNotNull(report.boardName());
        assertNotNull(report.controllers());
        assertNotNull(report.renderSummery());
        for (var controller : report.controllers()) {
            assertNotNull(controller.subsystem());
            assertNotNull(controller.identifier());
            assertNotEquals("SERIAL", controller.subsystem().name());
        }
    }
}
