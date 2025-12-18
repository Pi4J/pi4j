package com.pi4j.boardinfo.util;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SystemProperties.java
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
 * Interface that defines constants for common system properties.
 * These properties provide information about the operating system,
 * architecture, and Java runtime environment.
 */
public interface SystemProperties {

    /**
     * The name of the operating system.
     * <p>
     * This property represents the name of the operating system, such as
     * "Windows 10", "Linux", or "macOS".
     * </p>
     */
    String OS_NAME = "os.name";

    /**
     * The version of the operating system.
     * <p>
     * This property provides the version of the operating system, for example,
     * "10.0" for Windows 10 or "5.4.0" for a specific Linux kernel version.
     * </p>
     */
    String OS_VERSION = "os.version";

    /**
     * The architecture of the operating system.
     * <p>
     * This property indicates the CPU architecture, such as "x86", "x86_64", or "arm".
     * It gives a hint of the underlying hardware architecture on which the operating system is running.
     * </p>
     */
    String OS_ARCH = "os.arch";

    /**
     * The architecture's data model (either 32-bit or 64-bit).
     * <p>
     * This property indicates the data model of the JVM, either "32" or "64",
     * based on whether the underlying architecture is 32-bit or 64-bit.
     * </p>
     * For example, on a 64-bit system, it would return "64", whereas on a 32-bit system,
     * it would return "32". This property can help determine the system's memory addressing capacity.
     */
    String ARCHITECTURE_DATA_MODEL = "sun.arch.data.model";

    /**
     * The version of the Java Runtime Environment (JRE).
     * <p>
     * This property provides the version number of the Java runtime, such as "1.8.0_291"
     * or "17.0.1", depending on the version of the JRE installed on the system.
     * </p>
     */
    String JAVA_VERSION = "java.version";

    /**
     * The version of the Java Runtime Environment (JRE) vendor.
     * <p>
     * This property provides the specific version of the Java runtime being used,
     * which may include additional information about vendor-specific patches or modifications.
     * For example, "Oracle Corporation" or "OpenJDK" may appear as the vendor name.
     * </p>
     */
    String JAVA_RUNTIME_VERSION = "java.runtime.version";

    /**
     * The name of the Java vendor.
     * <p>
     * This property returns the name of the company or organization that provides the Java runtime.
     * Examples include "Oracle Corporation", "OpenJDK", or "Amazon Corretto".
     * </p>
     */
    String JAVA_VENDOR = "java.vendor";

    /**
     * The version of the Java vendor's implementation of the JRE.
     * <p>
     * This property provides version information for the Java vendor's specific implementation.
     * It may include a specific release or patch version, such as "1.8.0_291" or "17.0.1".
     * </p>
     */
    String JAVA_VENDOR_VERSION = "java.vendor.version";
}
