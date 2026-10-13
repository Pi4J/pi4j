package com.pi4j.io.spi;

import com.pi4j.context.Context;
import com.pi4j.io.spi.impl.DefaultSpiConfigBuilder;

/**
 * Fluent builder for assembling an {@link SpiConfig}. Set the desired bus, channel/chip-select,
 * clock mode, baud rate, and bit-ordering, then call {@code build()} (inherited from
 * {@link SpiConfigBuilder}) to obtain an immutable configuration for use with a {@link SpiProvider}.
 */
public interface SpiConfigBuilder extends
    com.pi4j.io.IOConfigBuilder<SpiConfigBuilder, SpiConfig>, com.pi4j.config.ConfigBuilder<SpiConfigBuilder, SpiConfig> {
    /**
     * Creates a new {@code SpiConfigBuilder} instance.
     *
     * @param context the Pi4J runtime context
     * @return a new builder instance
     * @deprecated use Spi.newConfigBuilder() instead.
     */
    @Deprecated(since="5.0")
    static SpiConfigBuilder newInstance(Context context) {
        return DefaultSpiConfigBuilder.newInstance(context);
    }

    /**
     * Internal method to create a new {@code SpiConfigBuilder} instance; please use Spi.newConfigBuilder() instead.
     *
     * @return a new builder instance
     */
    static SpiConfigBuilder newInstance() {
        return DefaultSpiConfigBuilder.newInstance();
    }

    /**
     * Sets the bit order for read operations.
     *
     * @param shift {@code 0} to shift the least significant bit first, {@code 1} to shift the most significant bit first
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder readLsbFirst(Integer shift);

    /**
     * Sets the bit order for write operations.
     *
     * @param shift {@code 0} to shift the least significant bit first, {@code 1} to shift the most significant bit first
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder writeLsbFirst(Integer shift);


    /**
     * Sets the SPI clock (baud) rate.
     *
     * @param rate the clock rate in Hz (typically 500&nbsp;kHz to 32&nbsp;MHz)
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder baud(Integer rate);


    /**
     * Sets the SPI bus this device communicates over.
     *
     * @param bus the {@link SpiBus} to use
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder bus(SpiBus bus);

    /**
     * Sets the SPI bus by its numeric index.
     *
     * @param bus the SPI bus number, resolved via {@link SpiBus#getByNumber(int)}
     * @return this builder instance for method chaining
     */
    default SpiConfigBuilder bus(Integer bus) {
        return bus(SpiBus.getByNumber(bus));
    }

    /**
     * Sets the SPI clock mode (clock polarity and phase).
     *
     * @param mode the {@link SpiMode} to use
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder mode(SpiMode mode);

    /**
     * Sets the SPI clock mode by its numeric value.
     *
     * @param mode the mode number (0&ndash;3), resolved via {@link SpiMode#getByNumber(int)}
     * @return this builder instance for method chaining
     */
    default SpiConfigBuilder mode(Integer mode) {
        return mode(SpiMode.getByNumber(mode));
    }

    /**
     * Sets the raw provider flags value directly, overriding the encoding derived from mode, bus, and
     * bit-order settings.
     *
     * @param flags the encoded provider flags
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder flags(Long flags);

    /**
     * Sets the SPI channel (chip-select line) this device is addressed on. This is an alias for the
     * underlying channel/address value.
     *
     * @param channel the channel number
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder channel(Integer channel);

    /**
     * Sets the chip-select line for this device.
     *
     * @param chipSelect the {@link SpiChipSelect} to use
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder chipSelect(SpiChipSelect chipSelect);

    /**
     * Sets the target pin using its legacy "address" name.
     *
     * @param address the BCM GPIO pin number
     * @return this builder instance for method chaining
     * @deprecated use {@link #bcm(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    default SpiConfigBuilder address(Integer address) {
        return bcm(address);
    }

    /**
     * Sets the Broadcom (BCM) GPIO pin number this I/O is bound to.
     *
     * @param bcm the BCM GPIO pin number
     * @return this builder instance for method chaining
     */
    SpiConfigBuilder bcm(Integer bcm);
}
