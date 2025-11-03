package com.pi4j.library.pigpio.internal;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PIGPIO.java
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


import com.pi4j.library.pigpio.PiGpioConst;
import com.pi4j.library.pigpio.util.NativeLibraryLoader;

import java.util.concurrent.Callable;

/**
 * <h1>WiringPi Shift Library</h1>
 *
 * <p>
 * WiringPi includes a shift library which more or less mimics the one in the Arduino system. This
 * allows you to shift 8-bit data values out of the Pi, or into the Pi from devices such as
 * shift-registers (e.g. 74,595) and so-on, although it can also be used in some bit-banging
 * scenarios.
 * </p>
 *
 * <p>
 * Before using the Pi4J library, you need to ensure that the Java VM in configured with access to
 * the following system libraries:
 * <ul>
 * <li>pi4j</li>
 * <li>wiringPi</li>
 * </ul>
 * <blockquote> This library depends on the wiringPi native system library. (developed by
 * Gordon Henderson @ <a href="http://wiringpi.com">http://wiringpi.com</a>)
 * </blockquote>
 * </p>
 *
 * @author Robert Savage (<a
 * href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @see <a href="https://pi4j.com/">https://pi4j.com/</a>
 * @see <a
 * href="http://wiringpi.com/reference/shift-library/">http://wiringpi.com/reference/shift-library/</a>
 */
public class PIGPIO {

    // private constructor
    private PIGPIO() {
        // forbid object construction
    }

    static {
        // Load the platform library
        NativeLibraryLoader.load("libpi4j-pigpio.so", "pi4j-pigpio");
    }

    /**
     * <p>gpioInitialise.</p>
     *
     * @return a int.
     */
    public static native int gpioInitialise();

    /**
     * <p>gpioTerminate.</p>
     */
    public static native void gpioTerminate();

    /**
     * <p>gpioSetMode.</p>
     *
     * @param gpio a int.
     * @param mode a int.
     * @return a int.
     */
    public static native int gpioSetMode(int gpio, int mode);

    /**
     * <p>gpioGetMode.</p>
     *
     * @param gpio a int.
     * @return a int.
     */
    public static native int gpioGetMode(int gpio);

    /**
     * <p>gpioSetPullUpDown.</p>
     *
     * @param gpio a int.
     * @param pud  a int.
     * @return a int.
     */
    public static native int gpioSetPullUpDown(int gpio, int pud);

    /**
     * <p>gpioRead.</p>
     *
     * @param gpio a int.
     * @return a int.
     */
    public static native int gpioRead(int gpio);

    /**
     * <p>gpioWrite.</p>
     *
     * @param gpio  a int.
     * @param level a int.
     * @return a int.
     */
    public static native int gpioWrite(int gpio, int level);

    /**
     * <p>gpioPWM.</p>
     *
     * @param user_gpio a int.
     * @param dutycycle a int.
     * @return a int.
     */
    public static native int gpioPWM(int user_gpio, int dutycycle);

    /**
     * <p>gpioGetPWMdutycycle.</p>
     *
     * @param user_gpio a int.
     * @return a int.
     */
    public static native int gpioGetPWMdutycycle(int user_gpio);

    /**
     * <p>gpioSetPWMrange.</p>
     *
     * @param user_gpio a int.
     * @param range     a int.
     * @return a int.
     */
    public static native int gpioSetPWMrange(int user_gpio, int range);

    /**
     * <p>gpioGetPWMrange.</p>
     *
     * @param user_gpio a int.
     * @return a int.
     */
    public static native int gpioGetPWMrange(int user_gpio);

    /**
     * <p>gpioGetPWMrealRange.</p>
     *
     * @param user_gpio a int.
     * @return a int.
     */
    public static native int gpioGetPWMrealRange(int user_gpio);

    /**
     * <p>gpioSetPWMfrequency.</p>
     *
     * @param user_gpio a int.
     * @param frequency a int.
     * @return a int.
     */
    public static native int gpioSetPWMfrequency(int user_gpio, int frequency);

    /**
     * <p>gpioGetPWMfrequency.</p>
     *
     * @param user_gpio a int.
     * @return a int.
     */
    public static native int gpioGetPWMfrequency(int user_gpio);

    /**
     * <p>gpioServo.</p>
     *
     * @param user_gpio  a int.
     * @param pulsewidth a int.
     * @return a int.
     */
    public static native int gpioServo(int user_gpio, int pulsewidth);

    /**
     * <p>gpioGetServoPulsewidth.</p>
     *
     * @param user_gpio a int.
     * @return a int.
     */
    public static native int gpioGetServoPulsewidth(int user_gpio);

