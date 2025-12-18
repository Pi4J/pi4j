package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  InstructionSet.java
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
 * Enum representing various instruction sets supported by different Raspberry Pi processors.
 */
public enum InstructionSet {

    /**
     * ARMv6-M instruction set, typically used in microcontroller applications.
     */
    ARM_V6_M("ARMv6-M"),

    /**
     * ARMv6 instruction set, an early ARM architecture version used in older devices.
     */
    ARM_V6("ARMv6"),

    /**
     * ARMv7 instruction set, commonly used in mid-range ARM processors.
     */
    ARM_V7("ARMv7"),

    /**
     * ARMv8 instruction set, supporting 64-bit architecture and used in modern processors.
     */
    ARM_V8("ARMv8"),

    /**
     * Unknown or unspecified instruction set.
     */
    UNKNOWN("Unknown");

    private final String label;

    /**
     * Constructs an {@link InstructionSet} enum constant with a specific label.
     *
     * @param label the string label representing the instruction set.
     */
    InstructionSet(String label) {
        this.label = label;
    }

    /**
     * Retrieves the label for the instruction set.
     *
     * @return the label of the instruction set.
     */
    public String getLabel() {
        return label;
    }
}
