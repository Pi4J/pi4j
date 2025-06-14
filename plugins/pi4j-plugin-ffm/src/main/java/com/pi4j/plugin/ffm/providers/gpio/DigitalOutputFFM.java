package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

public class DigitalOutputFFM extends DigitalOutputBase implements DigitalOutput {
    private static final Logger logger = LoggerFactory.getLogger(DigitalOutputFFM.class);
    private final IoctlNative ioctl = new IoctlNative();
    private final FileDescriptorNative file = new FileDescriptorNative();

    private final String chipName;
    private final int pin;
    private int chipFileDescriptor;
    private boolean closed = false;

    public DigitalOutputFFM(String chipName, DigitalOutputProvider provider, DigitalOutputConfig config) {
        super(provider, config);
        this.pin = config.address();
        this.chipName = "/dev/" + chipName;
    }

    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        super.initialize(context);
        try {
            if (!deviceExists()) {
                throw new InitializeException("Device '" + chipName + "' does not exist.");
            }
            if (!canAccessDevice()) {
                var posix = Files.readAttributes(Path.of(chipName), PosixFileAttributes.class);
                logger.error("Inaccessible device: '{} {} {} {}'", PosixFilePermissions.toString(posix.permissions()), posix.owner().getName(), posix.group().getName(), chipName);
                logger.error("Please, read the documentation <link> to setup right permissions.");
                throw new InitializeException("Device '" + chipName + "' cannot be accessed with current user.");
            }
            logger.info("{}-{} - setting up DigitalOutput Pin...", chipName, pin);
            logger.trace("{}-{} - opening device file.", chipName, pin);
            var fd = file.open(chipName, FileFlag.O_RDONLY | FileFlag.O_CLOEXEC);
            var lineInfo = new LineInfo(new byte[]{}, new byte[]{}, pin, 0, 0, new LineAttribute[]{});
            logger.trace("{}-{} - getting line info.", chipName, pin);
            lineInfo = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), lineInfo);
            if ((lineInfo.flags() & PinFlag.USED.getValue()) > 0) {
                shutdown(context());
                throw new InitializeException("Pin " + pin + " is in use");
            }
            logger.trace("{}-{} - DigitalOutput Pin line info: {}", chipName, pin, lineInfo);
            var flags = PinFlag.OUTPUT.getValue();
            var lineConfig = new LineConfig(flags, 0, new LineConfigAttribute[]{});
            var lineRequest = new LineRequest(new int[]{pin}, ("pi4j." + getClass().getSimpleName()).getBytes(), lineConfig, 1, 0, 0);
            var result = ioctl.call(fd, Command.getGpioV2GetLineIoctl(), lineRequest);
            this.chipFileDescriptor = result.fd();

            file.close(fd);
            logger.info("{}-{} - DigitalOutput Pin configured: {}", chipName, pin, result);
        } catch (java.io.IOException e) {
            logger.error("{}-{} - DigitalOutput Pin Initialization error: {}", chipName, pin, e.getMessage());
            throw new InitializeException(e);
        }
        return this;
    }

    @Override
    public DigitalOutput shutdown(Context context) throws ShutdownException {
        super.shutdown(context);
        logger.info("{}-{} - closing GPIO Pin.", chipName, pin);
        try {
            if (chipFileDescriptor > 0) {
                file.close(chipFileDescriptor);
            }
        } catch (Exception e) {
            this.closed = true;
            throw new ShutdownException(e);
        }
        this.closed = true;
        logger.info("{}-{} - GPIO Pin is closed. Recreate the pin object to reuse.", chipName, pin);
        return this;
    }

    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        checkClosed();
        logger.trace("{}-{} - writing GPIO Pin {}.", chipName, pin, state);
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
            throw new Pi4JException("Pin " + pin + " is closed");
        }
    }

    private boolean canAccessDevice() {
        return new File(chipName).canRead();
    }

    private boolean deviceExists() {
        return new File(chipName).exists();
    }
}
