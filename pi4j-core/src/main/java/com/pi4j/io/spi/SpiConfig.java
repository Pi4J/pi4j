package com.pi4j.io.spi;

import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;

/**
 * Immutable configuration for an {@link Spi} device, describing the bus, channel/chip-select, clock
 * mode, baud rate, bit ordering, and provider flags. Instances are produced by a {@link SpiConfigBuilder}
 * and consumed by the {@link Context} when creating an SPI device.
 *
 * @param id the configuration identifier
 * @param name the configuration name
 * @param description the configuration description
 * @param bus the configured SPI bus this device communicates over
 * @param channel the SPI channel (chip-select line) this device is addressed on
 * @param mode the configured SPI clock mode (clock polarity and phase)
 * @param baud the configured SPI clock (baud) rate in Hz
 * @param readLsbFirst the bit order used for read operations (0 = MSB first, 1 = LSB first)
 * @param writeLsbFirst the bit order used for write operations (0 = MSB first, 1 = LSB first)
 * @param flags the raw provider flags value that encodes mode, bus, and bit-order settings
 * @param busUserProvided indicates whether the bus value was explicitly supplied by the user
 * @param modeUserProvided indicates whether the mode value was explicitly supplied by the user
 * @param readLsbFirstUserProvided indicates whether the read bit-order value was explicitly supplied by the user
 * @param writeLsbFirstUserProvided indicates whether the write bit-order value was explicitly supplied by the user
 */
public record SpiConfig(
    @Override String id,
    @Override String name,
    @Override String description,
    SpiBus bus,
    Integer channel,
    SpiMode mode,
    Integer baud,
    Integer readLsbFirst,
    Integer writeLsbFirst,
    Long flags,
    boolean busUserProvided,
    boolean modeUserProvided,
    boolean readLsbFirstUserProvided,
    boolean writeLsbFirstUserProvided
) implements IOConfig {


    /**
     * Creates a new {@link SpiConfigBuilder}.
     *
     * @param context the Pi4J runtime context
     * @return a new SPI configuration builder instance
     * @deprecated As of version 5, please use Spi.newConfigBuilder() instead.
     */
    @Deprecated(since = "5.0")
    public static SpiConfigBuilder newBuilder(Context context) {
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
    public int getUniqueIdentifier() {
        return ((bus == null ? 0 : bus.getBus()) << 8) + (channel == null ? 0 : channel);
    }

    /**
     * Returns the configured SPI clock (baud) rate in Hz.
     *
     * @return the clock rate in Hz
     */
    public Integer getBaud() {
        return baud;
    }

    /**
     * Returns the bit order used for read operations: {@code 0} shifts the least significant bit first,
     * {@code 1} shifts the most significant bit first.
     *
     * @return the read bit-order setting
     */
    public Integer getReadLsbFirst() {
        return readLsbFirst;
    }

    /**
     * Returns the bit order used for write operations: {@code 0} shifts the least significant bit first,
     * {@code 1} shifts the most significant bit first.
     *
     * @return the write bit-order setting
     */
    public Integer getWriteLsbFirst() {
        return writeLsbFirst;
    }

    /**
     * Returns the configured SPI bus this device communicates over.
     *
     * @return the {@link SpiBus} for this device
     */
    public SpiBus getBus() {
        return bus;
    }

    /**
     * Indicates whether the bus value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the bus value, otherwise {@code false}
     */
    public boolean getBusUserProvided() {
        return busUserProvided;
    }

    /**
     * Indicates whether the write bit-order value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the write bit-order value, otherwise {@code false}
     */
    public boolean getWriteLsbFIrstUserProvided() {
        return writeLsbFirstUserProvided;
    }

    /**
     * Indicates whether the read bit-order value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the read bit-order value, otherwise {@code false}
     */
    public boolean getReadLsbFIrstUserProvided() {
        return readLsbFirstUserProvided;
    }

    /**
     * Returns the configured SPI clock mode (clock polarity and phase).
     *
     * @return the {@link SpiMode} for this device
     */
    public SpiMode getMode() {
        return mode;
    }

    /**
     * Indicates whether the mode value was explicitly supplied by the user rather than defaulted.
     *
     * @return {@code true} if the user provided the mode value, otherwise {@code false}
     */
    public boolean getModeUserProvided() {
        return modeUserProvided;
    }

    /**
     * Returns the raw provider flags value that encodes mode, bus, and bit-order settings into the
     * bit layout expected by the underlying SPI driver.
     *
     * @return the encoded provider flags
     */
    public Long getFlags() {
        return flags;
    }

    /**
     * Returns the SPI channel (chip-select line) this device is addressed on.
     *
     * @return the channel number
     */
    public Integer getChannel() {
        return channel;
    }

    /**
     * Returns the chip-select line for this device, derived from the configured channel.
     *
     * @return the {@link SpiChipSelect} corresponding to the configured channel
     */
    public SpiChipSelect chipSelect() {
        return channel == null ? null : SpiChipSelect.getByNumber(channel);
    }

    /**
     * Returns the chip-select line for this device, derived from the configured channel.
     *
     * @return the {@link SpiChipSelect} corresponding to the configured channel
     */
    public SpiChipSelect getChipSelect() {
        return chipSelect();
    }

    @Override
    public void validate() {
        if (id == null) {
            throw new IllegalStateException("ID cannot be null");
        }
    }
}
