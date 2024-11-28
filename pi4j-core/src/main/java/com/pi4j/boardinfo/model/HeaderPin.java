package com.pi4j.boardinfo.model;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  HeaderPin.java
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

import com.pi4j.boardinfo.definition.PinFunction;
import com.pi4j.boardinfo.definition.PinType;

/**
 * Represents a pin on a Raspberry Pi header.
 * Contains information about the pin's number, type, function,
 * as well as its BCM and WiringPi numbers, name, and any remarks.
 */
public class HeaderPin {

    private final int pinNumber;        // The physical pin number on the header
    private final PinType pinType;      // The type of pin (Power, Ground, Digital, etc.)
    private final PinFunction pinFunction; // The function of the pin (e.g., UART, I2C, SPI)
    private final Integer bcmNumber;    // The BCM GPIO number, if applicable
    private final Integer wiringPiNumber; // The WiringPi number, if applicable
    private final String name;          // The name of the pin (e.g., "GPIO17")
    private final String remark;        // Any additional remarks or information about the pin

    /**
     * Constructs a HeaderPin with only the pin number, pin type, and name.
     *
     * @param pinNumber The physical pin number.
     * @param pinType The type of pin (e.g., Power, Ground, Digital).
     * @param name The name of the pin (e.g., "GPIO17").
     */
    public HeaderPin(int pinNumber, PinType pinType, String name) {
        this(pinNumber, pinType, null, null, null, name, "");
    }

    /**
     * Constructs a HeaderPin with the pin number, pin type, pin function,
     * BCM number, WiringPi number, and name.
     *
     * @param pinNumber The physical pin number.
     * @param pinType The type of pin (e.g., Power, Ground, Digital).
     * @param pinFunction The function of the pin (e.g., UART, I2C).
     * @param bcmNumber The BCM GPIO number, if applicable.
     * @param wiringPiNumber The WiringPi number, if applicable.
     * @param name The name of the pin (e.g., "GPIO17").
     */
    public HeaderPin(int pinNumber, PinType pinType, PinFunction pinFunction, Integer bcmNumber, Integer wiringPiNumber, String name) {
        this(pinNumber, pinType, pinFunction, bcmNumber, wiringPiNumber, name, "");
    }

    /**
     * Constructs a HeaderPin with all the available information, including remarks.
     *
     * @param pinNumber The physical pin number.
     * @param pinType The type of pin (e.g., Power, Ground, Digital).
     * @param pinFunction The function of the pin (e.g., UART, I2C).
     * @param bcmNumber The BCM GPIO number, if applicable.
     * @param wiringPiNumber The WiringPi number, if applicable.
     * @param name The name of the pin (e.g., "GPIO17").
     * @param remark Any additional remarks about the pin.
     */
    public HeaderPin(int pinNumber, PinType pinType, PinFunction pinFunction, Integer bcmNumber, Integer wiringPiNumber, String name, String remark) {
        this.pinNumber = pinNumber;
        this.pinType = pinType;
        this.pinFunction = pinFunction;
        this.bcmNumber = bcmNumber;
        this.wiringPiNumber = wiringPiNumber;
        this.name = name;
        this.remark = remark;
    }

    /**
     * Gets the physical pin number on the header.
     *
     * @return The pin number as an integer.
     */
    public int getPinNumber() {
        return pinNumber;
    }

    /**
     * Gets the type of pin (e.g., Power, Ground, Digital).
     *
     * @return The PinType for this pin.
     */
    public PinType getPinType() {
        return pinType;
    }

    /**
     * Gets the function of the pin (e.g., UART, I2C, SPI).
     *
     * @return The PinFunction of the pin.
     */
    public PinFunction getPinFunction() {
        return pinFunction;
    }

    /**
     * Gets the BCM GPIO number associated with this pin, if available.
     *
     * @return The BCM GPIO number as an Integer, or null if not available.
     */
    public Integer getBcmNumber() {
        return bcmNumber;
    }

    /**
     * Gets the WiringPi number associated with this pin, if available.
     *
     * @return The WiringPi number as an Integer, or null if not available.
     */
    public Integer getWiringPiNumber() {
        return wiringPiNumber;
    }

    /**
     * Gets the name of the pin (e.g., "GPIO17").
     *
     * @return The name of the pin as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets any additional remarks or information about the pin.
     *
     * @return Any remarks as a String, or an empty string if no remarks exist.
     */
    public String getRemark() {
        return remark;
    }
}