    /**
     * <p>gpioSetAlertFunc.</p>
     *
     * @param user_gpio a int.
     * @param f         a {@link PiGpioAlertCallback} object.
     * @return a int.
     */
    public static native int gpioSetAlertFunc(int user_gpio, PiGpioAlertCallback f);

    /**
     * <p>gpioSetAlertFuncEx.</p>
     *
     * @param user_gpio a int.
     * @param f         a {@link Callable} object.
     * @param userdata  a {@link Object} object.
     * @return a int.
     */
    public static native int gpioSetAlertFuncEx(int user_gpio, PiGpioAlertCallbackEx f, Object userdata);

    /**
     * <p>gpioDisableAlertFunc.</p>
     *
     * @param user_gpio a int.
     * @return a int.
     */
    public static int gpioDisableAlertFunc(int user_gpio) {
        return gpioSetAlertFunc(user_gpio, null);
    }

    /**
     * <p>gpioSetISRFunc.</p>
     *
     * @param gpio    a int.
     * @param edge    a int.
     * @param timeout a int.
     * @param f       a {@link PiGpioIsrCallback} object.
     * @return a int.
     */
    public static native int gpioSetISRFunc(int gpio, int edge, int timeout, PiGpioIsrCallback f);

    /**
     * <p>gpioSetISRFunc.</p>
     *
     * @param gpio a int.
     * @param edge a int.
     * @param f    a {@link PiGpioIsrCallback} object.
     * @return a int.
     */
    public static int gpioSetISRFunc(int gpio, int edge, PiGpioIsrCallback f) {
        return gpioSetISRFunc(gpio, edge, 0, f);
    }

    /**
     * <p>gpioSetISRFunc.</p>
     *
     * @param gpio a int.
     * @param f    a {@link PiGpioIsrCallback} object.
     * @return a int.
     */
    public static int gpioSetISRFunc(int gpio, PiGpioIsrCallback f) {
        return gpioSetISRFunc(gpio, PiGpioConst.PI_EITHER_EDGE, f);
    }

    /**
     * <p>gpioDisableISRFunc.</p>
     *
     * @param gpio a int.
     * @return a int.
     */
    public static int gpioDisableISRFunc(int gpio) {
        return gpioSetISRFunc(gpio, 0, null);
    }

    /**
     * <p>gpioSetISRFuncEx.</p>
     *
     * @param gpio     a int.
     * @param edge     a int.
     * @param timeout  a int.
     * @param f        a {@link PiGpioIsrCallbackEx} object.
     * @param userdata a {@link java.lang.Object} object.
     * @return a int.
     */
    public static native int gpioSetISRFuncEx(int gpio, int edge, int timeout, PiGpioIsrCallbackEx f, Object userdata);

    /**
     * <p>gpioSetISRFuncEx.</p>
     *
     * @param gpio     a int.
     * @param edge     a int.
     * @param f        a {@link PiGpioIsrCallbackEx} object.
     * @param userdata a {@link java.lang.Object} object.
     * @return a int.
     */
    public static int gpioSetISRFuncEx(int gpio, int edge, PiGpioIsrCallbackEx f, Object userdata) {
        return gpioSetISRFuncEx(gpio, edge, 0, f, userdata);
    }

    /**
     * <p>gpioSetISRFuncEx.</p>
     *
     * @param gpio     a int.
     * @param f        a {@link PiGpioIsrCallbackEx} object.
     * @param userdata a {@link java.lang.Object} object.
     * @return a int.
     */
    public static int gpioSetISRFuncEx(int gpio, PiGpioIsrCallbackEx f, Object userdata) {
        return gpioSetISRFuncEx(gpio, PiGpioConst.PI_EITHER_EDGE, 0, f, userdata);
    }

    /**
     * <p>gpioNotifyOpen.</p>
     *
     * @return a int.
     */
    public static native int gpioNotifyOpen();

    /**
     * <p>gpioNotifyOpenWithSize.</p>
     *
     * @param bufSize a int.
     * @return a int.
     */
    public static native int gpioNotifyOpenWithSize(int bufSize);

    /**
     * <p>gpioNotifyBegin.</p>
     *
     * @param handle a int.
     * @param bits   a long.
     * @return a int.
     */
    public static native int gpioNotifyBegin(int handle, long bits);

