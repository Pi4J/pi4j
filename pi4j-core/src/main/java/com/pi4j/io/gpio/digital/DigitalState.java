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

    public boolean isHigh() {
        return (this == HIGH);
    }

    public boolean isLow() {
        return (this == LOW);
    }

    public Number value() {
        return getValue();
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     */
    public Number getValue() {
        return value;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     */
    public String getName() {
        return name;
    }

    public boolean equals(DigitalState state) {
        return this == state;
    }
    public boolean equals(Number state) {
        return this == DigitalState.getState(state);
    }
    public boolean equals(boolean state) {
        return this == DigitalState.getState(state);
    }
    public boolean equals(byte state){
        return equals(DigitalState.getState(state));
    }
    public boolean equals(short state){
        return equals(DigitalState.getState(state));
    }
    public boolean equals(int state){
        return equals(DigitalState.getState(state));
    }
    public boolean equals(long state){
        return equals(DigitalState.getState(state));
    }
    public boolean equals(float state){
        return equals(DigitalState.getState(state));
    }
    public boolean equals(double state){
        return equals(DigitalState.getState(state));
    }


    @Override
    public String toString() {
        return name;
    }

    public static DigitalState state(Number state) {
        return getState(state);
    }

    public static DigitalState getState(Number state) {
        for (var item : DigitalState.values()) {
            if (item.getValue().intValue() == state.intValue()) {
                return item;
            }
        }
        return null;
    }

    public static DigitalState inverseState(DigitalState state) {
        return getInverseState(state);
    }

    public static DigitalState getInverseState(DigitalState state) {
        return (state == HIGH ? LOW : HIGH);
    }

    public static DigitalState getState(boolean state) {
        return (state ? DigitalState.HIGH : DigitalState.LOW);
    }

    /**
     * @return an array of {@link com.pi4j.io.gpio.digital.DigitalState} objects.
     */
    public static DigitalState[] allStates() {
        return DigitalState.values();
    }

    public static EnumSet<DigitalState> all() {
        return EnumSet.of(DigitalState.HIGH, DigitalState.LOW);
    }

    public static DigitalState parse(String state) {
        if(state.equalsIgnoreCase("0")) return DigitalState.LOW;
        if(state.equalsIgnoreCase("1")) return DigitalState.HIGH;
        if(state.toLowerCase().startsWith("l")) return DigitalState.LOW;
        if(state.toLowerCase().startsWith("h")) return DigitalState.HIGH;
        return DigitalState.UNKNOWN;
    }
}
