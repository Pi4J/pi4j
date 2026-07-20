package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
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
import java.util.List;

/**
 * Low-level wrapper around a single Linux GPIO v2 character-device line. Shared by
 * {@link FFMDigitalInput} and {@link FFMDigitalOutput} to eliminate duplicated native code.
 * <p>
 * A line is opened via {@link #openAndRequest} (which issues the {@code GPIO_V2_GET_LINE_IOCTL}
 * and retains the resulting per-request file descriptor), read via {@link #readState}, written
 * via {@link #writeState}, and released via {@link #close}.
 */
class FFMGpioLine {
    private static final Logger logger = LoggerFactory.getLogger(FFMGpioLine.class);

    final IoctlNative ioctl = new IoctlNative();
    final FileDescriptorNative file = new FileDescriptorNative();

    final String deviceName;
    final int bcm;

    int chipFileDescriptor;
    boolean closed = false;

    FFMGpioLine(int bcm, int bus) {
        this.bcm = bcm;
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
            logger.error("Please, read the documentation <link> to setup right permissions.");
            throw new InitializeException(
                "Device '" + deviceName + "' cannot be accessed with current user.");
        }
        logger.info("{}-{} - requesting GPIO line ({})...", deviceName, bcm, consumer);
        logger.trace("{}-{} - opening device file.", deviceName, bcm);
        var fd = file.open(deviceName, FileFlag.O_RDONLY | FileFlag.O_CLOEXEC);
        // The chip fd is only needed to read line info and issue the request.
        // Close it in a finally so any early-exit cannot leak it.
        try {
            var lineInfo = new LineInfo(new byte[]{}, new byte[]{}, bcm, 0, 0, new LineAttribute[]{});
            logger.trace("{}-{} - getting line info.", deviceName, bcm);
            lineInfo = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), lineInfo);
            if ((lineInfo.flags() & PinFlag.USED.getValue()) > 0) {
                throw new InitializeException("BCM " + bcm + " is in use");
            }
            logger.trace("{}-{} - GPIO line info: {}", deviceName, bcm, lineInfo);
            var lineConfig = new LineConfig(flags, attributes.size(),
                attributes.toArray(new LineConfigAttribute[0]));
            var lineRequest = new LineRequest(
                new int[]{bcm}, ("pi4j." + consumer).getBytes(), lineConfig, 1, 0, 0);
            var result = ioctl.call(fd, Command.getGpioV2GetLineIoctl(), lineRequest);
            this.chipFileDescriptor = result.fd();
            this.closed = false;
            logger.info("{}-{} - GPIO line configured: {}", deviceName, bcm, result);
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
    DigitalState readState() {
        checkClosed();
        logger.trace("{}-{} - reading GPIO BCM.", deviceName, bcm);
        var lineValues = new LineValues(0, 1);
        try {
            var result = ioctl.call(chipFileDescriptor, Command.getGpioV2GetValuesIoctl(), lineValues);
            var state = DigitalState.getState(result.bits());
            logger.trace("{}-{} - GPIO BCM state is {}.", deviceName, bcm, state);
            return state;
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Drives the requested line to the given level via {@code GPIO_V2_LINE_SET_VALUES_IOCTL}.
     *
     * @param state the desired {@link DigitalState}
     * @throws Pi4JException if the line is closed or the ioctl call fails
     */
    void writeState(DigitalState state) {
        checkClosed();
        logger.trace("{}-{} - writing GPIO BCM {}.", deviceName, bcm, state);
        var lineValues = new LineValues(state.getValue().intValue(), 1);
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
            logger.trace("{}-{} - closing GPIO file descriptor '{}'.", deviceName, bcm, chipFileDescriptor);
            file.close(chipFileDescriptor);
        }
        this.closed = true;
    }

    void checkClosed() {
        if (closed) {
            throw new Pi4JException("BCM " + bcm + " is closed");
        }
    }

    boolean canAccessDevice() {
        return file.access(deviceName, FileFlag.R_OK) == 0;
    }

    boolean deviceExists() {
        return file.access(deviceName, FileFlag.F_OK) == 0;
    }
}
