package com.pi4j.io.spi;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SpiBus.java
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
 * <p>SpiBus class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public enum SpiBus {
    BUS_0(0), BUS_1(1), BUS_2(2), BUS_3(3), BUS_4(4), BUS_5(5), BUS_6(6);

    private final int bus;

    private SpiBus(int bus) {
        this.bus = bus;
    }

    /**
     * <p>Getter for the field <code>bus</code>.</p>
     *
     * @return a int.
     */
    public int getBus() {
        return bus;
    }

    /**
     * <p>getByNumber.</p>
     *
     * @param bus a short.
     * @return a {@link SpiBus} object.
     */
    public static SpiBus getByNumber(short bus) {
        return getByNumber((int) bus);
    }

    /**
     * <p>getByNumber.</p>
     *
     * @param bus a int.
     * @return a {@link SpiBus} object.
     */
    public static SpiBus getByNumber(int bus) {
        for (var item : SpiBus.values()) {
            if (item.getBus() == bus) {
                return item;
            }
        }
        return null;
    }

    /**
     * <p>parse.</p>
     *
     * @param bus a {@link String} object.
     * @return a {@link SpiBus} object.
     */
    public static SpiBus parse(String bus) {
        if (bus.equalsIgnoreCase("0")) return SpiBus.BUS_0;
        if (bus.equalsIgnoreCase("1")) return SpiBus.BUS_1;
        if (bus.equalsIgnoreCase("2")) return SpiBus.BUS_2;
        if (bus.equalsIgnoreCase("3")) return SpiBus.BUS_3;
        if (bus.equalsIgnoreCase("4")) return SpiBus.BUS_4;
        if (bus.equalsIgnoreCase("5")) return SpiBus.BUS_5;
        if (bus.equalsIgnoreCase("6")) return SpiBus.BUS_6;
        return Spi.DEFAULT_BUS;
    }
}
