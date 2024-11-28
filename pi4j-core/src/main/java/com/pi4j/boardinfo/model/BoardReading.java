package com.pi4j.boardinfo.model;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BoardReading.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the readings from a Raspberry Pi board including information
 * about its code, version, temperature, uptime, voltage, and memory usage.
 * Provides utility methods to parse and convert these readings.
 */
public class BoardReading {

    private static final Logger logger = LoggerFactory.getLogger(BoardReading.class);

    private final String boardCode;
    private final String boardVersionCode;
    private final String temperature;
    private final String uptimeInfo;
    private final String volt;
    private final String memory;

    /**
     * Constructor to initialize a {@link BoardReading} object.
     *
     * @param boardCode the unique code for the board.
     * @param boardVersionCode the version code of the board.
     * @param temperature the temperature reading of the board (in string format).
     * @param uptimeInfo the uptime information for the board.
     * @param volt the voltage reading of the board (in string format).
     * @param memory the memory usage information for the board.
     */
    public BoardReading(String boardCode, String boardVersionCode, String temperature, String uptimeInfo,
                        String volt, String memory) {
        this.boardCode = boardCode;
        this.boardVersionCode = boardVersionCode;
        this.temperature = temperature;
        this.uptimeInfo = uptimeInfo;
        this.volt = volt;
        this.memory = memory;
    }

    /**
     * Gets the unique code of the board.
     *
     * @return the board code as a string.
     */
    public String getBoardCode() {
        return boardCode;
    }

    /**
     * Gets the version code of the board.
     *
     * @return the version code of the board as a string.
     */
    public String getBoardVersionCode() {
        return boardVersionCode;
    }

    /**
     * Gets the temperature reading of the board in string format.
     *
     * @return the temperature reading as a string (e.g., "temp=45.0'C").
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     * Gets the uptime information of the board.
     *
     * @return the uptime information as a string.
     */
    public String getUptimeInfo() {
        return uptimeInfo;
    }

    /**
     * Gets the voltage reading of the board in string format.
     *
     * @return the voltage as a string (e.g., "volt=5.1V").
     */
    public String getVolt() {
        return volt;
    }

    /**
     * Gets the memory usage information of the board.
     *
     * @return the memory usage as a string.
     */
    public String getMemory() {
        return memory;
    }

    /**
     * Converts the temperature reading to Celsius. The expected input format is:
     * "temp=XX.X'C" or "temp=XX.X°C".
     *
     * @return the temperature in Celsius as a double, or 0 if the conversion fails.
     */
    public double getTemperatureInCelsius() {
        if (temperature.contains("temp=")) {
            try {
                return Double.parseDouble(temperature
                    .replace("temp=", "")
                    .replace("'C", "")
                    .replace("°C", ""));
            } catch (Exception e) {
                logger.error("Can't convert temperature value: {}", e.getMessage());
            }
        }
        return 0;
    }

    /**
     * Converts the temperature reading to Fahrenheit.
     * This method uses the Celsius temperature and applies the conversion formula:
     * (Celsius * 1.8) + 32.
     *
     * @return the temperature in Fahrenheit.
     */
    public double getTemperatureInFahrenheit() {
        return (getTemperatureInCelsius() * 1.8) + 32;
    }

    /**
     * Converts the voltage reading to a numeric value.
     * The expected input format is "volt=XX.XV".
     *
     * @return the voltage value as a double, or 0 if the conversion fails.
     */
    public double getVoltValue() {
        if (volt.contains("volt=")) {
            try {
                return Double.parseDouble(volt
                    .replace("volt=", "")
                    .replace("V", ""));
            } catch (Exception e) {
                logger.error("Can't convert volt value: {}", e.getMessage());
            }
        }
        return 0;
    }
}
