package com.pi4j.io.spi;

/**
 * Defines the four standard SPI clock modes, which combine clock polarity (CPOL) and clock phase (CPHA)
 * to determine the idle clock level and the edge on which data is sampled. The selected mode is carried
 * in an {@link SpiConfig} and must match the connected device.
 */
public enum SpiMode {
    /** CPOL=0, CPHA=0: clock idles low, data sampled on the rising (leading) edge. */
    MODE_0(0),
    /** CPOL=0, CPHA=1: clock idles low, data sampled on the falling (trailing) edge. */
    MODE_1(1),
    /** CPOL=1, CPHA=0: clock idles high, data sampled on the falling (leading) edge. */
    MODE_2(2),
    /** CPOL=1, CPHA=1: clock idles high, data sampled on the rising (trailing) edge. */
    MODE_3(3);

    private final int mode;

    private SpiMode(int mode) {
        this.mode = mode;
    }

    /**
     * Returns the numeric mode value represented by this constant.
     *
     * @return the SPI mode number (0&ndash;3)
     */
    public int getMode() {
        return mode;
    }

    /**
     * Returns the {@code SpiMode} constant matching the given mode number.
     *
     * @param modeNumber the SPI mode number to look up
     * @return the matching {@code SpiMode}, or {@code null} if no constant has that number
     */
    public static SpiMode getByNumber(short modeNumber){
        return getByNumber((int)modeNumber);
    }

    /**
     * Returns the {@code SpiMode} constant matching the given mode number.
     *
     * @param modeNumber the SPI mode number to look up
     * @return the matching {@code SpiMode}, or {@code null} if no constant has that number
     */
    public static SpiMode getByNumber(int modeNumber){
        for(var item : SpiMode.values()){
            if(item.getMode() == modeNumber){
                return item;
            }
        }
        return null;
    }

    /**
     * Parses a textual mode number into the corresponding {@code SpiMode} constant.
     *
     * @param mode the mode number as a string ("0" through "3")
     * @return the matching {@code SpiMode}, or {@link Spi#DEFAULT_MODE} if the value is not recognized
     */
    public static SpiMode parse(String mode) {
        if(mode.equalsIgnoreCase("0")) return SpiMode.MODE_0;
        if(mode.equalsIgnoreCase("1")) return SpiMode.MODE_1;
        if(mode.equalsIgnoreCase("2")) return SpiMode.MODE_2;
        if(mode.equalsIgnoreCase("3")) return SpiMode.MODE_3;
        return Spi.DEFAULT_MODE;
    }
}
