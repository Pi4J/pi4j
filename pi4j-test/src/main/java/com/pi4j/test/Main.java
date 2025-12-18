package com.pi4j.test;/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  Main.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.io.IOType;
import com.pi4j.test.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>Main class.</p>
 * Simple test of the six providers. Dependent upon the wiring described in the README file.
 *
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * The entry point for the application. This method processes command-line
     * arguments to configure the application, initializes required components,
     * runs tests, and outputs results.
     *
     * @param args Command-line arguments for the application. Accepted parameters:
     *             - "-p <provider>": Specifies the test provider, either "linuxfs" or "ffm".
     *             - "-h": Displays help information and exits the application.
     * @throws Exception If an error occurs during the execution of the application.
     */
    public static void main(String[] args) throws Exception {

        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

        logger.info("==============================================================");
        logger.info("startup  Main ");
        logger.info("==============================================================");

        ProviderContext providerContext = null;
        String helpString = "params: -p linuxfs or ffm, -h help";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-p")) {
                String a = args[i + 1];
                providerContext = new ProviderContext(ProviderContext.TestProvider.getByName(a));
                i++;
            } else if (o.contentEquals("-h")) {
                logger.info(helpString);
                System.exit(31);
            } else {
                logger.info("  !!! Invalid Parm " + args);
                logger.info(helpString);
                System.exit(32);
            }
        }

        // Fallback in case no providerContext was specified with a parameter
        if (providerContext == null) {
            providerContext = new ProviderContext(ProviderContext.TestProvider.LINUXFS);
        }

        // create About class instance
        About about = new About(providerContext);
        about.enumerateProviders();
        about.enumeratePlatforms();
        about.describeDefaultPlatform();

        for (var ioType : IOType.values()) {
            about.enumerateProviders(ioType);
        }

        // Run the tests
        var tests = List.of(
            I2CTestCase.run(providerContext),
            SpiTestCase.run(providerContext),
            PWMTestCase.run(providerContext),
            DigitalInputTestCase.run(providerContext),
            DigitalOutputTestCase.run(providerContext),
            SerialTestCase.run(providerContext)
        );

        // Output results
        tests.forEach(t -> logger.info(t.getLogOutput()));

        providerContext.getContext().shutdown();
    }
}
