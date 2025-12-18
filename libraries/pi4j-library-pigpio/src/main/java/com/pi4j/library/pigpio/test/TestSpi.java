package com.pi4j.library.pigpio.test;
/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  TestSpi.java
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

import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.library.pigpio.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * <p>Main class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestSpi {

    private static final Logger logger = LoggerFactory.getLogger(TestSpi.class);

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(String[] args) {

        PiGpio piGpio = PiGpio.newNativeInstance();

        int init = piGpio.initialize();
        logger.info("PIGPIO INITIALIZED: {}", init);

        logger.info("PIGPIO VERSION   : {}", piGpio.gpioVersion());
        logger.info("PIGPIO HARDWARE  : {}", piGpio.gpioHardwareRevision());

        int handle = piGpio.spiOpen(0, 50000, 0);
        logger.info("PIGPIO SPI OPEN  : {}", handle);
        if(handle < 0) {
            logger.error("ERROR; SPI OPEN FAILED: ERROR CODE: {}", handle);
            System.exit(handle);
        }

        // iterate over BYTE range of values, WRITE the byte then immediately
        // READ back the byte value and compare to make sure they are the same values.
        for(int b = 0; b < 256; b++) {
            logger.info("[W/R BYTE]");
            // WRITE :: SINGLE RAW BYTE
            logger.info(" (WRITE) 0x{}", Integer.toHexString(b));
            int result = piGpio.spiWrite(handle, new byte[]{ (byte)b }, 1);
            if(result < 0) {
                logger.error("\nERROR; SPI WRITE FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }

            // READ :: SINGLE RAW BYTE
            byte rx[] = new byte[1];

            result = piGpio.spiRead(handle, rx, 1);
            logger.info(" (READ) 0x{}", StringUtil.toHexString(rx));
            logger.info("");
            if(result < 0) {
                logger.error("\nERROR; SPI READ FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }

            int expected = b;
            int received = Byte.toUnsignedInt(rx[0]);
            if(received != expected) {
                logger.error("\nERROR; SPI READ FAILED: BYTE MISMATCH: expected={}; received={}", expected, received);
                System.exit(0);
            }
        }

        for(int b = 0; b < 256; b++) {
            logger.info("[XFER BYTE]");
            // WRITE :: SINGLE RAW BYTE
            byte tx[] = new byte[] {(byte)(b)};
            byte rx[] = new byte[] {0};
            logger.info(" (WRITE) 0x{}", StringUtil.toHexString(tx));
            int result = piGpio.spiXfer(handle, tx, rx, 1);
            logger.info(" (READ) 0x{}", StringUtil.toHexString(rx));
            logger.info("");
            if(result < 0) {
                logger.error("\nERROR; SPI XFER FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }
            if(b>0) { // ignore first read
                int expected = b-1;
                int received = Byte.toUnsignedInt(rx[0]);
                if (received != expected) {
                    logger.error("\nERROR; SPI XFER FAILED: BYTE MISMATCH: expected={}; received={}", expected, received);
                    System.exit(0);
                }
            }
        }

        // iterate over series of test values, WRITE the byte then immediately
        // READ back the byte value and compare to make sure they are the same values.
        for(int x = 1; x < 50; x++) {
            logger.info("[W/R BUFFER]");

            Random r = new Random();
            int len = r.nextInt((20)) + 2; // minimum of 2 bytes
            byte[] writeBuffer = new byte[len];
            r.nextBytes(writeBuffer);

            // WRITE :: MULTI-BYTE
            logger.info(" (WRITE) 0x{}", StringUtil.toHexString(writeBuffer));
            int result = piGpio.spiWrite(handle, writeBuffer, len);
            if(result < 0) {
                logger.error("\nERROR; SPI WRITE FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }

            // take a breath to allow time for the serial data
            // to get updated in the serial receive buffer
            //Thread.sleep(50);

            // READ :: MULTI-BYTE
            byte[] readBuffer = new byte[len];
            result = piGpio.spiRead(handle, readBuffer, len);
            logger.info(" (READ) 0x{}", StringUtil.toHexString(readBuffer));
            logger.info("");
            if(result < 0) {
                logger.error("\nERROR; SPI READ FAILED: ERROR CODE: {}", result);
                System.exit(result);
            }

            // validate read length
            if(result != len) {
                logger.error("\nERROR; SPI READ FAILED: LENGTH MISMATCH: {}", result);
                System.exit(result);
            }

            // the test harness will return a byte array where the first byte value in the array
            // is the last byte written to the SPI device followed by bytes from the original write
            // buffer from left to right (excluding the last byte)
            // example : WRITE="ABCDEF" / READ="FABCDE"
            ByteBuffer expectBuffer = ByteBuffer.allocate(len);
            expectBuffer.put(writeBuffer[writeBuffer.length-1]);
            expectBuffer.put(Arrays.copyOfRange(writeBuffer, 0, writeBuffer.length-1));

            if(expectBuffer.get(0) != readBuffer[0]) {
                logger.error("\nERROR; SPI READ FAILED: BYTE MISMATCH: expected={}; received={}",
                             StringUtil.toHexString(expectBuffer), StringUtil.toHexString(readBuffer));
                System.exit(0);
            }
        }

        // close SPI channel
        piGpio.spiClose(handle);

        piGpio.shutdown();
        logger.info("PIGPIO TERMINATED");
    }
}
