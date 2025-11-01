package com.pi4j.io.spi.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultSpiConfigBuilder.java
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
import com.pi4j.io.impl.IOPinConfigBuilderBase;
import com.pi4j.io.spi.*;

/**
 * <p>DefaultSpiConfigBuilder class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultSpiConfigBuilder
    extends IOPinConfigBuilderBase<SpiConfigBuilder, SpiConfig>
    implements SpiConfigBuilder {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultSpiConfigBuilder(Context context) {
        super(context);
    }

    /**
     * <p>newInstance.</p>
     *
     * @param context
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    public static SpiConfigBuilder newInstance(Context context) {
        return new DefaultSpiConfigBuilder(context);
    }

    @Override
    public SpiConfigBuilder readLsbFirst(Integer shift) {
        this.properties.put(SpiConfig.READ_LSB_KEY, shift.toString());
        return this;
    }

    @Override
    public SpiConfigBuilder writeLsbFirst(Integer shift) {
        this.properties.put(SpiConfig.WRITE_LSB_KEY, shift.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpiConfigBuilder baud(Integer rate) {
        this.properties.put(SpiConfig.BAUD_KEY, rate.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(forRemoval = true)
    public SpiConfigBuilder address(Integer address) {
        this.properties.put(SpiConfig.ADDRESS_KEY, address.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpiConfigBuilder bus(SpiBus bus) {
        this.properties.put(SpiConfig.BUS_KEY, Integer.toString(bus.getBus()));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpiConfigBuilder mode(SpiMode mode) {
        this.properties.put(SpiConfig.MODE_KEY, Integer.toString(mode.getMode()));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpiConfigBuilder flags(Long flags) {
        this.properties.put(SpiConfig.FLAGS_KEY, flags.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpiConfigBuilder channel(Integer channel) {
        this.properties.put(SpiConfig.CHANNEL_KEY, channel.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpiConfigBuilder chipSelect(SpiChipSelect chipSelect) {
        this.properties.put(SpiConfig.CHANNEL_KEY, Integer.toString(chipSelect.getChipSelect()));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpiConfig build() {
        SpiConfig config = new DefaultSpiConfig(this.properties);
        return config;
    }
}
