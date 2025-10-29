package com.pi4j.io.gpio.analog.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  AnalogConfigBuilderBase.java
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
import com.pi4j.io.gpio.analog.AnalogConfig;
import com.pi4j.io.gpio.analog.AnalogConfigBuilder;
import com.pi4j.io.gpio.analog.AnalogOutputConfig;
import com.pi4j.io.impl.IOPinConfigBuilderBase;

/**
 * <p>Abstract AnalogConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public abstract class AnalogConfigBuilderBase<BUILDER_TYPE extends AnalogConfigBuilder, CONFIG_TYPE extends AnalogConfig>
    extends IOPinConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements AnalogConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected AnalogConfigBuilderBase(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BUILDER_TYPE min(Integer value) {
        this.properties.put(AnalogOutputConfig.RANGE_MIN_KEY, value.toString());
        return (BUILDER_TYPE) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BUILDER_TYPE max(Integer value) {
        this.properties.put(AnalogOutputConfig.RANGE_MAX_KEY, value.toString());
        return (BUILDER_TYPE) this;
    }
}
