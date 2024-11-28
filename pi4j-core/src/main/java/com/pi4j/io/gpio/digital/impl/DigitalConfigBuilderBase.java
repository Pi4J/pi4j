package com.pi4j.io.gpio.digital.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalConfigBuilderBase.java
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
import com.pi4j.io.gpio.digital.DigitalConfig;
import com.pi4j.io.gpio.digital.DigitalConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.impl.IOAddressConfigBuilderBase;

/**
 * <p>Abstract AnalogConfigBuilderBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class DigitalConfigBuilderBase<BUILDER_TYPE extends DigitalConfigBuilder, CONFIG_TYPE extends DigitalConfig>
        extends IOAddressConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
        implements DigitalConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DigitalConfigBuilderBase(Context context){
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE onState(DigitalState state) {
        this.properties.put(DigitalConfig.ON_STATE_KEY, state.toString());
        return (BUILDER_TYPE)this;
    }
}
