package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Native {@link DigitalOutput} implementation for the FFM backend. Requests a single GPIO line from a
 * {@code /dev/gpiochipN} character device as an output via the GPIO v2 character-device ioctl
 * ({@code GPIO_V2_GET_LINE_IOCTL}) and drives its level with {@code GPIO_V2_LINE_SET_VALUES_IOCTL}.
 * <p>
 * The pin can be atomically reconfigured as a digital input at runtime via {@link #reconfigure()}.
 */
public class FFMDigitalOutput extends DigitalOutputBase implements DigitalOutput {
    private static final Logger logger = LoggerFactory.getLogger(FFMDigitalOutput.class);

    private final FFMGpioLine line;

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
        this.line = new FFMGpioLine(config.bcm(), config.bus());
        FFMPermissionHelper.checkDevicePermissions(line.deviceName, config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Opens the GPIO chip device, reads the line info to ensure the BCM line is not already in use,
     * then requests it as an output via {@code GPIO_V2_GET_LINE_IOCTL}. When the configuration
     * specifies an initial state, that level is supplied in the same request as a
     * {@code GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES} attribute so the pin is driven to it the moment the
     * line is requested, avoiding the transient low-then-high glitch (issue #654).
     *
     * @throws InitializeException if the device cannot be accessed, the line is already in use, or a
     *                             native {@code ioctl}/open call fails
     */
    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        var flags = PinFlag.OUTPUT.getValue();
        var attributes = new ArrayList<LineConfigAttribute>();
        var initialState = config().initialState();
        if (initialState != null) {
            // Supply the initial state as part of the line request so the pin is driven to it the
            // moment the line is claimed — without this the kernel drives a newly requested output
            // low by default, producing a transient glitch (issue #654).
            var values = initialState.isHigh() ? 1L : 0L;
            var outputValues = new LineAttribute(
                LineAttributeId.GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES.getValue(), 0, values, 0);
            attributes.add(new LineConfigAttribute(outputValues, 1L));
            logger.trace("{}-{} - DigitalOutput BCM initial state: {}", line.deviceName, line.bcm, initialState);
        }
        try {
            line.openAndRequest(flags, attributes, getClass().getSimpleName());
        } catch (InitializeException e) {
            logger.error("{}-{} - DigitalOutput BCM Initialization error: {}", line.deviceName, line.bcm, e.getMessage());
            throw e;
        }
        logger.info("{}-{} - DigitalOutput BCM configured.", line.deviceName, line.bcm);
        return super.initialize(context);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Closes the requested line file descriptor, releasing the GPIO line back to the kernel.
     *
     * @throws ShutdownException if closing the native line file descriptor fails
     */
    @Override
    public DigitalOutput shutdownInternal(Context context) throws ShutdownException {
        super.shutdownInternal(context);
        logger.info("{}-{} - closing GPIO BCM.", line.deviceName, line.bcm);
        try {
            line.close();
        } catch (Exception e) {
            throw new ShutdownException(e);
        }
        logger.info("{}-{} - GPIO BCM is closed. Recreate the pin object to reuse.", line.deviceName, line.bcm);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Drives the requested line to the given level via {@code GPIO_V2_LINE_SET_VALUES_IOCTL},
     * then updates the cached state in the base class.
     *
     * @throws IOException declared by the contract for write failures
     * @throws com.pi4j.exception.Pi4JException if the line is closed or the ioctl call fails
     */
    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        line.writeState(state);
        return super.state(state);
    }
}
