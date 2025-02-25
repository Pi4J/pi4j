package com.pi4j.library.gpiod.internal;

import com.pi4j.boardinfo.util.BoardInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GpioDContext implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(GpioDContext.class);

    private static final GpioDContext instance;

    static {
        instance = new GpioDContext();
    }

    public static GpioDContext getInstance() {
        return instance;
    }

    private GpioChip gpioChip;
    private final Map<Integer, GpioLine> openLines;
    private final Set<Long> openLineEvents;
    private String desiredChipName = null; // Holds the chip name specified by the user

    public GpioDContext() {
        this.openLines = new HashMap<>();
        this.openLineEvents = new HashSet<>();
    }

    /**
     * Sets the name of the GPIO chip to use.
     *
     * @param chipName The name of the GPIO chip to use (e.g., "gpiochip0", "gpiochip2").
     *                 If null or an empty string is specified, the default chip will be selected.
     */
    public synchronized void setChip(String chipName) {
        if (this.gpioChip != null) {
            logger.warn("GpioD context already initialized with chip: {}.  Ignoring new chip request.", this.gpioChip.getName());
            return;
        }
        this.desiredChipName = chipName;
        logger.debug("GpioD chip name set to: {}", (chipName == null || chipName.isEmpty()) ? "[default]" : chipName);
    }

    /**
     * Sets the number of the GPIO chip to use.
     *
     * @param chipNumber The number of the GPIO chip to use (e.g., 0, 2).
     *                   If a negative value is specified, the default chip will be selected.
     */
    public synchronized void setChip(int chipNumber) {
        if (chipNumber < 0) {
            setChip(null);
        } else {
            setChip("gpiochip" + chipNumber);
        }
    }

    /**
     * Initializes the GpioDContext.
     * If no chip is specified with setChip(), the default chip will be selected.
     */
    public synchronized void initialize() {
        if (!BoardInfoHelper.runningOnRaspberryPi()) {
            logger.warn("Can't initialize GpioD context, board model is unknown");
            return;
        }

        // already initialized
        if (this.gpioChip != null) {
            logger.info("GpioD context already initialized with chip: {}", this.gpioChip.getName());
            return;
        }

        long chipIterPtr = GpioD.chipIterNew();
        GpioChip found = null;
        try {
            Long chipPtr;
            while ((chipPtr = GpioD.chipIterNextNoClose(chipIterPtr)) != null) {
                GpioChip chip = new GpioChip(chipPtr);
                boolean useThisChip = false;

                if (this.desiredChipName != null && !this.desiredChipName.isEmpty()) {
                    // If the user has specified a chip name
                    if (chip.getName().equals(this.desiredChipName)) {
                        useThisChip = true;
                    }
                } else {
                    // If the user has not specified a chip name, use the first chip with a label containing "pinctrl" as the default
                    if (chip.getLabel().contains("pinctrl")) {
                        useThisChip = true;
                    }
                }

                if (useThisChip) {
                    found = chip;
                    break;
                } else {
                    GpioD.chipClose(chip.getCPointer());
                }
            }
        } finally {
            if (found != null) {
                GpioD.chipIterFreeNoClose(chipIterPtr);
            } else {
                GpioD.chipIterFree(chipIterPtr);
            }
        }

        if (found == null) {
            if (this.desiredChipName != null && !this.desiredChipName.isEmpty()) {
                throw new IllegalStateException("Couldn't find gpiochip with name: " + this.desiredChipName);
            } else {
                throw new IllegalStateException("Couldn't identify suitable gpiochip (no chip name specified and no 'pinctrl' chip found)!");
            }
        }

        this.gpioChip = found;
        logger.info("Using chip {} {}", this.gpioChip.getName(), this.gpioChip.getLabel());
    }

    /**
     * Initializes the GpioDContext with properties.
     * If no chip is specified with setChip(), the default chip will be selected.
     */
    public synchronized void initialize(Map<String, String> properties) {
        if (!BoardInfoHelper.runningOnRaspberryPi()) {
            logger.warn("Can't initialize GpioD context, board model is unknown");
            return;
        }

        // already initialized
        if (this.gpioChip != null) {
            logger.info("GpioD context already initialized with chip: {}", this.gpioChip.getName());
            return;
        }

        // Set chip name from properties if available
        String chipName = properties.get("gpio.chip.name");
        if (chipName != null) {
            setChip(chipName);
        }

        long chipIterPtr = GpioD.chipIterNew();
        GpioChip found = null;
        try {
            Long chipPtr;
            while ((chipPtr = GpioD.chipIterNextNoClose(chipIterPtr)) != null) {
                GpioChip chip = new GpioChip(chipPtr);
                boolean useThisChip = false;
                if (this.desiredChipName != null && !this.desiredChipName.isEmpty()) {
                    if (chip.getName().equals(this.desiredChipName)) {
                        useThisChip = true;
                    }
                } else {
                    if (chip.getLabel().contains("pinctrl")) {
                        useThisChip = true;
                    }
                }

                if (useThisChip) {
                    found = chip;
                    break;
                } else {
                    GpioD.chipClose(chip.getCPointer());
                }
            }
        } finally {
            if (found != null) {
                GpioD.chipIterFreeNoClose(chipIterPtr);
            } else {
                GpioD.chipIterFree(chipIterPtr);
            }
        }

        if (found == null) {
            if (this.desiredChipName != null && !this.desiredChipName.isEmpty()) {
                throw new IllegalStateException("Couldn't find gpiochip with name: " + this.desiredChipName);
            } else {
                throw new IllegalStateException("Couldn't identify suitable gpiochip (no chip name specified and no 'pinctrl' chip found)!");
            }
        }

        this.gpioChip = found;
        logger.info("Using chip {} {}", this.gpioChip.getName(), this.gpioChip.getLabel());
    }


    public synchronized GpioLine getOrOpenLine(int offset) {
        if (this.gpioChip == null) {
            initialize(); // If the chip has not been initialized yet, try to initialize with the default chip
        }
        if (this.gpioChip == null)
            throw new IllegalStateException("No gpio chip yet initialized!");
        return this.openLines.computeIfAbsent(offset, o -> {
            long chipLinePtr = GpioD.chipGetLine(this.gpioChip.getCPointer(), offset);
            return new GpioLine(o, chipLinePtr);
        });
    }

    public synchronized void closeLine(GpioLine gpioLine) {
        long linePtr = gpioLine.getCPointer();
        GpioD.lineRelease(linePtr);
    }

    public synchronized GpioLineEvent openLineEvent() {
        long lineEventPtr = GpioD.lineEventNew();
        this.openLineEvents.add(lineEventPtr);
        return new GpioLineEvent(lineEventPtr);
    }

    public synchronized void closeLineEvent(GpioLineEvent... lineEvents) {
        for (GpioLineEvent lineEvent : lineEvents) {
            GpioD.lineEventFree(lineEvent.getCPointer());
            this.openLineEvents.remove(lineEvent.getCPointer());
        }
    }

    @Override
    public synchronized void close() {
        if (this.gpioChip == null)
            return;

        for (Long openLineEvent : this.openLineEvents) {
            GpioD.lineEventFree(openLineEvent);
        }
        this.openLineEvents.clear();

        for (int address : new HashSet<>(this.openLines.keySet())) {
            GpioLine line = this.openLines.remove(address);
            GpioD.lineRelease(line.getCPointer());
        }
        this.openLines.clear();

        if (this.gpioChip != null)
            GpioD.chipClose(this.gpioChip.getCPointer());
        this.gpioChip = null;
    }
}