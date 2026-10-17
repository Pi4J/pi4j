package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.RestoreException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.Checkpointable;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.MaskUtils;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.FFMGpioLine;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Native {@link DigitalOutput} implementation for the FFM backend. Requests a single GPIO line from a
 * {@code /dev/gpiochipN} character device as an output via the GPIO v2 character-device ioctl
 * ({@code GPIO_V2_GET_LINE_IOCTL}) and drives its level with {@code GPIO_V2_LINE_SET_VALUES_IOCTL}.
 * <p>
 * Around a CRaC checkpoint the line is released and requested again, driven to the last written state.
 */
public class FFMDigitalOutput extends DigitalOutputBase implements DigitalOutput, Checkpointable {
    private static final Logger logger = LoggerFactory.getLogger(FFMDigitalOutput.class);

    private final FFMGpioLine line;

    /**
     * Creates a digital output bound to a GPIO line. Resolves the target device path
     * ({@code /dev/gpiochip} + the configured bus number), captures the BCM line offset from the
     * configuration, and verifies that the current user has the required permissions on the device
     * file. The line itself is not requested until {@link com.pi4j.io.IOBase#initialize()} is called.
     *
     * @param config   the {@link DigitalOutputConfig} supplying the BCM line offset, bus number and
     *                 initial state
     */
    public FFMDigitalOutput(Context context, DigitalOutputConfig config) {
        super(context, config);
        this.line = new FFMGpioLine(MaskUtils.mask(config.bcm()), config.bus());
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
    public DigitalOutput applyInitialState() throws InitializeException {
        try {
            line.openAndRequest(PinFlag.OUTPUT.getValue(), outputValueAttributes(config().initialState()),
                getClass().getSimpleName());
        } catch (InitializeException e) {
            logger.error("{}-{} - DigitalOutput BCM Initialization error: {}", line.deviceName, line.mask, e.getMessage());
            throw e;
        }
        logger.info("{}-{} - DigitalOutput BCM configured.", line.deviceName, line.mask);
        return super.applyInitialState();
    }

    /**
     * Builds the line attributes that drive the pin to the given state the moment the line is requested.
     * Without them the kernel drives a newly requested output low by default, producing a transient
     * glitch (issue #654).
     *
     * @param state the state to request the line with; {@code null} or {@link DigitalState#UNKNOWN} for none
     * @return the {@code GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES} attribute, or an empty list
     */
    private List<LineConfigAttribute> outputValueAttributes(DigitalState state) {
        var attributes = new ArrayList<LineConfigAttribute>();
        if (state != null && state != DigitalState.UNKNOWN) {
            var values = state.isHigh() ? 1L : 0L;
            var outputValues = new LineAttribute(
                LineAttributeId.GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES.getValue(), 0, values, 0);
            attributes.add(new LineConfigAttribute(outputValues, 1L));
            logger.trace("{}-{} - DigitalOutput BCM requested state: {}", line.deviceName, line.mask, state);
        }
        return attributes;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Releases the GPIO line file descriptor; the cached state is kept.
     */
    @Override
    public void suspend() {
        if (!closed) {
            line.suspend();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Requests the GPIO line again, driving it to the state it had before the checkpoint.
     */
    @Override
    public void resume() throws RestoreException {
        if (!closed) {
            line.resume(PinFlag.OUTPUT.getValue(), outputValueAttributes(state));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Closes the requested line file descriptor, releasing the GPIO line back to the kernel.
     *
     * Throws a ShutdownException if closing the native line file descriptor fails
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        logger.info("{}-{} - closing GPIO BCM.", line.deviceName, line.mask);
        // super.close() sets the closing state so it needs to be called before physically shutting down.
        super.close();
        try {
                line.close();
        } catch (Exception e) {
            throw new ShutdownException(e);
        }
        logger.info("{}-{} - GPIO BCM is closed. Recreate the pin object to reuse.", line.deviceName, line.mask);
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
        line.writeValue(state.getValue().intValue());
        return super.state(state);
    }
}
