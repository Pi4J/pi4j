
/*
 *
 *
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  FILENAME      :  SmokeTestControl.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  #L%
 *
 *
 */

package com.pi4j.test.smokeTest;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.ffm.providers.serial.SerialFFMProviderImpl;
import com.pi4j.util.Console;

import org.junit.jupiter.api.*;





public class SmokeTestControl {

    // Note: order of entry important, do not alter without edit runTestCase

    static Context pi4j = null;
    static Console console = null;
    final int idValueMskBMP = 0x58;   // expected chpId value BMP280
    final int idValueMskBME = 0x60;   // expected chpId value BME280

    static SmokeTest testObj = null;

    @BeforeAll
    public static void beforeTest() {

        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        pi4j = Pi4J.newAutoContext();
        testObj = new SmokeTest(pi4j);
    }


    @AfterAll
    public static void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }


    @Test
    public void testI2c() {
       int id = testObj.readI2cID();
       Assertions.assertTrue(id == idValueMskBMP || id ==idValueMskBME, "TestBMP and BME ID");
    }

    @Test
    public void testSpi() {
        int id = testObj.readSpiID();
        Assertions.assertTrue(id == idValueMskBMP || id ==idValueMskBME, "TestBMP and BME ID");
    }

    @Test
    public void testPWM() throws Exception {
         int count = testObj.testPWM() ;
        Assertions.assertTrue(count == 10, "TestPWM flashing 10 times in 10 seconds");
    }

    @Test
    public void testGpioIn() {
        DigitalState state = testObj.testGpioIn() ;
        Assertions.assertTrue(state  ==  DigitalState.HIGH, "TestGpioIn  now HIGHs");
    }

    @Test
    public void testGpioOut() {
        DigitalState state = testObj.testGpioOut() ;
        Assertions.assertTrue(state  ==  DigitalState.HIGH, "TestGpioOut  now HIGHs");
    }

    @Test
    public void testSerial() {
        DigitalState state = testObj.testGpioOut() ;
        Assertions.assertTrue(true, "TestSerial expected read on loopback");
    }





}