    /**
     * <p>gpioNotifyPause.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int gpioNotifyPause(int handle);

    /**
     * <p>gpioNotifyClose.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int gpioNotifyClose(int handle);
//    public static native int gpioWaveClear();
//    public static native int gpioWaveAddNew();
//    public static native int gpioWaveAddGeneric(int numPulses, gpioPulse_t *pulses)
//    public static native int gpioWaveAddSerial(int user_gpio, int baud, int data_bits, int stop_bits, int offset, int numBytes, char *str);
//    public static native int gpioWaveCreate();
//    public static native int gpioWaveDelete(int wave_id);
//    public static native int gpioWaveTxSend(int wave_id, int wave_mode);
//    public static native int gpioWaveChain(byte[] buf, int bufSize);
//    public static native int gpioWaveTxAt();
//    public static native int gpioWaveTxBusy();
//    public static native int gpioWaveTxStop();
//    public static native int gpioWaveGetMicros();
//    public static native int gpioWaveGetHighMicros();
//    public static native int gpioWaveGetMaxMicros();
//    public static native int gpioWaveGetPulses();
//    public static native int gpioWaveGetHighPulses();
//    public static native int gpioWaveGetMaxPulses();
//    public static native int gpioWaveGetCbs();
//    public static native int gpioWaveGetHighCbs();
//    public static native int gpioWaveGetMaxCbs();

    /**
     * <p>gpioSerialReadOpen.</p>
     *
     * @param user_gpio a int.
     * @param baud      a int.
     * @param data_bits a int.
     * @return a int.
     */
    public static native int gpioSerialReadOpen(int user_gpio, int baud, int data_bits);

    /**
     * <p>gpioSerialReadInvert.</p>
     *
     * @param user_gpio a int.
     * @param invert    a int.
     * @return a int.
     */
    public static native int gpioSerialReadInvert(int user_gpio, int invert);

    /**
     * <p>gpioSerialRead.</p>
     *
     * @param user_gpio a int.
     * @param buffer    an array of {@link byte} objects.
     * @param bufSize   a int.
     * @return a int.
     */
    public static native int gpioSerialRead(int user_gpio, byte[] buffer, int bufSize);

    /**
     * <p>gpioSerialReadClose.</p>
     *
     * @param user_gpio a int.
     * @return a int.
     */
    public static native int gpioSerialReadClose(int user_gpio);

    /**
     * <p>i2cOpen.</p>
     *
     * @param i2cBus   a int.
     * @param i2cAddr  a int.
     * @param i2cFlags a int.
     * @return a int.
     */
    public static native int i2cOpen(int i2cBus, int i2cAddr, int i2cFlags);

    /**
     * <p>i2cClose.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int i2cClose(int handle);

    /**
     * <p>i2cWriteQuick.</p>
     *
     * @param handle a int.
     * @param bit    a boolean.
     * @return a int.
     */
    public static native int i2cWriteQuick(int handle, boolean bit);

    /**
     * <p>i2cWriteByte.</p>
     *
     * @param handle a int.
     * @param bVal   a byte.
     * @return a int.
     */
    public static native int i2cWriteByte(int handle, int bVal);

    /**
     * <p>i2cWriteByte.</p>
     *
     * @param handle a int.
     * @param bVal   a byte.
     * @return a int.
     */
    public static int i2cWriteByte(int handle, byte bVal) {
        return i2cWriteByte(handle, Byte.toUnsignedInt(bVal));
    }

    /**
     * <p>i2cReadByte.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int i2cReadByte(int handle);

    /**
     * <p>i2cWriteByteData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param bVal   a byte.
     * @return a int.
     */
    public static native int i2cWriteByteData(int handle, int i2cReg, int bVal);

    /**
     * <p>i2cWriteByteData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param bVal   a byte.
     * @return a int.
     */
    public static int i2cWriteByteData(int handle, int i2cReg, byte bVal) {
        return i2cWriteByteData(handle, i2cReg, Byte.toUnsignedInt(bVal));
    }

    /**
     * <p>i2cWriteWordData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param wVal   a int.
     * @return a int.
     */
    public static native int i2cWriteWordData(int handle, int i2cReg, int wVal);

    /**
     * <p>i2cReadByteData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @return a int.
     */
    public static native int i2cReadByteData(int handle, int i2cReg);

    /**
     * <p>i2cReadWordData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @return a int.
     */
    public static native int i2cReadWordData(int handle, int i2cReg);

    /**
     * <p>i2cProcessCall.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param wVal   a 16 bit value.
     * @return a int.
     */
    public static native int i2cProcessCall(int handle, int i2cReg, int wVal);

    /**
     * <p>i2cWriteBlockData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset starting position in buffer
     * @param count  a int.
     * @return a int.
     */
    public static native int i2cWriteBlockData(int handle, int i2cReg, byte[] buf, int offset, int count);

