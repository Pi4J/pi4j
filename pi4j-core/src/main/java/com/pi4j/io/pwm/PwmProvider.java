package com.pi4j.io.pwm;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PwmProvider.java
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

import com.pi4j.provider.Provider;

/**
 * <p>PwmProvider interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface PwmProvider extends Provider<PwmProvider, Pwm, PwmConfig> {

    /**
     * <p>create.</p>
     *
     * @param builder a {@link com.pi4j.io.pwm.PwmConfigBuilder} object.
     * @param <T>     a T object.
     * @return a T object.
     */
    default <T extends Pwm> T create(PwmConfigBuilder builder) {
        return (T) create(builder.build());
    }

    /**
     * <p>create.</p>
     *
     * @param channel a {@link java.lang.Integer} object.
     * @param <T>     a T object.
     * @return a T object.
     */
    default <T extends Pwm> T create(Integer bus, Integer channel) {
        var config = Pwm.newConfigBuilder(context())
            .bus(bus)
            .channel(channel)
            .build();
        return (T) create(config);
    }

    /**
     * <p>create.</p>
     *
     * @param bus     a {@link java.lang.Integer} object.
     * @param channel a {@link java.lang.Integer} object.
     * @param id      a {@link java.lang.String} object.
     * @param <T>     a T object.
     * @return a T object.
     */
    default <T extends Pwm> T create(Integer bus, Integer channel, String id) {
        var config = Pwm.newConfigBuilder(context())
            .bus(bus)
            .channel(channel)
            .id(id)
            .build();
        return (T) create(config);
    }

    /**
     * <p>create.</p>
     *
     * @param bus     a {@link java.lang.Integer} object.
     * @param channel a {@link java.lang.Integer} object.
     * @param id      a {@link java.lang.String} object.
     * @param name    a {@link java.lang.String} object.
     * @param <T>     a T object.
     * @return a T object.
     */
    default <T extends Pwm> T create(Integer bus, Integer channel, String id, String name) {
        var config = Pwm.newConfigBuilder(context())
            .bus(bus)
            .channel(channel)
            .id(id)
            .name(name)
            .build();
        return (T) create(config);
    }

    /**
     * <p>create.</p>
     *
     * @param bus         a {@link java.lang.Integer} object.
     * @param channel     a {@link java.lang.Integer} object.
     * @param id          a {@link java.lang.String} object.
     * @param name        a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param <T>         a T object.
     * @return a T object.
     */
    default <T extends Pwm> T create(Integer bus, Integer channel, String id, String name, String description) {
        var config = Pwm.newConfigBuilder(context())
            .bus(bus)
            .channel(channel)
            .id(id)
            .name(name)
            .description(description)
            .build();
        return (T) create(config);
    }
}
