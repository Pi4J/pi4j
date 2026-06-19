package com.pi4j.io.pwm;

import java.util.EnumSet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PwmType.java
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
 * PWM Type Enumerations
 *
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public enum PwmType {
    SOFTWARE(0, "software"),
    HARDWARE(1, "hardware");

    private final int value;
    private final String name;

    private PwmType(int value, String name) {
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
    public static EnumSet<PwmType> all() {
        return EnumSet.allOf(PwmType.class);
    }

    /**
     * <p>parse.</p>
     *
     * @param pwmType a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.pwm.PwmType} object.
     */
    public static PwmType parse(String pwmType) {
        if(pwmType.equalsIgnoreCase("0")) return PwmType.SOFTWARE;
        if(pwmType.equalsIgnoreCase("1")) return PwmType.HARDWARE;
        if(pwmType.toLowerCase().startsWith("h")) return PwmType.HARDWARE;
        if(pwmType.toLowerCase().startsWith("s")) return PwmType.SOFTWARE;
        return PwmType.SOFTWARE; // default
    }
}
