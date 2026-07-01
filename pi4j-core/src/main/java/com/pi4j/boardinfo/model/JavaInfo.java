package com.pi4j.boardinfo.model;

/**
 * Represents information about the Java runtime environment.
 * This includes the version, runtime, vendor, and vendor version of the Java environment.
 */
public class JavaInfo {

    private final String version;        // Java version (e.g., "11.0.10")
    private final String runtime;        // The runtime environment (e.g., "Zulu OpenJDK 11.0.10")
    private final String vendor;         // The vendor of the Java runtime (e.g., "Azul Systems")
    private final String vendorVersion;  // The vendor's version of the Java runtime (e.g., "11.0.10+10")

    /**
     * Constructs a JavaInfo object with the specified details about the Java runtime.
     *
     * @param version The version of the Java runtime (e.g., "11.0.10").
     * @param runtime The runtime environment (e.g., "Zulu OpenJDK 11.0.10").
     * @param vendor The vendor of the Java runtime (e.g., "Azul Systems").
     * @param vendorVersion The vendor's version of the Java runtime (e.g., "11.0.10+10").
     */
    public JavaInfo(String version, String runtime, String vendor, String vendorVersion) {
        this.version = version;
        this.runtime = runtime;
        this.vendor = vendor;
        this.vendorVersion = vendorVersion;
    }

    /**
     * Gets the version of the Java runtime.
     *
     * @return The version of the Java runtime as a String.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the runtime environment of Java.
     *
     * @return The runtime environment as a String (e.g., "Zulu OpenJDK 11.0.10").
     */
    public String getRuntime() {
        return runtime;
    }

    /**
     * Gets the vendor of the Java runtime.
     *
     * @return The vendor of the Java runtime (e.g., "Azul Systems").
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Gets the version of the Java runtime as provided by the vendor.
     *
     * @return The vendor version of the Java runtime (e.g., "11.0.10+10").
     */
    public String getVendorVersion() {
        return vendorVersion;
    }

    /**
     * Returns a string representation of the Java runtime information.
     *
     * @return A string summarizing the version, runtime, vendor, and vendor version.
     */
    @Override
    public String toString() {
        return "Version: " + version
            + ", runtime: " + runtime
            + ", vendor: " + vendor
            + ", vendor version: " + vendorVersion;
    }
}
