package com.pi4j.boardinfo.model;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  JvmMemory.java
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
 * Provides memory information about the Java Virtual Machine (JVM).
 * This class allows you to retrieve details such as total memory, free memory,
 * used memory, and maximum available memory, as well as their values in megabytes.
 */
public class JvmMemory {

    // Constant to convert bytes to megabytes
    private static final double MB = 1024.0 * 1024.0;

    // Instance variables to store memory values
    private final long total;  // Total memory in bytes
    private final long free;   // Free memory in bytes
    private final long used;   // Used memory in bytes
    private final long max;    // Maximum memory in bytes

    /**
     * Constructor that initializes memory details based on the JVM runtime.
     *
     * @param runtime The runtime instance used to retrieve memory information.
     */
    public JvmMemory(Runtime runtime) {
        total = runtime.totalMemory();         // Total memory allocated to the JVM
        free = runtime.freeMemory();           // Free memory available for objects in the JVM
        used = total - free;                   // Used memory is the difference between total and free
        max = runtime.maxMemory();             // Maximum memory that the JVM can use
    }

    /**
     * Gets the total memory allocated to the JVM in bytes.
     *
     * @return Total memory in bytes.
     */
    public long getTotal() {
        return total;
    }

    /**
     * Gets the free memory available in the JVM in bytes.
     *
     * @return Free memory in bytes.
     */
    public long getFree() {
        return free;
    }

    /**
     * Gets the used memory in the JVM in bytes.
     *
     * @return Used memory in bytes.
     */
    public long getUsed() {
        return used;
    }

    /**
     * Gets the maximum memory that the JVM can use in bytes.
     *
     * @return Maximum memory in bytes.
     */
    public long getMax() {
        return max;
    }

    /**
     * Gets the total memory in megabytes.
     *
     * @return Total memory in MB.
     */
    public double getTotalInMb() {
        return total / MB;
    }

    /**
     * Gets the free memory in megabytes.
     *
     * @return Free memory in MB.
     */
    public double getFreeInMb() {
        return free / MB;
    }

    /**
     * Gets the used memory in megabytes.
     *
     * @return Used memory in MB.
     */
    public double getUsedInMb() {
        return used / MB;
    }

    /**
     * Gets the maximum memory the JVM can use in megabytes.
     *
     * @return Maximum memory in MB.
     */
    public double getMaxInMb() {
        return max / MB;
    }
}
