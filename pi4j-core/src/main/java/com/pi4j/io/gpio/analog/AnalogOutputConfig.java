package com.pi4j.io.gpio.analog;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  AnalogOutputConfig.java
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

/**
 * <p>AnalogOutputConfig interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface AnalogOutputConfig
        extends AnalogConfig<AnalogOutputConfig> {

    /** Constant <code>SHUTDOWN_VALUE_KEY="shutdown"</code> */
    String SHUTDOWN_VALUE_KEY = "shutdown";
    /** Constant <code>INITIAL_VALUE_KEY="initial"</code> */
    String INITIAL_VALUE_KEY = "initial";
    /** Constant <code>STEP_VALUE_KEY="step"</code> */
    String STEP_VALUE_KEY = "step";

    /**
     * <p>shutdownValue.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer shutdownValue();
    /**
     * <p>shutdownValue.</p>
     *
     * @param value a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutputConfig} object.
     */
    AnalogOutputConfig shutdownValue(Integer value);
    /**
     * <p>getShutdownValue.</p>
     *
     * @return a {@link java.lang.Number} object.
     */
    default Number getShutdownValue() {
        return this.shutdownValue();
    }
    /**
     * <p>setShutdownValue.</p>
     *
     * @param value a {@link java.lang.Integer} object.
     */
    default void setShutdownValue(Integer value) {
        this.shutdownValue(value);
    }

    /**
     * <p>stepValue.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer stepValue();
    /**
     * <p>stepValue.</p>
     *
     * @param value a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutputConfig} object.
     */
    AnalogOutputConfig stepValue(Integer value);
    /**
     * <p>getStepValue.</p>
     *
     * @return a {@link java.lang.Number} object.
     */
    default Number getStepValue() {
        return this.stepValue();
    }
    /**
     * <p>setStepValue.</p>
     *
     * @param value a {@link java.lang.Integer} object.
     */
    default void setStepValue(Integer value) {
        this.setStepValue(value);
    }

    /**
     * <p>initialValue.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer initialValue();
    /**
     * <p>getInitialValue.</p>
     *
     * @return a {@link java.lang.Number} object.
     */
    default Number getInitialValue() {
        return this.initialValue();
    }


    /**
     * <p>newBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.gpio.analog.AnalogOutputConfigBuilder} object.
     */
    @Deprecated
    static AnalogOutputConfigBuilder newBuilder(Context context)  {
        return AnalogOutputConfigBuilder.newInstance(context);
    }

    static AnalogOutputConfigBuilder newBuilder()  {
        return AnalogOutputConfigBuilder.newInstance();
    }
}
