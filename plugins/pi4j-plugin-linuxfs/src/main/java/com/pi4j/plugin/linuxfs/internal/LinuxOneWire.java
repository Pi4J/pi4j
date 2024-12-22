package com.pi4j.plugin.linuxfs.internal;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * <p>LinuxOneWire class.</p>
 *
 * @see "https://www.kernel.org/doc/html/latest/driver-api/w1.html"
 */
public class LinuxOneWire {

    /** Constant <code>DEFAULT_SYSTEM_PATH="/sys/bus/w1/devices"</code> */
    public static final String DEFAULT_SYSTEM_PATH = "/sys/bus/w1/devices";

    protected final String systemPath;
    protected final String deviceId;
    protected final String devicePath;

    /**
     * <p>Constructor for LinuxOneWire.</p>
     *
     * @param systemPath a {@link String} object.
     * @param deviceId a {@link String} object.
     */
    public LinuxOneWire(String systemPath, String deviceId) {
        this.systemPath = systemPath;
        this.deviceId = deviceId;
        this.devicePath = Paths.get(systemPath, deviceId).toString();
    }

    /**
     * <p>Constructor for LinuxOneWire.</p>
     *
     * @param deviceId a {@link String} object.
     */
    public LinuxOneWire(String deviceId) {
        this(DEFAULT_SYSTEM_PATH, deviceId);
    }

    /**
     * Checks if the 1-Wire device is connected and accessible.
     *
     * @return {@code true} if the device is connected, {@code false} otherwise.
     */
    public boolean isConnected() {
        return Files.exists(Paths.get(devicePath));
    }

    /**
     * Gets the Linux File System path for the 1-Wire system.
     *
     * @return The 1-Wire system path as a {@link String}.
     */
    public String getSystemPath() {
        return systemPath;
    }

    /**
     * Gets the Linux File System path for this 1-Wire device instance.
     *
     * @return The 1-Wire device path as a {@link String}.
     */
    public String getDevicePath() {
        return devicePath;
    }

    /**
     * Gets the device ID of this 1-Wire device.
     *
     * @return The device ID as a {@link String}.
     */
    public String getDeviceId() {
        return deviceId;
    }
}
