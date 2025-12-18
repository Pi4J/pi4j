package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PinFunction.java
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
