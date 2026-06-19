package com.pi4j.io.spi;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SpiChipSelect.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Identifies the SPI chip-select (CS) line used to address a particular device on a bus.
 *
 * @deprecated The chip-select is now expressed through the channel value of an {@link SpiConfig};
 *             this enum is retained only for backward compatibility.
 */
@Deprecated
public enum SpiChipSelect {
    CS_0(0), CS_1(1), CS_2(2), CS_3(3), CS_4(4), CS_5(5), CS_6(6), CS_7(7), CS_8(8), CS_9(9), CS_10(10);

    private static final Logger log = LoggerFactory.getLogger(SpiChipSelect.class);
    private final int address;

    private SpiChipSelect(int address) {
        this.address = address;
    }

    /**
     * Returns the numeric chip-select index represented by this constant.
     *
     * @return the chip-select number (0&ndash;10)
     */
    public int getChipSelect() {
        return address;
    }

    /**
     * Returns the {@code SpiChipSelect} constant matching the given chip-select number.
     *
     * @param address the chip-select number to look up
     * @return the matching {@code SpiChipSelect}, or {@code null} if no constant has that number
     */
    public static SpiChipSelect getByNumber(short address){
        return getByNumber((int) address);
    }

    /**
     * Returns the {@code SpiChipSelect} constant matching the given chip-select number.
     *
     * @param address the chip-select number to look up
     * @return the matching {@code SpiChipSelect}, or {@code null} if no constant has that number
     */
    public static SpiChipSelect getByNumber(int address){
        for (var item : SpiChipSelect.values()){
            if (item.getChipSelect() == address){
                return item;
            }
        }
        return null;
    }

    /**
     * Parses a textual chip-select number into the corresponding {@code SpiChipSelect} constant.
     *
     * @param bus the chip-select number as a string
     * @return the matching {@code SpiChipSelect}, or {@link Spi#DEFAULT_CHIP_SELECT} if the value is
     *         not a recognized number or no constant matches
     */
    public static SpiChipSelect parse(String bus) {
        for (SpiChipSelect item : SpiChipSelect.values()) {
            try {
                if (item.getChipSelect() == Integer.parseInt(bus)) {
                    return item;
                }
            } catch (NumberFormatException e) {
                log.warn("Unable to parse chip select bus as number: {}. Returning default {}.", bus, Spi.DEFAULT_CHIP_SELECT);
                return Spi.DEFAULT_CHIP_SELECT;
            }
        }
        log.warn("Didn't find matching chip select bus for: {}. Returning default {}.", bus, Spi.DEFAULT_CHIP_SELECT);
        return Spi.DEFAULT_CHIP_SELECT;
    }
}
