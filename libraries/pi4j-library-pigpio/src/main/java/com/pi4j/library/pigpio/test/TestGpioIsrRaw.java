package com.pi4j.library.pigpio.test;
/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  TestGpioIsrRaw.java
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
import com.pi4j.library.pigpio.internal.PIGPIO;
import com.pi4j.library.pigpio.internal.PiGpioIsrCallback;
import com.pi4j.library.pigpio.internal.PiGpioIsrCallbackEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;

/**
 * <p>Main class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestGpioIsrRaw {

    private static final Logger logger = LoggerFactory.getLogger(TestGpioIsrRaw.class);

    public static int GPIO_PIN = 21;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String loglevel = "INFO";
        if(args != null && args.length > 0){
            Level lvl = Level.valueOf(args[0].toUpperCase());
            loglevel = lvl.name();
        }
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", loglevel);

        logger.info("");
        logger.info("");
        logger.info("-----------------------------------------------------");
        logger.info("-----------------------------------------------------");
        logger.info("Pi4J Library :: PIGPIO JNI (Raw) Wrapper Library");
        logger.info("-----------------------------------------------------");
        logger.info("-----------------------------------------------------");
        logger.info("PIGPIO VERSION   : {}", PIGPIO.gpioVersion());
        logger.info("PIGPIO HARDWARE  : {}", Integer.toHexString(PIGPIO.gpioHardwareRevision()));
        int init = PIGPIO.gpioInitialise();
        if(init < 0){
            logger.error("ERROR; PIGPIO INIT FAILED; ERROR CODE: {}", init);
        } else {
            logger.info("-----------------------------------------------------");
            logger.info("PIGPIO INITIALIZED SUCCESSFULLY");
            logger.info("-----------------------------------------------------");

            PIGPIO.gpioSetMode(GPIO_PIN, PiGpioConst.PI_INPUT);
            PIGPIO.gpioSetPullUpDown(GPIO_PIN, PiGpioConst.PI_PUD_DOWN);
            PIGPIO.gpioGlitchFilter(GPIO_PIN, 100000);
            PIGPIO.gpioSetISRFunc(GPIO_PIN, new PiGpioIsrCallback() {
                @Override
                public void call(int pin, int state, long tick) {
                    logger.info("RECEIVED ISR EVENT! {} : {} :{}", pin, state, tick);
                }
            });

            System.in.read();
            logger.info("PIGPIO ISR CALLBACK REMOVED");
            PIGPIO.gpioDisableISRFunc(GPIO_PIN);
            System.in.read();

            logger.info("PIGPIO ISR EXTENDED CALLBACK ADDED");
            String testdata = "Hello World!";
            PIGPIO.gpioSetISRFuncEx(GPIO_PIN, new PiGpioIsrCallbackEx() {
                @Override
                public void call(int pin, int state, long tick, Object userdata) {
                    logger.info("RECEIVED ISR EVENT! {} : {} :{} : " + userdata, pin, state, tick);
                }
            }, testdata);

            System.in.read();
            logger.info("PIGPIO ISR CALLBACK REMOVED");
            PIGPIO.gpioDisableISRFunc(GPIO_PIN);
            System.in.read();

            logger.info("-----------------------------------------------------");
            PIGPIO.gpioTerminate();
            logger.info("PIGPIO TERMINATED");
        }
        logger.info("-----------------------------------------------------");
        logger.info("");
        logger.info("");
    }
}
