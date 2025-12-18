package com.pi4j.test.provider;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  TestAnalogOutput.java
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

import com.pi4j.common.Metadata;
import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.binding.AnalogBinding;
import com.pi4j.io.exception.IOBoundsException;
import com.pi4j.io.exception.IOIllegalValueException;
import com.pi4j.io.gpio.analog.AnalogOutput;
import com.pi4j.io.gpio.analog.AnalogOutputConfig;
import com.pi4j.io.gpio.analog.AnalogOutputProvider;
import com.pi4j.io.gpio.analog.AnalogValueChangeListener;

/**
 * <p>TestAnalogOutput class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestAnalogOutput implements AnalogOutput {

    /** {@inheritDoc} */
    @Override
    public AnalogOutput value(Integer value) throws IOIllegalValueException, IOBoundsException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput stepUp() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput stepDown() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput step(Integer value) throws IOIllegalValueException, IOBoundsException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Integer value() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput addListener(AnalogValueChangeListener... listener) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput removeListener(AnalogValueChangeListener... listener) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput bind(AnalogBinding... binding) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput unbind(AnalogBinding... binding) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutputConfig config() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput name(String name) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutput description(String description) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnalogOutputProvider provider() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String id() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String name() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String description() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Metadata metadata() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Object initialize(Context context) throws InitializeException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Object shutdownInternal(Context context) throws ShutdownException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
    }
}
