package com.pi4j.boardinfo.util;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BoardInfoHelper.java
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

import com.pi4j.boardinfo.datareader.BoardCodeReader;
import com.pi4j.boardinfo.datareader.CpuInfoReader;
import com.pi4j.boardinfo.datareader.MemInfoReader;
import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.boardinfo.model.BoardInfo;
import com.pi4j.boardinfo.model.BoardReading;
import com.pi4j.boardinfo.model.JavaInfo;
import com.pi4j.boardinfo.model.JvmMemory;
import com.pi4j.boardinfo.model.OperatingSystem;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pi4j.boardinfo.util.Command.CORE_VOLTAGE_COMMAND;
import static com.pi4j.boardinfo.util.Command.TEMPERATURE_COMMAND;
import static com.pi4j.boardinfo.util.Command.THROTTLED_STATE_COMMAND;
import static com.pi4j.boardinfo.util.Command.UPTIME_COMMAND;
import static com.pi4j.boardinfo.util.SystemProperties.ARCHITECTURE_DATA_MODEL;
import static com.pi4j.boardinfo.util.SystemProperties.JAVA_RUNTIME_VERSION;
import static com.pi4j.boardinfo.util.SystemProperties.JAVA_VENDOR;
import static com.pi4j.boardinfo.util.SystemProperties.JAVA_VENDOR_VERSION;
import static com.pi4j.boardinfo.util.SystemProperties.JAVA_VERSION;
import static com.pi4j.boardinfo.util.SystemProperties.OS_ARCH;
import static com.pi4j.boardinfo.util.SystemProperties.OS_NAME;
import static com.pi4j.boardinfo.util.SystemProperties.OS_VERSION;
import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

/**
 * The {@code BoardInfoHelper} class provides utility methods for detecting system and board information.
 * It retrieves detailed information about the operating system, Java runtime, board model, memory, temperature,
 * voltage, and other relevant hardware details of the Raspberry Pi or other compatible devices.
 *
 * <p>This class uses a singleton pattern to ensure that board information is detected only once and can be reused
 * throughout the application. The detected board information is stored in the {@link BoardInfo} object and can
 * be accessed via the {@link #current()} method.</p>
 */
public class BoardInfoHelper {

    private static final Logger logger = LoggerFactory.getLogger(BoardInfoHelper.class);

    private final BoardInfo boardInfo;

    /**
     * Private constructor to initialize the board information by detecting the operating system, Java version,
     * and board details.
     * This method will try to detect the board info using both the board code and name.
     */
    private BoardInfoHelper() {
        OperatingSystem os = new OperatingSystem(
            System.getProperty(OS_NAME),
            System.getProperty(OS_VERSION),
            System.getProperty(OS_ARCH)
        );
        logger.info("Detected OS: {}", os);

        JavaInfo java = new JavaInfo(
            System.getProperty(JAVA_VERSION),
            System.getProperty(JAVA_RUNTIME_VERSION),
            System.getProperty(JAVA_VENDOR),
            System.getProperty(JAVA_VENDOR_VERSION)
        );
        logger.info("Detected Java: {}", java);

        this.boardInfo = detectBoardInfo(os, java);
    }

    /**
     * Sets the model of the board. To be used when the detected board is not correct,
     * or to force a specific model e.g. on Orange Pi, or during testing.
     *
     * @param boardModel
     */
    public void setBoardModel(BoardModel boardModel) {
        this.boardInfo.setBoardModel(boardModel);
    }

    /**
     * Returns the current instance of {@code BoardInfoHelper}, which contains board information.
     *
     * @return the current {@link BoardInfo} instance
     */
    public static BoardInfo current() {
        return SingletonHelper.INSTANCE.boardInfo;
    }

    /**
     * Reinitializes the singleton instance of {@code BoardInfoHelper}.
     * This method is useful for testing or reloading board information in controlled scenarios.
     *
     * <p>Note: Frequent usage of this method is not recommended as it resets the global state.</p>
     */
    public static synchronized void reinitialize() {
        logger.info("Reinitializing BoardInfoHelper singleton instance.");
        SingletonHelper.resetInstance();
    }

    /**
     * Inner static class responsible for holding the singleton instance of {@code BoardInfoHelper}.
     * Implements the Bill Pugh Singleton Design, ensuring thread-safety and lazy initialization.
     */
    private static class SingletonHelper {
        private static volatile BoardInfoHelper INSTANCE = new BoardInfoHelper();

        /**
         * Resets the singleton instance for reinitialization.
         */
        private static synchronized void resetInstance() {
            INSTANCE = new BoardInfoHelper();
        }
    }

