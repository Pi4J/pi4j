package com.pi4j.io.spi;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SpiConfigBuilder.java
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
import com.pi4j.io.IOBcmConfigBuilder;
import com.pi4j.io.spi.impl.DefaultSpiConfigBuilder;

/**
 * <p>SpiConfigBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface SpiConfigBuilder extends
    IOBcmConfigBuilder<SpiConfigBuilder, SpiConfig> {
    /**
     * <p>newInstance.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    static SpiConfigBuilder newInstance(Context context) {
        return DefaultSpiConfigBuilder.newInstance(context);
    }

    /**
     * <p>readLsbFirst.</p>
     *
     * @param shift a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    SpiConfigBuilder readLsbFirst(Integer shift);

    /**
     * <p>writeLsbFirst.</p>
     *
     * @param shift a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    SpiConfigBuilder writeLsbFirst(Integer shift);


    /**
     * <p>baud.</p>
     *
     * @param rate a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    SpiConfigBuilder baud(Integer rate);


    /**
     * <p>bus.</p>
     * <p>If the Bus value is configured, that SpiBus
     * value will be set in the flags {@link #flags(Long)}   bit 'A' 8
     * </p>
     *
     * @param bus a {@link com.pi4j.io.spi.SpiBus} object.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    SpiConfigBuilder bus(SpiBus bus);

    /**
     * <p>bus.</p>
     *
     * @param bus a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    default SpiConfigBuilder bus(Integer bus) {
        return bus(SpiBus.getByNumber(bus));
    }

    /**
     * <p>mode.</p>
     * <p>If the Mode value is configured, that SpiMode
     * value will be set in the flags  {@link #flags(Long)}  bit 'm m' 1:0
     * </p>
     *
     * @param mode a {@link com.pi4j.io.spi.SpiMode} object.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    SpiConfigBuilder mode(SpiMode mode);

    /**
     * <p>mode.</p>
     *
     * @param mode a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    default SpiConfigBuilder mode(Integer mode) {
        return mode(SpiMode.getByNumber(mode));
    }

    /**
     * <p>flags.</p>
     *
     * @param flags a {@link java.lang.Long} value.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    SpiConfigBuilder flags(Long flags);

    /**
     * <p>channel. (ALIAS for 'address')</p>
     *
     * @param channel a {@link java.lang.Integer} value.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    SpiConfigBuilder channel(Integer channel);

    /**
     * <p>chipSelect. (ALIAS for 'address')</p>
     *
     * @param chipSelect a {@link com.pi4j.io.spi.SpiChipSelect} value.
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    SpiConfigBuilder chipSelect(SpiChipSelect chipSelect);
}
