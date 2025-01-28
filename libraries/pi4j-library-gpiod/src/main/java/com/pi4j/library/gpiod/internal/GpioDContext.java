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

    private static final GpioDContext instance = new GpioDContext();

    public static GpioDContext getInstance() {
        return instance;
    }

    private GpioChip gpioChip;
    private final Map<Integer, GpioLine> openLines;
    private final Set<Long> openLineEvents;
    private String desiredChipName = null; // ユーザーが指定したチップ名を保持

    public GpioDContext() {
        this.openLines = new HashMap<>();
        this.openLineEvents = new HashSet<>();
    }

    /**
     * 使用する GPIO チップ名を設定します。
     *
     * @param chipName 使用する GPIO チップの名前 (例: "gpiochip0", "gpiochip2")。
     *                 null または空文字列が指定された場合、デフォルトのチップが選択されます。
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
     * 使用する GPIO チップ番号を設定します。
     *
     * @param chipNumber 使用する GPIO チップの番号 (例: 0, 2)。
     *                   負の値が指定された場合、デフォルトのチップが選択されます。
     */
    public synchronized void setChip(int chipNumber) {
        if (chipNumber < 0) {
            setChip(null);
        } else {
            setChip("gpiochip" + chipNumber);
        }
    }

    /**
     * GpioDContext を初期化します。
     * setChip() でチップが指定されていない場合は、デフォルトのチップが選択されます。
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
                    // ユーザーがチップ名を指定している場合
                    if (chip.getName().equals(this.desiredChipName)) {
                        useThisChip = true;
                    }
                } else {
                    // ユーザーがチップ名を指定していない場合、"pinctrl" を含むラベルを持つ最初のチップをデフォルトとして使用
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
            initialize(); // チップがまだ初期化されていない場合は、デフォルトのチップで初期化を試みる
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