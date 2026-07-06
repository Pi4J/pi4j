package com.pi4j.boardinfo.model;

/**
 * Represents information about an operating system.
 * This includes the name, version, and architecture of the operating system.
 */
public class OperatingSystem {

    private final String name;          // The name of the operating system (e.g., "Linux")
    private final String version;       // The version of the operating system (e.g., "Ubuntu 20.04")
    private final String architecture;  // The architecture of the operating system (e.g., "x86_64")

    /**
     * Constructs an OperatingSystem object with the specified details.
     *
     * @param name The name of the operating system (e.g., "Linux").
     * @param version The version of the operating system (e.g., "Ubuntu 20.04").
     * @param architecture The architecture of the operating system (e.g., "x86_64").
     */
    public OperatingSystem(String name, String version, String architecture) {
        this.name = name;
        this.version = version;
        this.architecture = architecture;
    }

    /**
     * Gets the name of the operating system.
     *
     * @return The name of the operating system as a String (e.g., "Linux").
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version of the operating system.
     *
     * @return The version of the operating system as a String (e.g., "Ubuntu 20.04").
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the architecture of the operating system.
     *
     * @return The architecture of the operating system as a String (e.g., "x86_64").
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * Returns a string representation of the operating system information.
     *
     * @return A string summarizing the name, version, and architecture of the operating system.
     */
    @Override
    public String toString() {
        return "Name: " + name
            + ", version: " + version
            + ", architecture: " + architecture;
    }
}

