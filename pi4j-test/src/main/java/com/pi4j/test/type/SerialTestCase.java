package com.pi4j.test.type;

import com.pi4j.io.serial.*;
import com.pi4j.test.ProviderContext;
import com.pi4j.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SerialTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(SerialTestCase.class);

    public static TestResult run(ProviderContext providerContext) {
        if (providerContext.getTestProvider() == ProviderContext.TestProvider.LINUXFS) {
            logger.info("SKIP test, not in LINUXFS");
            return new TestResult("Serial", false, "Not available in LINUXFS");
        }

        final SerialReader[] serialReader = new SerialReader[1];

        String testData = "serial_test serial_test serial_test serial_test  ";

        var serialConfig = Serial.newConfigBuilder(providerContext.getContext())
            .baud(Baud._4800)
            .dataBits_8()
            .parity(Parity.NONE)
            .stopBits(StopBits._1)
            .flowControl(FlowControl.NONE)
            .id("smokeTest port")
            .port("/dev/ttyAMA0")       //serial0")    // /dev/ttyAMA0  /dev/ttyS0
            .build();
        var serial = providerContext.getContext().create(serialConfig);

        serial.open();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.info("BUG, isOpen not implemented...Waiting till serial port is open");
                while (!serial.isOpen()) {
                    try {
                        Thread.sleep(1000);
                        logger.info("retry...");
                        break;   //  bug work around
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                //opened now!
                logger.info("serial port is open!");

                // Start a thread to handle the incoming data from the serial port
                serialReader[0] = new SerialReader(serial);
                Thread serialReaderThread = new Thread(serialReader[0], "SerialReader");
                serialReaderThread.setDaemon(true);
                serialReaderThread.start();

            }
        };

        logger.info("about to start runnable");

        runnable.run();

        for (int i = 0; i < 10; i++) {
            serial.write(testData);
        }

        // allow time for the serail reader to process incoming data
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        serialReader[0].stopReading();
        String read = serialReader[0].getReadData();

        if (read.indexOf(testData) > -1) {
            return new TestResult("Serial", true, "Expected data received");
        } else {
            return new TestResult("Serial", false, "Didn't receive the expected data: " + read);
        }
    }

    private static class SerialReader implements Runnable {

        private final Serial serial;
        private String line = "";
        private boolean continueReading = true;


        public SerialReader(Serial serial) {
            this.serial = serial;
        }

        public void stopReading() {
            continueReading = false;
        }

        @Override
        public void run() {
            // We use a buffered reader to handle the data received from the serial port
            BufferedReader br = new BufferedReader(new InputStreamReader(serial.getInputStream()));

            try {
                // Read data until the flag is false
                while (continueReading) {
                    // First we need to check if there is data available to read.
                    var available = serial.available();
                    if (available > 0) {
                        for (int i = 0; i < available; i++) {
                            byte b = (byte) br.read();
                            if (b < 32) {
                                //logger.info("All non-string bytes are ignored");
                            } else {
                                line += (char) b;
                                //logger.info("line: '" + line + "'");
                            }
                        }
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (Exception e) {
                logger.info("Error reading data from serial: " + e.getMessage());
                System.out.println(e.getStackTrace());
            }
        }

        private String getReadData() {
            return line;
        }
    }
}