    /**
     * <p>i2cWriteBlockData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int i2cWriteBlockData(int handle, int i2cReg, byte[] buf, int count) {
        return i2cWriteBlockData(handle, i2cReg, buf, 0, count);
    }

    /**
     * <p>i2cReadBlockData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset starting position in buffer
     * @return a int.
     */
    public static native int i2cReadBlockData(int handle, int i2cReg, byte[] buf, int offset);

    /**
     * <p>i2cReadBlockData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @return a int.
     */
    public static int i2cReadBlockData(int handle, int i2cReg, byte[] buf) {
        return i2cReadBlockData(handle, i2cReg, buf, 0);
    }

    /**
     * <p>i2cBlockProcessCall.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset starting position in buffer
     * @param count  a int.
     * @return a int.
     */
    public static native int i2cBlockProcessCall(int handle, int i2cReg, byte[] buf, int offset, int count);

    /**
     * <p>i2cBlockProcessCall.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int i2cBlockProcessCall(int handle, int i2cReg, byte[] buf, int count) {
        return i2cBlockProcessCall(handle, i2cReg, buf, 0, count);
    }

    /**
     * <p>i2cReadI2CBlockData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset starting position in buffer
     * @param count  a int.
     * @return a int.
     */
    public static native int i2cReadI2CBlockData(int handle, int i2cReg, byte[] buf, int offset, int count);

    /**
     * <p>i2cReadI2CBlockData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int i2cReadI2CBlockData(int handle, int i2cReg, byte[] buf, int count) {
        return i2cReadI2CBlockData(handle, i2cReg, buf, 0, count);
    }

    /**
     * <p>i2cWriteI2CBlockData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset starting position in buffer
     * @param count  a int.
     * @return a int.
     */
    public static native int i2cWriteI2CBlockData(int handle, int i2cReg, byte[] buf, int offset, int count);

    /**
     * <p>i2cWriteI2CBlockData.</p>
     *
     * @param handle a int.
     * @param i2cReg a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int i2cWriteI2CBlockData(int handle, int i2cReg, byte[] buf, int count) {
        return i2cWriteI2CBlockData(handle, i2cReg, buf, 0, count);
    }

    /**
     * <p>i2cReadDevice.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset starting position in buffer.
     * @param count  a int.
     * @return a int.
     */
    public static native int i2cReadDevice(int handle, byte[] buf, int offset, int count);

    /**
     * <p>i2cReadDevice.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int i2cReadDevice(int handle, byte[] buf, int count) {
        return i2cReadDevice(handle, buf, 0, count);
    }

    /**
     * <p>i2cWriteDevice.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset starting position in buffer.
     * @param count  a int.
     * @return a int.
     */
    public static native int i2cWriteDevice(int handle, byte[] buf, int offset, int count);

    /**
     * <p>i2cWriteDevice.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int i2cWriteDevice(int handle, byte[] buf, int count) {
        return i2cWriteDevice(handle, buf, 0, count);
    }

    /**
     * <p>i2cSwitchCombined.</p>
     *
     * @param setting a int.
     */
    public static native void i2cSwitchCombined(int setting);
//    public static native int i2cSegments(int handle, pi_i2c_msg_t *segs, int numSegs);

    /**
     * <p>i2cZip.</p>
     *
     * @param handle a int.
     * @param inBuf  an array of {@link byte} objects.
     * @param inLen  a int.
     * @param outBuf an array of {@link byte} objects.
     * @param outLen a int.
     * @return a int.
     */
    public static native int i2cZip(int handle, byte[] inBuf, int inLen, byte[] outBuf, int outLen);

    /**
     * <p>bbI2COpen.</p>
     *
     * @param SDA  a int.
     * @param SCL  a int.
     * @param baud a int.
     * @return a int.
     */
    public static native int bbI2COpen(int SDA, int SCL, int baud);

    /**
     * <p>bbI2CClose.</p>
     *
     * @param SDA a int.
     * @return a int.
     */
    public static native int bbI2CClose(int SDA);

    /**
     * <p>bbI2CZip.</p>
     *
     * @param SDA    a int.
     * @param inBuf  an array of {@link byte} objects.
     * @param inLen  a int.
     * @param outBuf an array of {@link byte} objects.
     * @param outLen a int.
     * @return a int.
     */
    public static native int bbI2CZip(int SDA, byte[] inBuf, int inLen, byte[] outBuf, int outLen);
//    public static native int bscXfer(bsc_xfer_t *bsc_xfer);

    /**
     * <p>bbSPIOpen.</p>
     *
     * @param CS       a int.
     * @param MISO     a int.
     * @param MOSI     a int.
     * @param SCLK     a int.
     * @param baud     a int.
     * @param spiFlags a int.
     * @return a int.
     */
    public static native int bbSPIOpen(int CS, int MISO, int MOSI, int SCLK, int baud, int spiFlags);

