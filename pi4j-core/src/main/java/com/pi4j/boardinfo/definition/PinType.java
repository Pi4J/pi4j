package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PinType.java
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
 * Enum representing the different types of pins on a Raspberry Pi header.
 * Each pin type is associated with a label for its purpose and a color code for visualization.
 */
public enum PinType {

    /**
     * Power Pin:
     * Supplies electrical power (e.g., 3.3V or 5V) to devices or circuits.
     * Represented by the color code 0x990000 (red).
     */
    POWER("Power", 0x990000),

    /**
     * Ground Pin:
     * Provides a common return path for electrical current.
     * Represented by the color code 0x000000 (black).
     */
    GROUND("Ground", 0x000000),

    /**
     * Digital Pin:
     * Supports general-purpose digital input/output.
     * Represented by the color code 0x009900 (green).
     */
    DIGITAL("Digital", 0x009900),

    /**
     * Digital and PWM Pin:
     * Supports general-purpose digital input/output as well as Pulse Width Modulation (PWM) for controlling devices like motors.
     * Represented by the color code 0xff7f00 (orange).
     */
    DIGITAL_AND_PWM("Digital and PWM", 0xff7f00),

    /**
     * Digital without Pulldown Pin:
     * Digital pin that does not include a pull-down resistor, which may require external circuitry for proper usage.
     * Represented by the color code 0x800080 (purple).
     */
    DIGITAL_NO_PULL_DOWN("Digital without pulldown", 0x800080);

    private final String label;
    private final int color;

    /**
     * Constructs a {@link PinType} enum constant with a label and a color code.
     *
     * @param label a descriptive name for the pin type.
     * @param color a hexadecimal color code representing the pin type for visualization.
     */
    PinType(String label, int color) {
        this.label = label;
        this.color = color;
    }

    /**
     * Retrieves the label for the pin type.
     *
     * @return the label of the pin type.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Retrieves the color code associated with the pin type.
     *
     * @return the hexadecimal color code of the pin type.
     */
    public int getColor() {
        return color;
    }
}
