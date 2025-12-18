package com.pi4j.io.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Pwm.java
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

import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.OnOff;
import com.pi4j.io.exception.IOException;
import com.pi4j.util.Frequency;

import java.util.Map;

/**
 * <p>Pwm interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Pwm extends IO<Pwm, PwmConfig, PwmProvider>, OnOff<Pwm> {

    static final long MEGAHERTZ = Frequency.MEGAHERTZ;
    static final long KILOHERTZ = Frequency.KILOHERTZ;
    static final long HERTZ = Frequency.HERTZ;

    /**
     * <p>newConfigBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.pwm.PwmConfigBuilder} object.
     */
    static PwmConfigBuilder newConfigBuilder(Context context) {
        return PwmConfigBuilder.newInstance(context);
    }

    /**
     * Get the chip of this PWM instance, for hardware PWM.
     *
     * @return pwm chip
     */
    default int getChip() {
        return config().chip();
    }

    /**
     * Get the channel of this PWM instance, for hardware PWM.
     *
     * @return pwm channel
     */
    default int getChannel() {
        return config().getChannel();
    }

    /**
     * Get the bcm of this PWM instance, for software PWM.
     *
     * @return pwm bcm
     */
    default int getBcm() {
        return config().getBcm();
    }

    /**
     * Get the PWM signal ON/ENABLED state.
     *
     * @return returns 'true' if the PWM signal is in the ON state; else returns 'false'
     */
    @Override
    boolean isOn();

    /**
     * Get the PWM signal OFF/DISABLED state.
     *
     * @return returns 'true' if the PWM signal is in the OFF state; else returns 'false'
     */
    @Override
    default boolean isOff() {
        return !isOn();
    }

    /**
     * Turn the PWM signal [ON] using the configured frequency and duty-cycle.
     *
     * @return returns this PWM instance
     * @throws IOException if fails to communicate with the PWM pin
     */
    @Override
    Pwm on() throws IOException;

    /**
     * Turn the PWM signal [OFF] by applying a zero frequency and zero duty-cycle to the PWM pin.
     *
     * @return a {@link com.pi4j.io.pwm.Pwm} object.
     * @throws IOException if fails to communicate with the PWM pin
     */
    @Override
    Pwm off() throws IOException;

    /**
     * Get the PWM type of this PWM instance. (Hardware/Software)
     *
     * @return the PWM type of this PWM instance.
     */
    default PwmType pwmType() {
        return config().getPwmType();
    }

    /**
     * Get the PWM type of this PWM instance. (Hardware/Software)
     *
     * @return the PWM type of this PWM instance.
     */
    default PwmType getPwmType() {
        return pwmType();
    }

    /**
     * Get the polarity of this PWM instance. (Normal/Inversed)
     *
     * @return the polarity of this PWM instance.
     */
    default PwmPolarity polarity() {
        return config().getPolarity();
    }

    /**
     * Get the polarity of this PWM instance. (Normal/Inversed)
     *
     * @return the polarity of this PWM instance.
     */
    default PwmPolarity getPolarity() {
        return polarity();
    }

    /**
     * Turn the PWM signal [ON] using a specified duty-cycle (%)
     * at the pre-configured frequency (Hz).
     *
     * @param dutyCycle The duty-cycle value is an Integer value that represents the
     *                  percentage of the ON vs OFF time of the PWM signal for each
     *                  period.  A value of 50 represents a duty-cycle where half of
     *                  the time period the signal is LOW and the other half is HIGH.
     *                  The duty-cycle range is valid from 0 to 100.
     *                  (Values above 50% mean the signal will remain HIGH more
     *                  time than LOW.)
     * @return returns this PWM instance
     * @throws IOException if fails to communicate with the PWM pin
     */
    default Pwm on(Integer dutyCycle) throws IOException {
        if (dutyCycle > 0) {
            setDutyCycle(dutyCycle);
            return on();
        } else {
            return off();
        }
    }

    /**
     * Turn the PWM signal [ON] using a specified duty-cycle (%)
     * at the pre-configured frequency (Hz).
     *
     * @param dutyCycle The duty-cycle value is an Integer value that represents the
     *                  percentage of the ON vs OFF time of the PWM signal for each
     *                  period.  A value of 50 represents a duty-cycle where half of
     *                  the time period the signal is LOW and the other half is HIGH.
     *                  The duty-cycle range is valid from 0 to 100.
     *                  (Values above 50% mean the signal will remain HIGH more
     *                  time than LOW.)
     * @param frequency The desired frequency value in Hertz (number of cycles per second)
     *                  that the PWM signal generator should attempt to output.  Please
     *                  note that certain PWM signal generators may be limited to
     *                  specific frequency bands and may not generate all possible explicit
     *                  frequency values.  Immediately after calling this method, you can
     *                  check the 'Pwm::actualFrequency()' or 'Pwm::getActualFrequency()'
     *                  properties to determine what frequency the PWM generator actually
     *                  applied.
     * @return returns this PWM instance
     * @throws IOException if fails to communicate with the PWM pin
     */
    default Pwm on(Integer dutyCycle, int frequency) throws IOException {
        if (dutyCycle > 0 && frequency > 0) {
            setDutyCycle((Integer) dutyCycle);
            setFrequency(frequency);
            return on();
        } else {
            return off();
        }
    }

    /**
     * Get the duty-cycle value as an Integer value that represents the
     * percentage of the ON vs OFF time of the PWM signal for each
     * period.  The duty-cycle range is valid from 0 to 100.
     * (Values above 50% mean the signal will remain HIGH more time
     * than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     * @throws IOException if fails to communicate with the PWM pin
     */
    Integer getDutyCycle() throws IOException;

    /**
     * Get the duty-cycle value as an Integer value that represents the
     * percentage of the ON vs OFF time of the PWM signal for each
     * period.  The duty-cycle range is valid from 0 to 100.
     * (Values above 50% mean the signal will remain
     * HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     * @throws IOException if fails to communicate with the PWM pin
     */
    default Integer dutyCycle() throws IOException {
        return getDutyCycle();
    }

    /**
     * Set the duty-cycle value as an Integer value that represents the
     * percentage of the ON vs OFF time of the PWM signal for each
     * period.  The duty-cycle range is valid from 0 to 100.
     * This method will not update a live PWM signal, but rather stage
     * the duty-cycle value for subsequent call to the 'Pwm::On()' method.
     * Call 'Pwm::On()' if you wish to make a live/
     * immediate change to the duty-cycle on an existing PWM signal.
     * (Values above 50% mean the signal will remain HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @throws IOException if fails to communicate with the PWM pin
     */
    void setDutyCycle(Integer dutyCycle) throws IOException;

    /**
     * Set the duty-cycle value as an Integer value that represents the
     * percentage of the ON vs OFF time of the PWM signal for each
     * period.  The duty-cycle range is valid from 0 to 100 .
     * This method will not update a live PWM signal, but rather stage
     * the duty-cycle value for subsequent call to the 'Pwm::On()' method.
     * Call 'Pwm::On()' if you wish to make a live/
     * immediate change to the duty-cycle on an existing PWM signal.
     * (Values above 50% mean the signal will remain HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return returns this PWM instance
     * @throws IOException if fails to communicate with the PWM pin
     */
    default Pwm dutyCycle(Integer dutyCycle) throws IOException {
        setDutyCycle(dutyCycle);
        return this;
    }

    /**
     * Get the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should attempt to output when the PWM signal
     * is turned 'ON'.
     * <p>
     * Please note that certain PWM signal generators may be limited to specific
     * frequency bands and may not generate all possible explicit frequency values.
     * After enabling the PWM signal using the 'on(..) method, you can check the
     * 'Pwm::frequency()' or 'Pwm::getFrequency()' properties to determine what
     * frequency the PWM generator actually applied.
     *
     * @return the configured frequency (Hz) that is used when turning the
     * PWM signal to the 'ON' state.
     * @throws IOException if fails to communicate with the PWM pin
     */
    int getFrequency() throws IOException;

    /**
     * Get the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should attempt to output when the PWM signal
     * is turned 'ON'.
     * <p>
     * Please note that certain PWM signal generators may be limited to specific
     * frequency bands and may not generate all possible explicit frequency values.
     * After enabling the PWM signal using the 'on(...)' method, you can check the
     * 'Pwm::frequency()' or 'Pwm::getFrequency()' properties to determine what
     * frequency the PWM generator actually applied.
     *
     * @return the configured frequency (Hz) that is used when turning the
     * PWM signal to the 'ON' state.
     * @throws IOException if fails to communicate with the PWM pin
     */
    default int frequency() throws IOException {
        return getFrequency();
    }

    /**
     * Get the actual frequency value in Hertz (number of cycles per second)
     * applied by the PWM signal generator after the PWM signal is turned 'ON'.
     * <p>
     * Please note that certain PWM signal generators may be limited to specific
     * frequency bands and may not generate all possible explicit frequency values.
     * After enabling the PWM signal using the 'on(...)' method, you can call this
     * method to determine what frequency the PWM generator actually applied.
     *
     * @return the actual frequency (Hz) applied by the PWM generator when the
     * PWM signal is set to the 'ON' state.
     * @throws IOException if fails to communicate with the PWM pin
     */
    int getActualFrequency() throws IOException;

    /**
     * Get the actual frequency value in Hertz (number of cycles per second)
     * applied by the PWM signal generator after the PWM signal is turned 'ON'.
     * <p>
     * Please note that certain PWM signal generators may be limited to specific
     * frequency bands and may not generate all possible explicit frequency values.
     * After enabling the PWM signal using the 'on(...)' method, you can call this
     * method to determine what frequency the PWM generator actually applied.
     *
     * @return the actual frequency (Hz) applied by the PWM generator when the
     * PWM signal is set to the 'ON' state.
     * @throws IOException if fails to communicate with the PWM pin
     */
    default int actualFrequency() throws IOException {
        return getActualFrequency();
    }

    /**
     * Set the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should use when the PWM signal is turned 'ON'.
     * <p>
     * Note: This method will not update a live PWM signal, but rather stage the
     * frequency value for subsequent call to the 'Pwm::On()' method.  Call 'Pwm::On()'
     * if you wish to make a live/immediate change to the duty-cycle on an existing
     * PWM signal.
     *
     * @param frequency the number of cycles per second (Hertz)
     * @throws IOException if fails to communicate with the PWM pin
     */
    void setFrequency(int frequency) throws IOException;

    /**
     * Set the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should use when the PWM signal is turned 'ON'.
     * <p>
     * Note: This method will not update a live PWM signal, but rather stage the
     * frequency value for subsequent call to the 'Pwm::On()' method.  Call 'Pwm::On()'
     * if you wish to make a live/immediate change to the duty-cycle on an existing
     * PWM signal.
     *
     * @param frequency the number of cycles per second (Hertz)
     * @return returns this PWM instance
     * @throws IOException if fails to communicate with the PWM pin
     */
    default Pwm frequency(int frequency) throws IOException {
        setFrequency(frequency);
        return this;
    }

    /**
     * Get all the PwmPreset instances assigned to this PWM instance.
     *
     * @return a map of PwmPresets indexed/cataloged by preset name.
     */
    Map<String, PwmPreset> getPresets();

    /**
     * Get all the PwmPreset assigned to this PWM instance.
     *
     * @return a map of PwmPreset indexed/cataloged by preset name.
     */
    default Map<String, PwmPreset> presets() {
        return getPresets();
    }

    /**
     * Get a single PwmPreset from this PWM instance by the preset's name.
     *
     * @param name preset name string
     * @return a single PwmPreset instance
     */
    PwmPreset getPreset(String name);

    /**
     * Get a single PwmPreset from this PWM instance by the preset's name.
     *
     * @param name preset name string
     * @return a single PwmPreset instance
     */
    default PwmPreset preset(String name) {
        return getPreset(name);
    }

    /**
     * Delete/remove a PwmPreset by name from this PWM instance.
     *
     * @param name preset name string
     * @return the deleted PWM Preset instance
     */
    PwmPreset deletePreset(String name);

    /**
     * Add a new PwmPreset to this PWM instance. You can create new PWM
     * preset instance using the 'PwmPreset::newBuilder(name)' static
     * factory method.
     *
     * @param preset a pre-configured PwmPreset instance
     * @return this PWM instance
     */
    Pwm addPreset(PwmPreset preset);

    /**
     * Apply/recall a PwmPreset by name to this PWM instance.
     * This will update the PWM signal with the configured
     * PWM frequency and duty-cycle defined in the preset object.
     *
     * @param name preset name string
     * @return the deleted PWM Preset instance
     * @throws IOException if fails to communicate with the PWM pin
     */
    Pwm applyPreset(String name) throws IOException;
}
