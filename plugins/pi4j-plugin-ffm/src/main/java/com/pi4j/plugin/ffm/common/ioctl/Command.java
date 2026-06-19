package com.pi4j.plugin.ffm.common.ioctl;

/**
 * Provides the {@code ioctl} request codes used across the FFM serial, GPIO, SPI and I2C backends.
 * Most values are delegated to {@link IoctlMagic} (which computes them from the kernel encoding
 * macros); a few well-known fixed codes are returned directly. These codes are passed as the
 * request argument to the native {@code ioctl} handles in {@link IoctlContext}.
 */
public final class Command {

    /**
     * Forbids creating an instance of this class.
     */
    private Command() {
    }

    /*
    Serial commands
     */

    /**
     * Returns the {@code TCSETS2} request code used to apply a {@code struct termios2} terminal
     * configuration to a serial device.
     *
     * @return the {@code TCSETS2} ioctl request code
     */
    public static long getTermiosSet() {
        return IoctlMagic.TCSETS2;
    }

    /**
     * Returns the {@code TCGETS2} request code used to read the current {@code struct termios2}
     * terminal configuration of a serial device.
     *
     * @return the {@code TCGETS2} ioctl request code
     */
    public static long getTermiosGet() {
        return IoctlMagic.TCGETS2;
    }

    /**
     * Returns the {@code FIONREAD} request code ({@code 0x541B}) used to query the number of bytes
     * currently available to read from a device.
     *
     * @return the {@code FIONREAD} ioctl request code
     */
    public static long getFIONREAD() {
        return 0x541B;
    }

    /*
    GPIO commands
     */

    /**
     * Returns the {@code GPIO_GET_CHIPINFO_IOCTL} request code used to query a GPIO chip's name,
     * label and line count.
     *
     * @return the {@code GPIO_GET_CHIPINFO_IOCTL} request code
     */
    public static long getGpioGetChipInfoIoctl() {
        return IoctlMagic.GPIO_GET_CHIPINFO_IOCTL;
    }

    /**
     * Returns the {@code GPIO_V2_GET_LINEINFO_IOCTL} request code used to query the v2 line info
     * (flags, name, consumer) for a single GPIO line.
     *
     * @return the {@code GPIO_V2_GET_LINEINFO_IOCTL} request code
     */
    public static long getGpioV2GetLineInfoIoctl() {
        return IoctlMagic.GPIO_V2_GET_LINEINFO_IOCTL;
    }

    /**
     * Returns the {@code GPIO_V2_GET_LINE_IOCTL} request code used to request one or more GPIO
     * lines and obtain a line-request file descriptor.
     *
     * @return the {@code GPIO_V2_GET_LINE_IOCTL} request code
     */
    public static long getGpioV2GetLineIoctl() {
        return IoctlMagic.GPIO_V2_GET_LINE_IOCTL;
    }

    /**
     * Returns the {@code GPIO_V2_LINE_GET_VALUES_IOCTL} request code used to read the current
     * logic values of requested GPIO lines.
     *
     * @return the {@code GPIO_V2_LINE_GET_VALUES_IOCTL} request code
     */
    public static long getGpioV2GetValuesIoctl() {
        return IoctlMagic.GPIO_V2_LINE_GET_VALUES_IOCTL;
    }

    /**
     * Returns the {@code GPIO_V2_LINE_SET_VALUES_IOCTL} request code used to drive the output
     * values of requested GPIO lines.
     *
     * @return the {@code GPIO_V2_LINE_SET_VALUES_IOCTL} request code
     */
    public static long getGpioV2SetValuesIoctl() {
        return IoctlMagic.GPIO_V2_LINE_SET_VALUES_IOCTL;
    }

    /*
    SPI Commands
     */

    /**
     * Returns the {@code SPI_IOC_MESSAGE(n)} request code used to execute {@code n} chained
     * {@code spi_ioc_transfer} segments in a single full-duplex SPI transaction. The code encodes
     * the total transfer buffer size and therefore depends on {@code n}.
     *
     * @param n the number of {@code spi_ioc_transfer} structures in the message
     * @return the {@code SPI_IOC_MESSAGE} request code for {@code n} transfers
     */
    public static long getSpiIocMessage(int n) {
        return IoctlMagic.SPI_IOC_MESSAGE(n);
    }

    /**
     * Returns the {@code SPI_IOC_RD_MODE} request code used to read the SPI mode (clock polarity
     * and phase and related flags) as a single byte.
     *
     * @return the {@code SPI_IOC_RD_MODE} request code
     */
    public static long getSpiIocRdMode() {
        return IoctlMagic.SPI_IOC_RD_MODE;
    }

