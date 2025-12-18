package com.pi4j.io.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PwmConfigBuilder.java
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

import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.io.pwm.impl.DefaultPwmConfigBuilder;

/**
 * <p>PwmConfigBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface PwmConfigBuilder extends
    IOConfigBuilder<PwmConfigBuilder, PwmConfig>,
    ConfigBuilder<PwmConfigBuilder, PwmConfig> {
    /**
     * <p>newInstance.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.pwm.PwmConfigBuilder} object.
     */
    static PwmConfigBuilder newInstance(Context context) {
        return DefaultPwmConfigBuilder.newInstance(context);
    }

    /**
     * Sets the chip number for the PWM configuration.
     *
     * @param chip the chip number to be configured, used for hardware PWM signals,
     *             to identify the specific hardware interface or connection point.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    PwmConfigBuilder chip(Integer chip);

    /**
     * Sets the channel number for the PWM configuration.
     *
     * @param channel the channel number to be configured, used for hardware PWM signals,
     *                to specify a particular hardware channel or output line
     *                for the PWM signal.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    PwmConfigBuilder channel(Integer channel);

    /**
     * Sets the bcm number for the PWM configuration.
     *
     * @param bcm the bcm number to be configured, used for software PWM signals.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    PwmConfigBuilder bcm(Integer bcm);

    /**
     * Set the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should attempt to output when the PWM state
     * is enabled.
     * <p>
     * Please note that certain PWM signal generators may be limited to specific
     * frequency bands and may not generate all possible explicit frequency values.
     * After enabling the PWM signal using the 'on(...)' method, you can check the
     * 'Pwm::frequency()' or 'Pwm::getFrequency()' properties to determine what
     * frequency the PWM generator actually applied.
     *
     * @param frequency the number of cycles per second (Hertz)
     * @return this builder instance
     */
    PwmConfigBuilder frequency(Integer frequency);

    /**
     * Set the duty-cycle value as a decimal value that represents the
     * percentage of the ON vs OFF time of the PWM signal for each
     * period.  The duty-cycle range is valid from 0 to 100 including
     * factional values.  (Values above 50% mean the signal will
     * remain HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    PwmConfigBuilder dutyCycle(Integer dutyCycle);

    /**
     * Set the PwmType of this PWM instance. (Hardware/Software)
     * Please note that not all PWM providers support both hardware and software
     * PWM generators.  Please consult the documentation for your PWM provider
     * to determine what support is available and what limitations may apply.
     *
     * @param pwmType a {@link com.pi4j.io.pwm.PwmType} object.
     * @return this builder instance
     */
    PwmConfigBuilder pwmType(PwmType pwmType);

    /**
     * Set the polarity of this PWM instance. (Normal/Inversed)
     * Please note that not all PWM providers support polarity.  Please
     * consult the documentation for your PWM provider to determine
     * what support is available and what limitations may apply.
     *
     * @param polarity a {@link com.pi4j.io.pwm.PwmPolarity} object.
     * @return this builder instance
     */
    PwmConfigBuilder polarity(PwmPolarity polarity);

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when the Pi4J context is shutdown.
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    PwmConfigBuilder shutdown(Integer dutyCycle);

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when this PWM instance is created and initialized.
     * This option can be helpful if you wish to do something like set a default PWM
     * signal (by configuring this 'initial' value to 50%) when your application
     * creates the PWM instance.  This just helps eliminate a second line of code
     * to manually start the PWM signal for cases where you prefer it is auto-started.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    PwmConfigBuilder initial(Integer dutyCycle);

    /**
     * Add one or more PwmPresets to this PWM instance. You can create new PWM
     * preset instance using the 'PwmPreset::newBuilder(name)' static
     * factory method.
     *
     * @param preset one or more pre-configured PwmPreset instances
     * @return this builder instance
     */
    PwmConfigBuilder preset(PwmPreset... preset);
}
