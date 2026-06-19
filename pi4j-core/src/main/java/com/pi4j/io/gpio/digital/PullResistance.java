package com.pi4j.io.gpio.digital;

import java.util.EnumSet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PullResistance.java
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
 * Digital Pin Pull Resistance Enumerations
 *
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public enum PullResistance {
    OFF(0, "off"),
    PULL_DOWN(1, "down"),
    PULL_UP(2, "up");

    private final int value;
    private final String name;

    private PullResistance(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a int.
     */
    public int getValue() {
        return value;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }

    /**
     * <p>all.</p>
     *
     * @return a {@link java.util.EnumSet} object.
     */
    public static EnumSet<PullResistance> all() {
        return EnumSet.allOf(PullResistance.class);
    }

    /**
     * <p>parse.</p>
     *
     * @param pull a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.gpio.digital.PullResistance} object.
     */
    public static PullResistance parse(String pull) {
        if(pull.equalsIgnoreCase("0")) return PullResistance.OFF;
        if(pull.equalsIgnoreCase("1")) return PullResistance.PULL_DOWN;
        if(pull.equalsIgnoreCase("2")) return PullResistance.PULL_UP;
        if(pull.toLowerCase().startsWith("u")) return PullResistance.PULL_UP;
        if(pull.toLowerCase().startsWith("d")) return PullResistance.PULL_DOWN;
        if(pull.toLowerCase().contains("up")) return PullResistance.PULL_UP;
        if(pull.toLowerCase().contains("down")) return PullResistance.PULL_DOWN;
        return PullResistance.OFF;
    }
}
