package com.pi4j.io.gpio.analog;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  AnalogOutputBase.java
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
import com.pi4j.io.exception.IOBoundsException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.exception.IOIllegalValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Abstract AnalogOutputBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public abstract class AnalogOutputBase extends AnalogBase<AnalogOutput, AnalogOutputConfig, AnalogOutputProvider> implements AnalogOutput {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Integer value = 0;

    /**
     * <p>Constructor for AnalogOutputBase.</p>
     *
     * @param provider a {@link com.pi4j.io.gpio.analog.AnalogOutputProvider} object.
     * @param config   a {@link com.pi4j.io.gpio.analog.AnalogOutputConfig} object.
     */
    public AnalogOutputBase(AnalogOutputProvider provider, AnalogOutputConfig config) {
        super(provider, config);
        if (this.id == null) this.id = "AOUT-" + config.pin();
        if (this.name == null) this.name = "AOUT-" + config.pin();

        // update the analog value to the initial value if an initial value was configured
        if (config().initialValue() != null) {
            try {
                value(config().initialValue());
            } catch (IOIllegalValueException | IOBoundsException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogOutput stepUp() {
        try {
            return step(config().stepValue());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogOutput stepDown() {
        try {
            return step(0 - config().stepValue());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogOutput step(Integer value) throws IOIllegalValueException, IOBoundsException {

        // validate value
        if (value == null)
            throw new IOIllegalValueException();

        Integer oldValue = this.value();
        Integer newValue = value + oldValue; // increment value by step increment
        if (config().range() != null) {
            newValue = config().range().sanitize(newValue);
        }
        return this.value(newValue);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogOutput value(Integer value) throws IOIllegalValueException, IOBoundsException {

        // validate value
        if (value == null)
            throw new IOIllegalValueException();

        // validate value bounds
        if (config().range() != null) {
            if (!config().range().validate(value)) {
                throw new IOBoundsException(value, config().range().min(), config().range().max());
            }
        }

        // check to see of there is a value change; if there is then we need
        // to update the internal value variable and dispatch the change event
        if (this.value().intValue() != value.intValue()) {
            // cache copy of old value for change event
            Integer oldValue = this.value();

            // update current/new value
            this.value = value;

            // dispatch value change event
            this.dispatch(new AnalogValueChangeEvent<AnalogOutputBase>(this, this.value(), oldValue));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogOutput shutdown(Context context) {
        // update the analog value to the shutdown value if a shutdown value is configured
        if (config().shutdownValue() != null) {
            try {
                value(config().shutdownValue());
            } catch (IOIllegalValueException | IOBoundsException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value() {
        return this.value;
    }
}
