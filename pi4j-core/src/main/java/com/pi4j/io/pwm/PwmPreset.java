package com.pi4j.io.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PwmPreset.java
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

import com.pi4j.io.pwm.impl.DefaultPwmPresetBuilder;

/**
 * <p>PwmPreset interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface PwmPreset {

    /**
     * Return a new PWM Preset builder; (static factory method)
     *
     * @param name the unique preset name assigned to the PWM preset instance being created.
     * @return a new PWM preset builder instance.
     */
    static PwmPresetBuilder newBuilder(String name){
        return DefaultPwmPresetBuilder.newInstance(name);
    }

    /**
     * Get the preset name assigned to this PWM preset instance.
     *
     * @return preset name
     */
    String name();

    /**
     * Get the preset name assigned to this PWM preset instance.
     *
     * @return preset name
     */
    default String getName() {
        return name();
    }

    /**
     *  Get the duty-cycle value as a decimal value that represents the
     *  percentage of the ON vs OFF time of the PWM signal for each
     *  period.  The duty-cycle range is valid from 0 to 100 including
     *  factional values. (Values above 50% mean the signal will
     *  remain HIGH more time than LOW.)
     * <p>
     *  Example: A value of 50 represents a duty-cycle where half of
     *  the time period the signal is LOW and the other half is HIGH.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     */
    Float dutyCycle();

    /**
     *  Get the duty-cycle value as a decimal value that represents the
     *  percentage of the ON vs OFF time of the PWM signal for each
     *  period.  The duty-cycle range is valid from 0 to 100 including
     *  factional values.  (Values above 50% mean the signal will
     *  remain HIGH more time than LOW.)
     * <p>
     *  Example: A value of 50 represents a duty-cycle where half of
     *  the time period the signal is LOW and the other half is HIGH.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     */
    default Float getDutyCycle() {
        return dutyCycle();
    }

    /**
     *  Get the configured frequency value in Hertz (number of cycles per second)
     *  that the PWM signal generator should attempt to output when this preset
     *  is applied to a PWM instance.
     * <p>
     *  Please note that certain PWM signal generators may be limited to specific
     *  frequency bands and may not generate all possible explicit frequency values.
     *  After enabling the PWM signal using the 'on(...)' method, you can check the
     *  'Pwm::frequency()' or 'Pwm::getFrequency()' properties to determine what
     *  frequency the PWM generator actually applied.
     *
     * @return the configured frequency (Hz) that is used when turning the
     *         PWM signal to the 'ON' state when applying this PWM preset.
     */
    Integer frequency();

    /**
     *  Get the configured frequency value in Hertz (number of cycles per second)
     *  that the PWM signal generator should attempt to output when this preset
     *  is applied to a PWM instance.
     * <p>
     *  Please note that certain PWM signal generators may be limited to specific
     *  frequency bands and may not generate all possible explicit frequency values.
     *  After enabling the PWM signal using the 'on(...)' method, you can check the
     *  'Pwm::frequency()' or 'Pwm::getFrequency()' properties to determine what
     *  frequency the PWM generator actually applied.
     *
     * @return the configured frequency (Hz) that is used when turning the
     *         PWM signal to the 'ON' state when applying this PWM preset.
     */
    default Integer getFrequency() {
        return frequency();
    }
}
