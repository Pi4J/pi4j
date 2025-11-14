/*
 *
 * -
 *  * #%L
 *  * **********************************************************************
 *  * ORGANIZATION  :  Pi4J
 *  * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  * FILENAME      :  SmokeTestAll.java
 *  *
 *  * This file is part of the Pi4J project. More information about
 *  * this project can be found here:  https://pi4j.com/
 *  * **********************************************************************
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 *
 *
 */

/*
 *
 * -
 *  * #%L
 *  * **********************************************************************
 *  * ORGANIZATION  :  Pi4J
 *  * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  * FILENAME      :  SmokeTestAll.java
 *  *
 *  * This file is part of the Pi4J project. More information about
 *  * this project can be found here:  https://pi4j.com/
 *  * **********************************************************************
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 *
 *
 */

package com.pi4j.test.smokeTest;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.util.Console;




public class SmokeTestControl {

    // Note: order of entry important, do not alter without edit runTestCase
    static String[][] testName = {{"1","I2C     "}, {"2","SPI     "}, {"3", "PWM     "},
        {"4","GPIO_IN "} ,{"5", "GPIO_OUT"},{"6", "SERIAL  "}};

    static Context pi4j = null;
    static Console console = null;

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
            testObj = new SmokeTest(pi4j, console, testNumber);
            overResult+=  String.format("Test result   \n") ;
            for (int t = 0; t < testName.length; t++) {
                int testNum = Integer.parseInt(testName[t][0]) ;
                boolean result = testObj.runTestCase(testNum);
                overResult+=  String.format(" Test # %d) %s  Result %b  \n", testNum, testName[testNum-1][1],  result) ;
            }
        } else {
            testObj = new SmokeTest(pi4j, console, testNumber);
            boolean result = testObj.runTestCase(testNumber);
            overResult+=  String.format(" Test # %d) %s  Result %b  \n", testNumber, testName[testNumber-1][1],  result) ;
        }
        console.println(overResult);
        pi4j.shutdown();
    }

}
