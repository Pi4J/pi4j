package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PiModel.java
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
 * Enum representing various Raspberry Pi models, their general features, and form factors.
 */
public enum PiModel {

    /**
     * Compute Module: Raspberry Pi on a 200-pin DDR2-memory-like module
     * designed for integration in embedded devices.
     */
    COMPUTE("Compute Module", "Pi on a 200-pin DDR2-memory-like module for integration in embedded devices"),

    /**
     * Model A: Raspberry Pi without an Ethernet connector.
     * A more cost-effective and compact model.
     */
    MODEL_A("Model A", "Without ethernet connector"),

    /**
     * Model B: Raspberry Pi with an Ethernet connector.
     * Offers more connectivity options compared to Model A.
     */
    MODEL_B("Model B", "With ethernet connector"),

    /**
     * Pico: A microcontroller version of Raspberry Pi.
     * Designed for IoT, robotics, and low-power applications.
     */
    PICO("Pico", "Microcontroller"),

    /**
     * Zero: Smaller-sized Raspberry Pi with reduced GPIO capabilities,
     * ideal for compact and lightweight applications.
     */
    ZERO("Zero", "Smaller size and reduced GPIO capabilities"),

    /**
     * Unknown: Represents an unspecified or unsupported Raspberry Pi model.
     */
    UNKNOWN("Unknown", "");

    private final String label;
    private final String description;

    /**
     * Constructs a {@link PiModel} enum constant with a label and description.
     *
     * @param label       the name of the Raspberry Pi model.
     * @param description a brief description of the model's features or intended use.
     */
    PiModel(String label, String description) {
        this.label = label;
        this.description = description;
    }

    /**
     * Retrieves the label for the Raspberry Pi model.
     *
     * @return the label of the Pi model.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Retrieves the description of the Raspberry Pi model.
     *
     * @return a brief description of the model.
     */
    public String getDescription() {
        return description;
    }
}
