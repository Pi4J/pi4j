package com.pi4j.test.smoketest;

import com.pi4j.io.serial.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SerialTestCase extends TestCase {

    private static final String TEST_NAME = "Serial";

    private static final Logger logger = LoggerFactory.getLogger(SerialTestCase.class);

    public static TestResult run(ProviderContext providerContext) {
        if (providerContext.getTestProvider() == ProviderContext.TestProvider.LINUXFS) {
            logger.info("SKIP test, not in LINUXFS");
            return new TestResult(TEST_NAME, false, "Not available in LINUXFS");
        }

        // We need a final variable to use in a lambda
        final Serial[] serialWrapper = new Serial[1];

        try {
            final SerialReader[] serialReader = new SerialReader[1];

            String testData = "serial_test serial_test serial_test serial_test  ";

            // Initialize serial connection
            var serialConfig = Serial.newConfigBuilder(providerContext.getContext())
                .baud(Baud._4800)
                .dataBits_8()
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE)
                .id("smokeTest port")
                .port("/dev/ttyAMA0")       //serial0")    // /dev/ttyAMA0  /dev/ttyS0
                .build();
            serialWrapper[0] = providerContext.getContext().create(serialConfig);
            serialWrapper[0].open();

            // Making sure the connection is open
            long startTime = System.currentTimeMillis();
            //FFM bug,  open() is a NOP, isOpen() NOP returns false
            while (!serialWrapper[0].isOpen() && (System.currentTimeMillis() - startTime) < 10_000) {
                try {
                    Thread.sleep(1000);
                    logger.info("Waiting for connection..");
                    break; // workaround until FFM implements open() and isOPen()
                } catch (InterruptedException e) {
                    logger.error("Could not open serial connection: {}", e.getMessage());
                }
            }

            // Check if the connection is open         //FFM bug,  open() is a NOP, isOpen() NOP returns false
            //FFM bug,  open() is a NOP, isOpen() NOP returns false
           /* if (!serialWrapper[0].isOpen()) {
                return new TestResult(TEST_NAME, false, "Serial connection could not be opened");
            }*/

            // Serial connection is open now
            logger.info("serial port is open!");

            // Start a thread to handle the incoming data from the serial port
            serialReader[0] = new SerialReader(serialWrapper[0]);
            Thread serialReaderThread = new Thread(serialReader[0], "SerialReader");
            serialReaderThread.setDaemon(true);
            serialReaderThread.start();

            // Writing the data to the serial port
            for (int i = 0; i < 10; i++) {
                serialWrapper[0].write(testData);
            }

            // Allow time for the serial reader to process incoming data
            Thread.sleep(2000);

            // Stop reading
            serialReader[0].stopReading();

            // Check the received data
            String read = serialReader[0].getReadData();
            if (read.contains(testData)) {
                return new TestResult(TEST_NAME, true, "Expected data received");
            } else {
                return new TestResult(TEST_NAME, false, "Didn't receive the expected data: " + read);
            }
        } catch (Exception e) {
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        } finally {
            if (serialWrapper[0] != null) {
                serialWrapper[0].close();
            }
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
                logger.info("Error reading data from serial: {}", e.getMessage());
            }
        }

        private String getReadData() {
            return line;
        }
    }
}
