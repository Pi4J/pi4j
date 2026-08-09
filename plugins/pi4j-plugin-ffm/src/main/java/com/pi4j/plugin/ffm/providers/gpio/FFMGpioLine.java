package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.MaskUtils;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;

/**
 * Low-level wrapper around a single Linux GPIO v2 character-device line. Shared by
 * {@link FFMDigitalInput} and {@link FFMDigitalOutput} to eliminate duplicated native code.
 * <p>
 * A line is opened via {@link #openAndRequest} (which issues the {@code GPIO_V2_GET_LINE_IOCTL}
 * and retains the resulting per-request file descriptor), read via {@link #readValue()}, written
 * via {@link #writeValue(int)}, and released via {@link #close}.
 */
class FFMGpioLine {
    private static final Logger logger = LoggerFactory.getLogger(FFMGpioLine.class);

    final IoctlNative ioctl = new IoctlNative();
    final FileDescriptorNative file = new FileDescriptorNative();

    final String deviceName;
    final long mask;

    int chipFileDescriptor;
    boolean closed = false;

    /**
     * Construct an instance of this helper with the specified mask and bus.
     * @param mask the mask containing pin offsets to pass to ioctl
     * @param bus the bus number of the GPIO chip
     */
    FFMGpioLine(long mask, int bus) {
        if (mask == 0) {
            throw new IllegalArgumentException("Mask must does not specify any offsets");
        }
        this.mask = mask;
        this.deviceName = "/dev/gpiochip" + bus;
    }

    /**
     * Opens the GPIO chip device, verifies that the target BCM line is not already in use,
     * requests it with the supplied flags and attributes, and retains the per-request line fd.
     *
     * @param flags      OR-combination of {@link PinFlag} values (direction, edge detection, bias)
     * @param attributes optional line-config attributes (debounce period, initial output value, etc.)
     * @param consumer   label embedded in the kernel line-request (appears in {@code gpioinfo})
     * @throws InitializeException if the device is inaccessible, the line is in use, or an
     *                             ioctl / file-open call fails
     */
    void openAndRequest(long flags, List<LineConfigAttribute> attributes, String consumer)
        throws InitializeException {
        if (!canAccessDevice()) {
            try {
                var posix = Files.readAttributes(Path.of(deviceName), PosixFileAttributes.class);
                logger.error("Inaccessible device: '{} {} {} {}'",
                    PosixFilePermissions.toString(posix.permissions()),
                    posix.owner().getName(), posix.group().getName(), deviceName);
            } catch (java.io.IOException e) {
                logger.error("Cannot read device attributes for '{}'", deviceName);
                throw new InitializeException(e);
            }
            logger.error("Please, read the documentation https://www.pi4j.com/documentation/providers/ffm/ to setup right permissions.");
            throw new InitializeException(
                "Device '" + deviceName + "' cannot be accessed with current user.");
        }
        logger.info("{}-{} - requesting GPIO line ({})...", deviceName, mask, consumer);
        logger.trace("{}-{} - opening device file.", deviceName, mask);
        var fd = file.open(deviceName, FileFlag.O_RDONLY | FileFlag.O_CLOEXEC);
        // The chip fd is only needed to read line info and issue the request.
        // Close it in a finally so any early-exit cannot leak it.
        try {
            var linesInUse = new ArrayList<Integer>();
            var offsets = MaskUtils.offsets(mask);
            for (var offset : offsets) {
                var lineInfo = new LineInfo(new byte[]{}, new byte[]{}, offset, 0, 0, new LineAttribute[]{});
                logger.trace("{}-{} - getting line info.", deviceName, mask);
                lineInfo = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), lineInfo);
                if ((lineInfo.flags() & PinFlag.USED.getValue()) > 0) {
                    linesInUse.add(offset);
                }
                logger.trace("{}-{} - GPIO line info: {}", deviceName, mask, lineInfo);
            }
            if (!linesInUse.isEmpty()) {
                throw new InitializeException("Offsets are in use: " + linesInUse);
            }

            var lineConfig = new LineConfig(flags, attributes.size(), attributes.toArray(new LineConfigAttribute[0]));
            var lineRequest = new LineRequest(offsets, ("pi4j." + consumer).getBytes(), lineConfig, offsets.length, 0, 0);
            var result = ioctl.call(fd, Command.getGpioV2GetLineIoctl(), lineRequest);
            this.chipFileDescriptor = result.fd();
            this.closed = false;
            logger.info("{}-{} - GPIO line configured: {}", deviceName, mask, result);
        } finally {
            file.close(fd);
        }
    }

    /**
     * Reads the current logic level of the requested line via {@code GPIO_V2_LINE_GET_VALUES_IOCTL}.
     *
     * @return the current {@link DigitalState}
     * @throws Pi4JException if the line is closed or the ioctl call fails
     */
    int readValue() {
        checkClosed();
        logger.trace("{}-{} - reading GPIO offset.", deviceName, mask);
        var lineValues = new LineValues(0, Long.MAX_VALUE);
        try {
            var result = ioctl.call(chipFileDescriptor, Command.getGpioV2GetValuesIoctl(), lineValues);
            logger.trace("{}-{} - GPIO offset state is {}.", deviceName, mask, result.bits());
            return (int) result.bits();
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Drives the requested line to the given level via {@code GPIO_V2_LINE_SET_VALUES_IOCTL}.
     *
     * @param value the desired numeric value
     * @throws Pi4JException if the line is closed or the ioctl call fails
     */
    void writeValue(int value) {
        checkClosed();
        logger.trace("{}-{} - writing GPIO offset {}.", deviceName, mask, value);
        var lineValues = new LineValues(value, Long.MAX_VALUE);
        try {
            ioctl.call(chipFileDescriptor, Command.getGpioV2SetValuesIoctl(), lineValues);
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Closes the per-request line file descriptor, releasing the GPIO line back to the kernel.
     */
    void close() {
        if (chipFileDescriptor > 0) {
            logger.trace("{}-{} - closing GPIO file descriptor '{}'.", deviceName, mask, chipFileDescriptor);
            file.close(chipFileDescriptor);
        }
        this.closed = true;
    }

    void checkClosed() {
        if (closed) {
            throw new Pi4JException("Offset " + mask + " is closed");
        }
    }

    boolean canAccessDevice() {
        return file.access(deviceName, FileFlag.R_OK) == 0;
    }

    boolean deviceExists() {
        return file.access(deviceName, FileFlag.F_OK) == 0;
    }
}
