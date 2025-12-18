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
 * <p>DigitalInputProvider interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface DigitalInputProvider extends DigitalProvider<DigitalInputProvider, DigitalInput, DigitalInputConfig> {

    /**
     * <p>create.</p>
     *
     * @param builder a {@link com.pi4j.io.gpio.digital.DigitalInputConfigBuilder} object.
     * @param <T>     a T object.
     * @return a T object.
     */
    default <T extends DigitalInput> T create(DigitalInputConfigBuilder builder) {
        return (T) create(builder.build());
    }

    /**
     * <p>create.</p>
     *
     * @param bcm a {@link java.lang.Integer} object.
     * @param <T> a T object.
     * @return a T object.
     */
    default <T extends DigitalInput> T create(Integer bcm) {
        var config = DigitalInput.newConfigBuilder(context())
            .bcm(bcm)
            .build();
        return (T) create(config);
    }

    /**
     * <p>create.</p>
     *
     * @param bcm a {@link java.lang.Integer} object.
     * @param id  pin@link java.lang.String} object.
     * @param <T> a T object.
     * @return a T object.
     */
    default <T extends DigitalInput> T create(Integer bcm, String id) {
        var config = DigitalInput.newConfigBuilder(context())
            .bcm(bcm)
            .id(id)
            .build();
        return (T) create(config);
    }

    /**
     * <p>create.</p>
     *
     * @param bcm  a {@link java.lang.Integer} object.
     * @param id   a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @param <T>  a T object.
     * @return a T object.
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
     * <p>create.</p>
     *
     * @param bcm         a {@link java.lang.Integer} object.
     * @param id          a {@link java.lang.String} object.
     * @param name        a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param <T>         a T object.
     * @return a T object.
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
