package com.pi4j.library.pigpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PiGpio_Servo.java
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
 * <p>PiGpio_Servo interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface PiGpio_Servo {

    /**
     * Starts servo pulses on the GPIO, 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise).
     * <p>
     * The range supported by servos varies and should probably be determined by experiment. A value
     * of 1500 should always be safe and represents the mid-point of rotation. You can DAMAGE a servo
     * if you command it to move beyond its limits.
     * <p>
     * The following causes an on pulse of 1500 microseconds duration to be transmitted on GPIO 17 at
     * a rate of 50 times per second. This will command a servo connected to GPIO 17 to rotate to its
     * mid-point.
     * <p>
     * Example:
     *  - gpioServo(17, 1000); // Move servo to safe position anti-clockwise.
     *  - gpioServo(23, 1500); // Move servo to centre position.
     *  - gpioServo(25, 2000); // Move servo to safe position clockwise.
     * <p>
     * OTHER UPDATE RATES:
     * This function updates servos at 50Hz. If you wish to use a different
     * update frequency you will have to use the PWM functions.
     * <p>
     *    PWM Hz      50     100    200    400    500
     *    1E6/Hz   20000   10000   5000   2500   2000
     * <p>
     * Firstly set the desired PWM frequency using gpioSetPWMfrequency.
     * Then set the PWM range using gpioSetPWMrange to 1E6/frequency. Doing this
     * allows you to use units of microseconds when setting the servo pulsewidth.
     * <p>
     * E.g. If you want to update a servo connected to GPIO25 at 400Hz*
     *  - gpioSetPWMfrequency(25, 400);
     *  - gpioSetPWMrange(25, 2500);
     * <p>
     * Thereafter use the PWM command to move the servo, e.g. gpioPWM(25, 1500) will set a 1500 us pulse.
     *
     * @param pin user_gpio: 0-31
     * @param pulseWidth  0, 500-2500
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioServo">PIGPIO::gpioServo</a>
     */
    void gpioServo(int pin, int pulseWidth);

    /**
     * Starts servo pulses on the GPIO, 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise).
     * <p>
     * The range supported by servos varies and should probably be determined by experiment. A value
     * of 1500 should always be safe and represents the mid-point of rotation. You can DAMAGE a servo
     * if you command it to move beyond its limits.
     * <p>
     * The following causes an on pulse of 1500 microseconds duration to be transmitted on GPIO 17 at
     * a rate of 50 times per second. This will command a servo connected to GPIO 17 to rotate to its
     * mid-point.
     * <p>
     * Example:
     *  - gpioServo(17, 1000); // Move servo to safe position anti-clockwise.
     *  - gpioServo(23, 1500); // Move servo to centre position.
     *  - gpioServo(25, 2000); // Move servo to safe position clockwise.
     * <p>
     * OTHER UPDATE RATES:
     * This function updates servos at 50Hz. If you wish to use a different
     * update frequency you will have to use the PWM functions.
     * <p>
     *    PWM Hz      50     100    200    400    500
     *    1E6/Hz   20000   10000   5000   2500   2000
     * <p>
     * Firstly set the desired PWM frequency using gpioSetPWMfrequency.
     * Then set the PWM range using gpioSetPWMrange to 1E6/frequency. Doing this
     * allows you to use units of microseconds when setting the servo pulsewidth.
     * <p>
     * E.g. If you want to update a servo connected to GPIO25 at 400Hz*
     *  - gpioSetPWMfrequency(25, 400);
     *  - gpioSetPWMrange(25, 2500);
     * <p>
     * Thereafter use the PWM command to move the servo, e.g. gpioPWM(25, 1500) will set a 1500 us pulse.
     *
     * @param pin user_gpio: 0-31
     * @param pulseWidth  0, 500-2500
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioServo">PIGPIO::gpioServo</a>
     */
    default void gpioSetServoPulsewidth(int pin, int pulseWidth){
        gpioServo(pin,pulseWidth);
    }

    /**
     * Returns the servo pulse-width setting for the GPIO.
     *
     * @param pin user_gpio: 0-31
     * @return Returns 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise) if OK.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetServoPulsewidth">PIGPIO::gpioGetServoPulsewidth</a>
     */
    int gpioGetServoPulsewidth(int pin);
}
