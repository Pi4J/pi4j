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
 * Configuration for a {@link DigitalOutput}, extending the common {@link DigitalConfig} with output-specific
 * settings: the state applied when the output is initialized and the state applied when Pi4J shuts down.
 * Instances are assembled with a {@link DigitalOutputConfigBuilder}.
 */
public interface DigitalOutputConfig extends DigitalConfig<DigitalOutputConfig> {
    /** Configuration property key for the shutdown state value. */
    String SHUTDOWN_STATE_KEY = "shutdown";
    /** Configuration property key for the initial state value. */
    String INITIAL_STATE_KEY = "initial";

    /**
     * Returns the state the output is driven to when Pi4J shuts down.
     *
     * @return the configured shutdown state, or {@code null} if none was set
     */
    DigitalState shutdownState();
    /**
     * Sets the state the output is driven to when Pi4J shuts down.
     *
     * @param state the shutdown state to apply
     * @return this configuration for method chaining
     */
    DigitalOutputConfig shutdownState(DigitalState state);
    /**
     * Bean-style accessor equivalent to {@link #shutdownState()}.
     *
     * @return the configured shutdown state, or {@code null} if none was set
     */
    default DigitalState getShutdownState(){
        return shutdownState();
    }
    /**
     * Bean-style mutator equivalent to {@link #shutdownState(DigitalState)}.
     *
     * @param state the shutdown state to apply
     */
    default void setShutdownState(DigitalState state){
        this.shutdownState(state);
    }

    /**
     * Returns the state the output is driven to immediately after it is initialized.
     *
     * @return the configured initial state, or {@code null} if none was set
     */
    DigitalState initialState();
    /**
     * Bean-style accessor equivalent to {@link #initialState()}.
     *
     * @return the configured initial state, or {@code null} if none was set
     */
    default DigitalState getInitialState(){
        return initialState();
    }

    /**
     * Creates a new {@link DigitalOutputConfigBuilder}.
     *
     * @param context the Pi4J runtime context
     * @return a new configuration builder instance
     * @deprecated As of version 5, please use {@link #newBuilder()} instead.
     */
    @Deprecated
    static DigitalOutputConfigBuilder newBuilder(Context context)  {
        return DigitalOutputConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new {@link DigitalOutputConfigBuilder}.
     *
     * @return a new configuration builder instance
     */
    static DigitalOutputConfigBuilder newBuilder()  {
        return DigitalOutputConfigBuilder.newInstance();
    }
}
