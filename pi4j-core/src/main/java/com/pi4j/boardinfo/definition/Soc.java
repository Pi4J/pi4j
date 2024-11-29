package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Soc.java
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
 * Enum representing various System on Chip (SoC) models used in Raspberry Pi and related hardware.
 * Each SoC is associated with a specific instruction set architecture.
 */
public enum Soc {

    /**
     * BCM2710A1:
     * SoC based on the ARMv8 instruction set architecture.
     */
    BCM2710A1(InstructionSet.ARM_V8),

    /**
     * BCM2711:
     * SoC based on the ARMv8 instruction set architecture.
     */
    BCM2711(InstructionSet.ARM_V8),

    /**
     * BCM2711C0:
     * Revision C0 of the BCM2711 SoC, based on the ARMv8 instruction set architecture.
     */
    BCM2711C0(InstructionSet.ARM_V8),

    /**
     * BCM2712:
     * SoC based on the ARMv8 instruction set architecture, used in newer Raspberry Pi models.
     */
    BCM2712(InstructionSet.ARM_V8),

    /**
     * BCM2835:
     * SoC based on the ARMv6 instruction set architecture, used in older Raspberry Pi models.
     */
    BCM2835(InstructionSet.ARM_V6),

    /**
     * BCM2836:
     * SoC based on the ARMv7 instruction set architecture.
     */
    BCM2836(InstructionSet.ARM_V7),

    /**
     * BCM2837:
     * SoC based on the ARMv8 instruction set architecture, used in Raspberry Pi 3 models.
     */
    BCM2837(InstructionSet.ARM_V8),

    /**
     * BCM2837B0:
     * Revision B0 of the BCM2837 SoC, based on the ARMv8 instruction set architecture.
     */
    BCM2837B0(InstructionSet.ARM_V8),

    /**
     * RP2040:
     * Microcontroller SoC based on the ARMv6-M instruction set architecture, used in the Raspberry Pi Pico.
     */
    RP2040(InstructionSet.ARM_V6_M),

    /**
     * RP2350:
     * Microcontroller SoC based on the ARMv6-M instruction set architecture.
     */
    RP2350(InstructionSet.ARM_V6_M),

    /**
     * UNKNOWN:
     * Placeholder for unidentified or unsupported SoCs, associated with an unknown instruction set.
     */
    UNKNOWN(InstructionSet.UNKNOWN);

    private final InstructionSet instructionSet;

    /**
     * Constructs a {@link Soc} enum constant with its associated instruction set architecture.
     *
     * @param instructionSet the {@link InstructionSet} associated with the SoC.
     */
    Soc(InstructionSet instructionSet) {
        this.instructionSet = instructionSet;
    }

    /**
     * Retrieves the instruction set architecture for this SoC.
     *
     * @return the {@link InstructionSet} of the SoC.
     */
    public InstructionSet getInstructionSet() {
        return instructionSet;
    }
}
