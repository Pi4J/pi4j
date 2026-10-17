package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.RestoreException;
import com.pi4j.io.gpio.MaskUtils;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInput;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Low-level wrapper around a single Linux GPIO v2 character-device line. Shared by
 * {@link FFMDigitalInput} and {@link FFMDigitalOutput} to eliminate duplicated native code.
 * <p>
 * A line is opened via {@link #openAndRequest} (which issues the {@code GPIO_V2_GET_LINE_IOCTL}
 * and retains the resulting per-request file descriptor), read via {@link #readValue()}, written
 * via {@link #writeValue(int)}, and released via {@link #close}.
 * <p>
 * For CRaC checkpoints, {@link #suspend()} releases the line file descriptor while remembering the request,
 * and {@link #resume(long, List)} requests the line again.
 */
public class FFMGpioLine {
    private static final Logger logger = LoggerFactory.getLogger(FFMGpioLine.class);

    final IoctlNative ioctl = new IoctlNative();
    public final FileDescriptorNative file = new FileDescriptorNative();

    public final String deviceName;
    public final long mask;

    public int chipFileDescriptor;
    public boolean closed = false;
    public volatile boolean suspended = false;

    // Last requested line configuration, reused when the line is requested again after a restore.
    private long requestFlags;
    private List<LineConfigAttribute> requestAttributes = List.of();
    private String consumer;

    /**
     * Construct an instance of this helper with the specified mask and bus.
     * @param mask the mask containing pin offsets to pass to ioctl
     * @param bus the bus number of the GPIO chip
     */
    public FFMGpioLine(long mask, int bus) {
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
    public void openAndRequest(long flags, List<LineConfigAttribute> attributes, String consumer)
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
            this.suspended = false;
            this.requestFlags = flags;
            this.requestAttributes = List.copyOf(attributes);
            this.consumer = consumer;
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
    public int readValue() {
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
    public void writeValue(int value) {
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
     * Allow the line to be reconfigured at runtime
     * @param config the line config to apply
     */
    public void reconfigure(LineConfig config) {
        checkClosed();
        logger.trace("{}-{} - reconfiguring GPIO offset.", deviceName, mask);
        try {
            ioctl.call(chipFileDescriptor, Command.getGpioV2LineSetConfigIoctl(), config);
            this.requestFlags = config.flags();
            this.requestAttributes = List.of(config.attrs());
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Closes the per-request line file descriptor, releasing the GPIO line back to the kernel.
     */
    public void close() {
        if (chipFileDescriptor > 0) {
            logger.trace("{}-{} - closing GPIO file descriptor '{}'.", deviceName, mask, chipFileDescriptor);
            file.close(chipFileDescriptor);
        }
        this.closed = true;
    }

    /**
     * Releases the line file descriptor ahead of a CRaC checkpoint, remembering the last requested
     * configuration so {@link #resume(long, List)} can request the line again. No-op when the line is
     * closed or already suspended.
     */
    public void suspend() {
        if (closed || suspended) {
            return;
        }
        logger.debug("{}-{} - suspending GPIO line for checkpoint.", deviceName, mask);
        suspended = true;
        if (chipFileDescriptor > 0) {
            file.close(chipFileDescriptor);
        }
        chipFileDescriptor = -1;
    }

    /**
     * Requests the line again after a CRaC restore with the given flags and attributes. No-op when the
     * line isn't suspended.
     *
     * @param flags      OR-combination of {@link PinFlag} values to request the line with
     * @param attributes line-config attributes to request the line with
     * @throws RestoreException if the line cannot be requested, e.g. because another process claimed it
     */
    public void resume(long flags, List<LineConfigAttribute> attributes) throws RestoreException {
        if (closed || !suspended) {
            return;
        }
        logger.debug("{}-{} - resuming GPIO line after restore.", deviceName, mask);
        try {
            openAndRequest(flags, attributes, consumer);
        } catch (RuntimeException e) {
            throw new RestoreException("Cannot request GPIO offset(s) " + Arrays.toString(MaskUtils.offsets(mask))
                + " on " + deviceName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Requests the line again after a CRaC restore with the configuration it had before it was suspended.
     *
     * @throws RestoreException if the line cannot be requested
     * @see #resume(long, List)
     */
    public void resume() throws RestoreException {
        resume(requestFlags, requestAttributes);
    }

    /**
     * @return the flags the line was last requested or reconfigured with
     */
    public long requestFlags() {
        return requestFlags;
    }

    /**
     * @return the attributes the line was last requested or reconfigured with
     */
    public List<LineConfigAttribute> requestAttributes() {
        return requestAttributes;
    }

    public void checkClosed() {
        if (closed) {
            throw new Pi4JException("Offset " + mask + " is closed");
        }
        if (suspended) {
            throw new Pi4JException("Offset " + mask + " is suspended for a CRaC checkpoint");
        }
    }

    boolean canAccessDevice() {
        return file.access(deviceName, FileFlag.R_OK) == 0;
    }

    boolean deviceExists() {
        return file.access(deviceName, FileFlag.F_OK) == 0;
    }
}
