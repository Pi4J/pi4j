package com.pi4j.io.gpio.analog.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultAnalogOutputConfig.java
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

import com.pi4j.io.gpio.analog.AnalogOutputConfig;
import com.pi4j.io.gpio.analog.AnalogRange;
import com.pi4j.util.StringUtil;

import java.util.Map;

/**
 * <p>DefaultAnalogOutputConfig class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultAnalogOutputConfig
    extends AnalogConfigBase<AnalogOutputConfig>
    implements AnalogOutputConfig {

    // private configuration properties
    protected Integer bus = null;
    protected Integer shutdownValue = null;
    protected Integer initialValue = null;
    protected Integer stepValue = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    private DefaultAnalogOutputConfig() {
        super();
    }

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link java.util.Map} object.
     */
    protected DefaultAnalogOutputConfig(Map<String, String> properties) {
        super(properties);

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "AOUT-" + this.bcm, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "AOUT-" + this.bcm, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "AOUT-" + this.bcm, true);

        if (properties.containsKey(BUS_KEY)) {
            this.bus = Integer.parseInt(properties.get(BUS_KEY));
        }

        // load initial value property
        if (properties.containsKey(INITIAL_VALUE_KEY)) {
            this.initialValue = Integer.parseInt(properties.get(INITIAL_VALUE_KEY));
        }

        // load shutdown value property
        if (properties.containsKey(SHUTDOWN_VALUE_KEY)) {
            this.shutdownValue = Integer.parseInt(properties.get(SHUTDOWN_VALUE_KEY));
        }

        // load shutdown value property
        if (properties.containsKey(STEP_VALUE_KEY)) {
            this.stepValue = Integer.parseInt(properties.get(STEP_VALUE_KEY));
        }

        // load range value property
        if (properties.containsKey(RANGE_MIN_KEY) || properties.containsKey(RANGE_MAX_KEY)) {
            // load optional range properties
            Integer min = null;
            Integer max = null;
            if (properties.containsKey(RANGE_MIN_KEY))
                min = Integer.parseInt(properties.get(RANGE_MIN_KEY));
            if (properties.containsKey(RANGE_MAX_KEY))
                max = Integer.parseInt(properties.get(RANGE_MAX_KEY));

            // create new range from loaded properties
            if (min != null || max != null)
                this.range = new DefaultAnalogRange(min, max);
        }

        // default values
        if (this.stepValue == null) this.stepValue = 1;
    }

    @Override
    public Integer bus() {
        return this.bus;
    }

    /**
     * @deprecated use {@link #bcm()} instead.
     */
    @Override
    @Deprecated(forRemoval = true)
    public Integer address() {
        return this.bcm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer bcm() {
        return this.bcm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer shutdownValue() {
        return this.shutdownValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultAnalogOutputConfig shutdownValue(Integer value) {
        this.shutdownValue = value;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer stepValue() {
        return this.stepValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogOutputConfig stepValue(Integer value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer initialValue() {
        return this.initialValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogRange range() {
        return this.range;
    }
}
