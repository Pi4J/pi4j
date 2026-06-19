package com.pi4j.io.gpio.digital;

import java.util.EnumSet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalState.java
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
 * Digital State Enumerations
 *
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public enum DigitalState {

    UNKNOWN(-1, "UNKNOWN"),
    LOW(0, "LOW"),
    HIGH(1, "HIGH");

    private final Integer value;
    private final String name;

    private DigitalState(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * <p>isHigh.</p>
     *
     * @return a boolean.
     */
    public boolean isHigh() {
        return (this == HIGH);
    }

    /**
     * <p>isLow.</p>
     *
     * @return a boolean.
     */
    public boolean isLow() {
        return (this == LOW);
    }

    /**
     * <p>value.</p>
     *
     * @return a {@link java.lang.Number} object.
     */
    public Number value() {
        return getValue();
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a {@link java.lang.Number} object.
     */
    public Number getValue() {
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

    /**
     * <p>equals.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a boolean.
     */
    public boolean equals(DigitalState state) {
        return this == state;
    }
    /**
     * <p>equals.</p>
     *
     * @param state a {@link java.lang.Number} object.
     * @return a boolean.
     */
    public boolean equals(Number state) {
        return this == DigitalState.getState(state);
    }
    /**
     * <p>equals.</p>
     *
     * @param state a boolean.
     * @return a boolean.
     */
    public boolean equals(boolean state) {
        return this == DigitalState.getState(state);
    }
    /**
     * <p>equals.</p>
     *
     * @param state a byte.
     * @return a boolean.
     */
    public boolean equals(byte state){
        return equals(DigitalState.getState(state));
    }
    /**
     * <p>equals.</p>
     *
     * @param state a short.
     * @return a boolean.
     */
    public boolean equals(short state){
        return equals(DigitalState.getState(state));
    }
    /**
     * <p>equals.</p>
     *
     * @param state a int.
     * @return a boolean.
     */
    public boolean equals(int state){
        return equals(DigitalState.getState(state));
    }
    /**
     * <p>equals.</p>
     *
     * @param state a long.
     * @return a boolean.
     */
    public boolean equals(long state){
        return equals(DigitalState.getState(state));
    }
    /**
     * <p>equals.</p>
     *
     * @param state a float.
     * @return a boolean.
     */
    public boolean equals(float state){
        return equals(DigitalState.getState(state));
    }
    /**
     * <p>equals.</p>
     *
     * @param state a double.
     * @return a boolean.
     */
    public boolean equals(double state){
        return equals(DigitalState.getState(state));
    }


    @Override
    public String toString() {
        return name;
    }

    /**
     * <p>state.</p>
     *
     * @param state a {@link java.lang.Number} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    public static DigitalState state(Number state) {
        return getState(state);
    }

    /**
     * <p>getState.</p>
     *
     * @param state a {@link java.lang.Number} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    public static DigitalState getState(Number state) {
        for (var item : DigitalState.values()) {
            if (item.getValue().intValue() == state.intValue()) {
                return item;
            }
        }
        return null;
    }

    /**
     * <p>inverseState.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    public static DigitalState inverseState(DigitalState state) {
        return getInverseState(state);
    }

    /**
     * <p>getInverseState.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    public static DigitalState getInverseState(DigitalState state) {
        return (state == HIGH ? LOW : HIGH);
    }

    /**
     * <p>getState.</p>
     *
     * @param state a boolean.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    public static DigitalState getState(boolean state) {
        return (state ? DigitalState.HIGH : DigitalState.LOW);
    }

    /**
     * <p>allStates.</p>
     *
     * @return an array of {@link com.pi4j.io.gpio.digital.DigitalState} objects.
     */
    public static DigitalState[] allStates() {
        return DigitalState.values();
    }

    /**
     * <p>all.</p>
     *
     * @return a {@link java.util.EnumSet} object.
     */
    public static EnumSet<DigitalState> all() {
        return EnumSet.of(DigitalState.HIGH, DigitalState.LOW);
    }

    /**
     * <p>parse.</p>
     *
     * @param state a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    public static DigitalState parse(String state) {
        if(state.equalsIgnoreCase("0")) return DigitalState.LOW;
        if(state.equalsIgnoreCase("1")) return DigitalState.HIGH;
        if(state.toLowerCase().startsWith("l")) return DigitalState.LOW;
        if(state.toLowerCase().startsWith("h")) return DigitalState.HIGH;
        return DigitalState.UNKNOWN;
    }
}
