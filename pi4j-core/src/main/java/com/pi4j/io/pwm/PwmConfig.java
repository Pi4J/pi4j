package com.pi4j.io.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PwmConfig.java
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

import com.pi4j.config.AddressConfig;
import com.pi4j.io.gpio.GpioConfig;

import java.util.Collection;

/**
 * <p>PwmConfig interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface PwmConfig extends GpioConfig<PwmConfig>, AddressConfig<PwmConfig> {

    /** Constant <code>PWM_TYPE_KEY="pwm-type"</code> */
    String PWM_TYPE_KEY = "pwm-type";
    /** Constant <code>POLARITY_KEY="polarity"</code> */
    String POLARITY_KEY = "polarity";
    /** Constant <code>FREQUENCY_KEY="frequency"</code> */
    String FREQUENCY_KEY = "frequency";
    /** Constant <code>DUTY_CYCLE_KEY="duty-cycle"</code> */
    String DUTY_CYCLE_KEY = "duty-cycle";
    /** Constant <code>SHUTDOWN_VALUE_KEY="shutdown"</code> */
    String SHUTDOWN_VALUE_KEY = "shutdown";
    /** Constant <code>INITIAL_VALUE_KEY="initial"</code> */
    String INITIAL_VALUE_KEY = "initial";

    /**
     *  Get the configured duty-cycle value as a decimal value that represents
     *  the percentage of the ON vs OFF time of the PWM signal for each period.
     *  The duty-cycle range is valid from 0 to 100 including factional values.
     *  (Values above 50% mean the signal will remain HIGH more time than LOW.)
     * <p>
     *  Example: A value of 50 represents a duty-cycle where half of
     *  the time period the signal is LOW and the other half is HIGH.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     */
     Integer dutyCycle();

    /**
     *  Get the configured duty-cycle value as a decimal value that represents
     *  the percentage of the ON vs OFF time of the PWM signal for each period.
     *  The duty-cycle range is valid from 0 to 100 including factional values.
     *  (Values above 50% mean the signal will remain HIGH more time than LOW.)
     * <p>
     *  Example: A value of 50 represents a duty-cycle where half of
     *  the time period the signal is LOW and the other half is HIGH.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     */
    default Integer getDutyCycle() {
        return dutyCycle();
    }

    /**
     *  Get the configured frequency value in Hertz (number of cycles per second)
     *  that the PWM signal generator should attempt to output when the PWM state is
     *  enabled.
     *
     * @return frequency value in Hz (number of cycles per second)
     */
    Integer frequency();

    /**
     *  Get the configured frequency value in Hertz (number of cycles per second)
     *  that the PWM signal generator should attempt to output when the PWM state is
     *  enabled.
     *
     * @return frequency value in Hz (number of cycles per second)
     */
    default Integer getFrequency() {
        return frequency();
    }

    /**
     * Get the configured PwmType of this PWM instance. (Hardware/Software)
     * Please note that not all PWM providers support both hardware and software
     * PWM generators.  Please consult the documentation for your PWM provider
     * to determine what support is available and what limitations may apply.
     *
     * @return the PwmType for this PWM instance
     */
    PwmType pwmType();

    /**
     * Get the configured PwmType of this PWM instance. (Hardware/Software)
     * Please note that not all PWM providers support both hardware and software
     * PWM generators.  Please consult the documentation for your PWM provider
     * to determine what support is available and what limitations may apply.
     *
     * @return the PwmType for this PWM instance
     */
    default PwmType getPwmType(){
        return pwmType();
    }

    /**
     * Get the configured polarity of this PWM instance. (Normal/Inversed)
     * Please note that not all PWM providers support polarity. Please
     * consult the documentation for your PWM provider to determine what
     * support is available and what limitations may apply.
     *
     * @return the PwmPolarity for this PWM instance
     */
    PwmPolarity polarity();

    /**
     * Get the configured polarity of this PWM instance. (Normal/Inversed)
     * Please note that not all PWM providers support polarity. Please
     * consult the documentation for your PWM provider to determine what
     * support is available and what limitations may apply.
     *
     * @return the PwmPolarity for this PWM instance
     */
    default PwmPolarity getPolarity(){
        return polarity();
    }

    /**
     * Get configured PWM duty-cycle value that is automatically applied
     * to the PWM instance when the Pi4J context is shutdown.
     * <p>
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @return optional duty-cycle value expressed as a percentage (rage: 0-100)
     *         that is applied when shutting down the Pi4J context.
     */
    Integer shutdownValue();

    /**
     * Get configured PWM duty-cycle value that is automatically applied
     * to the PWM instance when the Pi4J context is shutdown.
     * <p>
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     *         that is applied when shutting down the Pi4J context.
     */
    default Integer getShutdownValue(){
        return shutdownValue();
    }

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when the Pi4J context is shutdown.
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this PwmConfig instance
     */
    PwmConfig shutdownValue(Integer dutyCycle);

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when the Pi4J context is shutdown.
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     */
    default void setShutdownValue(Integer dutyCycle){
        this.shutdownValue(dutyCycle);
    }

    /**
     * Get configured PWM duty-cycle value that is automatically applied to
     * the PWM instance when this PWM instance is created and initialized.
     * <p>
     * This option can be helpful if you wish to do something like set a default PWM
     * signal (by configuring this 'initial' value to 50%) when your application
     * creates the PWM instance.  This just helps eliminate a second line of code
     * to manually start the PWM signal for cases where you prefer it is auto-started.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     *         that is applied when creating and initializing the PWM instance.
     */
    Integer initialValue();

    /**
     * Get configured PWM duty-cycle value that is automatically applied to
     * the PWM instance when this PWM instance is created and initialized.
     * <p>
     * This option can be helpful if you wish to do something like set a default PWM
     * signal (by configuring this 'initial' value to 50%) when your application
     * creates the PWM instance.  This just helps eliminate a second line of code
     * to manually start the PWM signal for cases where you prefer it is auto-started.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     *         that is applied when creating and initializing the PWM instance.
     */
    default Integer getInitialValue(){
        return initialValue();
    }

    /**
     * Get the configured PwmPresets assigned to this PWM instance.
     *
     * @return collection of PwmPresets
     */
    Collection<PwmPreset> presets();

    /**
     * Get the configured PwmPresets assigned to this PWM instance.
     *
     * @return collection of PwmPresets
     */
    default Collection<PwmPreset> getPresets(){
        return presets();
    }
}
