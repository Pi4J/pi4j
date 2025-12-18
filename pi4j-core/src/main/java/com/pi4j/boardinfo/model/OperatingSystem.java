package com.pi4j.boardinfo.model;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OperatingSystem.java
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