    /**
     * <p>bbSPIClose.</p>
     *
     * @param CS a int.
     * @return a int.
     */
    public static native int bbSPIClose(int CS);

    /**
     * <p>bbSPIXfer.</p>
     *
     * @param CS     a int.
     * @param inBuf  an array of {@link byte} objects.
     * @param outBuf an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static native int bbSPIXfer(int CS, byte[] inBuf, byte[] outBuf, int count);

    /**
     * <p>spiOpen.</p>
     *
     * @param spiChan  a int.
     * @param baud     a int.
     * @param spiFlags a int.
     * @return a int.
     */
    public static native int spiOpen(int spiChan, int baud, int spiFlags);

    /**
     * <p>spiClose.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int spiClose(int handle);

    /**
     * <p>spiRead.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset position in buffer to start
     * @param count  a int.
     * @return a int.
     */
    public static native int spiRead(int handle, byte[] buf, int offset, int count);

    /**
     * <p>spiRead.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int spiRead(int handle, byte[] buf, int count) {
        return spiRead(handle, buf, 0, count);
    }

    /**
     * <p>spiWrite.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset position in buffer to start
     * @param count  a int.
     * @return a int.
     */
    public static native int spiWrite(int handle, byte[] buf, int offset, int count);

    /**
     * <p>spiWrite.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int spiWrite(int handle, byte[] buf, int count) {
        return spiWrite(handle, buf, 0, count);
    }

    /**
     * <p>spiXfer.</p>
     *
     * @param handle   a int.
     * @param txBuf    an array of {@link byte} objects.
     * @param txOffset position in txBuf array to start
     * @param rxBuf    an array of {@link byte} objects.
     * @param rxOffset position in rxBuf array to start
     * @param count    a int.
     * @return a int.
     */
    public static native int spiXfer(int handle, byte[] txBuf, int txOffset, byte[] rxBuf, int rxOffset, int count);

    /**
     * <p>spiXfer.</p>
     *
     * @param handle a int.
     * @param txBuf  an array of {@link byte} objects.
     * @param rxBuf  an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int spiXfer(int handle, byte[] txBuf, byte[] rxBuf, int count) {
        return spiXfer(handle, txBuf, 0, rxBuf, 0, count);
    }

    /**
     * <p>serOpen.</p>
     *
     * @param sertty   a {@link java.lang.String} object.
     * @param baud     a int.
     * @param serFlags a int.
     * @return a int.
     */
    public static native int serOpen(String sertty, int baud, int serFlags);

    /**
     * <p>serOpen.</p>
     *
     * @param sertty a {@link java.lang.String} object.
     * @param baud   a int.
     * @return a int.
     */
    public static int serOpen(String sertty, int baud) {
        return serOpen(sertty, baud, 0);
    }

    /**
     * <p>serClose.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int serClose(int handle);

    /**
     * <p>serWriteByte.</p>
     *
     * @param handle a int.
     * @param bVal   a byte.
     * @return a int.
     */
    public static native int serWriteByte(int handle, byte bVal);

    /**
     * <p>serReadByte.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int serReadByte(int handle);

    /**
     * <p>serWrite.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset position in array to start from
     * @param count  a int.
     * @return a int.
     */
    public static native int serWrite(int handle, byte[] buf, int offset, int count);

    /**
     * <p>serWrite.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int serWrite(int handle, byte[] buf, int count) {
        return serWrite(handle, buf, 0, count);
    }

    /**
     * <p>serRead.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param offset position in array to start from
     * @param count  a int.
     * @return a int.
     */
    public static native int serRead(int handle, byte[] buf, int offset, int count);

    /**
     * <p>serRead.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static int serRead(int handle, byte[] buf, int count) {
        return serRead(handle, buf, 0, count);
    }

    /**
     * <p>serDataAvailable.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int serDataAvailable(int handle);

    /**
     * <p>serDrain.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int serDrain(int handle);

    /**
     * <p>gpioTrigger.</p>
     *
     * @param user_gpio a int.
     * @param pulseLen  a int.
     * @param level     a int.
     * @return a int.
     */
    public static native int gpioTrigger(int user_gpio, int pulseLen, int level);

    /**
     * <p>gpioSetWatchdog.</p>
     *
     * @param user_gpio a int.
     * @param timeout   a int.
     * @return a int.
     */
    public static native int gpioSetWatchdog(int user_gpio, int timeout);

    /**
     * <p>gpioNoiseFilter.</p>
     *
     * @param user_gpio a int.
     * @param steady    a int.
     * @param active    a int.
     * @return a int.
     */
    public static native int gpioNoiseFilter(int user_gpio, int steady, int active);