    /**
     * Returns the {@code SPI_IOC_WR_MODE} request code used to set the SPI mode as a single byte.
     *
     * @return the {@code SPI_IOC_WR_MODE} request code
     */
    public static long getSpiIocWrMode() {
        return IoctlMagic.SPI_IOC_WR_MODE;
    }

    /**
     * Returns the {@code SPI_IOC_RD_BITS_PER_WORD} request code used to read the device's bits-per-word setting.
     *
     * @return the {@code SPI_IOC_RD_BITS_PER_WORD} request code
     */
    public static long getSpiIocRdBitsPerWord() {
        return IoctlMagic.SPI_IOC_RD_BITS_PER_WORD;
    }

    /**
     * Returns the {@code SPI_IOC_WR_BITS_PER_WORD} request code used to set the device's bits-per-word setting.
     *
     * @return the {@code SPI_IOC_WR_BITS_PER_WORD} request code
     */
    public static long getSpiIocWrBitsPerWord() {
        return IoctlMagic.SPI_IOC_WR_BITS_PER_WORD;
    }

    /**
     * Returns the {@code SPI_IOC_RD_MAX_SPEED_HZ} request code used to read the device's maximum
     * clock speed in hertz.
     *
     * @return the {@code SPI_IOC_RD_MAX_SPEED_HZ} request code
     */
    public static long getSpiIocRdMaxSpeedHz() {
        return IoctlMagic.SPI_IOC_RD_MAX_SPEED_HZ;
    }

    /**
     * Returns the {@code SPI_IOC_WR_MAX_SPEED_HZ} request code used to set the device's maximum
     * clock speed in hertz.
     *
     * @return the {@code SPI_IOC_WR_MAX_SPEED_HZ} request code
     */
    public static long getSpiIocWrMaxSpeedHz() {
        return IoctlMagic.SPI_IOC_WR_MAX_SPEED_HZ;
    }

    /**
     * Returns the {@code SPI_IOC_RD_MODE32} request code used to read the SPI mode as a 32-bit
     * value (supporting the extended mode flags).
     *
     * @return the {@code SPI_IOC_RD_MODE32} request code
     */
    public static long getSpiIocRdMode32() {
        return IoctlMagic.SPI_IOC_RD_MODE32;
    }

    /**
     * Returns the {@code SPI_IOC_WR_MODE32} request code used to set the SPI mode as a 32-bit value.
     *
     * @return the {@code SPI_IOC_WR_MODE32} request code
     */
    public static long getSpiIocWrMode32() {
        return IoctlMagic.SPI_IOC_WR_MODE32;
    }

    /**
     * Returns the {@code SPI_IOC_RD_LSB_FIRST} request code used to read whether the device
     * transfers least-significant bit first.
     *
     * @return the {@code SPI_IOC_RD_LSB_FIRST} request code
     */
    public static long getSpiIocRdLsbFirst() {
        return IoctlMagic.SPI_IOC_RD_LSB_FIRST;
    }

    /**
     * Returns the {@code SPI_IOC_WR_LSB_FIRST} request code used to set whether the device
     * transfers least-significant bit first.
     *
     * @return the {@code SPI_IOC_WR_LSB_FIRST} request code
     */
    public static long getSpiIocWrLsbFirst() {
        return IoctlMagic.SPI_IOC_WR_LSB_FIRST;
    }

    /*
    I2C Commands
     */

    /**
     * Returns the {@code I2C_SLAVE} request code ({@code 0x0703}) used to set the 7-bit slave
     * address the I2C device file will talk to.
     *
     * @return the {@code I2C_SLAVE} ioctl request code
     */
    public static long getI2CSlave() {
        return 0x0703L;
    }

    /**
     * Returns the {@code I2C_TENBIT} request code ({@code 0x0704}) used to select 10-bit slave
     * addressing mode.
     *
     * @return the {@code I2C_TENBIT} ioctl request code
     */
    public static long getI2CTenBit() {
        return 0x0704L;
    }

    /**
     * Returns the {@code I2C_SMBUS} request code ({@code 0x0720}) used to perform an SMBus
     * transaction via a {@code struct i2c_smbus_ioctl_data} argument.
     *
     * @return the {@code I2C_SMBUS} ioctl request code
     */
    public static long getI2CSMBus() {
        return 0x0720L;
    }

    /**
     * Returns the {@code I2C_FUNCS} request code ({@code 0x0705}) used to query the functionality
     * bitmask supported by the I2C adapter.
     *
     * @return the {@code I2C_FUNCS} ioctl request code
     */
    public static long getI2CFuncs() {
        return 0x0705L;
    }
}
