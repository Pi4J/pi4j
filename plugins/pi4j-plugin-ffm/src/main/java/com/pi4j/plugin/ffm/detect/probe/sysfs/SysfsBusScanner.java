package com.pi4j.plugin.ffm.detect.probe.sysfs;

import com.pi4j.plugin.ffm.detect.model.DetectedHardware;
import com.pi4j.plugin.ffm.detect.model.HWInterfaces;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Fallback discovery for platforms that do <em>not</em> expose a device tree — typically x86/x86-64, or any
 * machine where the firmware (UEFI/ACPI, vendor BIOS) keeps the hardware description closed.
 * <p>
 * Controllers are enumerated through the sysfs bus/class hierarchy via {@link SysfsReader}:
 * <ul>
 *     <li>{@code /sys/bus/i2c/devices}  — I2C/SMBus adapters (present even when {@code i2c-dev} is not loaded),</li>
 *     <li>{@code /sys/class/spi_master} — SPI controllers,</li>
 *     <li>{@code /sys/bus/gpio/devices} — GPIO chips,</li>
 *     <li>{@code /sys/bus/pci/devices}  — PCI SMBus controllers (class {@code 0x0c05}).</li>
 * </ul>
 * The enable hints point at the real levers on such platforms (loading a kernel module or a BIOS/UEFI
 * setting) rather than a {@code config.txt} overlay.
 */
public final class SysfsBusScanner {

    private static final Path DEV = Path.of("/dev");
    private static final Path SYS_BUS_I2C = Path.of("/sys/bus/i2c/devices");
    private static final Path SYS_CLASS_SPI = Path.of("/sys/class/spi_master");
    private static final Path SYS_BUS_GPIO = Path.of("/sys/bus/gpio/devices");
    private static final Path SYS_BUS_PCI = Path.of("/sys/bus/pci/devices");

    // PCI base class code (top 16 bits of the 24-bit class): serial bus controller / SMBus.
    private static final int PCI_CLASS_SMBUS = 0x0c05;

    private static final String HINT_I2C_DEV =
        "modprobe i2c-dev  (adapter is present; only the I2C character-device interface is missing)";
    private static final String HINT_PCI_SMBUS =
        "load the chipset SMBus driver (e.g. i2c-i801 on Intel, i2c-piix4 on AMD), then modprobe i2c-dev; "
            + "if the controller is hidden, enable it in BIOS/UEFI";
    private static final String HINT_SPI =
        "bind the spidev driver (modprobe spidev) and declare the chip via an ACPI override or board file";
    private static final String HINT_GPIO =
        "load the platform pinctrl/GPIO driver for your chipset (e.g. an Intel/AMD pinctrl module)";

    /**
     * Enumerates controllers visible through sysfs for the requested subsystems that have no live device node.
     *
     * @param wanted subsystems to look for (typically the ones with no live {@code /dev} node)
     * @return discovered controllers, marked {@link DetectedHardware.State#NOT_BOUND} with a platform hint
     */
    public List<DetectedHardware> scan(Set<HWInterfaces> wanted) {
        var out = new ArrayList<DetectedHardware>();
        if (wanted.contains(HWInterfaces.I2C)) {
            scanI2cAdapters(out);
        }
        if (wanted.contains(HWInterfaces.SPI)) {
            scanSpiMasters(out);
        }
        if (wanted.contains(HWInterfaces.GPIO)) {
            scanGpioChips(out);
        }
        if (wanted.contains(HWInterfaces.I2C)) {
            scanPciSmbus(out);
        }
        out.sort(Comparator.comparing(a -> (a.subsystem().getLabel() + a.identifier())));
        return out;
    }

    private void scanI2cAdapters(List<DetectedHardware> out) {
        for (var entry : SysfsReader.listChildren(SYS_BUS_I2C, "i2c-")) {
            var node = entry.getFileName().toString();           // e.g. "i2c-0"
            if (Files.exists(DEV.resolve(node))) {
                continue;                                        // already live, counted elsewhere
            }
            var name = SysfsReader.firstLine(entry.resolve("name"));
            out.add(new DetectedHardware(HWInterfaces.I2C, DetectedHardware.Source.SYSFS_BUS,
                DetectedHardware.State.NOT_BOUND, node,
                name == null ? "i2c-adapter" : name, "I2C adapter (no character device)", HINT_I2C_DEV, List.of()));
        }
    }

    private void scanSpiMasters(List<DetectedHardware> out) {
        for (var entry : SysfsReader.listChildren(SYS_CLASS_SPI, "spi")) {
            var node = entry.getFileName().toString();           // e.g. "spi0"
            out.add(new DetectedHardware(HWInterfaces.SPI, DetectedHardware.Source.SYSFS_BUS,
                DetectedHardware.State.NOT_BOUND, node, node,
                "SPI master controller (no spidev node)", HINT_SPI, List.of()));
        }
    }

    private void scanGpioChips(List<DetectedHardware> out) {
        for (var entry : SysfsReader.listChildren(SYS_BUS_GPIO, "gpiochip")) {
            var node = entry.getFileName().toString();           // e.g. "gpiochip0"
            if (Files.exists(DEV.resolve(node))) {
                continue;                                        // already live
            }
            var label = SysfsReader.firstLine(entry.resolve("label"));
            out.add(new DetectedHardware(HWInterfaces.GPIO, DetectedHardware.Source.SYSFS_BUS,
                DetectedHardware.State.NOT_BOUND, node,
                label == null ? "gpiochip" : label, "GPIO chip (no character device)", HINT_GPIO, List.of()));
        }
    }

    private void scanPciSmbus(List<DetectedHardware> out) {
        for (var dev : SysfsReader.listChildren(SYS_BUS_PCI, "")) {
            var classCode = SysfsReader.readHex(dev.resolve("class"));   // e.g. 0x0c0500
            if (classCode < 0) {
                continue;
            }
            var baseClass = (classCode >> 8) & 0xFFFF;
            if (baseClass == PCI_CLASS_SMBUS) {
                var address = dev.getFileName().toString();             // e.g. 0000:00:1f.4
                out.add(new DetectedHardware(HWInterfaces.I2C, DetectedHardware.Source.SYSFS_BUS,
                    DetectedHardware.State.NOT_BOUND, address,
                    "PCI SMBus " + pciId(dev), "SMBus host controller (no i2c-dev node)", HINT_PCI_SMBUS, List.of()));
            }
        }
    }

    private static String pciId(Path dev) {
        var vendor = SysfsReader.firstLine(dev.resolve("vendor"));
        var device = SysfsReader.firstLine(dev.resolve("device"));
        if (vendor == null || device == null) {
            return "";
        }
        return "[" + vendor.replace("0x", "") + ":" + device.replace("0x", "") + "]";
    }
}