    /**
     * <p>gpioGlitchFilter.</p>
     *
     * @param user_gpio a int.
     * @param steady    a int.
     * @return a int.
     */
    public static native int gpioGlitchFilter(int user_gpio, int steady);
//    public static native int gpioSetGetSamplesFunc(gpioGetSamplesFunc_t f, int bits);
//    public static native int gpioSetGetSamplesFuncEx(gpioGetSamplesFuncEx_t f, int bits, void *userdata);
//    public static native int gpioSetTimerFunc(int timer, int millis, gpioTimerFunc_t f);
//    public static native int gpioSetTimerFuncEx(int timer, int millis, gpioTimerFuncEx_t f, void *userdata);
//    public static native pthread_t *gpioStartThread(gpioThreadFunc_t f, void *userdata);
//    public static native void gpioStopThread(pthread_t *pth);

    /**
     * <p>gpioStoreScript.</p>
     *
     * @param script a {@link java.lang.String} object.
     * @return a int.
     */
    public static native int gpioStoreScript(String script);

    /**
     * <p>gpioRunScript.</p>
     *
     * @param script_id a int.
     * @param numPar    a int.
     * @param param     an array of {@link int} objects.
     * @return a int.
     */
    public static native int gpioRunScript(int script_id, int numPar, int[] param);

    /**
     * <p>gpioUpdateScript.</p>
     *
     * @param script_id a int.
     * @param numPar    a int.
     * @param param     an array of {@link int} objects.
     * @return a int.
     */
    public static native int gpioUpdateScript(int script_id, int numPar, int[] param);

    /**
     * <p>gpioScriptStatus.</p>
     *
     * @param script_id a int.
     * @param param     an array of {@link int} objects.
     * @return a int.
     */
    public static native int gpioScriptStatus(int script_id, int[] param);

    /**
     * <p>gpioStopScript.</p>
     *
     * @param script_id a int.
     * @return a int.
     */
    public static native int gpioStopScript(int script_id);

    /**
     * <p>gpioDeleteScript.</p>
     *
     * @param script_id a int.
     * @return a int.
     */
    public static native int gpioDeleteScript(int script_id);

    /**
     * <p>gpioSetSignalFunc.</p>
     *
     * @param signum a int.
     * @param f      a {@link PiGpioSignalCallback} object.
     * @return a int.
     */
    public static native int gpioSetSignalFunc(int signum, PiGpioSignalCallback f);

    /**
     * <p>gpioSetSignalFuncEx.</p>
     *
     * @param signum   a int.
     * @param f        a {@link PiGpioSignalCallbackEx} object.
     * @param userdata a {@link java.lang.Object} object.
     * @return a int.
     */
    public static native int gpioSetSignalFuncEx(int signum, PiGpioSignalCallbackEx f, Object userdata);

    /**
     * <p>gpioRead_Bits_0_31.</p>
     *
     * @return a int.
     */
    public static native int gpioRead_Bits_0_31();

    /**
     * <p>gpioRead_Bits_32_53.</p>
     *
     * @return a int.
     */
    public static native int gpioRead_Bits_32_53();

    /**
     * <p>gpioWrite_Bits_0_31_Clear.</p>
     *
     * @param bits a int.
     * @return a int.
     */
    public static native int gpioWrite_Bits_0_31_Clear(int bits);

    /**
     * <p>gpioWrite_Bits_32_53_Clear.</p>
     *
     * @param bits a int.
     * @return a int.
     */
    public static native int gpioWrite_Bits_32_53_Clear(int bits);

    /**
     * <p>gpioWrite_Bits_0_31_Set.</p>
     *
     * @param bits a int.
     * @return a int.
     */
    public static native int gpioWrite_Bits_0_31_Set(int bits);

    /**
     * <p>gpioWrite_Bits_32_53_Set.</p>
     *
     * @param bits a int.
     * @return a int.
     */
    public static native int gpioWrite_Bits_32_53_Set(int bits);

    /**
     * <p>gpioHardwareClock.</p>
     *
     * @param gpio    a int.
     * @param clkfreq a long.
     * @return a int.
     */
    public static native int gpioHardwareClock(int gpio, long clkfreq);

    /**
     * <p>gpioHardwarePWM.</p>
     *
     * @param gpio    a int.
     * @param PWMfreq a int.
     * @param PWMduty a int.
     * @return a int.
     */
    public static native int gpioHardwarePWM(int gpio, int PWMfreq, int PWMduty);

