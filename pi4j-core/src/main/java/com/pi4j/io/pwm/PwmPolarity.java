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
 * Enumerates the supported polarities of a PWM signal, as configured via
 * {@link PwmConfig#polarity()} and {@link PwmConfigBuilder#polarity(PwmPolarity)}.
 * Polarity determines whether the active (high) portion of each period corresponds
 * to the duty-cycle directly or to its complement. Not all providers support
 * inverted polarity.
 */
public enum PwmPolarity {
    /** Normal polarity: the duty-cycle represents the proportion of each period the signal is HIGH. */
    NORMAL(0, "normal"),
    /** Inverted polarity: the duty-cycle represents the proportion of each period the signal is LOW. */
    INVERSED(1, "inversed");

    private final int value;
    private final String name;

    private PwmPolarity(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Returns the numeric code of this polarity (0 for {@link #NORMAL}, 1 for {@link #INVERSED}).
     *
     * @return the numeric polarity code
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the lower-case textual name of this polarity ({@code "normal"} or {@code "inversed"}).
     *
     * @return the polarity name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }

    /**
     * Returns a set containing every defined polarity.
     *
     * @return an {@link EnumSet} of all {@link PwmPolarity} values
     */
    public static EnumSet<PwmPolarity> all() {
        return EnumSet.allOf(PwmPolarity.class);
    }

    /**
     * Parses a polarity from a string. Accepts the numeric codes {@code "0"}/{@code "1"}
     * or any value starting with {@code 'n'} (normal) or {@code 'i'} (inversed),
     * case-insensitively; any unrecognized value defaults to {@link #NORMAL}.
     *
     * @param polarity the polarity text to parse
     * @return the matching polarity, or {@link #NORMAL} if not recognized
     */
    public static PwmPolarity parse(String polarity) {
        if(polarity.equalsIgnoreCase("0")) return PwmPolarity.NORMAL;
        if(polarity.equalsIgnoreCase("1")) return PwmPolarity.INVERSED;
        if(polarity.toLowerCase().startsWith("n")) return PwmPolarity.NORMAL;
        if(polarity.toLowerCase().startsWith("i")) return PwmPolarity.INVERSED;
        return PwmPolarity.NORMAL; // default
    }
}
