package com.pi4j.io.pwm.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultPwmPreset.java
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

import com.pi4j.io.pwm.PwmPreset;

/**
 * <p>DefaultPwmPreset class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultPwmPreset  implements PwmPreset {

    protected final String name;
    protected final Integer dutyCycle;
    protected final Integer frequency;

    /**
     * <p>Constructor for DefaultPwmPreset.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param dutyCycle a {@link java.lang.Integer} object.
     */
    public DefaultPwmPreset(String name, Integer dutyCycle){
        this.name = name.toLowerCase().trim();
        this.dutyCycle = dutyCycle;
        this.frequency = null;
    }

    /**
     * <p>Constructor for DefaultPwmPreset.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param dutyCycle a {@link java.lang.Integer} object.
     * @param frequency a {@link java.lang.Integer} object.
     */
    public DefaultPwmPreset(String name, Integer dutyCycle, Integer frequency){
        this.name = name.toLowerCase().trim();

        // bounds check the duty-cycle value
        if(dutyCycle != null) {
            Integer dc = dutyCycle;
            if (dc < 0) dc = 0;
            if (dc > 100) dc = 100;
            this.dutyCycle = dc;
        } else {
            this.dutyCycle = null;
        }
        this.frequency = frequency;
    }

    /** {@inheritDoc} */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer dutyCycle() {
        return this.dutyCycle;
    }

    /** {@inheritDoc} */
    @Override
    public Integer frequency() {
        return this.frequency;
    }
}