    /**
     * <p>gpioTime.</p>
     *
     * @param timetype a int.
     * @param seconds  a int.
     * @param micros   a int.
     * @return a int.
     */
    public static native int gpioTime(int timetype, int seconds, int micros);

    /**
     * <p>gpioSleep.</p>
     *
     * @param timetype a int.
     * @param seconds  a int.
     * @param micros   a int.
     * @return a int.
     */
    public static native int gpioSleep(int timetype, int seconds, int micros);

    /**
     * <p>gpioDelay.</p>
     *
     * @param micros a long.
     * @return a long.
     */
    public static native long gpioDelay(long micros);

    /**
     * <p>gpioTick.</p>
     *
     * @return a long.
     */
    public static native long gpioTick();

    /**
     * <p>gpioHardwareRevision.</p>
     *
     * @return a int.
     */
    public static native int gpioHardwareRevision();

    /**
     * <p>gpioVersion.</p>
     *
     * @return a int.
     */
    public static native int gpioVersion();

    /**
     * <p>gpioGetPad.</p>
     *
     * @param pad a int.
     * @return a int.
     */
    public static native int gpioGetPad(int pad);

    /**
     * <p>gpioSetPad.</p>
     *
     * @param pad         a int.
     * @param padStrength a int.
     * @return a int.
     */
    public static native int gpioSetPad(int pad, int padStrength);

    /**
     * <p>eventMonitor.</p>
     *
     * @param handle a int.
     * @param bits   a int.
     * @return a int.
     */
    public static native int eventMonitor(int handle, int bits);

    /**
     * <p>eventSetFunc.</p>
     *
     * @param event a int.
     * @param f     a {@link PiGpioEventCallback} object.
     * @return a int.
     */
    public static native int eventSetFunc(int event, PiGpioEventCallback f);

    /**
     * <p>eventSetFuncEx.</p>
     *
     * @param event    a int.
     * @param f        a {@link PiGpioEventCallbackEx} object.
     * @param userdata a {@link java.lang.Object} object.
     * @return a int.
     */
    public static native int eventSetFuncEx(int event, PiGpioEventCallbackEx f, Object userdata);

    /**
     * <p>eventSetFunc.</p>
     *
     * @param event a int.
     * @return a int.
     */
    public static int eventRemoveFunc(int event) {
        return eventSetFunc(event, null);
    }

    /**
     * <p>eventTrigger.</p>
     *
     * @param event a int.
     * @return a int.
     */
    public static native int eventTrigger(int event);

    /**
     * <p>shell.</p>
     *
     * @param scriptName   a {@link java.lang.String} object.
     * @param scriptString a {@link java.lang.String} object.
     * @return a int.
     */
    public static native int shell(String scriptName, String scriptString);

    /**
     * <p>fileOpen.</p>
     *
     * @param file a {@link java.lang.String} object.
     * @param mode a int.
     * @return a int.
     */
    public static native int fileOpen(String file, int mode);

    /**
     * <p>fileClose.</p>
     *
     * @param handle a int.
     * @return a int.
     */
    public static native int fileClose(int handle);

    /**
     * <p>fileWrite.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static native int fileWrite(int handle, byte[] buf, int count);

    /**
     * <p>fileRead.</p>
     *
     * @param handle a int.
     * @param buf    an array of {@link byte} objects.
     * @param count  a int.
     * @return a int.
     */
    public static native int fileRead(int handle, byte[] buf, int count);

    /**
     * <p>fileSeek.</p>
     *
     * @param handle     a int.
     * @param seekOffset a int.
     * @param seekFrom   a int.
     * @return a int.
     */
    public static native int fileSeek(int handle, int seekOffset, int seekFrom);

    /**
     * <p>fileList.</p>
     *
     * @param fpat  a {@link java.lang.String} object.
     * @param buf   a {@link java.lang.String} object.
     * @param count a int.
     * @return a int.
     */
    public static native int fileList(String fpat, String buf, int count);

    /**
     * <p>gpioCfgBufferSize.</p>
     *
     * @param cfgMillis a int.
     * @return a int.
     */
    public static native int gpioCfgBufferSize(int cfgMillis);

    /**
     * Configures pigpio to use a particular sample rate timed by a specified peripheral.
     * This function is only effective if called before gpioInitialise.
     * The timings are provided by the specified peripheral (PWM or PCM).
     * The default setting is 5 microseconds using the PCM peripheral.
     *
     * @param cfgMicros     1, 2, 4, 5, 8, 10
     * @param cfgPeripheral 0 (PWM), 1 (PCM)
     * @param cfgSource     deprecated, value is ignored
     * @return
     */
    public static native int gpioCfgClock(int cfgMicros, int cfgPeripheral, int cfgSource);

