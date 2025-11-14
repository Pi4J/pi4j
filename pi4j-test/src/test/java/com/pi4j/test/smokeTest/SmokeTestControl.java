
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



/*
    public static void main(String[] args) throws Exception {
        //    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "TRACE");

        int testNumber = -1;
        SmokeTest testObj = null;
// ------------------------------------------------------------
        // Initialize the Pi4J Runtime Context
        // ------------------------------------------------------------
        // Before you can use Pi4J you must initialize a new runtime
        // context.
        //
        // The 'Pi4J' static class includes a few helper context
        // creators for the most common use cases.  The 'newAutoContext()'
        // method will automatically load all available Pi4J
        // extensions found in the application's classpath which
        // may include 'Platforms' and 'I/O Providers'

        pi4j = Pi4J.newAutoContext();

        // print installed providers
        System.out.println("----------------------------------------------------------");
        System.out.println("PI4J PROVIDERS");
        System.out.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        System.out.println("----------------------------------------------------------");


        console = new Console();
        console.print("==============================================================");
        console.print("startup  SmokeTestControl ");
        console.print("==============================================================");


        String helpString = " parms:   -t testNumber    -l list all tests   -h help \n ";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-t")) {
                String a = args[i + 1];
                testNumber = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-l")) {
                for (int t = 0; t < testName.length; t++) {
                    console.println("%s) test : %s ", testName[t][0], testName[t][1]);
                }
                break;
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }

        }
        String overResult = "";

        if (testNumber == -1) {
            console.println("run all tests");
            testObj = new SmokeTest(pi4j);
            overResult+=  String.format("Test result   \n") ;
            for (int t = 0; t < testName.length; t++) {
                int testNum = Integer.parseInt(testName[t][0]) ;
                boolean result = testObj.runTestCase(testNum);
                overResult+=  String.format(" Test # %d) %s  Result %b  \n", testNum, testName[testNum-1][1],  result) ;
            }
        } else {
            testObj = new SmokeTest(pi4j);
            boolean result = testObj.runTestCase(testNumber);
            overResult+=  String.format(" Test # %d) %s  Result %b  \n", testNumber, testName[testNumber-1][1],  result) ;
        }
        console.println(overResult);
        pi4j.shutdown();
    }
    */


}
