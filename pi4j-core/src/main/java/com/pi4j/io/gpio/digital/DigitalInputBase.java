package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalInputBase.java
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

/**
 * Abstract base class for {@link DigitalInput} implementations, specializing {@link DigitalBase}
 * with the digital-input type parameters. Provider-specific subclasses extend this to supply the
 * actual hardware or expander read behaviour.
 */
public abstract class DigitalInputBase extends DigitalBase<DigitalInput, DigitalInputConfig, DigitalInputProvider> implements DigitalInput {
    /**
     * Creates a digital input bound to the given provider and configuration.
     *
     * @param provider the {@link DigitalInputProvider} responsible for this input's underlying I/O
     * @param config the configuration describing this input (pin, pull resistance, debounce, etc.)
     */
    public DigitalInputBase(DigitalInputProvider provider, DigitalInputConfig config){
        super(provider, config);
    }
}
