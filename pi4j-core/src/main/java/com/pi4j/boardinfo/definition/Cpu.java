package com.pi4j.boardinfo.definition;

/**
 * Represents the Central Processing Unit (CPU) types used in various Raspberry Pi boards and microcontrollers.
 */
public enum Cpu {

    /**
     * ARM1176JZF-S: A single-core ARM11 processor used in early Raspberry Pi models, such as the Raspberry Pi 1.
     */
    ARM1176JZF_S("ARM1176JZF-S"),

    /**
     * Cortex-A53: A quad-core 64-bit ARM processor used in Raspberry Pi 3 series and similar boards.
     */
    CORTEX_A53("Cortex-A53"),

    /**
     * Cortex-A7: A quad-core ARM processor used in some variants of the Raspberry Pi 2 Model B.
     */
    CORTEX_A7("Cortex-A7"),

    /**
     * Cortex-A72: A quad-core 64-bit ARM processor used in the Raspberry Pi 4 Model B and Compute Module 4.
     */
    CORTEX_A72("Cortex-A72"),

    /**
     * Cortex-A76: A high-performance ARM processor used in the Raspberry Pi 5 Model B.
     */
    CORTEX_A76("Cortex-A76"),

    /**
     * Cortex-M0+: A low-power ARM processor used in Raspberry Pi Pico microcontrollers.
     */
    CORTEX_MO_PLUS("Cortex-M0+"),

    /**
     * Cortex-M33: A high-performance microcontroller processor used in the Raspberry Pi Pico 2 series.
     */
    CORTEX_M33("Cortex-M33"),

    /**
     * Unknown: Represents an unidentified or unsupported CPU type.
     */
    UNKNOWN("Unknown");

    private final String label;

    /**
     * Constructs a {@link Cpu} enum constant with the specified label.
     *
     * @param label the descriptive label for the CPU type.
     */
    Cpu(String label) {
        this.label = label;
    }

    /**
     * Retrieves the label of the CPU type.
     *
     * @return the label describing the CPU.
     */
    public String getLabel() {
        return label;
    }
}
