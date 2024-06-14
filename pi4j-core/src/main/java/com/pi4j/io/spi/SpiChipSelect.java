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

/**
 * <p>SpiChipSelect class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public enum SpiChipSelect {
    CS_0(0), CS_1(1), CS_2(2);

    private final int address;

    private SpiChipSelect(int address) {
        this.address = address;
    }

    /**
     * <p>Getter for the field <code>chipSelect</code>.</p>
     *
     * @return a int.
     */
    public int getChipSelect() {
        return address;
    }

    /**
     * <p>getByNumber.</p>
     *
     * @param address a short.
     * @return a {@link SpiChipSelect} object.
     */
    public static SpiChipSelect getByNumber(short address){
        return getByNumber((int)address);
    }

    /**
     * <p>getByNumber.</p>
     *
     * @param address a int.
     * @return a {@link SpiChipSelect} object.
     */
    public static SpiChipSelect getByNumber(int address){
        for(var item : SpiChipSelect.values()){
            if(item.getChipSelect() == address){
                return item;
            }
        }
        return null;
    }

    /**
     * <p>parse.</p>
     *
     * @param bus a {@link String} object.
     * @return a {@link SpiChipSelect} object.
     */
    public static SpiChipSelect parse(String bus) {
        if(bus.equalsIgnoreCase("0")) return SpiChipSelect.CS_0;
        if(bus.equalsIgnoreCase("1")) return SpiChipSelect.CS_1;
        if(bus.equalsIgnoreCase("2")) return SpiChipSelect.CS_2;
        return Spi.DEFAULT_CHIP_SELECT;
    }
}
