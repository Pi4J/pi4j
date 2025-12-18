package com.pi4j.library.pigpio.test;
/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  InitRemote.java
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
import com.pi4j.library.pigpio.PiGpioConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * <p>Main class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class InitRemote {

    private static final Logger logger = LoggerFactory.getLogger(InitRemote.class);

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(String[] args) {
        String loglevel = "INFO";
        String host = PiGpioConst.DEFAULT_HOST;
        if(args != null && args.length > 0){
            if(args.length >= 2) {
                Level lvl = Level.valueOf(args[1].toUpperCase());
                loglevel = lvl.name();
            }
            host = args[0];
        }
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", loglevel);

        // create native PiGpio instance
        PiGpio piGpio = PiGpio.newSocketInstance(host);
        logger.info("");
        logger.info("");
        logger.info("-----------------------------------------------------");
        logger.info("-----------------------------------------------------");
        logger.info("Pi4J Library :: PIGPIO (Remote) JNI Wrapper Library");
        logger.info("-----------------------------------------------------");
        logger.info("-----------------------------------------------------");
        int init = piGpio.gpioInitialise();
        if(init < 0){
            logger.error("ERROR; PIGPIO INIT FAILED; ERROR CODE: {}", init);
        } else {
            logger.info("PIGPIO INITIALIZED SUCCESSFULLY");
        }
        logger.info("PIGPIO VERSION   : {}", piGpio.gpioVersion());
        logger.info("PIGPIO HARDWARE  : {}", Long.toHexString(piGpio.gpioHardwareRevision()));
        piGpio.gpioTerminate();
        logger.info("PIGPIO TERMINATED");
        logger.info("-----------------------------------------------------");
        logger.info("");
        logger.info("");
    }
}
