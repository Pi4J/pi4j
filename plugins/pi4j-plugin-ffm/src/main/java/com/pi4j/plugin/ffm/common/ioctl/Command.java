package com.pi4j.plugin.ffm.common.ioctl;

/**
 * Commands to be provided for ioctl calls.
 */
public final class Command {

    /**
     * Forbids creating an instance of this class.
     */
    private Command() {
    }

    public static long getGpioGetChipInfoIoctl() {
        return IoctlMagic.GPIO_GET_CHIPINFO_IOCTL;
    }

    public static long getSpiIocMessage(int n) {
        return IoctlMagic.SPI_IOC_MESSAGE(n);
    }

    public static long getSpiIocRdMode() {
        return IoctlMagic.SPI_IOC_RD_MODE;
    }

    public static long getSpiIocWrMode() {
        return IoctlMagic.SPI_IOC_WR_MODE;
    }

    public static long getSpiIocRdBitsPerWord() {
        return IoctlMagic.SPI_IOC_RD_BITS_PER_WORD;
    }

    public static long getSpiIocWrBitsPerWord() {
        return IoctlMagic.SPI_IOC_WR_BITS_PER_WORD;
    }

    public static long getSpiIocRdMaxSpeedHz() {
        return IoctlMagic.SPI_IOC_RD_MAX_SPEED_HZ;
    }

    public static long getSpiIocWrMaxSpeedHz() {
        return IoctlMagic.SPI_IOC_WR_MAX_SPEED_HZ;
    }

    public static long getSpiIocRdMode32() {
        return IoctlMagic.SPI_IOC_RD_MODE32;
    }

    public static long getSpiIocWrMode32() {
        return IoctlMagic.SPI_IOC_WR_MODE32;
    }

    public static long getSpiIocRdLsbFirst() {
        return IoctlMagic.SPI_IOC_RD_LSB_FIRST;
    }

    public static long getSpiIocWrLsbFirst() {
        return IoctlMagic.SPI_IOC_WR_LSB_FIRST;
    }


    public static long getGpioV2GetLineInfoIoctl() {
        return IoctlMagic.GPIO_V2_GET_LINEINFO_IOCTL;
    }

    public static long getGpioV2GetLineIoctl() {
        return IoctlMagic.GPIO_V2_GET_LINE_IOCTL;
    }

    public static long getGpioV2GetValuesIoctl() {
        return IoctlMagic.GPIO_V2_LINE_GET_VALUES_IOCTL;
    }

    public static long getGpioV2SetValuesIoctl() {
        return IoctlMagic.GPIO_V2_LINE_SET_VALUES_IOCTL;
    }

    public static long getI2CSlave() {
        return 0x0703L;
    }

    public static long getI2CTenBit() {
        return 0x0704L;
    }

    public static long getI2CSMBus() {
        return 0x0720L;
    }

    public static long getI2CFuncs() {
        return 0x0705L;
    }
}
