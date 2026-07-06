package com.pi4j.boardinfo.definition;

/**
 * Enum representing the functions of pins on a Raspberry Pi header.
 * Each pin function is associated with a label and a description explaining its purpose.
 */
public enum PinFunction {

    /**
     * UART (Universal Asynchronous Receiver and Transmitter):
     * Used for asynchronous serial communication between devices.
     */
    UART("Universal Asynchronous Receiver and Transmitter", "Asynchronous serial communication protocol"),

    /**
     * GPCLK (General Purpose Clock):
     * Provides a fixed frequency output for various clock-related applications.
     */
    GPCLK("General Purpose Clock", "Output a fixed frequency"),

    /**
     * I2C (Inter Integrated Circuit):
     * A synchronous, multi-master, multi-slave serial computer bus used for low-speed communication between components.
     */
    I2C("Inter Integrated Circuit", "Synchronous serial computer bus"),

    /**
     * SPI (Serial Peripheral Interface):
     * A four-wire serial communication protocol commonly used for short-distance device communication.
     */
    SPI("Serial Peripheral Interface", "Four-wire serial bus");

    private final String label;
    private final String description;

    /**
     * Constructs a {@link PinFunction} enum constant with a label and description.
     *
     * @param label       the name of the pin function.
     * @param description a brief explanation of the pin function.
     */
    PinFunction(String label, String description) {
        this.label = label;
        this.description = description;
    }

    /**
     * Retrieves the label for the pin function.
     *
     * @return the label of the pin function.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Retrieves the description of the pin function.
     *
     * @return a brief explanation of the pin function.
     */
    public String getDescription() {
        return description;
    }
}
