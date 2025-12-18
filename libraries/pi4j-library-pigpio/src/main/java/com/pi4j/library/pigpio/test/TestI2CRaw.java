package com.pi4j.library.pigpio.test;
/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  TestI2CRaw.java
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

import com.pi4j.library.pigpio.internal.PIGPIO;
import com.pi4j.library.pigpio.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;

/**
 * <p>Main class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestI2CRaw {

    private static final Logger logger = LoggerFactory.getLogger(TestI2CRaw.class);

    private static int I2C_BUS = 1;
    private static int I2C_DEVICE = 0x04;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(String[] args) {
        logger.info("PIGPIO VERSION   : {}", PIGPIO.gpioVersion());
        logger.info("PIGPIO HARDWARE  : {}", PIGPIO.gpioHardwareRevision());

        int init = PIGPIO.gpioInitialise();
        logger.info("PIGPIO INITIALIZE: {}", init);
        if(init < 0){
            logger.error("ERROR; PIGPIO INIT FAILED; ERROR CODE: {}", init);
            System.exit(init);
        }

        // open I2C channel/bus/device
        int handle = PIGPIO.i2cOpen(I2C_BUS, I2C_DEVICE, 0);
        logger.info("PIGPIO I2C OPEN  : {}", handle);
        if(handle < 0) {
            logger.error("ERROR; I2C OPEN FAILED: ERROR CODE: {}", handle);
            System.exit(handle);
        }

        logger.info("");
        logger.info("----------------------------------------");
        logger.info("TEST I2C SINGLE BYTE RAW READ/WRITE");
        logger.info("----------------------------------------");

        // iterate over BYTE range of values, WRITE the byte then immediately
        // READ back the byte value and compare to make sure they are the same values.
        for(int b = 0; b < 255; b++) {
            logger.info("[W/R BYTE]");

            // WRITE :: SINGLE RAW BYTE
            logger.info(" (WRITE) 0x{}", Integer.toHexString(b));
            int result = PIGPIO.i2cWriteByte(handle, (byte)b);
            if(result < 0) {
                logger.error("\nERROR; I2C WRITE FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }

            // READ :: SINGLE RAW BYTE
            result = PIGPIO.i2cReadByte(handle);
            if(result < 0) {
                logger.error("\nERROR; I2C READ FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }
            logger.info(" (READ) 0x{}", Integer.toHexString(result));
            logger.info("");

            int expected = b;
            int received = result;
            if(received != expected) {
                logger.error("\nERROR; I2C READ FAILED: BYTE MISMATCH: expected={}; received={}", expected, received);
                System.exit(0);
            }
        }

        // iterate over series of test values, WRITE the byte then immediately
        // READ back the byte value and compare to make sure they are the same values.
        for(int x = 1; x < 100; x++) {
            logger.info("[W/R BUFFER]");

            Random r = new Random();
            int len = r.nextInt((20)) + 2; // minimum of 2 bytes
            byte[] writeBuffer = new byte[len];
            r.nextBytes(writeBuffer);

            // WRITE :: MULTI-BYTE
            logger.info(" (WRITE) 0x{}", StringUtil.toHexString(writeBuffer));
            int result = PIGPIO.i2cWriteDevice(handle, writeBuffer, len);
            if(result < 0) {
                logger.error("\nERROR; I2C WRITE FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }

            // READ :: MULTI-BYTE
            byte[] readBuffer = new byte[len];
            result = PIGPIO.i2cReadDevice(handle, readBuffer, len);
            logger.info(" (READ) 0x{}", StringUtil.toHexString(readBuffer));
            logger.info("");
            if(result < 0) {
                logger.error("\nERROR; I2C READ FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }

            // validate read length
            if(result != len) {
                logger.error("\nERROR; I2C READ FAILED: LENGTH MISMATCH: {}", result);
                System.exit(result);
            }

            //validate data read back is same as written
            if(!Arrays.equals(writeBuffer, readBuffer)) {
                logger.error("\nERROR; I2C READ FAILED: BYTE MISMATCH: expected={}; received={}",
                             StringUtil.toHexString(writeBuffer), StringUtil.toHexString(readBuffer));
                System.exit(0);
            }
        }

        // close I2C channel
        PIGPIO.i2cClose(handle);

        PIGPIO.gpioTerminate();
        logger.info("PIGPIO TERMINATED");
        logger.info("ALL I2C RAW DEVICE TESTS COMPLETED SUCCESSFULLY");
    }
}
