package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalOutputConfig.java
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
 * <p>DigitalOutputConfig interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface DigitalOutputConfig extends DigitalConfig<DigitalOutputConfig> {
    /** Constant <code>SHUTDOWN_STATE_KEY="shutdown"</code> */
    String SHUTDOWN_STATE_KEY = "shutdown";
    /** Constant <code>INITIAL_STATE_KEY="initial"</code> */
    String INITIAL_STATE_KEY = "initial";

    /**
     * <p>shutdownState.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    DigitalState shutdownState();
    /**
     * <p>shutdownState.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputConfig} object.
     */
    DigitalOutputConfig shutdownState(DigitalState state);
    /**
     * <p>getShutdownState.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    default DigitalState getShutdownState(){
        return shutdownState();
    }
    /**
     * <p>setShutdownState.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    default void setShutdownState(DigitalState state){
        this.shutdownState(state);
    }

    /**
     * <p>initialState.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    DigitalState initialState();
    /**
     * <p>getInitialState.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    default DigitalState getInitialState(){
        return initialState();
    }

    /**
     * <p>newBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder} object.
     * @deprecated As of version 5, please use {@link #newBuilder()} instead.
     */
    @Deprecated
    static DigitalOutputConfigBuilder newBuilder(Context context)  {
        return DigitalOutputConfigBuilder.newInstance(context);
    }

    static DigitalOutputConfigBuilder newBuilder()  {
        return DigitalOutputConfigBuilder.newInstance();
    }
}
