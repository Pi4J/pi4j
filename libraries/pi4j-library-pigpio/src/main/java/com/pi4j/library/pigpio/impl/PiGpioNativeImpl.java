package com.pi4j.library.pigpio.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PiGpioNativeImpl.java
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

import com.pi4j.library.pigpio.*;
import com.pi4j.library.pigpio.internal.PIGPIO;
import com.pi4j.library.pigpio.internal.PiGpioAlertCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static com.pi4j.library.pigpio.PiGpioConst.PI_IF_DISABLE_FIFO;
import static com.pi4j.library.pigpio.PiGpioConst.PI_IF_DISABLE_SOCK;
import static com.pi4j.library.pigpio.PiGpioConst.PI_TIME_RELATIVE;

/**
 * <p>PiGpioNativeImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioNativeImpl extends PiGpioBase implements PiGpio {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private int SPI_BUFFSIZ = 4096;

    private static final PiGpioNativeImpl instance;
    static {
        instance = new PiGpioNativeImpl();
    }

    /**
     * <p>Creates a PiGpio instance using direct (native) JNI access to the
     * libpigpio.so shared library.  This instance may only be used
     * when running directly on the Raspberry Pi hardware and when the
     * PiGpio Daemon is not running.  PiGpio does not support accessing
     * the native shared library while the daemon is running concurrently.</p>
     *
     * <p>This instance is a singleton, so that the runtime can be shutdown,
     * as initialization of the pigpio can not be done twice in a JVM</p>
     *
     * @return the {@link PiGpio} instance
     */
    public static PiGpio newInstance() {
        return instance;
    }

    /**
     * DEFAULT PRIVATE CONSTRUCTOR
     */
    private PiGpioNativeImpl() {
        super();
        this.initialized = false;
    }

    /**
     * {@inheritDoc}
     *
     * Initializes the library.
     * (The Java implementation of this function does not return a value)
     * <p>
     * gpioInitialise must be called before using the other library functions with the following exceptions:
     * - gpioCfg*
     * - gpioVersion
     * - gpioHardwareRevision
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioInitialise">PIGPIO::gpioInitialise</a>
     */
    @Override
    public int gpioInitialise() {
        int result = 0;

        logger.trace("[INITIALIZE] -> STARTED");

        if(!this.initialized) {
            // disable socket and pipes interfaces
            int rslt = PIGPIO.gpioCfgInterfaces(PI_IF_DISABLE_FIFO | PI_IF_DISABLE_SOCK);
            validateResult(rslt);

            // initialize the PiGpio native library
            result = PIGPIO.gpioInitialise();
            validateResult(result);

            try {
                Scanner scanner = new Scanner(new File("/sys/module/spidev/parameters/bufsiz"));
                if (scanner.hasNextInt()) {
                    SPI_BUFFSIZ = scanner.nextInt();
                }
                logger.trace("[INITIALIZE] -> SPI_BUFFSIZ={}", SPI_BUFFSIZ);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            // initialization successful
            this.initialized = true;
            logger.debug("[INITIALIZE] -- INITIALIZED SUCCESSFULLY");
        }
        else{
            logger.warn("[INITIALIZE] -- ALREADY INITIALIZED");
        }
        logger.trace("[INITIALIZE] <- FINISHED");
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Shutdown the library.
     * <p>
     * Returns nothing.
     * Call before program exit.
     * This function resets the used DMA channels, releases memory, and terminates any running threads.
     */
    @Override
    public void gpioTerminate() {
        logger.trace("[SHUTDOWN] -> STARTED");
        if(this.initialized) {
            // close all open SPI, SERIAL, I2C handles
            closeAllOpenHandles();
        }

        // terminate PiGPio library
        PIGPIO.gpioTerminate();

        // clear initialized flag
        this.initialized = false;
        logger.trace("[SHUTDOWN] <- FINISHED");
    }

    /**
     * {@inheritDoc}
     *
     * Returns the pigpio library version.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioVersion">PIGPIO::gpioVersion</a>
     */
    @Override
    public int gpioVersion() {
        logger.trace("[VERSION] -> GET VERSION");
        validateReady();
        int version = PIGPIO.gpioVersion();
        logger.trace("[VERSION] <- RESULT={}", version);
        return version;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the hardware revision.
     * <p>
     * If the hardware revision can not be found or is not a valid hexadecimal number the function returns 0.
     * The hardware revision is the last few characters on the Revision line of /proc/cpuinfo.
     * The revision number can be used to determine the assignment of GPIO to pins (see gpio).
     * <p>
     * There are at least three types of board.
     *  - Type 1 boards have hardware revision numbers of 2 and 3.
     *  - Type 2 boards have hardware revision numbers of 4, 5, 6, and 15.
     *  - Type 3 boards have hardware revision numbers of 16 or greater.
     * <p>
     *     for "Revision : 0002" the function returns 2.
     *     for "Revision : 000f" the function returns 15.
     *     for "Revision : 000g" the function returns 0.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioHardwareRevision">PIGPIO::gpioHardwareRevision</a>
     */
    @Override
    public long gpioHardwareRevision() {
        logger.trace("[HARDWARE] -> GET REVISION");
        validateReady();
        int revision = PIGPIO.gpioHardwareRevision();
        logger.trace("[HARDWARE] <- REVISION: {}", revision);
        if(revision <= 0) throw new PiGpioException("Hardware revision could not be determined.");
        return revision;
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // GPIO IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * {@inheritDoc}
     *
     * Sets or clears resistor pull ups or downs on the GPIO.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetPullUpDown">PIGPIO::gpioSetPullUpDown</a>
     */
    @Override
    public void gpioSetPullUpDown(int pin, PiGpioPud pud) {
        logger.trace("[GPIO::PUD-SET] -> PIN: {}; PUD={}({});", pin, pud.name(), pud.value());
        validateReady();
        validatePin(pin);
        int result = PIGPIO.gpioSetPullUpDown(pin, pud.value());
        logger.trace("[GPIO::PUD-SET] <- PIN: {}; PUD={}({}); SUCCESS={}", pud.name(), pud.value(), (result>=0));
        validateResult(result); // Returns 0 if OK, otherwise PI_BAD_GPIO or PI_BAD_MODE.
    }

    /**
     * {@inheritDoc}
     *
     * Gets the GPIO mode.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetMode">PIGPIO::gpioGetMode</a>
     */
    @Override
    public PiGpioMode gpioGetMode(int pin) {
        logger.trace("[GPIO::MODE-GET] -> PIN: {};", pin);
        validateReady();
        validatePin(pin);
        int result = PIGPIO.gpioGetMode(pin);
        validateResult(result); // Returns the GPIO mode if OK, otherwise PI_BAD_GPIO.
        PiGpioMode mode = PiGpioMode.from(result);
        logger.trace("[GPIO::MODE-GET] <- PIN: {}; MODE={}({})", pin, mode.name(), mode.value());
        return mode;
    }

    /**
     * {@inheritDoc}
     *
     * Sets the GPIO mode, typically input or output.
     * <p>
     * gpio: 0-53
     * mode: 0-7
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetMode">PIGPIO::gpioSetMode</a>
     */
    @Override
    public void gpioSetMode(int pin, PiGpioMode mode) {
        logger.trace("[GPIO::MODE-SET] -> PIN: {}; MODE={}({});", pin, mode.name(), mode.value());
        validateReady();
        validatePin(pin);
        int result = PIGPIO.gpioSetMode(pin, mode.value());
        logger.trace("[GPIO::MODE-SET] <- PIN: {}; MODE={}({}); SUCCESS={}", pin, mode.name(), mode.value(), (result>=0));
        validateResult(result); // Returns 0 if OK, otherwise PI_BAD_GPIO or PI_BAD_PUD.
    }

    /**
     * {@inheritDoc}
     *
     * Reads the GPIO level, on (HIGH) or off (LOW).
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioRead">PIGPIO::gpioRead</a>
     */
    @Override
    public PiGpioState gpioRead(int pin) {
        logger.trace("[GPIO::GET] -> PIN: {}", pin);
        validateReady();
        validatePin(pin);
        int result = PIGPIO.gpioRead(pin);
        validateResult(result); // Returns the GPIO level if OK, otherwise PI_BAD_GPIO.
        PiGpioState state = PiGpioState.from(result);
        logger.trace("[GPIO::GET] <- PIN: {} is {}({})", pin, state.name(), state.value());
        return state;
    }

    /**
     * {@inheritDoc}
     *
     * Sets the GPIO level, on (HIGH) or off (LOW).
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioWrite">PIGPIO::gpioWrite</a>
     */
    @Override
    public void gpioWrite(int pin, PiGpioState state) {
        logger.trace("[GPIO::SET] -> PIN: {}; {}({});", pin, state.name(), state.value());
        validateReady();
        validatePin(pin);
        int result = PIGPIO.gpioWrite(pin, state.value());
        logger.trace("[GPIO::SET] <- PIN: {}; {}({}); SUCCESS={}",  pin, state.name(), state.value(), (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_GPIO or PI_BAD_LEVEL.
    }

    /**
     * {@inheritDoc}
     *
     * Sets a glitch filter on a GPIO.  (AKA Debounce)
     * <p>
     * Level changes on the GPIO are not reported unless the level has been stable for at
     * least 'steady' microseconds. The level is then reported. Level changes of less
     * than 'steady' microseconds are ignored.
     * <p>
     * This filter affects the GPIO samples returned to callbacks set up with:
     *  - gpioSetAlertFunc
     *  - gpioSetAlertFuncEx
     *  - gpioSetGetSamplesFunc
     *  - gpioSetGetSamplesFuncEx.
     * <p>
     * It does not affect interrupts set up with gpioSetISRFunc, gpioSetISRFuncEx, or
     * levels read by gpioRead, gpioRead_Bits_0_31, or gpioRead_Bits_32_53.
     * Each (stable) edge will be timestamped steady microseconds after it was first detected.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGlitchFilter">PIGPIO::gpioGlitchFilter</a>
     */
    public void gpioGlitchFilter(int pin, int steady) {
        logger.trace("[GPIO::GLITCH] -> PIN: {}; INTERVAL: {};", pin, steady);
        validateReady();
        validatePin(pin);
        validateGpioGlitchFilter(steady);
        int result = PIGPIO.gpioGlitchFilter(pin, steady);
        logger.trace("[GPIO::GLITCH] <- PIN: {}; SUCCESS={}", pin, (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO, or PI_BAD_FILTER.
    }

    /**
     * {@inheritDoc}
     *
     * Sets a noise filter on a GPIO.
     * <p>
     * Level changes on the GPIO are ignored until a level which has been stable for 'steady'
     * microseconds is detected. Level changes on the GPIO are then reported for 'active'
     * microseconds after which the process repeats.
     * <p>
     * This filter affects the GPIO samples returned to callbacks set up with:
     *  - gpioSetAlertFunc
     *  - gpioSetAlertFuncEx
     *  - gpioSetGetSamplesFunc
     *  - gpioSetGetSamplesFuncEx.     *
     * It does not affect interrupts set up with gpioSetISRFunc, gpioSetISRFuncEx, or
     * levels read by gpioRead, gpioRead_Bits_0_31, or gpioRead_Bits_32_53.
     * <p>
     * Level changes before and after the active period may be reported.
     * Your software must be designed to cope with such reports.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGlitchFilter">PIGPIO::gpioGlitchFilter</a>
     */
    public void gpioNoiseFilter(int pin, int steady, int active) {
        logger.trace("[GPIO::NOISE] -> PIN: {}; INTERVAL: {};", pin, steady);
        validateReady();
        validatePin(pin);
        validateGpioNoiseFilter(steady, active);
        int result = PIGPIO.gpioNoiseFilter(pin, steady, active);
        logger.trace("[GPIO::NOISE] <- PIN: {}; SUCCESS={}",  pin, (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO, or PI_BAD_FILTER.
    }


    // *****************************************************************************************************
    // *****************************************************************************************************
    // PWM IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * {@inheritDoc}
     *
     * Starts PWM on the GPIO, dutycycle between 0 (off) and range (fully on). Range defaults to 255.
     * <p>
     * This and the servo functionality use the DMA and PWM or PCM peripherals to control and schedule
     * the pulse lengths and duty cycles.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioPWM">PIGPIO::gpioPWM</a>
     */
    @Override
    public void gpioPWM(int pin, int dutyCycle) {
        logger.trace("[PWM::SET] -> PIN: {}; DUTY-CYCLE={};", pin, dutyCycle);
        validateReady();
        validateUserPin(pin);
        validateDutyCycle(dutyCycle);
        int result = PIGPIO.gpioPWM(pin, dutyCycle);
        logger.trace("[PWM::SET] <- PIN: {}; DUTY-CYCLE={}; SUCCESS={}",  pin, dutyCycle, (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_GPIO or PI_BAD_LEVEL.
    }

    /**
     * {@inheritDoc}
     *
     * Returns the PWM dutycycle setting for the GPIO.
     * <p>
     * For normal PWM the dutycycle will be out of the defined range for the GPIO (see gpioGetPWMrange).
     * If a hardware clock is active on the GPIO the reported dutycycle will be 500000 (500k) out of 1000000 (1M).
     * If hardware PWM is active on the GPIO the reported dutycycle will be out of a 1000000 (1M).
     * <p>
     * Normal PWM range defaults to 255.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMdutycycle">PIGPIO::gpioGetPWMdutycycle</a>
     */
    @Override
    public int gpioGetPWMdutycycle(int pin) {
        logger.trace("[PWM::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        int result = PIGPIO.gpioGetPWMdutycycle(pin);
        logger.trace("[PWM::GET] <- PIN: {}; DUTY-CYCLE={}; SUCCESS={}",  pin, result, (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_NOT_PWM_GPIO.
        return result;
    }

    /**
     * {@inheritDoc}
     *
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
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetPWMrange">PIGPIO::gpioSetPWMrange</a>
     */
    @Override
    public int gpioSetPWMrange(int pin, int range) {
        logger.trace("[PWM-RANGE::SET] -> PIN: {}; RANGE={}", pin, range);
        validateReady();
        validateUserPin(pin);
        //validateDutyCycleRange(range);
        int result = PIGPIO.gpioSetPWMrange(pin, range);
        logger.trace("[PWM-RANGE::SET] <- PIN: {}; REAL-RANGE={}; SUCCESS={}",  pin, result, (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_DUTYRANGE.
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the duty-cycle range used for the GPIO if OK.
     * If a hardware clock or hardware PWM is active on the GPIO the reported range will be 1000000 (1M).
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMrange">PIGPIO::gpioGetPWMrange</a>
     */
    @Override
    public int gpioGetPWMrange(int pin) {
        logger.trace("[PWM-RANGE::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        int result = PIGPIO.gpioGetPWMrange(pin);
        logger.trace("[PWM-RANGE::GET] <- PIN: {}; RANGE={}; SUCCESS={}",  pin, result, (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_DUTYRANGE.
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the real range used for the GPIO if OK.
     * If a hardware clock is active on the GPIO the reported real range will be 1000000 (1M).
     * If hardware PWM is active on the GPIO the reported real range will be approximately 250M
     * divided by the set PWM frequency.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMrealRange">PIGPIO::gpioGetPWMrealRange</a>
     */
    @Override
    public int gpioGetPWMrealRange(int pin) {
        logger.trace("[PWM-REAL-RANGE::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        int result = PIGPIO.gpioGetPWMrealRange(pin);
        logger.trace("[PWM-REAL-RANGE::GET] <- PIN: {}; RANGE={}; SUCCESS={}",  pin, result, (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_DUTYRANGE.
        return result;
    }

    /**
     * {@inheritDoc}
     *
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
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetPWMrange">PIGPIO::gpioSetPWMrange</a>
     */
    @Override
    public int gpioSetPWMfrequency(int pin, int frequency) {
        logger.trace("[PWM-FREQ::SET] -> PIN: {}; FREQUENCY={}", pin, frequency);
        validateReady();
        validateUserPin(pin);
        // validateFrequency(frequency); TODO :: IMPLEMENT 'validateFrequency()'
        int result = PIGPIO.gpioSetPWMfrequency(pin, frequency);
        logger.trace("[PWM-FREQ::SET] <- PIN: {}; FREQUENCY={}; SUCCESS={}",  pin, result, (result>=0));
        validateResult(result);  // Returns the numerically closest frequency if OK, otherwise PI_BAD_USER_GPIO.
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the frequency (in hertz) used for the GPIO
     * <p>
     * For normal PWM the frequency will be that defined for the GPIO by gpioSetPWMfrequency.
     * If a hardware clock is active on the GPIO the reported frequency will be that set by gpioHardwareClock.
     * If hardware PWM is active on the GPIO the reported frequency will be that set by gpioHardwarePWM.
     * <p>
     * Example:
     *    f = gpioGetPWMfrequency(23); // Get frequency used for GPIO23.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMfrequency">PIGPIO::gpioGetPWMfrequency</a>
     */
    @Override
    public int gpioGetPWMfrequency(int pin) {
        logger.trace("[PWM-FREQ::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        int result = PIGPIO.gpioGetPWMfrequency(pin);
        logger.trace("[PWM-FREQ::GET] <- PIN: {}; FREQUENCY={}; SUCCESS={}",  pin, result, (result>=0));
        validateResult(result);  // Returns the frequency (in hertz) used for the GPIO if OK, otherwise PI_BAD_USER_GPIO.
        return result;
    }

    /**
     * {@inheritDoc}
     *
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
     */
    @Override
    public void gpioHardwarePWM(int pin, int frequency, int dutyCycle) {
        logger.trace("[HW-PWM::SET] -> PIN: {}; FREQUENCY={}; DUTY-CYCLE={}", pin, frequency, dutyCycle);
        validateReady();
        validateUserPin(pin);
        // validateHwPwmFrequency(frequency); TODO :: IMPLEMENT 'validateHwPwmFrequency()'
        int result = PIGPIO.gpioHardwarePWM(pin, frequency, dutyCycle);
        logger.trace("[HW-PWM::SET] <- PIN: {}; SUCCESS={}",  pin, (result>=0));
        validateResult(result);  // Returns the numerically closest frequency if OK, otherwise PI_BAD_USER_GPIO.
    }

    @Override
    public void gpioNotifications(int pin, boolean enabled) {
        if(enabled)
            PIGPIO.gpioSetAlertFunc(pin, gpioAlertCallbackHandler);
        else
            PIGPIO.gpioDisableAlertFunc(pin);
    }

    /**
     * This handler is used internally to dispatch GPI state change events to Java consumers
     * (*this method is only invoked from native JNI code*)
     */
    private PiGpioAlertCallback gpioAlertCallbackHandler = new PiGpioAlertCallback() {
        @Override
        public void call(int pin, int state, long tick) {
            try {
                dispatchEvent(new PiGpioStateChangeEvent(pin, PiGpioState.from(state), tick));
            }
            catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }
    };

    // *****************************************************************************************************
    // *****************************************************************************************************
    // SERVO IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * {@inheritDoc}
     *
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
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioServo">PIGPIO::gpioServo</a>
     */
    public void gpioServo(int pin, int pulseWidth){
        logger.trace("[SERVO::SET] -> PIN: {}; PULSE-WIDTH={};", pin, pulseWidth);
        validateReady();
        validateUserPin(pin);
        validatePulseWidth(pulseWidth);
        int result = PIGPIO.gpioServo(pin, pulseWidth);
        logger.trace("[SERVO::SET] <- PIN: {}; PULSE-WIDTH={}; SUCCESS={}",  pin, pulseWidth, (result>=0));
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_PULSEWIDTH.
    }

    /**
     * {@inheritDoc}
     *
     * Returns the servo pulse-width setting for the GPIO.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetServoPulsewidth">PIGPIO::gpioGetServoPulsewidth</a>
     */
    public int gpioGetServoPulsewidth(int pin){
        logger.trace("[SERVO::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        int result = PIGPIO.gpioGetServoPulsewidth(pin);
        logger.trace("[SERVO::GET] <- PIN: {}; PULSE-WIDTH={}; SUCCESS={}",  pin, result, (result>=0));

        // Returns 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise)
        // if OK, otherwise PI_BAD_USER_GPIO or PI_NOT_SERVO_GPIO.
        validateResult(result);
        return result;
    }


    // *****************************************************************************************************
    // *****************************************************************************************************
    // DELAY/SLEEP/TIMER IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * {@inheritDoc}
     *
     * Delays for at least the number of microseconds specified by micros.
     * (Delays of 100 microseconds or less use busy waits.)
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioDelay">PIGPIO::gpioDelay</a>
     */
    @Override
    public long gpioDelay(long micros) {
        logger.trace("[DELAY] -> MICROS: {}", micros);
        validateReady();
        validateDelayMicroseconds(micros);
        long result = PIGPIO.gpioDelay(micros);
        logger.trace("[DELAY] <- MICROS: {}; SUCCESS={}",  micros, (result>=0));
        validateResult(result); // Upon success nothing is returned. On error a negative status code will be returned.
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Delays for at least the number of milliseconds specified by micros. (between 1 and 60000 [1 minute])
     * @see <a href="http://abyz.me.uk/rpi/pigpio/pigs.html#MILS">PIGPIO::MILS</a>
     */
    @Override
    public int gpioDelayMilliseconds(int millis){
        logger.trace("[DELAY] -> MILLIS: {}", millis);
        validateReady();
        validateDelayMilliseconds(millis);

        // determine number of microseconds
        long total_micros = millis * 1000;
        int seconds =  (int)(total_micros/1000000);
        int micros = (int)(total_micros%1000000);
        int result = PIGPIO.gpioSleep(PI_TIME_RELATIVE, seconds, micros);
        logger.trace("[DELAY] <- MILLIS: {}; SUCCESS={}",  millis, (result>=0));
        validateResult(result); // Upon success nothing is returned. On error a negative status code will be returned.
        return millis;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the current system tick.
     * Tick is the number of microseconds since system boot.
     * <p>
     * As tick is an unsigned 32 bit quantity it wraps around after 2^32 microseconds, which is
     * approximately 1 hour 12 minutes.  You don't need to worry about the wrap around as long as you
     * take a tick (uint32_t) from another tick, i.e. the following code will always provide the
     * correct difference.
     * <p>
     * Example
     *   uint32_t startTick, endTick;
     *   int diffTick;
     *   startTick = gpioTick();
     * <p>
     *   // do some processing
     *   endTick = gpioTick();
     *   diffTick = endTick - startTick;
     *   printf("some processing took %d microseconds", diffTick);
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#gpioTick">PIGPIO::gpioTick</a>
     */
    @Override
    public long gpioTick() {
        logger.trace("[TICK::GET] -> Get current tick");
        validateReady();
        long result = PIGPIO.gpioTick();
        logger.trace("[TICK::GET] <- TICK: {}; SUCCESS={}",  result, (result>=0));
        return result;
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // I2C IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * {@inheritDoc}
     *
     * Opens a I2C device on a I2C bus for communications.
     * This returns a handle for the device at the address on the I2C bus.
     * Physically buses 0 and 1 are available on the Pi.
     * Higher numbered buses will be available if a kernel supported bus multiplexor is being used.
     * <p>
     * The GPIO used are given in the following table.
     *         SDA   SCL
     * I2C0     0     1
     * I2C1     2     3
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cOpen">PIGPIO::i2cOpen</a>
     */
    @Override
    public int i2cOpen(int bus, int device, int flags) {
        logger.trace("[I2C::OPEN] -> Open I2C Bus [{}] and Device [{}]; FLAGS=[{}]", bus, device, flags);
        validateReady();
        validateI2cBus(bus);
        validateI2cDeviceAddress(device);
        int handle = PIGPIO.i2cOpen(bus, device, flags);
        boolean success = (handle>=0);
        logger.trace("[I2C::OPEN] <- HANDLE={}; SUCCESS={}",  handle, success);
        validateResult(handle, false);

        // if the open was successful, then we need to cache the I2C handle
        if(success) {
            i2cHandles.add(handle);
        }

        // return handle
        return handle;
    }

    /**
     * {@inheritDoc}
     *
     * This closes the I2C device associated with the handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cClose">PIGPIO::i2cClose</a>
     */
    @Override
    public int i2cClose(int handle) {
        logger.trace("[I2C::CLOSE] -> HANDLE={}, Close I2C Bus", handle);
        validateReady();
        validateHandle(handle);
        int result = PIGPIO.i2cClose(handle);
        boolean success = (result>=0);
        logger.trace("[I2C::CLOSE] <- HANDLE={}; SUCCESS={}; RESULT={}",  handle, success, result);
        validateResult(result, false);

        // if the close was successful, then we need to remove the I2C handle from cache
        if(success) i2cHandles.remove(handle);

        // return result
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This sends a single bit (in the Rd/Wr bit) to the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteQuick">PIGPIO::i2cWriteQuick</a>
     */
    @Override
    public int i2cWriteQuick(int handle, boolean bit) {
        logger.trace("[I2C::WRITE] -> HANDLE={}; R/W Bit [{}]", handle, bit ? 1 : 0);
        validateReady();
        validateHandle(handle);
        int result = PIGPIO.i2cWriteQuick(handle ,bit);
        boolean success = (result>=0);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This sends a single byte to the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteByte">PIGPIO::i2cWriteByte</a>
     */
    @Override
    public int i2cWriteByte(int handle, byte value) {
        logger.trace("[I2C::WRITE] -> HANDLE={}; Byte [{}]", handle, Byte.toUnsignedInt(value));
        validateReady();
        validateHandle(handle);
        int result = PIGPIO.i2cWriteByte(handle, value);
        boolean success = (result>=0);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This reads a single byte from the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadByte">PIGPIO::i2cReadByte</a>
     */
    @Override
    public int i2cReadByte(int handle) {
        logger.trace("[I2C::READ] -> [{}]; Byte", handle);
        validateReady();
        validateHandle(handle);
        int result = PIGPIO.i2cReadByte(handle);
        boolean success = (result>=0);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}; RESULT={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This writes a single byte to the specified register of the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteByteData">PIGPIO::i2cWriteByteData</a>
     */
    @Override
    public int i2cWriteByteData(int handle, int register, byte value) {
        logger.trace("[I2C::WRITE] -> [{}]; Register [{}]; Byte [{}]", handle ,register, Byte.toUnsignedInt(value));
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        int result = PIGPIO.i2cWriteByteData(handle, register, value);
        boolean success = (result>=0);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This writes a single 16 bit word to the specified register of the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteWordData">PIGPIO::i2cWriteWordData</a>
     */
    @Override
    public int i2cWriteWordData(int handle, int register, int value) {
        logger.trace("[I2C::WRITE] -> [{}]; Register [{}]; Word [{}]", handle ,register, value);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        int result = PIGPIO.i2cWriteWordData(handle, register, value);
        boolean success = (result>=0);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This reads a single byte from the specified register of the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadByteData">PIGPIO::i2cReadByteData</a>
     */
    @Override
    public int i2cReadByteData(int handle, int register) {
        logger.trace("[I2C::READ] -> [{}]; Register [{}]; Byte", handle ,register);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        int result = PIGPIO.i2cReadByteData(handle, register);
        boolean success = (result>=0);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}; RESULT={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This reads a single 16 bit word from the specified register of the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadWordData">PIGPIO::i2cReadWordData</a>
     */
    @Override
    public int i2cReadWordData(int handle, int register) {
        logger.trace("[I2C::READ] -> [{}]; Register [{}]; Word", handle ,register);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        int result = PIGPIO.i2cReadWordData(handle, register);
        boolean success = (result>=0);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}; RESULT={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This writes 16 bits of data to the specified register of the device associated with
     * handle and reads 16 bits of data in return. (in a single transaction)
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cProcessCall">PIGPIO::i2cProcessCall</a>
     */
    @Override
    public int i2cProcessCall(int handle, int register, int value) {
        logger.trace("[I2C::W/R] -> [{}]; Register [{}]; Word [{}]", handle ,register, value);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        int result = PIGPIO.i2cProcessCall(handle, register, value);
        boolean success = (result>=0);
        logger.trace("[I2C::W/R] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This writes up to 32 bytes to the specified register of the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteBlockData">PIGPIO::i2cWriteBlockData</a>
     */
    @Override
    public int i2cWriteBlockData(int handle, int register, byte[] data, int offset, int length) {
        logger.trace("[I2C::WRITE] -> [{}]; Register [{}]; Block [{} bytes]; offset={}", handle ,register, length, offset);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        validateI2cBlockLength(length);
        Objects.checkFromIndexSize(offset, length, data.length);
        // write data array to I2C device register
        int result = PIGPIO.i2cWriteBlockData(handle, register, data, offset, length);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, (result>=0), result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This reads a block of up to 32 bytes from the specified register of the device associated with handle.
     * The amount of returned data is set by the device.
     */
    @Override
    public int i2cReadBlockData(int handle, int register, byte[] buffer, int offset, int length) {
        logger.trace("[I2C::READ] -> [{}]; Register [{}]; Block [{} bytes]; offset={}", handle, length, offset);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        Objects.checkFromIndexSize(offset, length, buffer.length);
        // perform the read on the I2C device register
        int result = PIGPIO.i2cReadBlockData(handle, register, buffer, offset);
        boolean success = result >=0;
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}; RESULT={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This writes data bytes to the specified register of the device associated with the handle and reads a
     * device specified number of bytes of data in return.
     * <p>
     * The SMBus 2.0 documentation states that a minimum of 1 byte may be sent and a minimum of 1 byte may be received.
     * The total number of bytes sent/received must be 32 or less.
     */
    @Override
    public int i2cBlockProcessCall(int handle, int register, byte[] data, int offset, int length){
        logger.trace("[I2C::W/R] -> [{}]; Register [{}]; Block [{} bytes]; offset={}", handle ,register, length, offset);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        Objects.checkFromIndexSize(offset, length, data.length);
        // write/read from I2C device register
        int result = PIGPIO.i2cBlockProcessCall(handle, register, data, offset, length);
        boolean success = result >=0;
        logger.trace("[I2C::W/R] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This writes data bytes to the specified register of the device associated with handle and reads a
     * device specified number of bytes of data in return.
     * <p>
     * The SMBus 2.0 documentation states that a minimum of 1 byte may be sent and a minimum of 1 byte may be received.
     * The total number of bytes sent/received must be 32 or less.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cBlockProcessCall">PIGPIO::i2cBlockProcessCall</a>
     */
    @Override
    public int i2cBlockProcessCall(int handle, int register,
                                   byte[] write, int writeOffset, int writeLength,
                                   byte[] read, int readOffset) {
        logger.trace("[I2C::W/R] -> [{}]; Register [{}]; Block [{} bytes]; woff={}, roff={}",
            handle ,register, writeLength, writeOffset, readOffset);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        validateI2cBlockLength(writeLength);
        Objects.checkFromIndexSize(writeOffset, writeLength, write.length);
        Objects.checkFromIndexSize(readOffset, writeLength, read.length);

        //create a temporary buffer to hold the write data and receive the read data
        // copy the write data from the offset to the length of write bytes
        byte[] buffer = Arrays.copyOfRange(write, writeOffset , writeOffset + writeLength);

        // write/read from I2C device register
        int result = PIGPIO.i2cBlockProcessCall(handle, register, buffer, 0, writeLength);
        boolean success = (result>=0);
        logger.trace("[I2C::W/R] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, success, result);
        validateResult(result, false);

        // copy data bytes returned in the temporary buffer/array to the "read" array
        // using the given offset position.
        if(success) {
            int readLength = result;
            if(buffer.length < readLength) readLength = buffer.length;

            // make sure the read array has sufficient space to store the bytes returned
           Objects.checkFromIndexSize(readOffset, readLength, read.length);
            System.arraycopy(buffer, 0, read, readOffset, readLength);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This reads count bytes from the specified register of the device associated with handle .
     * The maximum length of data that can be read is 32 bytes.
     * The minimum length of data that can be read is 1 byte.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadI2CBlockData">PIGPIO::i2cReadI2CBlockData</a>
     */
    @Override
    public int i2cReadI2CBlockData(int handle, int register, byte[] buffer, int offset, int length){
        logger.trace("[I2C::READ] -> [{}]; Register [{}]; I2C Block [{} bytes]; offset={}", handle ,register, length, offset);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        Objects.checkFromIndexSize(offset, length, buffer.length);
        // perform the read on the I2C device register
        int result = PIGPIO.i2cReadI2CBlockData(handle, register, buffer, offset, length);
        boolean success = result >=0;
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}; RESULT={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This writes 1 to 32 bytes to the specified register of the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteI2CBlockData">PIGPIO::i2cWriteI2CBlockData</a>
     */
    @Override
    public int i2cWriteI2CBlockData(int handle, int register, byte[] data, int offset, int length) {
        logger.trace("[I2C::WRITE] -> [{}]; Register [{}]; I2C Block [{} bytes]; offset={}", handle, register, length, offset);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        validateI2cBlockLength(length);
        Objects.checkFromIndexSize(offset, length, data.length);
        // write data array to I2C device register
        int result = PIGPIO.i2cWriteI2CBlockData(handle, register, data, offset, length);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, (result>=0), result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This reads count bytes from the raw device into byte buffer array.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadDevice">PIGPIO::i2cReadDevice</a>
     */
    @Override
    public int i2cReadDevice(int handle, byte[] buffer, int offset, int length) {
        logger.trace("[I2C::READ] -> [{}]; I2C Raw Read [{} bytes]; offset={}", handle, length, offset);
        validateReady();
        validateHandle(handle);
        Objects.checkFromIndexSize(offset, length, buffer.length);
        // perform the read on the I2C device
        int result = PIGPIO.i2cReadDevice(handle, buffer, offset, length);
        boolean success = result >=0;
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}; RESULT={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This writes the length of bytes from the provided data array to the raw I2C device.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteDevice">PIGPIO::i2cWriteDevice</a>
     */
    @Override
    public int i2cWriteDevice(int handle, byte[] data, int offset, int length) {
        logger.trace("[I2C::WRITE] -> [{}]; I2C Raw Write [{} bytes]; offset={}", handle, length, offset);
        validateReady();
        validateHandle(handle);
        Objects.checkFromIndexSize(offset, length, data.length);
        // write data array to I2C device
        int result = PIGPIO.i2cWriteDevice(handle, data, offset, length);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}; RESULT={}", handle, (result>=0), result);
        validateResult(result, false);
        return result;
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // SERIAL IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * {@inheritDoc}
     *
     * This function opens a serial device at a specified baud rate and with specified flags.
     * The device name must start with "/dev/tty" or "/dev/serial".
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serOpen">PIGPIO::serOpen</a>
     */
    @Override
    public int serOpen(CharSequence device, int baud, int flags) {
        logger.trace("[SERIAL::OPEN] -> Open Serial Port [{}] at Baud Rate [{}]", device, baud);
        validateReady();

        // open the serial port/device
        int result = PIGPIO.serOpen(device.toString(), baud, flags);
        int handle = result;
        boolean success = result >=0;
        logger.trace("[SERIAL::OPEN] <- HANDLE={}; SUCCESS={}",  handle, success);
        validateResult(result, false);

        // if the open was successful, then we need to add the SERIAL handle to cache
        if(success) serialHandles.add(handle);

        // return the handle
        return handle;
    }

    /**
     * {@inheritDoc}
     *
     * This function closes the serial device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serClose">PIGPIO::serClose</a>
     */
    @Override
    public int serClose(int handle) {
        logger.trace("[SERIAL::CLOSE] -> HANDLE={}, Close Serial Port", handle);
        validateReady();
        validateHandle(handle);

        // close the serial port/device
        int result  = PIGPIO.serClose(handle);
        boolean success = result >=0;
        logger.trace("[SERIAL::CLOSE] <- HANDLE={}; SUCCESS={}",  handle, success);
        validateResult(result, false);

        // if the close was successful, then we need to remove the SERIAL handle from cache
        if(success) serialHandles.remove(handle);

        // return result
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This function writes a single byte "value" to the serial port associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWriteByte">PIGPIO::serWriteByte</a>
     */
    @Override
    public int serWriteByte(int handle, byte value) {
        logger.trace("[SERIAL::WRITE] -> HANDLE={}; Byte [{}]", handle, Byte.toUnsignedInt(value));
        validateReady();
        validateHandle(handle);
        int result = PIGPIO.serWriteByte(handle, value);
        logger.trace("[SERIAL::WRITE] <- HANDLE={}; SUCCESS={}", handle, (result>=0));
        validateResult(result, false);
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * This function reads a byte from the serial port associated with handle.
     * If no data is ready PI_SER_READ_NO_DATA is returned.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serReadByte">PIGPIO::serReadByte</a>
     */
    @Override
    public int serReadByte(int handle) {
        logger.trace("[SERIAL::READ] -> [{}]; Byte", handle);
        validateReady();
        validateHandle(handle);
        int result = PIGPIO.serReadByte(handle);
        logger.trace("[SERIAL::READ] <- HANDLE={}; SUCCESS={}",  handle, (result>=0));
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This function writes multiple bytes from the buffer array ('data') to the serial
     * port associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serWrite">PIGPIO::serWrite</a>
     */
    @Override
    public int serWrite(int handle, byte[] data, int offset, int length) {
        logger.trace("[SERIAL::WRITE] -> [{}]; Serial Write [{} bytes]", handle, data.length);
        validateReady();
        Objects.checkFromIndexSize(offset, length, data.length);
        validateHandle(handle);
        // write data array to serial device/port
        int result = PIGPIO.serWrite(handle, data, offset, length);
        logger.trace("[SERIAL::WRITE] <- HANDLE={}; SUCCESS={}", handle, (result>=0));
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This function reads up count bytes from the serial port associated with handle and
     * writes them to the buffer parameter.   If no data is ready, zero is returned.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serRead">PIGPIO::serRead</a>
     */
    @Override
    public int serRead(int handle, byte[] buffer, int offset, int length) {
        logger.trace("[SERIAL::READ] -> [{}]; Serial Read [{} bytes]", handle, length);
        validateReady();
        Objects.checkFromIndexSize(offset, length, buffer.length);
        validateHandle(handle);
        // perform the read on the serial device/port
        int result = PIGPIO.serRead(handle, buffer, offset, length);
        boolean success = result >=0;
        logger.trace("[SERIAL::READ] <- HANDLE={}; SUCCESS={}; BYTES-READ={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This function returns the number of bytes available to be read from the device associated with handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#serDataAvailable">PIGPIO::serDataAvailable</a>
     */
    @Override
    public int serDataAvailable(int handle) {
        logger.trace("[SERIAL::AVAIL] -> Get number of bytes available to read");
        validateReady();
        int result = PIGPIO.serDataAvailable(handle);
        logger.trace("[SERIAL::AVAIL] <- HANDLE={}; SUCCESS={}; AVAILABLE={}",  handle, (result>=0), result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This function will drain the current serial receive buffer of any lingering bytes.
     */
    @Override
    public int serDrain(int handle){
        logger.trace("[SERIAL::DRAIN] -> Drain any remaining bytes in serial RX buffer");
        validateReady();

        // drain data is serial device/port receive buffer
        int result = PIGPIO.serDrain(handle);
        validateResult(result, false);

        // if any bytes are available, then drain them now
        logger.trace("[SERIAL::DRAIN] <- HANDLE={}; SUCCESS={}; DRAINED={}",  handle, (result>=0), result);
        return result;
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // SPI IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * {@inheritDoc}
     *
     * This function opens a SPI device channel at a specified baud rate and with specified flags.
     * Data will be transferred at baud bits per second.
     * The flags may be used to modify the default behaviour of 4-wire operation, mode 0, active low chip select.
     * <p>
     * The Pi has two SPI peripherals: main and auxiliary.
     * The main SPI has two chip selects (channels), the auxiliary has three.
     * The auxiliary SPI is available on all models but the A and B.
     * <p>
     * The GPIO pins used are given in the following table.
     * <p>
     *             MISO    MOSI   SCLK   CE0   CE1   CE2
     *             -------------------------------------
     *   Main SPI    9      10     11      8	 7	   -
     *   Aux SPI    19      20     21     18	17    16
     * <p>
     *
     *  spiChan  : 0-1 (0-2 for the auxiliary SPI)
     *  baud     : 32K-125M (values above 30M are unlikely to work)
     *  spiFlags : see below
     * <p>
     * spiFlags consists of the least significant 22 bits.
     * -----------------------------------------------------------------
     * 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
     *  b  b  b  b  b  b  R  T  n  n  n  n  W  A u2 u1 u0 p2 p1 p0  m  m
     * -----------------------------------------------------------------
     * <p>
     * [mm] defines the SPI mode.
     *      (Warning: modes 1 and 3 do not appear to work on the auxiliary SPI.)
     * <p>
     *      Mode POL  PHA
     *      -------------
     *       0    0    0
     *       1    0    1
     *       2    1    0
     *       3    1    1
     * <p>
     * [px] is 0 if CEx is active low (default) and 1 for active high.
     * [ux] is 0 if the CEx GPIO is reserved for SPI (default) and 1 otherwise.
     * [A] is 0 for the main SPI, 1 for the auxiliary SPI.
     * [W] is 0 if the device is not 3-wire, 1 if the device is 3-wire. Main SPI only.
     * [nnnn] defines the number of bytes (0-15) to write before switching the MOSI line to MISO to read data. This field is ignored if W is not set. Main SPI only.
     * [T] is 1 if the least significant bit is transmitted on MOSI first, the default (0) shifts the most significant bit out first. Auxiliary SPI only.
     * [R] is 1 if the least significant bit is received on MISO first, the default (0) receives the most significant bit first. Auxiliary SPI only.
     * [bbbbbb] defines the word size in bits (0-32). The default (0) sets 8 bits per word. Auxiliary SPI only.
     * <p>
     * The spiRead, spiWrite, and spiXfer functions transfer data packed into 1, 2, or 4 bytes according to the word size in bits.
     *  - For bits 1-8 there will be one byte per word.
     *  - For bits 9-16 there will be two bytes per word.
     *  - For bits 17-32 there will be four bytes per word.
     * <p>
     * Multi-byte transfers are made in least significant byte first order.
     * E.g. to transfer 32 11-bit words buf should contain 64 bytes and count should be 64.
     * E.g. to transfer the 14 bit value 0x1ABC send the bytes 0xBC followed by 0x1A.
     * The other bits in flags should be set to zero.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiOpen">PIGPIO::spiOpen</a>
     */
    @Override
    public int spiOpen(int channel, int baud, int flags) {
        logger.trace("[SPI::OPEN] -> Open SPI Channel [{}] at Baud Rate [{}]; Flags=[{}]", channel, baud, flags);
        validateReady();
        int handle = PIGPIO.spiOpen(channel, baud, flags);
        boolean success = handle >=0;
        logger.trace("[SPI::OPEN] <- HANDLE={}; SUCCESS={}",  handle, success);
        validateResult(handle, false);

        // if the open was successful, then we need to add the SPI handle to cache
        if(success) spiHandles.add(handle);

        // return handle
        return handle;
    }

    /**
     * {@inheritDoc}
     *
     * This functions closes the SPI device identified by the handle.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiClose">PIGPIO::spiClose</a>
     */
    @Override
    public int spiClose(int handle) {
        logger.trace("[SPI::CLOSE] -> HANDLE={}, Close Serial Port", handle);
        validateReady();
        validateHandle(handle);
        int result = PIGPIO.spiClose(handle);
        boolean success = result >=0;
        logger.trace("[SPI::CLOSE] <- HANDLE={}; SUCCESS={}",  handle, success);
        validateResult(result, false);

        // if the close was successful, then we need to remove the SPI handle from cache
        if(success) spiHandles.remove(handle);

        // return result
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This function writes multiple bytes from the byte array ('data') to the SPI
     * device associated with the handle from the given offset index to the specified length.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    @Override
    public int spiWrite(int handle, byte[] data, int offset, int length) {
        logger.trace("[SPI::WRITE] -> [{}]; Serial Write [{} bytes]", handle, data.length);
        validateReady();
        validateHandle(handle);
        Objects.checkFromIndexSize(offset, length, data.length);
        validateHandle(handle);
        // write data array to SPI bus/channel
        int result = 0;
        byte[] someData = Arrays.copyOfRange(data, offset, length);
        int start = 0;
        while (start < someData.length) {
            int end = Math.min(someData.length, start + SPI_BUFFSIZ);
            byte[] chunk = Arrays.copyOfRange(someData, start, end);
            result += PIGPIO.spiWrite(handle, chunk, 0, chunk.length );
            logger.trace("[SPI::WRITE] <- HANDLE={}; SUCCESS={}", handle, (result>=0));
            start += SPI_BUFFSIZ;
        }
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This function reads a number of bytes specified by the 'length' parameter from the
     * SPI device associated with the handle and copies them to the 'buffer' byte array parameter.
     * The 'offset' parameter determines where to start copying/inserting read data in the byte array.
     * If no data is ready, zero is returned; otherwise, the number of bytes read is returned.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiRead">PIGPIO::spiRead</a>
     */
    @Override
    public int spiRead(int handle, byte[] buffer, int offset, int length) {
        logger.trace("[SPI::READ] -> [{}]; Serial Read [{} bytes]", handle, length);
        validateReady();
        validateHandle(handle);
        Objects.checkFromIndexSize(offset, length, buffer.length);
        validateHandle(handle);
        // perform the read on the SPI bus/channel
        int result = PIGPIO.spiRead(handle, buffer, offset, length);
        boolean success = result >=0;
        logger.trace("[SPI::READ] <- HANDLE={}; SUCCESS={}; BYTES-READ={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'write' byte array
     * from the given 'writeOffset' index to the specified length ('numberOfBytes').  Data
     * read from the SPI device is then copied to the 'read' byte array at the given 'readOffset'
     * using the same length.  Both the 'write' and 'read' byte arrays must be at least the size
     * of the defined 'numberOfBytes' + their corresponding offsets.
     * @see <a href="http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite">PIGPIO::spiWrite</a>
     */
    @Override
    public int spiXfer(int handle, byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes) {
        logger.trace("[SPI::XFER] -> [{}]; Serial Transfer [{} bytes]", handle, numberOfBytes);
        validateReady();
        validateHandle(handle);
        Objects.checkFromIndexSize(writeOffset, numberOfBytes, write.length);
        Objects.checkFromIndexSize(readOffset, numberOfBytes, read.length);
        // perform SPI data transfer
        int result = PIGPIO.spiXfer(handle, write, writeOffset, read, readOffset, numberOfBytes);
        boolean success = result >= 0;
        logger.trace("[SPI::XFER] <- HANDLE={}; SUCCESS={}; BYTES-READ={}",  handle, success, result);
        validateResult(result, false);
        return result;
    }
}
