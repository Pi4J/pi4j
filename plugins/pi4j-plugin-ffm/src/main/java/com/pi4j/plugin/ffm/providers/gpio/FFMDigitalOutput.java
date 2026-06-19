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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;

public class FFMDigitalOutput extends DigitalOutputBase implements DigitalOutput {
    private static final Logger logger = LoggerFactory.getLogger(FFMDigitalOutput.class);
    private final IoctlNative ioctl = new IoctlNative();
    private final FileDescriptorNative file = new FileDescriptorNative();

    private final String deviceName;
    private final int bcm;
    private int chipFileDescriptor;
    private boolean closed = false;

    public FFMDigitalOutput(String chipName, DigitalOutputProvider provider, DigitalOutputConfig config) {
        super(provider, config);
        this.bcm = config.bcm();
        this.deviceName = "/dev/gpiochip" + config.bus();
        FFMPermissionHelper.checkDevicePermissions(deviceName, config);
    }

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
