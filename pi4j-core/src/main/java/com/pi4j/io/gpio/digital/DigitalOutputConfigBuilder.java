package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalOutputConfigBuilder.java
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
import com.pi4j.io.gpio.digital.impl.DefaultDigitalOutputConfigBuilder;

/**
 * Fluent builder for assembling a {@link DigitalOutputConfig}, adding output-specific settings (pin address,
 * initial state and shutdown state) on top of the shared {@link DigitalConfigBuilder} options.
 */
public interface DigitalOutputConfigBuilder extends DigitalConfigBuilder<DigitalOutputConfigBuilder, DigitalOutputConfig> {

    /**
     * Sets the BCM pin number the output controls.
     *
     * @param bcm the Broadcom GPIO pin number
     * @return this builder for method chaining
     */
    DigitalOutputConfigBuilder bcm(Integer bcm);

    /**
     * Sets the state the output is driven to when Pi4J shuts down.
     *
     * @param state the shutdown state to apply
     * @return this builder for method chaining
     */
    DigitalOutputConfigBuilder shutdown(DigitalState state);

    /**
     * Sets the state the output is driven to immediately after it is initialized.
     *
     * @param state the initial state to apply
     * @return this builder for method chaining
     */
    DigitalOutputConfigBuilder initial(DigitalState state);

    /**
     * Creates a new digital output config builder bound to the given Pi4J context.
     *
     * @param context the Pi4J runtime context
     * @return a new builder instance
     * @deprecated the context argument is no longer required; use {@link #newInstance()} instead
     */
    @Deprecated
    static DigitalOutputConfigBuilder newInstance(Context context) {
        return DefaultDigitalOutputConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new digital output config builder.
     *
     * @return a new builder instance
     */
    static DigitalOutputConfigBuilder newInstance() {
        return DefaultDigitalOutputConfigBuilder.newInstance();
    }

}
