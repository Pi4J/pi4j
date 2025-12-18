package com.pi4j.test.provider.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  TestAnalogOutputProviderImpl.java
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
import com.pi4j.io.gpio.analog.AnalogOutput;
import com.pi4j.io.gpio.analog.AnalogOutputConfig;
import com.pi4j.io.gpio.analog.AnalogOutputProviderBase;
import com.pi4j.test.provider.TestAnalogOutput;
import com.pi4j.test.provider.TestAnalogOutputProvider;

/**
 * <p>TestAnalogOutputProviderImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestAnalogOutputProviderImpl extends AnalogOutputProviderBase implements TestAnalogOutputProvider {

    /**
     * <p>Constructor for TestAnalogOutputProviderImpl.</p>
     */
    public TestAnalogOutputProviderImpl() {
        super();
    }

    /**
     * <p>Constructor for TestAnalogOutputProviderImpl.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public TestAnalogOutputProviderImpl(String id) {
        super(id);
    }

    /**
     * <p>Constructor for TestAnalogOutputProviderImpl.</p>
     *
     * @param id   a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public TestAnalogOutputProviderImpl(String id, String name) {
        super(id, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalogOutput create(AnalogOutputConfig config) {
        TestAnalogOutput output = new TestAnalogOutput();
        if (this.context.registry().exists(output.id()))
            throw new IOAlreadyExistsException(config.id());
        output.initialize(this.context);
        this.context.registry().add(output);
        return output;
    }
}
