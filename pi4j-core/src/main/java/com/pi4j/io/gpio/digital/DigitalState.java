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
 * Enumerates the logical states of a digital GPIO pin used throughout the digital I/O API, for example by
 * {@link DigitalOutput} and {@link DigitalStateChangeEvent}. Provides helpers to convert to and from numeric,
 * boolean and string representations and to compute the inverse state.
 */
public enum DigitalState {

    /** Indeterminate or not-yet-known state; backed by the numeric value {@code -1}. */
    UNKNOWN(-1, "UNKNOWN"),
    /** The logic-low (off / 0 V) state; backed by the numeric value {@code 0}. */
    LOW(0, "LOW"),
    /** The logic-high (on) state; backed by the numeric value {@code 1}. */
    HIGH(1, "HIGH");

    private final Integer value;
    private final String name;

    private DigitalState(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Returns whether this state is {@link #HIGH}.
     *
     * @return {@code true} if this state is {@link #HIGH}
     */
    public boolean isHigh() {
        return (this == HIGH);
    }

    /**
     * Returns whether this state is {@link #LOW}.
     *
     * @return {@code true} if this state is {@link #LOW}
     */
    public boolean isLow() {
        return (this == LOW);
    }

    /**
     * Returns the numeric representation of this state.
     *
     * @return the numeric value ({@code -1}, {@code 0} or {@code 1})
     */
    public Number value() {
        return getValue();
    }

    /**
     * Returns the numeric representation of this state.
     *
     * @return the numeric value ({@code -1}, {@code 0} or {@code 1})
     */
    public Number getValue() {
        return value;
    }

    /**
     * Returns the display name of this state ({@code "UNKNOWN"}, {@code "LOW"} or {@code "HIGH"}).
     *
     * @return the state name
     */
    public String getName() {
        return name;
    }

    /**
     * Compares this state to another for equality.
     *
     * @param state the state to compare against
     * @return {@code true} if they are the same state
     */
    public boolean equals(DigitalState state) {
        return this == state;
    }
    /**
     * Compares this state to the state represented by a numeric value.
     *
     * @param state the numeric value to interpret via {@link #getState(Number)}
     * @return {@code true} if this state matches the interpreted state
     */
    public boolean equals(Number state) {
        return this == DigitalState.getState(state);
    }
    /**
     * Compares this state to a boolean, where {@code true} maps to {@link #HIGH} and {@code false} to
     * {@link #LOW}.
     *
     * @param state the boolean value to interpret
     * @return {@code true} if this state matches the interpreted state
     */
    public boolean equals(boolean state) {
        return this == DigitalState.getState(state);
    }
    /**
     * Compares this state to the state represented by a numeric value.
     *
     * @param state the numeric value to interpret via {@link #getState(Number)}
     * @return {@code true} if this state matches the interpreted state
     */
    public boolean equals(byte state){
        return equals(DigitalState.getState(state));
    }
    /**
     * Compares this state to the state represented by a numeric value.
     *
     * @param state the numeric value to interpret via {@link #getState(Number)}
     * @return {@code true} if this state matches the interpreted state
     */
    public boolean equals(short state){
        return equals(DigitalState.getState(state));
    }
    /**
     * Compares this state to the state represented by a numeric value.
     *
     * @param state the numeric value to interpret via {@link #getState(Number)}
     * @return {@code true} if this state matches the interpreted state
     */
    public boolean equals(int state){
        return equals(DigitalState.getState(state));
    }
    /**
     * Compares this state to the state represented by a numeric value.
     *
     * @param state the numeric value to interpret via {@link #getState(Number)}
     * @return {@code true} if this state matches the interpreted state
     */
    public boolean equals(long state){
        return equals(DigitalState.getState(state));
    }
    /**
     * Compares this state to the state represented by a numeric value.
     *
     * @param state the numeric value to interpret via {@link #getState(Number)}
     * @return {@code true} if this state matches the interpreted state
     */
    public boolean equals(float state){
        return equals(DigitalState.getState(state));
    }
    /**
     * Compares this state to the state represented by a numeric value.
     *
     * @param state the numeric value to interpret via {@link #getState(Number)}
     * @return {@code true} if this state matches the interpreted state
     */
    public boolean equals(double state){
        return equals(DigitalState.getState(state));
    }


    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns the {@link DigitalState} corresponding to the given numeric value.
     *
     * @param state the numeric value to map (e.g. {@code 0}, {@code 1})
     * @return the matching state, or {@code null} if no state has the given value
     */
    public static DigitalState state(Number state) {
        return getState(state);
    }

    /**
     * Returns the {@link DigitalState} corresponding to the given numeric value, matching on the integer part.
     *
     * @param state the numeric value to map (e.g. {@code 0}, {@code 1})
     * @return the matching state, or {@code null} if no state has the given value
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
     * Returns the opposite of the given state.
     *
     * @param state the state to invert
     * @return {@link #LOW} when given {@link #HIGH}, otherwise {@link #HIGH}
     */
    public static DigitalState inverseState(DigitalState state) {
        return getInverseState(state);
    }

    /**
     * Returns the opposite of the given state.
     *
     * @param state the state to invert
     * @return {@link #LOW} when given {@link #HIGH}, otherwise {@link #HIGH}
     */
    public static DigitalState getInverseState(DigitalState state) {
        return (state == HIGH ? LOW : HIGH);
    }

    /**
     * Maps a boolean to a digital state.
     *
     * @param state the boolean value, where {@code true} is high
     * @return {@link #HIGH} if {@code state} is {@code true}, otherwise {@link #LOW}
     */
    public static DigitalState getState(boolean state) {
        return (state ? DigitalState.HIGH : DigitalState.LOW);
    }

    /**
     * Returns all defined digital states, including {@link #UNKNOWN}.
     *
     * @return an array of every {@link DigitalState} value
     */
    public static DigitalState[] allStates() {
        return DigitalState.values();
    }

    /**
     * Returns the set of actionable digital states, excluding {@link #UNKNOWN}.
     *
     * @return an {@link EnumSet} containing {@link #HIGH} and {@link #LOW}
     */
    public static EnumSet<DigitalState> all() {
        return EnumSet.of(DigitalState.HIGH, DigitalState.LOW);
    }

    /**
     * Parses a textual representation into a digital state, accepting {@code "0"}/{@code "1"} and strings
     * starting with {@code l}/{@code h} (case-insensitive).
     *
     * @param state the text to parse
     * @return the parsed state, or {@link #UNKNOWN} if it cannot be recognized
     */
    public static DigitalState parse(String state) {
        if(state.equalsIgnoreCase("0")) return DigitalState.LOW;
        if(state.equalsIgnoreCase("1")) return DigitalState.HIGH;
        if(state.toLowerCase().startsWith("l")) return DigitalState.LOW;
        if(state.toLowerCase().startsWith("h")) return DigitalState.HIGH;
        return DigitalState.UNKNOWN;
    }
}
