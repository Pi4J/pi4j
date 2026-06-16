package com.pi4j.io.gpio.analog;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  AnalogOutput.java
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
import com.pi4j.io.Output;
import com.pi4j.io.exception.IOBoundsException;
import com.pi4j.io.exception.IOIllegalValueException;

/**
 * <p>AnalogOutput interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface AnalogOutput extends Analog<AnalogOutput, AnalogOutputConfig, AnalogOutputProvider>, Output {

    /**
     * <p>newConfigBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutputConfigBuilder} object.
     */
    static AnalogOutputConfigBuilder newConfigBuilder(Context context){
        return AnalogOutputConfigBuilder.newInstance();
    }

    /**
     * <p>value.</p>
     *
     * @param value a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutput} object.
     * @throws com.pi4j.io.exception.IOIllegalValueException if any.
     * @throws com.pi4j.io.exception.IOBoundsException if any.
     */
    AnalogOutput value(Integer value) throws IOIllegalValueException, IOBoundsException;
    /**
     * <p>stepUp.</p>
     *
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutput} object.
     */
    AnalogOutput stepUp();
    /**
     * <p>stepDown.</p>
     *
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutput} object.
     */
    AnalogOutput stepDown();
    /**
     * <p>step.</p>
     *
     * @param value a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutput} object.
     * @throws com.pi4j.io.exception.IOIllegalValueException if any.
     * @throws com.pi4j.io.exception.IOBoundsException if any.
     */
    AnalogOutput step(Integer value) throws IOIllegalValueException, IOBoundsException;
    /**
     * <p>setValue.</p>
     *
     * @param value a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutput} object.
     * @throws com.pi4j.io.exception.IOIllegalValueException if any.
     * @throws com.pi4j.io.exception.IOBoundsException if any.
     */
    default AnalogOutput setValue(Integer value) throws IOIllegalValueException, IOBoundsException { return value(value); };
}
