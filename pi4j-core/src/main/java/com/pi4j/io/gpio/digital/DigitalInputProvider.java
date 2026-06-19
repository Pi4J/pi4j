package com.pi4j.io.gpio.digital;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalInputProvider.java
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
 * Provider contract for creating {@link DigitalInput} instances. Implementations bind digital inputs to a
 * specific platform or I/O expander, while the convenience {@code create} methods here build the necessary
 * {@link DigitalInputConfig} from simple parameters.
 */
public interface DigitalInputProvider extends DigitalProvider<DigitalInputProvider, DigitalInput, DigitalInputConfig> {

    /**
     * Creates a digital input from the configuration produced by the given builder.
     *
     * @param builder a configured {@link DigitalInputConfigBuilder}
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(DigitalInputConfigBuilder builder) {
        return (T) create(builder.build());
    }

    /**
     * Creates a digital input on the given BCM pin using default configuration.
     *
     * @param bcm the BCM (Broadcom) GPIO pin number
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(Integer bcm) {
        var config = DigitalInput.newConfigBuilder(context())
            .bcm(bcm)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital input on the given BCM pin with the given identifier.
     *
     * @param bcm the BCM (Broadcom) GPIO pin number
     * @param id the unique identifier to assign to the input
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(Integer bcm, String id) {
        var config = DigitalInput.newConfigBuilder(context())
            .bcm(bcm)
            .id(id)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital input on the given BCM pin with the given identifier and display name.
     *
     * @param bcm the BCM (Broadcom) GPIO pin number
     * @param id the unique identifier to assign to the input
     * @param name the human-readable name to assign to the input
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(Integer bcm, String id, String name) {
        var config = DigitalInput.newConfigBuilder(context())
            .bcm(bcm)
            .id(id)
            .name(name)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a digital input on the given BCM pin with the given identifier, display name, and description.
     *
     * @param bcm the BCM (Broadcom) GPIO pin number
     * @param id the unique identifier to assign to the input
     * @param name the human-readable name to assign to the input
     * @param description a free-form description of the input
     * @param <T> the concrete {@link DigitalInput} type returned by this provider
     * @return the newly created digital input instance
     */
    default <T extends DigitalInput> T create(Integer bcm, String id, String name, String description) {
        var config = DigitalInput.newConfigBuilder(context())
            .bcm(bcm)
            .id(id)
            .name(name)
            .description(description)
            .build();
        return (T) create(config);
    }
}
