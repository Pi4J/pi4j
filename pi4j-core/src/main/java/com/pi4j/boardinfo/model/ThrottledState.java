package com.pi4j.boardinfo.model;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ThrottledState.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * Enum representing the different throttling and under-voltage states of the Raspberry Pi board.
 * Each enum value represents a specific condition that might be detected on the board, such as
 * undervoltage, frequency capping, throttling, or temperature limits.
 */
public enum ThrottledState {
    UNDERVOLTAGE_DETECTED(0x1, "Undervoltage detected"),
    ARM_FREQUENCY_CAPPED(0x2, "ARM frequency capped"),
    CURRENTLY_THROTTLED(0x4, "Currently throttled"),
    SOFT_TEMPERATURE_LIMIT_ACTIVE(0x8, "Soft temperature limit active"),
    UNDERVOLTAGE_HAS_OCCURED(0x10000, "Undervoltage has occurred"),
    ARM_FREQUENCY_CAPPING_HAS_OCCURED(0x20000, "ARM frequency capping has occurred"),
    THROTTLING_HAS_OCCURED(0x40000, "Throttling has occurred"),
    SOFT_TEMPERATURE_LIMIT_HAS_OCCURED(0x80000, "Soft temperature limit has occurred");

    private final int value;
    private final String description;

    /**
     * Constructor for the ThrottledState enum.
     *
     * @param value The integer value representing the state.
     * @param description A human-readable description of the state.
     */
    ThrottledState(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * Returns the integer value representing this throttled state.
     *
     * @return the integer value associated with the state.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a human-readable description of this throttled state.
     *
     * @return the description of the throttled state.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Decodes a raw throttled state (as an integer) and returns a list of active {@link ThrottledState} enums.
     *
     * @param rawState the raw throttled state as an integer (e.g., 0x50005).
     * @return a list of active throttled states.
     */
    public static List<ThrottledState> decode(int rawState) {
        List<ThrottledState> activeStates = new ArrayList<>();
        for (ThrottledState state : ThrottledState.values()) {
            if ((rawState & state.getValue()) != 0) {
                activeStates.add(state);
            }
        }
        return activeStates;
    }

    /**
     * Returns a human-readable description of the active throttled states based on the raw throttled state value.
     *
     * @param rawState the raw throttled state as an integer (e.g., 0x50005).
     * @return a description of the active throttled states.
     */
    public static String getActiveStatesDescription(int rawState) {
        List<ThrottledState> activeStates = decode(rawState);
        StringBuilder description = new StringBuilder();
        for (ThrottledState state : activeStates) {
            if (description.length() > 0) {
                description.append(", ");
            }
            description.append(state.getDescription());
        }
        return description.length() > 0 ? description.toString() : "No active throttled states";
    }
}
