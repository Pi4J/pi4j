package com.pi4j.boardinfo.model;

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
