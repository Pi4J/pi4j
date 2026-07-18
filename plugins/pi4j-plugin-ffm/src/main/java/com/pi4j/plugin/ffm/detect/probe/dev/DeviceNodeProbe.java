package com.pi4j.plugin.ffm.detect.probe.dev;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.ChipInfo;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.i2c.I2cConstants;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.detect.model.GpioLine;
import com.pi4j.plugin.ffm.detect.model.HWInterfaces;
import com.pi4j.plugin.ffm.detect.probe.sysfs.SysfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Opens a live {@code /dev} character device and asks the kernel to describe the controller behind it,
 * reusing the plugin's existing native plumbing — {@link FileDescriptorNative}, {@link IoctlNative}, the
 * {@link Command} ioctl request numbers and the {@link ChipInfo} layout — plus {@link SysfsReader} for the
 * driver/adapter names that {@code ioctl} does not expose.
 * <ul>
 *     <li>GPIO — chip {@code name}, {@code label} (driver) and line count from {@link Command#getGpioGetChipInfoIoctl()};</li>
 *     <li>I2C — the adapter name from sysfs plus the {@link Command#getI2CFuncs()} bitmask decoded into capabilities;</li>
 *     <li>SPI — bus/chip-select from the node name plus mode, bits-per-word, max speed and bit order.</li>
 * </ul>
 * A permission problem is reported with guidance rather than a failing native open.
 */
public final class DeviceNodeProbe {

    private static final Logger logger = LoggerFactory.getLogger(DeviceNodeProbe.class);

    private final FileDescriptorNative file = new FileDescriptorNative();
    private final IoctlNative ioctl = new IoctlNative();
    /**
     * Outcome of probing a live device node: the driver/controller name, a free-form detail string, and
     * (for GPIO chips) the per-line table. The physical-pin field of each line is left {@code null} here —
     * the board-specific header mapping is applied later by the orchestrator that knows the board profile.
     *
     * @param driver      driver/controller/adapter name, or {@code null} when unknown
     * @param description human readable details
     * @param lines       the GPIO chip's lines (empty for non-GPIO controllers)
     */
    public record ProbeResult(String driver, String description, List<GpioLine> lines) {

        /**
         * @param driver      driver/controller/adapter name
         * @param description human readable details
         * @return a result with no GPIO lines
         */
        public static ProbeResult of(String driver, String description) {
            return new ProbeResult(driver, description, List.of());
        }

        /**
         * @param driver      driver/controller/adapter name
         * @param description human readable details
         * @param lines       the GPIO chip's lines
         * @return a result carrying a driver name, details and a line table
         */
        public static ProbeResult of(String driver, String description, List<GpioLine> lines) {
            return new ProbeResult(driver, description, lines);
        }

        /**
         * @param description human readable details
         * @return a result with no known driver name and no GPIO lines
         */
        public static ProbeResult description(String description) {
            return new ProbeResult(null, description, List.of());
        }
    }
    /**
     * Probes a live device node for its driver name and a one-line detail string.
     *
     * @param subsystem the subsystem the node belongs to
     * @param path      the {@code /dev} node path, e.g. {@code /dev/i2c-1}
     * @return a {@link ProbeResult}; never throws — failures degrade to a "present" marker
     */
    public ProbeResult describe(HWInterfaces subsystem, String path) {
//        // Reuse the plugin's permission helper: if the current user can't read the node, say why and which
//        // group to join instead of attempting a native open that would fail with a cryptic EACCES.
//        var accessIssue = FFMPermissionHelper.diagnoseAccess(path);
//        if (accessIssue.isPresent()) {
//            return ProbeResult.description("present (" + accessIssue.get() + ")");
//        }

        try {
            FFMPermissionHelper.checkDevicePermissions(path, subsystem, false);
        } catch (Pi4JException e) {
            return ProbeResult.description("Error: " + e.getMessage());
        }
        try {
            return switch (subsystem) {
                case GPIO -> describeGpio(path);
                case I2C -> describeI2c(path);
                case SPI -> describeSpi(path);
                default -> ProbeResult.description("present");
            };
        } catch (RuntimeException e) {
            logger.debug("Probe of {} failed: {}", path, e.getMessage());
            return ProbeResult.description("present (could not probe: " + e.getMessage() + ")");
        }
    }

    private ProbeResult describeGpio(String path) {
        var fd = file.open(path, FileFlag.O_RDONLY);
        try {
            var info = ioctl.call(fd, Command.getGpioGetChipInfoIoctl(), ChipInfo.createEmpty());
            var name = new String(info.name()).trim();
            var label = new String(info.label()).trim();
            var driver = label.isEmpty() ? "gpiochip" : label;
            var description = "name=" + (name.isEmpty() ? "?" : name) + ", lines=" + info.lines();
            return ProbeResult.of(driver, description, readLines(fd, info.lines()));
        } finally {
            file.close(fd);
        }
    }

    /**
     * Reads each line of an open GPIO chip via {@link Command#getGpioV2GetLineInfoIoctl()}, capturing the
     * line offset, kernel name and in-use flag. The physical header pin is left {@code null} here; it is
     * filled in by the orchestrator from the board profile. A failure on one line is skipped, not fatal.
     */
    private List<GpioLine> readLines(int fd, int lineCount) {
        var lines = new ArrayList<GpioLine>(lineCount);
        for (int offset = 0; offset < lineCount; offset++) {
            try {
                var request = new LineInfo(new byte[]{}, new byte[]{}, offset, 0, 0, new LineAttribute[]{});
                var lineInfo = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), request);
                var name = new String(lineInfo.name()).trim();
                var used = (lineInfo.flags() & PinFlag.USED.getValue()) != 0;
                lines.add(new GpioLine(offset, null, name.isEmpty() ? null : name, used));
            } catch (RuntimeException e) {
                logger.debug("Could not read GPIO line {}: {}", offset, e.getMessage());
            }
        }
        return lines;
    }

    private ProbeResult describeI2c(String path) {
        var fd = file.open(path, FileFlag.O_RDONLY);
        try {
            var funcs = ioctl.call(fd, Command.getI2CFuncs(), 0L);
            var adapter = SysfsReader.firstLine(Path.of("/sys/class/i2c-dev", nodeName(path), "name"));
            var driver = adapter == null ? "i2c-adapter" : adapter;
            var kind = (funcs & I2cConstants.I2C_FUNC_I2C.getValue()) != 0 ? "I2C+SMBus" : "SMBus-only";
            var description = kind + String.format(", funcs=0x%08x [", funcs) + decodeFunctionality(funcs) + "]";
            return ProbeResult.of(driver, description);
        } finally {
            file.close(fd);
        }
    }

    private ProbeResult describeSpi(String path) {
        var fd = file.open(path, FileFlag.O_RDONLY);
        try {
            var mode = ioctl.call(fd, Command.getSpiIocRdMode(), 0) & 0xFF;
            var bits = ioctl.call(fd, Command.getSpiIocRdBitsPerWord(), 0) & 0xFF;
            var maxSpeedHz = ioctl.call(fd, Command.getSpiIocRdMaxSpeedHz(), 0);
            var lsbFirst = (ioctl.call(fd, Command.getSpiIocRdLsbFirst(), 0) & 0xFF) != 0;
            var modalias = SysfsReader.firstLine(Path.of("/sys/class/spidev", nodeName(path), "device", "modalias"));
            var driver = modalias == null ? "spidev" : modalias;
            var bitsPerWord = bits == 0 ? 8 : bits; // 0 means the default of 8
            var flags = mode > 0x3 ? String.format(" (flags=0x%02x)", mode) : "";
            var description = busChipSelect(path)
                + ", mode=" + (mode & 0x3) + flags
                + ", bits=" + bitsPerWord
                + ", max=" + maxSpeedHz + " Hz"
                + ", LSB-first=" + (lsbFirst ? "yes" : "no");
            return ProbeResult.of(driver, description);
        } finally {
            file.close(fd);
        }
    }

    /**
     * Decodes an I2C functionality bitmask into a comma-separated list of capability names, reusing the
     * {@link I2cConstants} flags from {@code common}. Only atomic (single-bit) {@code I2C_FUNC_*} flags are
     * listed, so composite aliases (e.g. {@code I2C_FUNC_SMBUS_BYTE}) do not duplicate the output.
     *
     * @param funcs the bitmask returned by {@code I2C_FUNCS}
     * @return e.g. {@code "I2C, SMBUS_QUICK, SMBUS_READ_BYTE, SMBUS_WRITE_BYTE"}
     */
    static String decodeFunctionality(long funcs) {
        return Arrays.stream(I2cConstants.values())
            .filter(c -> c.name().startsWith("I2C_FUNC_"))
            .filter(c -> Integer.bitCount(c.getValue()) == 1)
            .filter(c -> (funcs & (c.getValue() & 0xFFFFFFFFL)) != 0)
            .map(c -> c.name().substring("I2C_FUNC_".length()))
            .collect(Collectors.joining(", "));
    }

    private static String nodeName(String path) {
        return Path.of(path).getFileName().toString();
    }

    /**
     * Turns a {@code spidevB.C} node name into a {@code "bus=B cs=C"} description.
     */
    private static String busChipSelect(String path) {
        var node = nodeName(path);
        var trimmed = node.startsWith("spidev") ? node.substring("spidev".length()) : node;
        var dot = trimmed.indexOf('.');
        if (dot > 0) {
            return "bus=" + trimmed.substring(0, dot) + " cs=" + trimmed.substring(dot + 1);
        }
        return node;
    }
}
