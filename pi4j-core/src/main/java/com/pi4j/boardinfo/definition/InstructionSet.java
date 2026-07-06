package com.pi4j.boardinfo.definition;

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
