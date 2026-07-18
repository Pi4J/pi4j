package com.pi4j.plugin.ffm.detect.model;

import java.util.List;

/**
 * A single hardware I/O controller discovered during detection.
 *
 * @param subsystem   which kind of I/O this controller provides
 * @param source      where the evidence came from (a live device node, the device tree, or sysfs buses)
 * @param state       whether the controller is usable right now, or merely present-but-not-enabled
 * @param identifier  a stable handle for the controller, e.g. {@code /dev/i2c-1}, {@code spi@7e204000}
 *                    or a PCI address {@code 0000:00:1f.4}
 * @param driver      the driver/controller/adapter name (chip label, I2C adapter name, DT {@code compatible}
 *                    binding, ...); may be {@code null} when unknown
 * @param description free-form details (line count, decoded I2C functionality, SPI mode/speed, status, ...)
 * @param enableHint  if {@code state} is not {@link State#ACTIVE}, how to turn the controller on; otherwise {@code null}
 * @param lines       for a live GPIO chip, the per-line offset → physical-pin → name mapping; empty otherwise
 */
public record DetectedHardware(HWInterfaces subsystem, Source source, State state, String identifier,
                               String driver,
                               String description,
                               String enableHint,
                               List<GpioLine> lines) {

    /**
     * Where the knowledge about a controller originated.
     */
    public enum Source {
        /**
         * A live {@code /dev} character device, probed directly via {@code ioctl}.
         */
        DEVICE_NODE,
        /**
         * The flattened device tree at {@code /proc/device-tree} (ARM/SoC platforms).
         */
        DEVICE_TREE,
        /**
         * The sysfs bus/class hierarchy ({@code /sys/bus}, {@code /sys/class}) — works on x86/ACPI/PCI too.
         */
        SYSFS_BUS
    }

    /**
     * The usability of a discovered controller.
     */
    public enum State {
        /**
         * Driver bound, device node present — ready to use now.
         */
        ACTIVE,
        /**
         * Hardware exists but is switched off (device tree {@code status = "disabled"}, or driver not loaded).
         */
        DISABLED,
        /**
         * Hardware/driver present in sysfs but no userspace device node was exported (e.g. {@code i2c-dev} not loaded).
         */
        NOT_BOUND
    }

    /**
     * Convenience factory for a live, ready-to-use controller.
     */
    public static DetectedHardware active(HWInterfaces subsystem, String identifier, String driver,
                                          String description, List<GpioLine> lines) {
        return new DetectedHardware(subsystem, Source.DEVICE_NODE, State.ACTIVE, identifier, driver, description,
            null, lines);
    }

    /**
     * @return {@code true} when the controller can be used without any configuration change
     */
    public boolean isActive() {
        return state == State.ACTIVE;
    }
}
