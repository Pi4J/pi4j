package com.pi4j.plugin.linuxfs.internal;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxPwm.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.pi4j.boardinfo.util.PwmChipUtil.DEFAULT_PWM_CHIP;
import static com.pi4j.boardinfo.util.PwmChipUtil.DEFAULT_PWM_SYSTEM_PATH;

/**
 * <p>LinuxPwm class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @see "https://www.kernel.org/doc/html/latest/driver-api/pwm.html"
 */
public class LinuxPwm {

    protected final String systemPath;
    protected final int chip;
    protected final int address;
    protected final String pwmPath;

    public enum Polarity {
        NORMAL,
        INVERSED,
        UNKNOWN
    }

    /**
     * <p>Constructor for LinuxPwm.</p>
     *
     * @param systemPath a {@link String} object.
     * @param chip
     * @param address    a int.
     */
    public LinuxPwm(String systemPath, int chip, int address) {
        this.chip = chip;
        this.address = address;
        this.systemPath = Paths.get(systemPath, String.format("pwmchip%d", chip)).toString();
        this.pwmPath = Paths.get(this.systemPath, String.format("pwm%d", address)).toString();
    }

    /**
     * <p>Constructor for LinuxPwm.</p>
     *
     * @param systemPath a {@link String} object.
     * @param address    a int.
     */

    public LinuxPwm(String systemPath, int address) {
        this(DEFAULT_PWM_SYSTEM_PATH, DEFAULT_PWM_CHIP, address);
    }

    /**
     * <p>Constructor for LinuxPwm.</p>
     *
     * @param address a int.
     */
    public LinuxPwm(int address) {
        this(DEFAULT_PWM_SYSTEM_PATH, address);
    }


    /**
     * <p>channels.</p>
     * The number of PWM channels this chip supports (read-only).
     *
     * @return The number of PWM channels this chip supports
     * @throws IOException if any.
     */
    public int channels() throws IOException {
        return getChannels();
    }

    /**
     * <p>getChannels.</p>
     * The number of PWM channels this chip supports (read-only).
     *
     * @return The number of PWM channels this chip supports
     * @throws IOException if any.
     */
    public int getChannels() throws IOException {
        var path = Paths.get(systemPath, "npwm");
        return Integer.parseInt(Files.readString(path).trim());
    }


    /**
     * Export GPIO pin by SoC address
     *
     * @throws IOException if any.
     */
    public void export() throws IOException {
        var path = Paths.get(systemPath, "export");
        Files.writeString(path, Integer.toString(address));
    }

    /**
     * <p>unexport.</p>
     *
     * @throws IOException if any.
     */
    public void unexport() throws IOException {
        var path = Paths.get(systemPath, "unexport");
        Files.writeString(path, Integer.toString(address));
    }

    /**
     * <p>isExported.</p>
     *
     * @return a boolean.
     * @throws IOException if any.
     */
    public boolean isExported() throws IOException {
        return Files.exists(Paths.get(pwmPath));
    }

    /**
     * <p>polarity.</p>
     *
     * @param polarity a {@link LinuxPwm.Polarity} object.
     * @throws IOException if any.
     */
    public void polarity(Polarity polarity) throws IOException {
        setPolarity(polarity);
    }

    /**
     * <p>setPolarity.</p>
     *
     * @param polarity a {@link LinuxPwm.Polarity} object.
     * @throws IOException if any.
     */
    public void setPolarity(Polarity polarity) throws IOException {
        var path = Paths.get(pwmPath, "polarity");
        Files.writeString(path, polarity.name().toLowerCase());
    }

    /**
     * <p>polarity.</p>
     *
     * @return a {@link LinuxPwm.Polarity} object.
     * @throws IOException if any.
     */
    public Polarity polarity() throws IOException {
        return getPolarity();
    }

    /**
     * <p>getPolarity.</p>
     *
     * @return a {@link LinuxPwm.Polarity} object.
     * @throws IOException if any.
     */
    public Polarity getPolarity() throws IOException {
        var path = Paths.get(pwmPath, "polarity");
        switch (Files.readString(path).trim().toLowerCase()) {
            case "inversed":
                return Polarity.INVERSED;
            case "normal":
                return Polarity.NORMAL;
            default:
                return Polarity.UNKNOWN;
        }
    }

    /**
     * <p>enable.</p>
     * Enable the PWM signal (read/write).
     *
     * @throws IOException if any.
     */
    public void enable() throws IOException {
        setEnabled(true);
    }

    /**
     * <p>disable.</p>
     * Disable the PWM signal (read/write).
     *
     * @throws IOException if any.
     */
    public void disable() throws IOException {
        setEnabled(false);
    }