    /**
     * Detects the board information by attempting to retrieve the board version code or name.
     * This method prioritizes detection by board version code and falls back to board name if necessary.
     *
     * @param os   the detected operating system information
     * @param java the detected Java runtime information
     * @return a {@link BoardInfo} object containing the detected board information
     */
    private BoardInfo detectBoardInfo(OperatingSystem os, JavaInfo java) {
        String boardVersionCode = getBoardVersionCode();
        try {
            BoardModel boardModelByBoardCode = BoardModel.getByBoardCode(boardVersionCode);
            if (boardModelByBoardCode != BoardModel.UNKNOWN) {
                logger.info("Detected board type {} by code: {}", boardModelByBoardCode.name(), boardVersionCode);
                return new BoardInfo(boardModelByBoardCode, os, java);
            }
        } catch (Exception e) {
            logger.warn("Could not detect the board type for code {}: {}", boardVersionCode, e.getMessage());
        }

        String boardName = getBoardName();
        BoardModel boardModelByBoardName = BoardModel.getByBoardName(boardName);
        if (boardModelByBoardName != BoardModel.UNKNOWN) {
            logger.info("Detected board type {} by name: {}", boardModelByBoardName.name(), boardName);
            return new BoardInfo(boardModelByBoardName, os, java);
        }

        logger.warn("Sorry, could not detect the board type");
        return new BoardInfo(BoardModel.UNKNOWN, os, java);
    }

    /**
     * Checks if the device uses the RP1 chip.
     *
     * @return {@code true} if the board is a Raspberry Pi Model 5B, otherwise {@code false}.
     */
    public static boolean usesRP1() {
        return current().getBoardModel() == BoardModel.MODEL_5_B
            || current().getBoardModel() == BoardModel.MODEL_500
            || current().getBoardModel() == BoardModel.GENERIC_RP1;
    }

    /**
     * Checks if the application is running on a Raspberry Pi device.
     *
     * @return {@code true} if the board model is not {@link BoardModel#UNKNOWN}, otherwise {@code false}.
     */
    public static boolean runningOnRaspberryPi() {
        return current().getBoardModel() != BoardModel.UNKNOWN;
    }

    /**
     * Checks if the system architecture is 32-bit.
     *
     * @return {@code true} if the system is 32-bit, otherwise {@code false}.
     */
    public static boolean is32bit() {
        return !is64bit();
    }

    /**
     * Checks if the system architecture is 64-bit.
     *
     * @return {@code true} if the system is 64-bit, otherwise {@code false}.
     */
    public static boolean is64bit() {
        return System.getProperty(ARCHITECTURE_DATA_MODEL).equals("64");
    }

    /**
     * Retrieves the board version code by reading CPU revision information.
     *
     * @return the board version code, or an empty string if not found
     */
    public static String getBoardVersionCode() {
        var output = CpuInfoReader.getCpuRevision();
        if (output.isSuccess()) {
            return output.getOutputMessage();
        }
        logger.error("Could not get the board version code: {}", output.getErrorMessage());
        return StringUtil.EMPTY;
    }

    /**
     * Retrieves the board name by reading device tree information.
     *
     * @return the board name, or an empty string if not found
     */
    public static String getBoardName() {
        var output = BoardCodeReader.getBoardCode();
        if (output.isSuccess()) {
            return output.getOutputMessage();
        }
        logger.error("Could not get the board name: {}", output.getErrorMessage());
        return "";
    }

    /**
     * Retrieves information about the JVM memory usage.
     *
     * @return a {@link JvmMemory} object containing memory details
     */
    public static JvmMemory getJvmMemory() {
        return new JvmMemory(Runtime.getRuntime());
    }

    /**
     * Retrieves a collection of readings about the board, including name, version code,
     * temperature, uptime, core voltage, and memory total.
     *
     * @return a {@link BoardReading} object containing the board readings
     */
    public static BoardReading getBoardReading() {
        return new BoardReading(
            getBoardName(),
            getBoardVersionCode(),
            getTemperature(),
            getUptime(),
            getCoreVoltage(),
            getMemTotal(),
            getThrottledState()
        );
    }

    /**
     * Retrieves the total memory information from the system.
     *
     * @return a string representing the total memory
     */
    private static String getMemTotal() {
        return MemInfoReader.getMemTotal().getOutputMessage();
    }

    /**
     * Retrieves the core voltage reading of the board.
     *
     * @return a string representing the core voltage
     */
    private static String getCoreVoltage() {
        return execute(CORE_VOLTAGE_COMMAND).getOutputMessage();
    }

    /**
     * Retrieves the system uptime.
     *
     * @return a string representing the uptime
     */
    private static String getUptime() {
        return execute(UPTIME_COMMAND).getOutputMessage();
    }

    /**
     * Retrieves the system temperature reading.
     *
     * @return a string representing the temperature
     */
    private static String getTemperature() {
        return execute(TEMPERATURE_COMMAND).getOutputMessage();
    }

    /**
     * Retrieves the throttled state of the system.
     * <p>
     * This command uses the `vcgencmd get_throttled` utility to check for under-voltage,
     * throttling, or frequency capping conditions on the board.
     * </p>
     *
     * @return a string representing the throttled state of the system
     */
    public static String getThrottledState() {
        return execute(THROTTLED_STATE_COMMAND).getOutputMessage();
    }
}

