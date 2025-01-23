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

    public GpioDContext() {
        this.openLines = new HashMap<>();
        this.openLineEvents = new HashSet<>();
    }

    public synchronized void initialize() {
        if (!BoardInfoHelper.runningOnRaspberryPi()) {
            logger.warn("Can't initialize GpioD context, board model is unknown");
            return;
        }

        // already initialized
        if (this.gpioChip != null)
            return;

        long chipIterPtr = GpioD.chipIterNew();
        GpioChip found = null;
        try {
            Long chipPtr;
            while ((chipPtr = GpioD.chipIterNextNoClose(chipIterPtr)) != null) {
                GpioChip chip = new GpioChip(chipPtr);
                //if (!chip.getLabel().contains("pinctrl")){
                if (!chip.getName().equals("gpiochip2")) {
                    GpioD.chipClose(chip.getCPointer());
                    continue;
                }

                found = chip;
                break;
            }
        } finally {
            if (found != null) {
                GpioD.chipIterFreeNoClose(chipIterPtr);
            } else {
                GpioD.chipIterFree(chipIterPtr);
            }
        }

        if (found == null)
            throw new IllegalStateException("Couldn't identify gpiochip!");

        this.gpioChip = found;
        logger.info("Using chip {} {}", this.gpioChip.getName(), this.gpioChip.getLabel());
    }

    public synchronized GpioLine getOrOpenLine(int offset) {
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
