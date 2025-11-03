package com.pi4j.io.gpio.digital.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultDigitalInputConfig.java
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

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.impl.IOPinConfigBase;
import com.pi4j.util.StringUtil;

import java.util.Map;

/**
 * <p>DefaultDigitalInputConfig class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultDigitalInputConfig
    extends IOPinConfigBase<DigitalInputConfig>
    implements DigitalInputConfig {

    /**
     * PRIVATE CONSTRUCTOR
     */
    private DefaultDigitalInputConfig() {
        super();
    }

    // private configuration properties
    protected Integer bus = 0;
    protected PullResistance pullResistance = PullResistance.OFF;
    protected Long debounce = DigitalInput.DEFAULT_DEBOUNCE;
    protected DigitalState onState = DigitalState.HIGH;

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link java.util.Map} object.
     */
    protected DefaultDigitalInputConfig(Map<String, String> properties) {
        super(properties);

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "DIN-" + this.pin, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "DIN-" + this.pin, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "DIN-" + this.pin, true);

        if (properties.containsKey(BUS_KEY)) {
            this.bus = Integer.parseInt(properties.get(BUS_KEY));
        }

        // load optional pull resistance from properties
        if (properties.containsKey(PULL_RESISTANCE_KEY)) {
            this.pullResistance = PullResistance.parse(properties.get(PULL_RESISTANCE_KEY));
        }

        // load optional pull resistance from properties
        if (properties.containsKey(DEBOUNCE_RESISTANCE_KEY)) {
            this.debounce = Long.parseLong(properties.get(DEBOUNCE_RESISTANCE_KEY));
        }

        // load on-state value property
        if (properties.containsKey(ON_STATE_KEY)) {
            this.onState = DigitalState.parse(properties.get(ON_STATE_KEY));
        }
    }

    /**
     * @deprecated use {@link #bus()} instead.
     * <p>
     * {@inheritDoc}
     */
    @Override
    @Deprecated(forRemoval = true)
    public Integer address() {
        return this.pin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer bus() {
        return this.bus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer pin() {
        return this.pin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PullResistance pull() {
        return this.pullResistance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long debounce() {
        return this.debounce;
    }

    @Override
    public DigitalState onState() {
        return this.onState;
    }
}
