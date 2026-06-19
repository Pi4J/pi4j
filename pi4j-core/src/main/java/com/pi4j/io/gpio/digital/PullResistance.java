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
 * Enumerates the pull resistor configuration applied to a digital input pin, controlling its default level
 * when left floating. Used as part of a digital input configuration to enable a pull-up, pull-down, or no
 * internal resistor.
 */
public enum PullResistance {
    /** No internal pull resistor; the pin floats unless externally driven. */
    OFF(0, "off"),
    /** Internal pull-down resistor, biasing the pin toward {@link DigitalState#LOW}. */
    PULL_DOWN(1, "down"),
    /** Internal pull-up resistor, biasing the pin toward {@link DigitalState#HIGH}. */
    PULL_UP(2, "up");

    private final int value;
    private final String name;

    private PullResistance(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Returns the numeric code for this pull resistance ({@code 0}=off, {@code 1}=down, {@code 2}=up).
     *
     * @return the numeric value
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the lowercase short name of this pull resistance ({@code "off"}, {@code "down"}, {@code "up"}).
     *
     * @return the short name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }

    /**
     * Returns all defined pull resistance options.
     *
     * @return an {@link EnumSet} of every {@link PullResistance} value
     */
    public static EnumSet<PullResistance> all() {
        return EnumSet.allOf(PullResistance.class);
    }

    /**
     * Parses a textual representation into a pull resistance, accepting numeric codes ({@code "0"},
     * {@code "1"}, {@code "2"}) or strings indicating up/down (case-insensitive).
     *
     * @param pull the text to parse
     * @return the parsed pull resistance, defaulting to {@link #OFF} if unrecognized
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
