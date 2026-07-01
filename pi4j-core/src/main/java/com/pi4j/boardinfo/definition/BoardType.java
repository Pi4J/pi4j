package com.pi4j.boardinfo.definition;

/**
 * Represents the types of boards available in the Pi4J Board Information API.
 */
public enum BoardType {

    /**
     * Represents an all-in-one computer board type, such as the Raspberry Pi 400.
     */
    ALL_IN_ONE_COMPUTER,

    /**
     * Represents a microcontroller board type, such as the Raspberry Pi Pico.
     */
    MICROCONTROLLER,

    /**
     * Represents a single-board computer (SBC) type, such as the Raspberry Pi Model B series.
     */
    SINGLE_BOARD_COMPUTER,

    /**
     * Represents a stack-on computer module type, such as the Compute Module series.
     */
    STACK_ON_COMPUTER,

    /**
     * Represents an unknown board type.
     */
    UNKNOWN
}
