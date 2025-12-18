package com.pi4j.library.pigpio.test;
/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  TestGpioAlert.java
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
public class TestGpioAlert {

    private static final Logger logger = LoggerFactory.getLogger(TestGpioAlert.class);

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
        PiGpio piGpio = PiGpio.newNativeInstance();

        piGpio.gpioInitialise();
        logger.info("-----------------------------------------------------");
        logger.info("PIGPIO INITIALIZED SUCCESSFULLY");
        logger.info("-----------------------------------------------------");

        logger.info("PIGPIO VERSION   : {}", piGpio.gpioVersion());
        logger.info("PIGPIO HARDWARE  : {}", piGpio.gpioHardwareRevision());


        piGpio.gpioSetMode(GPIO_PIN, PiGpioMode.INPUT);
        piGpio.gpioSetPullUpDown(GPIO_PIN, PiGpioPud.DOWN);
        piGpio.gpioGlitchFilter(GPIO_PIN, 1000);

        piGpio.addPinListener(GPIO_PIN, new PiGpioStateChangeListener() {
            @Override
            public void onChange(PiGpioStateChangeEvent event) {
                logger.info("RECEIVED ALERT EVENT! {}", event);
                throw new PiGpioException("TEST");
            }
        });

        System.in.read();
        logger.info("PIGPIO ALERT CALLBACK REMOVED");
        piGpio.removeAllPinListeners();
        //piGpio.removeAllListeners();
        System.in.read();

        logger.info("-----------------------------------------------------");
        piGpio.gpioTerminate();
        logger.info("PIGPIO TERMINATED");

        logger.info("-----------------------------------------------------");
        logger.info("");
        logger.info("");
    }
}
