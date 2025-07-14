package com.pi4j.test;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.onewire.OneWire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneWireTest {

    private static final Logger logger = LoggerFactory.getLogger(OneWireTest.class);

    // Device IDs for DS18B20 and DS2413
    private static final String DS18B20_DEVICE_ID = "28-06219443087e";
    private static final String DS2413_DEVICE_ID = "3a-0000005eaee8";

    // Desired resolution for DS18B20
    private static final String DS18B20_RESOLUTION = "10"; // Set to 10-bit precision

    // Value to set DS2413 state (0xf2 means State 1 ON, State 2 OFF)
    private static final byte DS2413_STATE_VALUE = (byte) 0xf2;

    public static void main(String[] args) {
        // Initialize Pi4J context
        Context pi4j = Pi4J.newAutoContext();

        try {
            // Handle DS18B20 sensor
            handleDS18B20(pi4j);

            // Handle DS2413 sensor
            handleDS2413(pi4j);
        } catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage());
        } finally {
            // Shutdown Pi4J context
            pi4j.shutdown();
        }
    }

    private static void handleDS18B20(Context pi4j) {
        logger.info("Interacting with DS18B20 sensor...");

        var ds18b20Config = OneWire.newConfigBuilder(pi4j)
            .id("ds18b20")
            .device(DS18B20_DEVICE_ID)
            .build();

        // Read and set temperature resolution for DS18B20
        try {
            var ds18b20 = pi4j.create(ds18b20Config);
            // Set resolution
            ds18b20.writeFile("resolution", DS18B20_RESOLUTION);
            logger.info("Set DS18B20 resolution to {} bits", DS18B20_RESOLUTION);

            // Read temperature
            String temperature = ds18b20.readFirstLine("temperature");

            // Parse and log temperature
            try {
                long tempValue = Long.parseLong(temperature);
                logger.info("Temperature: {}°C", tempValue / 100);
            } catch (NumberFormatException e) {
                logger.error("Failed to parse temperature: {}", temperature);
            }
        } catch (Exception e) {
            logger.error("Failed to interact with DS18B20 sensor: {}", e.getMessage());
        }
    }

    private static void handleDS2413(Context pi4j) {
        logger.info("Interacting with DS2413 sensor...");

        var ds2413Config = OneWire.newConfigBuilder(pi4j)
            .id("ds2413")
            .device(DS2413_DEVICE_ID)
            .build();

        // Read and set state for DS2413
        try {
            var ds2413 = pi4j.create(ds2413Config);

            // Read current state before setting new value
            String stateBefore = ds2413.readFirstLine("state");
            logger.info("Current state before update: {}", stateBefore);

            // Set new state to 0xf2 (State 1 ON, State 2 OFF)
            ds2413.writeFile("state", DS2413_STATE_VALUE);
            logger.info("Set DS2413 state to 0xf2 (State 1 ON, State 2 OFF)");

            // Read current state after setting new value
            String stateAfter = ds2413.readFirstLine("state");
            logger.info("Current state after update: {}", stateAfter);
        } catch (Exception e) {
            logger.error("Failed to interact with DS2413 sensor: {}", e.getMessage());
        }
    }
}
