package com.pi4j.io.spi;

/**
 * Identifies the SPI bus (the SPI controller/peripheral) that an {@link Spi} device is attached to.
 * The numeric bus value is carried in an {@link SpiConfig} and used by providers to select the
 * correct hardware SPI interface (e.g. {@code /dev/spidev0.x}).
 */
public enum SpiBus {
    BUS_0(0), BUS_1(1), BUS_2(2), BUS_3(3), BUS_4(4), BUS_5(5), BUS_6(6);

    private final int bus;

    private SpiBus(int bus) {
        this.bus = bus;
    }

    /**
     * Returns the numeric bus index represented by this constant.
     *
     * @return the SPI bus number (0&ndash;6)
     */
    public int getBus() {
        return bus;
    }

    /**
     * Returns the {@code SpiBus} constant matching the given bus number.
     *
     * @param bus the SPI bus number to look up
     * @return the matching {@code SpiBus}, or {@code null} if no constant has that number
     */
    public static SpiBus getByNumber(short bus) {
        return getByNumber((int) bus);
    }

    /**
     * Returns the {@code SpiBus} constant matching the given bus number.
     *
     * @param bus the SPI bus number to look up
     * @return the matching {@code SpiBus}, or {@code null} if no constant has that number
     */
    public static SpiBus getByNumber(int bus) {
        for (var item : SpiBus.values()) {
            if (item.getBus() == bus) {
                return item;
            }
        }
        return null;
    }

    /**
     * Parses a textual bus number into the corresponding {@code SpiBus} constant.
     *
     * @param bus the bus number as a string ("0" through "6")
     * @return the matching {@code SpiBus}, or {@link Spi#DEFAULT_BUS} if the value is not recognized
     */
    public static SpiBus parse(String bus) {
        if (bus.equalsIgnoreCase("0")) return SpiBus.BUS_0;
        if (bus.equalsIgnoreCase("1")) return SpiBus.BUS_1;
        if (bus.equalsIgnoreCase("2")) return SpiBus.BUS_2;
        if (bus.equalsIgnoreCase("3")) return SpiBus.BUS_3;
        if (bus.equalsIgnoreCase("4")) return SpiBus.BUS_4;
        if (bus.equalsIgnoreCase("5")) return SpiBus.BUS_5;
        if (bus.equalsIgnoreCase("6")) return SpiBus.BUS_6;
        return Spi.DEFAULT_BUS;
    }
}