    /**
     * <p>gpioCfgDMAchannel.</p>
     *
     * @param DMAchannel a int.
     * @return a int.
     */
    public static native int gpioCfgDMAchannel(int DMAchannel);

    /**
     * <p>gpioCfgDMAchannels.</p>
     *
     * @param primaryChannel   a int.
     * @param secondaryChannel a int.
     * @return a int.
     */
    public static native int gpioCfgDMAchannels(int primaryChannel, int secondaryChannel);

    /**
     * <p>gpioCfgPermissions.</p>
     *
     * @param updateMask a long.
     * @return a int.
     */
    public static native int gpioCfgPermissions(long updateMask);

    /**
     * <p>gpioCfgSocketPort.</p>
     *
     * @param port a int.
     * @return a int.
     */
    public static native int gpioCfgSocketPort(int port);

    /**
     * <p>gpioCfgInterfaces.</p>
     *
     * @param ifFlags a int.
     * @return a int.
     */
    public static native int gpioCfgInterfaces(int ifFlags);

    /**
     * <p>gpioCfgMemAlloc.</p>
     *
     * @param memAllocMode a int.
     * @return a int.
     */
    public static native int gpioCfgMemAlloc(int memAllocMode);

    /**
     * <p>gpioCfgNetAddr.</p>
     *
     * @param numSockAddr a int.
     * @param sockAddr    an array of {@link int} objects.
     * @return a int.
     */
    public static native int gpioCfgNetAddr(int numSockAddr, int[] sockAddr);

    /**
     * <p>gpioCfgInternals.</p>
     *
     * @param cfgWhat a int.
     * @param cfgVal  a int.
     * @return a int.
     */
    public static native int gpioCfgInternals(int cfgWhat, int cfgVal);

    /**
     * <p>gpioCfgGetInternals.</p>
     *
     * @return a long.
     */
    public static native long gpioCfgGetInternals();

    /**
     * <p>gpioCfgSetInternals.</p>
     *
     * @param cfgVal a long.
     * @return a int.
     */
    public static native int gpioCfgSetInternals(long cfgVal);

    /**
     * <p>gpioCustom1.</p>
     *
     * @param arg1 a int.
     * @param arg2 a int.
     * @param argx an array of {@link byte} objects.
     * @param argc a int.
     * @return a int.
     */
    public static native int gpioCustom1(int arg1, int arg2, byte[] argx, int argc);

    /**
     * <p>gpioCustom2.</p>
     *
     * @param arg1   a int.
     * @param argx   an array of {@link byte} objects.
     * @param argc   a int.
     * @param retBuf an array of {@link byte} objects.
     * @param retMax a int.
     * @return a int.
     */
    public static native int gpioCustom2(int arg1, byte[] argx, int argc, byte[] retBuf, int retMax);
//    public static native int rawWaveAddSPI(rawSPI_t *spi, int offset, int spiSS, byte[] buf, int spiTxBits, int spiBitFirst, int spiBitLast, int spiBits);
//    public static native int rawWaveAddGeneric(int numPulses, rawWave_t *pulses);
//    public static native int rawWaveCB();
//    public static native rawCbs_t *rawWaveCBAdr(int cbNum);
//    public static native long rawWaveGetOOL(int pos);
//    public static native void rawWaveSetOOL(int pos, long lVal);
//    public static native long rawWaveGetOut(int pos);
//    public static native void rawWaveSetOut(int pos, long lVal);
//    public static native long rawWaveGetIn(int pos);
//    public static native void rawWaveSetIn(int pos, long lVal);
//    public static native rawWaveInfo_t rawWaveInfo(int wave_id);

    /**
     * <p>getBitInBytes.</p>
     *
     * @param bitPos  a int.
     * @param buf     an array of {@link byte} objects.
     * @param numBits a int.
     * @return a int.
     */
    public static native int getBitInBytes(int bitPos, byte[] buf, int numBits);

    /**
     * <p>putBitInBytes.</p>
     *
     * @param bitPos a int.
     * @param buf    an array of {@link byte} objects.
     * @param bit    a int.
     */
    public static native void putBitInBytes(int bitPos, byte[] buf, int bit);

    /**
     * <p>time_time.</p>
     *
     * @return a double.
     */
    public static native double time_time();

    /**
     * <p>time_sleep.</p>
     *
     * @param seconds a double.
     */
    public static native void time_sleep(double seconds);

    /**
     * <p>rawDumpWave.</p>
     */
    public static native void rawDumpWave();

    /**
     * <p>rawDumpScript.</p>
     *
     * @param script_id a int.
     */
    public static native void rawDumpScript(int script_id);
}
