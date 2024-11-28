package com.pi4j.library.pigpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PiGpio_PWM.java
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

/**
 * <p>PiGpio_PWM interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface PiGpio_PWM {

    /**
     * Starts PWM on the GPIO, duty-cycle between 0 (off) and range (fully on). Range defaults to 255.
     * <p>
     * This and the servo functionality use the DMA and PWM or PCM peripherals to control and schedule
     * the pulse lengths and duty cycles.
     *
     * @param pin user_gpio: 0-31
     * @param dutyCycle dutycycle: 0-range
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioPWM">PIGPIO::gpioPWM</a>
     */
    void gpioPWM(int pin, int dutyCycle);

    /**
     * Starts PWM on the GPIO, duty-cycle between 0 (off) and range (fully on). Range defaults to 255.
     * <p>
     * This and the servo functionality use the DMA and PWM or PCM peripherals to control and schedule
     * the pulse lengths and duty cycles.
     *
     * @param pin user_gpio: 0-31
     * @param dutyCycle dutycycle: 0-range
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioPWM">PIGPIO::gpioPWM</a>
     */
    default void gpioSetPWMdutycycle(int pin, int dutyCycle){
        gpioPWM(pin,dutyCycle);
    }


    /**
     * Returns the PWM dutycycle setting for the GPIO.
     * <p>
     * For normal PWM the dutycycle will be out of the defined range for the GPIO (see gpioGetPWMrange).
     * If a hardware clock is active on the GPIO the reported dutycycle will be 500000 (500k) out of 1000000 (1M).
     * If hardware PWM is active on the GPIO the reported dutycycle will be out of a 1000000 (1M).
     * <p>
     * Normal PWM range defaults to 255.
     *
     * @param pin user_gpio: 0-31
     * @return Returns between 0 (off) and range (fully on) if OK.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMdutycycle">PIGPIO::gpioGetPWMdutycycle</a>
     */
    int gpioGetPWMdutycycle(int pin);


    /**
     * Selects the dutycycle range to be used for the GPIO. Subsequent calls to gpioPWM will use a dutycycle
     * between 0 (off) and range (fully on.  If PWM is currently active on the GPIO its dutycycle will be
     * scaled to reflect the new range.
     * <p>
     * The real range, the number of steps between fully off and fully on for each frequency,
     * is given in the following table.
     * <p>
     *  -------------------------------------------------------
     *   #1	   #2	 #3	   #4	 #5	   #6	 #7	    #8	   #9
     *   25,   50,  100,  125,  200,  250,  400,   500,   625,
     *  -------------------------------------------------------
     *  #10   #11   #12   #13   #14   #15    #16   #17    #18
     *  800, 1000, 1250, 2000, 2500, 4000, 5000, 10000, 20000
     *  -------------------------------------------------------
     * <p>
     * The real value set by gpioPWM is (dutycycle * real range) / range.
     * <p>
     * Example
     *   gpioSetPWMrange(24, 2000); // Now 2000 is fully on
     *                              //     1000 is half on
     *                              //      500 is quarter on, etc.
     *
     * @param pin user_gpio: 0-31
     * @param range range: 25-40000
     * @return real range for the given GPIO's frequency if OK.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetPWMrange">PIGPIO::gpioSetPWMrange</a>
     */
    int gpioSetPWMrange(int pin, int range);

    /**
     * Returns the duty-cycle range used for the GPIO if OK.
     * If a hardware clock or hardware PWM is active on the GPIO the reported range will be 1000000 (1M).
     *
     * @param pin user_gpio: 0-31
     * @return duty-cycle range
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMrange">PIGPIO::gpioGetPWMrange</a>
     */
    int gpioGetPWMrange(int pin);

    /**
     * Returns the real range used for the GPIO if OK.
     * If a hardware clock is active on the GPIO the reported real range will be 1000000 (1M).
     * If hardware PWM is active on the GPIO the reported real range will be approximately 250M
     * divided by the set PWM frequency.
     *
     * @param pin user_gpio: 0-31
     * @return real range used for the GPIO if OK.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMrealRange">PIGPIO::gpioGetPWMrealRange</a>
     */
    int gpioGetPWMrealRange(int pin);

    /**
     * Sets the frequency in hertz to be used for the GPIO.
     * <p>
     * If PWM is currently active on the GPIO it will be switched off and then back on at the new frequency.
     * Each GPIO can be independently set to one of 18 different PWM frequencies.
     * The selectable frequencies depend upon the sample rate which may be 1, 2, 4, 5, 8, or 10 microseconds (default 5).
     * <p>
     * The frequencies for each sample rate are:
     * <p>
     *                        Hertz
     * <p>
     *        1: 40000 20000 10000 8000 5000 4000 2500 2000 1600
     *            1250  1000   800  500  400  250  200  100   50
     * <p>
     *        2: 20000 10000  5000 4000 2500 2000 1250 1000  800
     *             625   500   400  250  200  125  100   50   25
     * <p>
     *        4: 10000  5000  2500 2000 1250 1000  625  500  400
     *             313   250   200  125  100   63   50   25   13
     * sample
     *  rate
     *  (us)  5:  8000  4000  2000 1600 1000  800  500  400  320
     *             250   200   160  100   80   50   40   20   10
     * <p>
     *        8:  5000  2500  1250 1000  625  500  313  250  200
     *             156   125   100   63   50   31   25   13    6
     * <p>
     *       10:  4000  2000  1000  800  500  400  250  200  160
     *             125   100    80   50   40   25   20   10    5
     * <p>
     *
     * Example:
     *    gpioSetPWMfrequency(23, 0); // Set GPIO23 to lowest frequency.
     *    gpioSetPWMfrequency(24, 500); // Set GPIO24 to 500Hz.
     *    gpioSetPWMfrequency(25, 100000); // Set GPIO25 to highest frequency.
     *
     * @param pin user_gpio: 0-31
     * @param frequency frequency: $gt;=0
     * @return Returns the numerically closest frequency if OK
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetPWMrange">PIGPIO::gpioSetPWMrange</a>
     */
    int gpioSetPWMfrequency(int pin, int frequency);


    /**
     * Returns the frequency (in hertz) used for the GPIO
     * <p>
     * For normal PWM the frequency will be that defined for the GPIO by gpioSetPWMfrequency.
     * If a hardware clock is active on the GPIO the reported frequency will be that set by gpioHardwareClock.
     * If hardware PWM is active on the GPIO the reported frequency will be that set by gpioHardwarePWM.
     * <p>
     * Example:
     *    f = gpioGetPWMfrequency(23); // Get frequency used for GPIO23.
     *
     * @param pin user_gpio: 0-31
     * @return Returns the frequency (in hertz) used for the GPIO if OK.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMfrequency">PIGPIO::gpioGetPWMfrequency</a>
     */
    int gpioGetPWMfrequency(int pin);


    /**
     * Starts hardware PWM on a GPIO at the specified frequency and duty-cycle.
     * Frequencies above 30MHz are unlikely to work.
     * <p>
     * NOTE: Any waveform started by gpioWaveTxSend, or gpioWaveChain will be cancelled.
     * <p>
     * This function is only valid if the pigpio main clock is PCM.
     * The main clock defaults to PCM but may be overridden by a call to gpioCfgClock.
     * <p>
     * The same PWM channel is available on multiple GPIO. The latest frequency and duty-cycle
     * setting will be used by all GPIO which share a PWM channel.
     * <p>
     * The GPIO must be one of the following.
     * <p>
     *   12  PWM channel 0  All models but A and B
     *   13  PWM channel 1  All models but A and B
     *   18  PWM channel 0  All models
     *   19  PWM channel 1  All models but A and B
     * <p>
     *   40  PWM channel 0  Compute module only
     *   41  PWM channel 1  Compute module only
     *   45  PWM channel 1  Compute module only
     *   52  PWM channel 0  Compute module only
     *   53  PWM channel 1  Compute module only
     * <p>
     *
     * The actual number of steps between off and fully on is the integral part of
     * 250M/PWMfreq (375M/PWMfreq for the BCM2711).
     * The actual frequency set is 250M/steps (375M/steps for the BCM2711).
     * There will only be a million steps for a frequency of 250 (375 for the BCM2711). Lower
     * frequencies will have more steps and higher frequencies will have fewer steps.
     * dutyCycle is automatically scaled to take this into account.
     *
     * @param pin a supported hardware PWM pin
     * @param frequency  0 (off) or 1-125M (1-187.5M for the BCM2711)
     * @param dutyCycle  0 (off) to 1000000 (1M)(fully on)
     */
    void gpioHardwarePWM(int pin, int frequency, int dutyCycle);
}
