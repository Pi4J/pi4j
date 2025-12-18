package com.pi4j.test.provider.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  TestAnalogInputProviderImpl.java
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

import com.pi4j.io.exception.IOAlreadyExistsException;
import com.pi4j.io.gpio.analog.AnalogInput;
import com.pi4j.io.gpio.analog.AnalogInputConfig;
import com.pi4j.io.gpio.analog.AnalogInputProviderBase;
import com.pi4j.test.provider.TestAnalogInput;
import com.pi4j.test.provider.TestAnalogInputProvider;

/**
 * <p>TestAnalogInputProviderImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestAnalogInputProviderImpl extends AnalogInputProviderBase implements TestAnalogInputProvider {

    /**
     * <p>Constructor for TestAnalogInputProviderImpl.</p>
     */
    public TestAnalogInputProviderImpl() {
        super();
    }

    /**
     * <p>Constructor for TestAnalogInputProviderImpl.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public TestAnalogInputProviderImpl(String id) {
        super(id);
    }

    /**
     * <p>Constructor for TestAnalogInputProviderImpl.</p>
     *
     * @param id   a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public TestAnalogInputProviderImpl(String id, String name) {
        super(id, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogInput create(AnalogInputConfig config) {
        TestAnalogInput input = new TestAnalogInput(this, config);
        if (this.context.registry().exists(input.id()))
            throw new IOAlreadyExistsException(config.id());
        input.initialize(this.context);
        this.context.registry().add(input);
        return input;
    }
}
