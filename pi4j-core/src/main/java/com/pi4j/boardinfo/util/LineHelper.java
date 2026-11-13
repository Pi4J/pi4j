package com.pi4j.boardinfo.util;

/**
 * Utility for converting a lettered GPIO bank plus a pin-within-bank number into the absolute
 * line offset expected by a Linux {@code /dev/gpiochip*} character device.
 *
 * <p>Allwinner (sunxi) SoCs, as used on boards like the Banana Pi, group their GPIOs into lettered
 * banks of 32 pins each (bank A = pins 0-31, bank B = pins 32-63, and so on). The kernel exposes all
 * banks behind a single {@code gpiochip} character device, so a pin such as {@code PI15} (bank I,
 * pin 15) must be translated to its absolute line offset ({@code 8 * 32 + 15 = 271}) before it can be
 * used with the GPIO character-device API.</p>
 *
 * <p>Other SoC families use a different number of lines per bank. NVIDIA Tegra SoCs, as used on
 * boards like the Jetson Nano, group their GPIOs into lettered ports of 8 pins each instead of 32
 * (e.g. {@code PZ.00}), so the same bank/line pair resolves to a different absolute offset there.
 * Use the overloads accepting a {@link GpioLineFamily} to target a family other than the default
 * {@link GpioLineFamily#ALLWINNER}.</p>
 *
 * @see GpioLineFamily
 */
public class LineHelper {

    /**
     * Number of GPIO lines per lettered bank on Allwinner (sunxi) SoCs.
     *
     * @deprecated use {@link GpioLineFamily#ALLWINNER}'s {@link GpioLineFamily#getLinesPerBank()} instead
     */
    @Deprecated
    public static final int LINES_PER_BANK = GpioLineFamily.ALLWINNER.getLinesPerBank();

    private LineHelper() {
    }

    /**
     * Calculates the absolute {@code gpiochip} line offset for a pin identified by its bank letter
     * and its pin number within that bank, assuming the {@link GpioLineFamily#ALLWINNER} family.
     *
     * @param bank the bank letter ({@code 'A'}-{@code 'Z'}, case-insensitive), e.g. {@code 'I'} for PI15
     * @param line the pin number within the bank ({@code 0}-{@code 31}), e.g. {@code 15} for PI15
     * @return the absolute line offset on the {@code gpiochip} device, e.g. {@code 271} for PI15
     * @throws IllegalArgumentException if {@code bank} is not a letter or {@code line} is out of range
     */
    public static int getAddress(char bank, int line) {
        return getAddress(GpioLineFamily.ALLWINNER, bank, line);
    }

    /**
     * Calculates the absolute {@code gpiochip} line offset for a pin identified by its bank index
     * and its pin number within that bank, assuming the {@link GpioLineFamily#ALLWINNER} family.
     *
     * @param bank the zero-based bank index ({@code 0} for bank A, {@code 1} for bank B, ...), e.g. {@code 8} for bank I
     * @param line the pin number within the bank ({@code 0}-{@code 31}), e.g. {@code 15} for PI15
     * @return the absolute line offset on the {@code gpiochip} device, e.g. {@code 271} for PI15
     * @throws IllegalArgumentException if {@code bank} is negative, {@code line} is out of range,
     *                                   or the resulting offset would overflow {@code int}
     */
    public static int getAddress(int bank, int line) {
        return getAddress(GpioLineFamily.ALLWINNER, bank, line);
    }

    /**
     * Calculates the absolute {@code gpiochip} line offset for a pin identified by its bank letter
     * and its pin number within that bank, for the given SoC {@link GpioLineFamily}.
     *
     * @param family the SoC GPIO family that determines how many lines are grouped per bank, e.g. {@link GpioLineFamily#TEGRA}
     * @param bank   the bank letter ({@code 'A'}-{@code 'Z'}, case-insensitive), e.g. {@code 'I'} for PI15
     * @param line   the pin number within the bank, e.g. {@code 15} for PI15
     * @return the absolute line offset on the {@code gpiochip} device
     * @throws IllegalArgumentException if {@code bank} is not a letter or {@code line} is out of range for {@code family}
     */
    public static int getAddress(GpioLineFamily family, char bank, int line) {
        return getAddress(family, bankIndex(bank), line);
    }

    /**
     * Calculates the absolute {@code gpiochip} line offset for a pin identified by its bank index
     * and its pin number within that bank, for the given SoC {@link GpioLineFamily}.
     *
     * @param family the SoC GPIO family that determines how many lines are grouped per bank, e.g. {@link GpioLineFamily#TEGRA}
     * @param bank   the zero-based bank index ({@code 0} for bank A, {@code 1} for bank B, ...), e.g. {@code 8} for bank I
     * @param line   the pin number within the bank, e.g. {@code 15} for PI15
     * @return the absolute line offset on the {@code gpiochip} device
     * @throws IllegalArgumentException if {@code bank} is negative, {@code line} is out of range for {@code family},
     *                                   or the resulting offset would overflow {@code int}
     */
    public static int getAddress(GpioLineFamily family, int bank, int line) {
        int linesPerBank = family.getLinesPerBank();
        if (bank < 0) {
            throw new IllegalArgumentException("Bank index must not be negative: " + bank);
        }
        if (line < 0 || line >= linesPerBank) {
            throw new IllegalArgumentException("Line must be between 0 and " + (linesPerBank - 1) + " for " + family + ": " + line);
        }
        long offset = (long) bank * linesPerBank + line;
        if (offset > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Bank index too large, line offset would overflow int: " + bank);
        }
        return (int) offset;
    }

    /**
     * Converts a bank letter to its zero-based bank index, e.g. {@code 'A'} -&gt; {@code 0}, {@code 'I'} -&gt; {@code 8}.
     *
     * @param bank the bank letter ({@code 'A'}-{@code 'Z'}, case-insensitive)
     * @return the zero-based bank index
     * @throws IllegalArgumentException if {@code bank} is not a letter
     */
    public static int bankIndex(char bank) {
        char upperBank = Character.toUpperCase(bank);
        if (upperBank < 'A' || upperBank > 'Z') {
            throw new IllegalArgumentException("Bank must be a letter A-Z: " + bank);
        }
        return upperBank - 'A';
    }
}
