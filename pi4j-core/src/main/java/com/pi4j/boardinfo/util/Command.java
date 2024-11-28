package com.pi4j.boardinfo.util;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Command.java
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

/**
 * Interface that defines constants for common system commands.
 * These commands are typically used to gather system-related information
 * such as voltage, uptime, and temperature on Raspberry Pi or similar systems.
 */
public interface Command {

    /**
     * Command to measure the core voltage of the system.
     * <p>
     * This command uses the `vcgencmd` tool to query the system's core voltage,
     * which is useful for monitoring the power supply to the system's central processing unit (CPU).
     * The result is usually in the format of a voltage value (e.g., "1.20V").
     * </p>
     */
    String CORE_VOLTAGE_COMMAND = "vcgencmd measure_volts";

    /**
     * Command to retrieve the system's uptime.
     * <p>
     * This command uses the `uptime` utility to get the amount of time the system has been running since
     * its last boot. The output typically includes the system's uptime in days, hours, and minutes.
     * This can be useful for monitoring the system's stability or for detecting if the system has been restarted recently.
     * </p>
     */
    String UPTIME_COMMAND = "uptime";

    /**
     * Command to measure the temperature of the system's CPU.
     * <p>
     * This command uses the `vcgencmd` tool to query the CPU temperature of the system, which is crucial
     * for thermal management and ensuring that the system is not overheating. The output typically includes
     * the temperature value in Celsius (e.g., "48.3'C").
     * </p>
     */
    String TEMPERATURE_COMMAND = "vcgencmd measure_temp";
}
