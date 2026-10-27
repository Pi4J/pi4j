package com.pi4j.io.spi;

import com.pi4j.context.Context;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.util.StringUtil;

/**
 * Fluent builder for assembling an {@link SpiConfig}. Set the desired bus, channel/chip-select,
 * clock mode, baud rate, and bit-ordering, then call {@code build()} to obtain an immutable configuration.
 */
public class SpiConfigBuilder extends IOConfigBuilder<SpiConfigBuilder, SpiConfig> {

    private SpiBus bus = Spi.DEFAULT_BUS;
    private boolean busUserProvided = false;
    private Integer channel = Spi.DEFAULT_CHANNEL;
    private SpiMode mode = Spi.DEFAULT_MODE;
    private boolean modeUserProvided = false;
    private Integer baud = Spi.DEFAULT_BAUD;
    private Integer readLsbFirst = 0;
    private boolean readLsbFirstUserProvided = false;
    private Integer writeLsbFirst = 0;
    private boolean writeLsbFirstUserProvided = false;
    private Long flags = null;

    /**
     * Default constructor for creating a new SPI configuration builder.
     */
    public SpiConfigBuilder() {
    }

    /**
     * Creates a new {@code SpiConfigBuilder} instance.
     *
     * @param context the Pi4J runtime context
     * @return a new builder instance
     * @deprecated use Spi.newConfigBuilder() instead.
     */
    @Deprecated(since = "5.0")
    public static SpiConfigBuilder newInstance(Context context) {
        return new SpiConfigBuilder();
    }

    /**
     * Internal method to create a new {@code SpiConfigBuilder} instance; please use Spi.newConfigBuilder() instead.
     *
     * @return a new builder instance
     */
    public static SpiConfigBuilder newInstance() {
        return new SpiConfigBuilder();
    }

    /**
     * Sets the bit order for read operations.
     *
     * @param shift {@code 0} to shift the least significant bit first, {@code 1} to shift the most significant bit first
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder readLsbFirst(Integer shift) {
        this.readLsbFirst = shift;
        this.readLsbFirstUserProvided = (shift != null);
        return this;
    }

    /**
     * Sets the bit order for write operations.
     *
     * @param shift {@code 0} to shift the least significant bit first, {@code 1} to shift the most significant bit first
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder writeLsbFirst(Integer shift) {
        this.writeLsbFirst = shift;
        this.writeLsbFirstUserProvided = (shift != null);
        return this;
    }

    /**
     * Sets the SPI clock (baud) rate.
     *
     * @param rate the clock rate in Hz (typically 500&nbsp;kHz to 32&nbsp;MHz)
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder baud(Integer rate) {
        this.baud = rate;
        return this;
    }

    /**
     * Sets the SPI bus this device communicates over.
     *
     * @param bus the {@link SpiBus} to use
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder bus(SpiBus bus) {
        this.bus = bus;
        this.busUserProvided = (bus != null);
        return this;
    }

    /**
     * Sets the SPI bus by its numeric index.
     *
     * @param bus the SPI bus number, resolved via {@link SpiBus#getByNumber(int)}
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder bus(Integer bus) {
        return bus(bus == null ? null : SpiBus.getByNumber(bus));
    }

    /**
     * Sets the SPI clock mode (clock polarity and phase).
     *
     * @param mode the {@link SpiMode} to use
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder mode(SpiMode mode) {
        this.mode = mode;
        this.modeUserProvided = (mode != null);
        return this;
    }

    /**
     * Sets the SPI clock mode by its numeric value.
     *
     * @param mode the mode number (0&ndash;3), resolved via {@link SpiMode#getByNumber(int)}
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder mode(Integer mode) {
        return mode(mode == null ? null : SpiMode.getByNumber(mode));
    }

    /**
     * Sets the raw provider flags value directly, overriding the encoding derived from mode, bus, and
     * bit-order settings.
     *
     * @param flags the encoded provider flags
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder flags(Long flags) {
        this.flags = flags;
        return this;
    }

    /**
     * Sets the SPI channel (chip-select line) this device is addressed on. This is an alias for the
     * underlying channel/address value.
     *
     * @param channel the channel number
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder channel(Integer channel) {
        this.channel = channel;
        return this;
    }

    /**
     * Sets the chip-select line for this device.
     *
     * @param chipSelect the {@link SpiChipSelect} to use
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder chipSelect(SpiChipSelect chipSelect) {
        return channel(chipSelect == null ? null : chipSelect.getChipSelect());
    }

    /**
     * Sets the target pin using its legacy "address" name.
     *
     * @param address the BCM GPIO pin number
     * @return this builder instance for method chaining
     * @deprecated use {@link #bcm(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public SpiConfigBuilder address(Integer address) {
        return bcm(address);
    }

    /**
     * Sets the Broadcom (BCM) GPIO pin number this I/O is bound to.
     *
     * @param bcm the BCM GPIO pin number
     * @return this builder instance for method chaining
     */
    public SpiConfigBuilder bcm(Integer bcm) {
        return channel(bcm);
    }

    @Override
    public SpiConfig build() {
        SpiBus resolvedBus = this.bus != null ? this.bus : Spi.DEFAULT_BUS;
        Integer resolvedChannel = this.channel != null ? this.channel : Spi.DEFAULT_CHANNEL;
        String defaultId = "SPI-" + resolvedBus.getBus() + "." + resolvedChannel;
        String id = StringUtil.setIfNullOrEmpty(this.id, defaultId, true);
        String name = StringUtil.setIfNullOrEmpty(this.name, id, true);
        String description = StringUtil.setIfNullOrEmpty(this.description, id, true);

        return new SpiConfig(
            id,
            name,
            description,
            resolvedBus,
            resolvedChannel,
            this.mode != null ? this.mode : Spi.DEFAULT_MODE,
            this.baud != null ? this.baud : Spi.DEFAULT_BAUD,
            this.readLsbFirst != null ? this.readLsbFirst : 0,
            this.writeLsbFirst != null ? this.writeLsbFirst : 0,
            this.flags,
            this.busUserProvided,
            this.modeUserProvided,
            this.readLsbFirstUserProvided,
            this.writeLsbFirstUserProvided
        );
    }
}
