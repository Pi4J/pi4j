package com.pi4j.boardinfo.model;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  JavaInfo.java
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
