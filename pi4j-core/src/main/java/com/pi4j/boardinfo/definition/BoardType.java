package com.pi4j.boardinfo.definition;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BoardType.java
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
