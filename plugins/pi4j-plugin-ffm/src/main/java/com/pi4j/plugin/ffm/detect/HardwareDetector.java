package com.pi4j.plugin.ffm.detect;

import com.pi4j.plugin.ffm.detect.model.BoardOverview;
import com.pi4j.plugin.ffm.detect.model.DetectedHardware;
import com.pi4j.plugin.ffm.detect.model.DetectionReport;
import com.pi4j.plugin.ffm.detect.model.GpioLine;
import com.pi4j.plugin.ffm.detect.model.HWInterfaces;
import com.pi4j.plugin.ffm.detect.probe.dev.DeviceNodeProbe;
import com.pi4j.plugin.ffm.detect.probe.dt.DeviceTreeScanner;
import com.pi4j.plugin.ffm.detect.probe.sysfs.SysfsBusScanner;
import com.pi4j.plugin.ffm.detect.probe.sysfs.SysfsReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Discovers the hardware I/O a Linux machine exposes — GPIO, I2C, SPI and PWM — without {@code gpiod},
 * without {@code libgpiod} and without shelling out to any terminal command.
 * <p>
 * Detection proceeds in three layers, from most to least authoritative:
 * <ol>
 *     <li><b>Live device nodes</b> ({@link DeviceNodeProbe}) — {@code /dev} character devices opened and
 *         probed directly via {@code ioctl}. These are the controllers usable right now.</li>
 *     <li><b>Device tree</b> ({@link DeviceTreeScanner}) — for subsystems with no live node, the SoC's
 *         disabled controllers, with board-appropriate enable hints from {@link BoardOverview}.</li>
 *     <li><b>Sysfs buses</b> ({@link SysfsBusScanner}) — on platforms with no device tree (x86/ACPI/closed
 *         firmware), the PCI/ACPI/i2c/spi bus hierarchy instead.</li>
 * </ol>
 *
 * @see DetectionReport
 */
public final class HardwareDetector {

    private static final Path DEV = Path.of("/dev");
    private static final Path SYS_CLASS_PWM = Path.of("/sys/class/pwm");

    private final DeviceNodeProbe devProbe = new DeviceNodeProbe();

    /**
     * Runs a full detection sweep.
     *
     * @return a structured {@link DetectionReport} listing every controller found and, for those not
     * already active, how to enable them
     */
    public DetectionReport detect() {
        var profile = BoardOverview.detect();
        var controllers = new ArrayList<DetectedHardware>();
        var live = EnumSet.noneOf(HWInterfaces.class);

        // Layer 1 — live character devices, plus PWM which lives only in sysfs.
        probeDeviceNodes(controllers, live, profile);
        if (probePwm(controllers)) {
            live.add(HWInterfaces.PWM);
        }

        // Layers 2 & 3 — fall back only for subsystems that produced no live node.
        var missing = EnumSet.allOf(HWInterfaces.class);
        missing.removeAll(live);
        var deviceTreePresent = DeviceTreeScanner.isAvailable();
        if (!missing.isEmpty()) {
            controllers.addAll(deviceTreePresent
                ? new DeviceTreeScanner(profile).scan(missing)
                : new SysfsBusScanner().scan(missing));
        }

        return new DetectionReport(profile.name(), profile.kernelVersion(), List.copyOf(controllers), deviceTreePresent);
    }

    private void probeDeviceNodes(List<DetectedHardware> out, Set<HWInterfaces> live, BoardOverview profile) {
        for (var node : SysfsReader.listChildren(DEV, "")) {
            var name = node.getFileName().toString();
            for (var subsystem : HWInterfaces.values()) {
                if (subsystem.matchesDevNode(name)) {
                    var probe = devProbe.describe(subsystem, node.toString());
                    out.add(DetectedHardware.active(subsystem, node.toString(), probe.driver(),
                        probe.description(), withPhysicalPins(probe.lines(), profile)));
                    live.add(subsystem);
                    break;
                }
            }
        }
    }

    /**
     * Fills in each GPIO line's physical header pin from the detected board profile (the prober reports the
     * kernel facts only — offset, name, in-use — and leaves the board-specific pin mapping to us).
     */
    private static List<GpioLine> withPhysicalPins(List<GpioLine> lines, BoardOverview profile) {
        if (lines.isEmpty()) {
            return lines;
        }
        return lines.stream()
            .map(l -> new GpioLine(l.offset(), profile.physicalPin(l.offset()), l.name(), l.used()))
            .toList();
    }

    private boolean probePwm(List<DetectedHardware> out) {
        var chips = SysfsReader.listChildren(SYS_CLASS_PWM, "pwmchip");
        for (var chip : chips) {
            var npwm = SysfsReader.firstLine(chip.resolve("npwm"));
            out.add(DetectedHardware.active(HWInterfaces.PWM, chip.toString(), pwmDriver(chip),
                npwm == null ? "PWM chip" : npwm + " channels", List.of()));
        }
        return !chips.isEmpty();
    }

    /**
     * The PWM chip's backing device name (e.g. {@code 1f00098000.pwm}), read from the {@code device} symlink.
     */
    private static String pwmDriver(Path chip) {
        try {
            return Files.readSymbolicLink(chip.resolve("device")).getFileName().toString();
        } catch (IOException e) {
            return null;
        }
    }
}
