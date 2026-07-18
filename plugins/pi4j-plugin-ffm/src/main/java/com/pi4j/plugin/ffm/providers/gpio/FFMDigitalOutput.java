package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.detect.model.HWInterfaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;

/**
 * Native {@link DigitalOutput} implementation for the FFM backend. Requests a single GPIO line from a
 * {@code /dev/gpiochipN} character device as an output via the GPIO v2 character-device ioctl
 * ({@code GPIO_V2_GET_LINE_IOCTL}) and drives its level with {@code GPIO_V2_LINE_SET_VALUES_IOCTL}.
 */
public class FFMDigitalOutput extends DigitalOutputBase implements DigitalOutput {
    private static final Logger logger = LoggerFactory.getLogger(FFMDigitalOutput.class);
    private final IoctlNative ioctl = new IoctlNative();
    private final FileDescriptorNative file = new FileDescriptorNative();

    private final String deviceName;
    private final int bcm;
    private int chipFileDescriptor;
    private boolean closed = false;

    /**
     * Creates a digital output bound to a GPIO line. Resolves the target device path
     * ({@code /dev/gpiochip} + the configured bus number), captures the BCM line offset from the
     * configuration, and verifies that the current user has the required permissions on the device
     * file. The line itself is not requested until {@link #initialize(Context)} is called.
     *
     * @param provider the {@link DigitalOutputProvider} that created this instance
     * @param config   the {@link DigitalOutputConfig} supplying the BCM line offset, bus number and
     *                 initial state
     */
    public FFMDigitalOutput(DigitalOutputProvider provider, DigitalOutputConfig config) {
        super(provider, config);
        this.bcm = config.bcm();
        this.deviceName = "/dev/gpiochip" + config.bus();
        FFMPermissionHelper.checkDevicePermissions(deviceName, HWInterfaces.GPIO, true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Opens the GPIO chip device, reads the line info to ensure the BCM line is not already in use,
     * then requests it as an output via {@code GPIO_V2_GET_LINE_IOCTL}. When the configuration
     * specifies an initial state, that level is supplied in the same request as a
     * {@code GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES} attribute so the pin is driven to it the moment the
     * line is requested, avoiding the transient low-then-high glitch that occurs when the value is set
     * only after the request (issue #654). The returned per-request line file descriptor is retained
     * for subsequent value writes.
     *
     * @throws InitializeException if the device cannot be accessed, the line is already in use, or a
     *                             native {@code ioctl}/open call fails
     */
    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        try {
            if (!canAccessDevice()) {
                var posix = Files.readAttributes(Path.of(deviceName), PosixFileAttributes.class);
                logger.error("Inaccessible device: '{} {} {} {}'", PosixFilePermissions.toString(posix.permissions()), posix.owner().getName(), posix.group().getName(), deviceName);
                logger.error("Please, read the documentation <link> to setup right permissions.");
                throw new InitializeException("Device '" + deviceName + "' cannot be accessed with current user.");
            }
            logger.info("{}-{} - setting up DigitalOutput BCM...", deviceName, bcm);
            logger.trace("{}-{} - opening device file.", deviceName, bcm);
            var fd = file.open(deviceName, FileFlag.O_RDONLY | FileFlag.O_CLOEXEC);
            var lineInfo = new LineInfo(new byte[]{}, new byte[]{}, bcm, 0, 0, new LineAttribute[]{});
            logger.trace("{}-{} - getting line info.", deviceName, bcm);
            lineInfo = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), lineInfo);
            if ((lineInfo.flags() & PinFlag.USED.getValue()) > 0) {
                this.shutdownInternal(context());
                throw new InitializeException("BCM " + bcm + " is in use");
            }
            logger.trace("{}-{} - DigitalOutput BCM line info: {}", deviceName, bcm, lineInfo);
            var flags = PinFlag.OUTPUT.getValue();
            var attributes = new ArrayList<LineConfigAttribute>();
            var initialState = config().initialState();
            if (initialState != null) {
                // Pass the configured initial state to the kernel as part of the line request so the
                // pin is driven to it the moment the line is requested. Without this the kernel drives
                // a newly requested output low by default and the pin is only switched to the initial
                // state afterwards, producing a transient low-then-high glitch (issue #654).
                var values = initialState.isHigh() ? 1L : 0L;
                var outputValues = new LineAttribute(LineAttributeId.GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES.getValue(), 0, values, 0);
                // The single requested line lives at index 0 of the offsets array, so its mask bit is 0.
                attributes.add(new LineConfigAttribute(outputValues, 1L));
                logger.trace("{}-{} - DigitalOutput BCM initial state: {}", deviceName, bcm, initialState);
            }
            var lineConfig = new LineConfig(flags, attributes.size(), attributes.toArray(new LineConfigAttribute[0]));
            var lineRequest = new LineRequest(new int[]{bcm}, ("pi4j." + getClass().getSimpleName()).getBytes(), lineConfig, 1, 0, 0);
            var result = ioctl.call(fd, Command.getGpioV2GetLineIoctl(), lineRequest);
            this.chipFileDescriptor = result.fd();

            file.close(fd);
            logger.info("{}-{} - DigitalOutput BCM configured: {}", deviceName, bcm, result);
        } catch (java.io.IOException e) {
            logger.error("{}-{} - DigitalOutput BCM Initialization error: {}", deviceName, bcm, e.getMessage());
            throw new InitializeException(e);
        }
        return super.initialize(context);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Closes the requested line file descriptor, releasing the GPIO line back to the kernel. The pin
     * object cannot be reused afterwards; a new one must be created to drive the same line again.
     *
     * @throws ShutdownException if closing the native line file descriptor fails
     */
    @Override
    public DigitalOutput shutdownInternal(Context context) throws ShutdownException {
        super.shutdownInternal(context);
        logger.info("{}-{} - closing GPIO BCM.", deviceName, bcm);
        try {
            if (chipFileDescriptor > 0) {
                file.close(chipFileDescriptor);
            }
        } catch (Exception e) {
            this.closed = true;
            throw new ShutdownException(e);
        }
        this.closed = true;
        logger.info("{}-{} - GPIO BCM is closed. Recreate the pin object to reuse.", deviceName, bcm);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Drives the requested line to the given level via {@code GPIO_V2_LINE_SET_VALUES_IOCTL}.
     *
     * @throws IOException     declared by the contract for write failures
     * @throws Pi4JException   if the line is closed or the native value-write {@code ioctl} fails
     */
    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        checkClosed();
        logger.trace("{}-{} - writing GPIO BCM {}.", deviceName, bcm, state);
        var lineValues = new LineValues(state.getValue().intValue(), 1);
        try {
            ioctl.call(chipFileDescriptor, Command.getGpioV2SetValuesIoctl(), lineValues);
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
        return super.state(state);
    }

    /**
     * Checks if GPIO Pin is closed.
     */
    private void checkClosed() {
        if (closed) {
            throw new Pi4JException("BCM " + bcm + " is closed");
        }
    }

    private boolean canAccessDevice() {
        return file.access(deviceName, FileFlag.R_OK) == 0;
    }

    private boolean deviceExists() {
        return file.access(deviceName, FileFlag.F_OK) == 0;
    }
}