    /**
     * <p>enabled.</p>
     * Enable/disable the PWM signal (read/write).
     *
     * @param enabled a boolean.
     * @throws IOException if any.
     */
    public void enabled(boolean enabled) throws IOException {
        setEnabled(enabled);
    }

    /**
     * <p>setEnabled.</p>
     * Enable/disable the PWM signal (read/write).
     *
     * @param enabled a boolean.
     * @throws IOException if any.
     */
    public void setEnabled(boolean enabled) throws IOException {
        var path = Paths.get(pwmPath, "enable");
        Files.writeString(path, (enabled ? "1" : "0"));
    }

    /**
     * <p>enabled.</p>
     *
     * @return a boolean.
     * @throws IOException if any.
     */
    public boolean enabled() throws IOException {
        return isEnabled();
    }

    /**
     * <p>isEnabled.</p>
     *
     * @return a boolean.
     * @throws IOException if any.
     */
    public boolean isEnabled() throws IOException {
        var path = Paths.get(pwmPath, "enable");
        return Files.readString(path).trim().equalsIgnoreCase("1");
    }

    /**
     * <p>period.</p>
     * The total period of the PWM signal (read/write). Value is in nanoseconds and is the sum of the active and inactive time of the PWM.
     *
     * @param period a long value representing nanoseconds.
     * @throws IOException if any.
     */
    public void period(long period) throws IOException {
        setPeriod(period);
    }

    public void period(Number period) throws IOException {
        setPeriod(period);
    }

    /**
     * <p>setPeriod.</p>
     *
     * @param period a long value representing nanoseconds.
     * @throws IOException if any.
     */
    public void setPeriod(long period) throws IOException {
        var path = Paths.get(pwmPath, "period");
        Files.writeString(path, Long.toUnsignedString(period));
    }

    public void setPeriod(Number period) throws IOException {
        var path = Paths.get(pwmPath, "period");
        Files.writeString(path, period.toString());
    }

    /**
     * <p>period.</p>
     * The total period of the PWM signal (read/write). Value is in nanoseconds and is the sum of the active and inactive time of the PWM.
     *
     * @return the period value in nanoseconds
     * @throws IOException if any.
     */
    public long period() throws IOException {
        return getPeriod();
    }

    /**
     * <p>getPeriod.</p>
     * The total period of the PWM signal (read/write). Value is in nanoseconds and is the sum of the active and inactive time of the PWM.
     *
     * @return the period value in nanoseconds
     * @throws IOException if any.
     */
    public long getPeriod() throws IOException {
        var path = Paths.get(pwmPath, "period");
        return Long.parseLong(Files.readString(path).trim());
    }

    /**
     * <p>dutyCycle.</p>
     * The active time of the PWM signal (read/write). Value is in nanoseconds and must be less than the period.
     *
     * @param dutyCycle a long value representing nanoseconds.
     * @throws IOException if any.
     */
    public void dutyCycle(long dutyCycle) throws IOException {
        setDutyCycle(dutyCycle);
    }

    /**
     * <p>setDutyCycle.</p>
     * The active time of the PWM signal (read/write). Value is in nanoseconds and must be less than the period.
     *
     * @param dutyCycle a long value representing nanoseconds.
     * @throws IOException if any.
     */
    public void setDutyCycle(long dutyCycle) throws IOException {
        var path = Paths.get(pwmPath, "duty_cycle");
        Files.writeString(path, Long.toString(dutyCycle));
    }

    /**
     * <p>dutyCycle.</p>
     * The active time of the PWM signal (read/write). Value is in nanoseconds and must be less than the period.
     *
     * @return the duty cycle value in nanoseconds
     * @throws IOException if any.
     */
    public long dutyCycle() throws IOException {
        return getDutyCycle();
    }

    /**
     * <p>getDutyCycle.</p>
     * The active time of the PWM signal (read/write). Value is in nanoseconds and must be less than the period.
     *
     * @return the duty cycle value in nanoseconds
     * @throws IOException if any.
     */
    public long getDutyCycle() throws IOException {
        var path = Paths.get(pwmPath, "duty_cycle");
        return Long.parseLong(Files.readString(path).trim());
    }

    /**
     * Get Linux File System path for PWM
     *
     * @return Linux File System path for PWM
     */
    public String systemPath() {
        return getSystemPath();
    }

    /**
     * Get Linux File System path for PWM
     *
     * @return Linux File System path for PWM
     */
    public String getSystemPath() {
        return this.systemPath;
    }

    /**
     * Get Linux File System path for this PWM pin instance
     *
     * @return Linux File System path for this PWM pin instance
     */
    public String pwmPath() {
        return getPwmPath();
    }

    /**
     * Get Linux File System path for this PWM pin instance
     *
     * @return Linux File System path for this PWM pin instance
     */
    public String getPwmPath() {
        return this.pwmPath;
    }
}
