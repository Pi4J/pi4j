package com.pi4j.test.provider;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  TestAnalogInput.java
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

import com.pi4j.io.gpio.analog.*;

/**
 * <p>TestAnalogInput class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestAnalogInput extends AnalogInputBase implements AnalogInput {

    private Integer value = 0;

    /** {@inheritDoc} */
    @Override
    public Integer value() {
        return this.value;
    }

    /**
     * <p>Constructor for TestAnalogInput.</p>
     *
     * @param provider a {@link com.pi4j.io.gpio.analog.AnalogInputProvider} object.
     * @param config a {@link com.pi4j.io.gpio.analog.AnalogInputConfig} object.
     */
    public TestAnalogInput(AnalogInputProvider provider, AnalogInputConfig config){
        super(provider, config);
    }

    /**
     * <p>test.</p>
     *
     * @param value a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.test.provider.TestAnalogInput} object.
     */
    public TestAnalogInput test(Integer value){


        // check to see of there is a value change; if there is then we need
        // to update the internal value variable and dispatch the change event
        if(this.value().intValue() != value.intValue()) {

            // cache copy of old value for change event
            Integer oldValue = this.value();

            // update current/new value
            this.value = value;

            // dispatch value change event
            this.dispatch(new AnalogValueChangeEvent(this, this.value(), oldValue));
        }
        return this;
    }
}
