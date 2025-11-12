package com.pi4j.io.spi;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SpiConfig.java
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

import com.pi4j.config.ChannelConfig;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;

/**
 * <p>SpiConfig interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface SpiConfig extends ChannelConfig<SpiConfig>, IOConfig<SpiConfig> {
    /**
     * @deprecated use {@link #BUS_KEY} instead.
     * <p>
     * Constant <code>BAUD_KEY="baud"</code>
     */
    @Deprecated(forRemoval = true)
    String ADDRESS_KEY = "address";
    /**
     * Constant <code>BAUD_KEY="baud"</code>
     */
    String BAUD_KEY = "baud";
    /**
     * Constant <code>BUS_KEY="bus"</code>
     */
    String BUS_KEY = "bus";
    /**
     * Constant <code>MODE_KEY="mode"</code>
     */
    String MODE_KEY = "mode";
    /**
     * Constant <code>FLAGS_KEY="flags"</code>
     */
    String FLAGS_KEY = "flags";
    /**
     * Constant <code>WRITE_LSB_KEY="baud"</code>
     */
    String WRITE_LSB_KEY = "write_lsb";
    /**
     * Constant <code>READ_LSB_KEY="baud"</code>
     */
    String READ_LSB_KEY = "read_lsb";

    /**
     * <p>newBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.spi.SpiConfigBuilder} object.
     */
    static SpiConfigBuilder newBuilder(Context context) {
        return SpiConfigBuilder.newInstance(context);
    }

    /**
     * SPI Device Identifier
     * To be able to identify unique SPI devices, an identifier is available which is based on the bus and channel value.
     *
     * @return Unique SPI device identifier.
     */
    @Override
    default int getUniqueIdentifier() {
        return (bus().getBus() << 8) + channel();
    }

    /**
     * <p>baud.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer baud();

    /**
     * <p>getBaud.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    default Integer getBaud() {
        return baud();
    }

    /**
     * <p>ReadLsbFirst.</p>
     * In accordance with the flags parm, Read operations
     * 0 is  LSB bit shifted first, 1 MSB bit shifted first
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer readLsbFirst();

    /**
     * <p>getreadLsbfISRT.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    default Integer getReadLsbFirst() {
        return readLsbFirst();
    }


    /**
     * <p>WriteLsbFirst.</p>
     * In accordance with the flags parm, Write operations
     * 0 is  LSB bit shifted first, 1 MSB bit shifted first
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer writeLsbFirst();

    /**
     * <p>getreadLsbfISRT.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    default Integer getWriteLsbFirst() {
        return writeLsbFirst();
    }

    /**
     * <p>bus.</p>
     * <p>If the Bus value is configured, that SpiBus
     * value will be set in the flags {@link #flags()}   bit 'A' 8
     * </p>
     *
     * @return a {@link com.pi4j.io.spi.SpiBus} object.
     */
    SpiBus bus();

    /**
     * <p>getBus.</p>
     *
     * @return a {@link com.pi4j.io.spi.SpiBus} object.
     */
    default SpiBus getBus() {
        return bus();
    }

    /**
     * <p>busUserProvided.</p>
     *
     * @return a boolean.
     */
    boolean busUserProvided();

    /**
     * <p>getBusUserProvided.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    default boolean getBusUserProvided() {
        return busUserProvided();
    }

    /**
     * <p>writeLsbFirstserProvided.</p>
     *
     * @return a boolean.
     */
    boolean writeLsbFirstUserProvided();

    /**
     * <p>getWriteLsbFirstUserProvided.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    default boolean getWriteLsbFIrstUserProvided() {
        return writeLsbFirstUserProvided();
    }

    /**
     * <p>writeLsbFirstserProvided.</p>
     *
     * @return a boolean.
     */
    boolean readLsbFirstUserProvided();

    /**
     * <p>getReadLsbFirstUserProvided.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    default boolean getReadLsbFIrstUserProvided() {
        return readLsbFirstUserProvided();
    }


    /**
     * <p>mode.</p>
     * <p>If the Mode value is configured, that SpiMode
     * value will be set in the flags  {@link #mode()}  bit 'm m' 1:0
     * </p>
     *
     * @return a {@link com.pi4j.io.spi.SpiMode} object.
     */
    SpiMode mode();

    /**
     * <p>getMode.</p>
     *
     * @return a {@link com.pi4j.io.spi.SpiMode} object.
     */
    default SpiMode getMode() {
        return mode();
    }

    /**
     * <p>modeUserProvided.</p>
     *
     * @return a boolean.
     */
    boolean modeUserProvided();

    /**
     * <p>bgetModeUserProvided.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    default boolean getModeUserProvided() {
        return modeUserProvided();
    }

    /**
     * <p>flags.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    Long flags();

    /**
     * <p>getFlags.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    default Long getFlags() {
        return flags();
    }

    /**
     * <p>channel. (ALIAS for 'address')</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer channel();

    /**
     * <p>getFlags. (ALIAS for 'getAddress')</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    default Integer getChannel() {
        return channel();
    }

    /**
     * <p>chipSelect. (ALIAS for 'address')</p>
     *
     * @return a {@link com.pi4j.io.spi.SpiChipSelect} object.
     */
    SpiChipSelect chipSelect();

    /**
     * <p>getFlags. (ALIAS for 'getAddress')</p>
     *
     * @return a {@link com.pi4j.io.spi.SpiChipSelect} object.
     */
    default SpiChipSelect getChipSelect() {
        return chipSelect();
    }
}
