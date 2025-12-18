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

import java.util.List;

/**
 * Represents the readings and status information from a Raspberry Pi board.
 * This includes the board's unique code, version, temperature, uptime, voltage, memory usage, and throttled state.
 * Provides utility methods to parse and convert these readings into more useful formats (e.g., Celsius, Fahrenheit, integer values for throttling state).
 * <p>
 * The throttled state reflects whether the board is under certain limitations like low voltage or throttling due to high temperature or CPU usage.
 * This class is intended to capture the board's operational data to help monitor its health and performance.
 * <p>
 * Fields:
 * - {@link #boardCode}: The unique code identifying the board model.
 * - {@link #boardVersionCode}: The version code for the specific model of the board.
 * - {@link #temperature}: The current temperature of the board (as a string).
 * - {@link #uptimeInfo}: Information about how long the board has been running.
 * - {@link #volt}: The current voltage reading (as a string).
 * - {@link #memory}: Information about the memory usage of the board.
 * - {@link #throttledState}: The throttling state of the board, indicating if the board is under-voltage, throttled, or experiencing frequency capping (as a string).
 */
public class BoardReading {

    private static final Logger logger = LoggerFactory.getLogger(BoardReading.class);

    private final String boardCode;
    private final String boardVersionCode;
    private final String temperature;
    private final String uptimeInfo;
    private final String volt;
    private final String memory;
    private final String throttledState;

    /**
     * Constructor to initialize a {@link BoardReading} object.
     *
     * @param boardCode        the unique code for the board.
     * @param boardVersionCode the version code of the board.
     * @param temperature      the temperature reading of the board (in string format).
     * @param uptimeInfo       the uptime information for the board.
     * @param volt             the voltage reading of the board (in string format).
     * @param memory           the memory usage information for the board.
     * @param throttledState   the throttled state of the board, indicating under-voltage, throttling,
     *                         or frequency capping conditions (in string format).
     */
    public BoardReading(String boardCode, String boardVersionCode, String temperature, String uptimeInfo,
                        String volt, String memory, String throttledState) {
        this.boardCode = boardCode;
        this.boardVersionCode = boardVersionCode;
        this.temperature = temperature;
        this.uptimeInfo = uptimeInfo;
        this.volt = volt;
        this.memory = memory;
        this.throttledState = throttledState;
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
     * Converts the throttled state to an integer value.
     * The expected input format is "throttled=0x<value>".
     *
     * @return the throttled state as an integer, or 0 if the conversion fails.
     */
    public int getThrottledStateAsInt() {
        try {
            if (throttledState.startsWith("throttled=0x")) {
                return Integer.parseInt(throttledState.substring(12), 16);
            } else {
                logger.warn("Unexpected throttled state format: {}", throttledState);
            }
        } catch (Exception e) {
            logger.error("Can't convert throttled state value: {}. {}", throttledState, e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the list of active throttled states as decoded from the raw throttled state integer.
     * This method calls {@link ThrottledState#decode(int)} to convert the raw throttled state value
     * into a list of active {@link ThrottledState} enum values.
     *
     * @return a list of {@link ThrottledState} enums representing the active throttled states.
     */
    public List<ThrottledState> getThrottledStates() {
        return ThrottledState.decode(getThrottledStateAsInt());
    }

    /**
     * Gets a human-readable description of the active throttled states.
     * This method calls {@link ThrottledState#getActiveStatesDescription(int)} to convert the raw throttled
     * state value into a string describing the active throttled states.
     *
     * @return a string containing the description of the active throttled states.
     */
    public String getThrottledStatesDescription() {
        return ThrottledState.getActiveStatesDescription(getThrottledStateAsInt());
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
