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
 * Immutable configuration for an {@link Spi} device, describing the bus, channel/chip-select, clock
 * mode, baud rate, bit ordering, and provider flags. Instances are produced by a {@link SpiConfigBuilder}
 * and consumed by a {@link SpiProvider} when creating an SPI device.
 */
public interface SpiConfig extends ChannelConfig<SpiConfig>, IOConfig<SpiConfig> {
    /**
     * Configuration property key for the legacy SPI address.
     *
     * @deprecated use {@link #BUS_KEY} instead.
     */
    @Deprecated(forRemoval = true)
    String ADDRESS_KEY = "address";
    /** Configuration property key for the SPI baud (clock) rate. */
    String BAUD_KEY = "baud";
    /** Configuration property key for the SPI bus number. */
    String BUS_KEY = "bus";
    /** Configuration property key for the SPI clock mode. */
    String MODE_KEY = "mode";
    /** Configuration property key for the raw provider flags value. */
    String FLAGS_KEY = "flags";
    /** Configuration property key for the write bit-order (LSB-first) setting. */
    String WRITE_LSB_KEY = "write_lsb";
    /** Configuration property key for the read bit-order (LSB-first) setting. */
    String READ_LSB_KEY = "read_lsb";

    /**
     * Creates a new {@link SpiConfigBuilder}.
     *
     * @param context the Pi4J runtime context
     * @return a new SPI configuration builder instance
     * @deprecated As of version 5, please use {@link #newBuilder()} instead.
     */
    @Deprecated
    static SpiConfigBuilder newBuilder(Context context) {
        return SpiConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new {@link SpiConfigBuilder}.
     *
     * @return a new SPI configuration builder instance
     */
    static SpiConfigBuilder newBuilder() {
        return SpiConfigBuilder.newInstance();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The SPI identifier is derived from the bus and channel: the bus number occupies the high byte
     * and the channel the low byte, so that each (bus, channel) pair maps to a distinct value.
     *
     * @return a unique identifier combining the bus number and channel
     */
    @Override
    default int getUniqueIdentifier() {
        return (bus().getBus() << 8) + channel();
    }

    /**
     * Returns the configured SPI clock (baud) rate in Hz.
     *
     * @return the clock rate in Hz
     */
    Integer baud();

    /**
     * Returns the configured SPI clock (baud) rate in Hz.
     *
     * @return the clock rate in Hz
     */
    default Integer getBaud() {
        return baud();
    }

    /**
     * Returns the bit order used for read operations: {@code 0} shifts the least significant bit first,
     * {@code 1} shifts the most significant bit first.
     *
     * @return the read bit-order setting
     */
    Integer readLsbFirst();

    /**
     * Returns the bit order used for read operations: {@code 0} shifts the least significant bit first,
     * {@code 1} shifts the most significant bit first.
     *
     * @return the read bit-order setting
     */
    default Integer getReadLsbFirst() {
        return readLsbFirst();
    }


    /**
     * Returns the bit order used for write operations: {@code 0} shifts the least significant bit first,
     * {@code 1} shifts the most significant bit first.
     *
     * @return the write bit-order setting
     */
    Integer writeLsbFirst();

    /**
     * Returns the bit order used for write operations: {@code 0} shifts the least significant bit first,
     * {@code 1} shifts the most significant bit first.
     *
     * @return the write bit-order setting
     */
    default Integer getWriteLsbFirst() {
        return writeLsbFirst();
    }

    /**
     * Returns the configured SPI bus this device communicates over.
     *
     * @return the {@link SpiBus} for this device
     */
    SpiBus bus();

    /**
     * Returns the configured SPI bus this device communicates over.
     *
     * @return the {@link SpiBus} for this device
     */
    default SpiBus getBus() {
        return bus();
    }

    /**
     * Indicates whether the bus value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the bus value, otherwise {@code false}
     */
    boolean busUserProvided();

    /**
     * Indicates whether the bus value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the bus value, otherwise {@code false}
     */
    default boolean getBusUserProvided() {
        return busUserProvided();
    }

    /**
     * Indicates whether the write bit-order value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the write bit-order value, otherwise {@code false}
     */
    boolean writeLsbFirstUserProvided();

    /**
     * Indicates whether the write bit-order value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the write bit-order value, otherwise {@code false}
     */
    default boolean getWriteLsbFIrstUserProvided() {
        return writeLsbFirstUserProvided();
    }

    /**
     * Indicates whether the read bit-order value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the read bit-order value, otherwise {@code false}
     */
    boolean readLsbFirstUserProvided();

    /**
     * Indicates whether the read bit-order value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the read bit-order value, otherwise {@code false}
     */
    default boolean getReadLsbFIrstUserProvided() {
        return readLsbFirstUserProvided();
    }


    /**
     * Returns the configured SPI clock mode (clock polarity and phase).
     *
     * @return the {@link SpiMode} for this device
     */
    SpiMode mode();

    /**
     * Returns the configured SPI clock mode (clock polarity and phase).
     *
     * @return the {@link SpiMode} for this device
     */
    default SpiMode getMode() {
        return mode();
    }

    /**
     * Indicates whether the mode value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the mode value, otherwise {@code false}
     */
    boolean modeUserProvided();

    /**
     * Indicates whether the mode value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the mode value, otherwise {@code false}
     */
    default boolean getModeUserProvided() {
        return modeUserProvided();
    }

    /**
     * Returns the raw provider flags value that encodes mode, bus, and bit-order settings into the
     * bit layout expected by the underlying SPI driver.
     *
     * @return the encoded provider flags
     */
    Long flags();

    /**
     * Returns the raw provider flags value that encodes mode, bus, and bit-order settings into the
     * bit layout expected by the underlying SPI driver.
     *
     * @return the encoded provider flags
     */
    default Long getFlags() {
        return flags();
    }

    /**
     * Returns the SPI channel (chip-select line) this device is addressed on. This is an alias for the
     * underlying channel/address value.
     *
     * @return the channel number
     */
    Integer channel();

    /**
     * Returns the SPI channel (chip-select line) this device is addressed on.
     *
     * @return the channel number
     */
    default Integer getChannel() {
        return channel();
    }

    /**
     * Returns the chip-select line for this device, derived from the configured channel.
     *
     * @return the {@link SpiChipSelect} corresponding to the configured channel
     */
    SpiChipSelect chipSelect();

    /**
     * Returns the chip-select line for this device, derived from the configured channel.
     *
     * @return the {@link SpiChipSelect} corresponding to the configured channel
     */
    default SpiChipSelect getChipSelect() {
        return chipSelect();
    }
}
