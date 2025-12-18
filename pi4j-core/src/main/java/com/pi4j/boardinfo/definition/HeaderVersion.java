package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  HeaderVersion.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * Enum representing different header versions available on various Raspberry Pi models.
 * Each header version includes a label, a description, and a list of associated header pin configurations.
 */
public enum HeaderVersion {

    /**
     * Header version used on the Pico microcontroller.
     */
    PICO("Pico", "Used on the Pico microcontroller", new ArrayList<>()),

    /**
     * Header version type 1, used on the original Model B.
     */
    TYPE_1("Type 1", "Used on original Model B", List.of(HeaderType.HEADER_26_TYPE_1)),

    /**
     * Header version type 2, used on Model A and Model B (revision 2).
     */
    TYPE_2("Type 2", "Used on Model A and Model B (revision 2)", List.of(HeaderType.HEADER_26_TYPE_2, HeaderType.HEADER_8)),

    /**
     * Header version type 3, used on various models including A+, B+, Pi Zero, and Pi5B.
     */
    TYPE_3("Type 3", "Used on Model A+, B+, Pi Zero, Pi Zero W, Pi2B, Pi3B, Pi4B, Pi5B", List.of(HeaderType.HEADER_40)),

    /**
     * Header version used for the Compute Module series with 54 GPIO pins.
     */
    COMPUTE("Compute Module", "54 GPIO", List.of(HeaderType.COMPUTE_J5, HeaderType.COMPUTE_J6)),

    /**
     * Unknown or unspecified header version.
     */
    UNKNOWN("Unknown", "", new ArrayList<>());

    private final String label;
    private final String description;
    private final List<HeaderType> headerTypes;

    /**
     * Constructs a {@link HeaderVersion} enum constant.
     *
     * @param label       the label describing the header version.
     * @param description a brief description of the header version and its use.
     * @param headerTypes the list of {@link HeaderType} associated with this header version.
     */
    HeaderVersion(String label, String description, List<HeaderType> headerTypes) {
        this.label = label;
        this.description = description;
        this.headerTypes = headerTypes;
    }

    /**
     * Retrieves the label for the header version.
     *
     * @return the label of the header version.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Retrieves the description of the header version.
     *
     * @return a brief description of the header version.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the list of header pin configurations associated with the header version.
     *
     * @return a list of {@link HeaderType} for the header version.
     */
    public List<HeaderType> getHeaderTypes() {
        return headerTypes;
    }
}
