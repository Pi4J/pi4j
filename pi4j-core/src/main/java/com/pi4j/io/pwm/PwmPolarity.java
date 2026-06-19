package com.pi4j.io.pwm;

import java.util.EnumSet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PwmPolarity.java
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
 * PWM Polarity Enumerations
 */
public enum PwmPolarity {
    NORMAL(0, "normal"),
    INVERSED(1, "inversed");

    private final int value;
    private final String name;

    private PwmPolarity(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     */
    public int getValue() {
        return value;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }

    public static EnumSet<PwmPolarity> all() {
        return EnumSet.allOf(PwmPolarity.class);
    }

    public static PwmPolarity parse(String polarity) {
        if(polarity.equalsIgnoreCase("0")) return PwmPolarity.NORMAL;
        if(polarity.equalsIgnoreCase("1")) return PwmPolarity.INVERSED;
        if(polarity.toLowerCase().startsWith("n")) return PwmPolarity.NORMAL;
        if(polarity.toLowerCase().startsWith("i")) return PwmPolarity.INVERSED;
        return PwmPolarity.NORMAL; // default
    }
}
