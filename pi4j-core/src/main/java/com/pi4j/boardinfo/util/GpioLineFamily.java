package com.pi4j.boardinfo.util;

/**
 * SoC GPIO controller families supported by {@link LineHelper}, distinguished by how many GPIO
 * lines are grouped into a single lettered bank/port.
 */
public enum GpioLineFamily {

    /**
     * Allwinner (sunxi) SoCs, as used on boards like the Banana Pi, group their GPIOs into
     * lettered banks of 32 pins each (bank A = pins 0-31, bank B = pins 32-63, and so on).
     *
     * @see <a href="https://docs.banana-pi.org/en/BPI-M4_Zero/BananaPi_BPI-M4_Zero#_gpio_pin_define">Banana Pi BPI-M4 Zero GPIO pin define</a>
     * @see <a href="https://linux-sunxi.org/GPIO">linux-sunxi.org GPIO</a>
     */
    ALLWINNER(32),

    /**
     * NVIDIA Tegra SoCs, as used on boards like the Jetson Nano, group their GPIOs into lettered
     * ports of 8 pins each (port A = pins 0-7, port B = pins 8-15, and so on).
     *
     * @see <a href="https://forums.developer.nvidia.com/t/tegra-gpio-port-offset/179621">Tegra GPIO port offset</a>
     */
    TEGRA(8);

    private final int linesPerBank;

    GpioLineFamily(int linesPerBank) {
        this.linesPerBank = linesPerBank;
    }

    /**
     * @return the number of GPIO lines per lettered bank for this SoC family
     */
    public int getLinesPerBank() {
        return linesPerBank;
    }
}
