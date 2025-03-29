package com.pi4j.plugin.ffm.common.ioctl;

import com.pi4j.plugin.ffm.common.gpio.structs.LineConfig;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.gpio.structs.LineRequest;
import com.pi4j.plugin.ffm.common.gpio.structs.LineValues;

/**
 * Internal reference file from Kernel headers to calculate ioctl commands.
 */
final class IoctlMagic {
    static final long SPI_IOC_RD_MODE;
    static final long SPI_IOC_WR_MODE;
    static final long SPI_IOC_RD_BITS_PER_WORD;
    static final long SPI_IOC_WR_BITS_PER_WORD;

    static final long SPI_IOC_RD_LSB_FIRST;
    static final long SPI_IOC_WR_LSB_FIRST;

    static final long SPI_IOC_RD_MAX_SPEED_HZ;
    static final long SPI_IOC_WR_MAX_SPEED_HZ;
    static final long SPI_IOC_RD_MODE32;
    static final long SPI_IOC_WR_MODE32;

    static int _SPI_IOC_TRANSFER_SIZE = -1;

    static final long GPIO_GET_CHIPINFO_IOCTL;

    static final long GPIO_V2_GET_LINEINFO_IOCTL;
    static final long GPIO_V2_GET_LINEINFO_WATCH_IOCTL;
    static final long GPIO_V2_GET_LINE_IOCTL;
    static final long GPIO_V2_LINE_SET_CONFIG_IOCTL;
    static final long GPIO_V2_LINE_GET_VALUES_IOCTL;
    static final long GPIO_V2_LINE_SET_VALUES_IOCTL;


    final static long SPI_IOC_MAGIC = 'k';
    final static long _IOC_NRBITS = 8;
    final static long _IOC_TYPEBITS = 8;
    final static long _IOC_SIZEBITS = 14;
    final static long _IOC_DIRBITS = 2;
    final static long _IOC_NRSHIFT = 0;
    final static long _IOC_NONE = 0;
    final static long _IOC_READ = 2;
    final static long _IOC_WRITE = 1;
    final static int GPIOHANDLES_MAX = 64;

    static long GET_SPI_IOC_MAGIC() {
        return SPI_IOC_MAGIC;
    }

    static long IOC_NRBITS() {
        return _IOC_NRBITS;
    }

    static long IOC_TYPEBITS() {
        return _IOC_TYPEBITS;
    }

    static long IOC_SIZEBITS() {
        return _IOC_SIZEBITS;
    }

    static long IOC_DIRBITS() {
        return _IOC_DIRBITS;
    }

    static long IOC_NRSHIFT() {
        return _IOC_NRSHIFT;
    }

    static long IOC_NONE() {
        return _IOC_NONE;
    }

    static long IOC_READ() {
        return _IOC_READ;
    }

    static long IOC_WRITE() {
        return _IOC_WRITE;
    }

    static int SPI_MSGSIZE(int N) {
        return ((((N) * (SPI_IOC_TRANSFER_SIZE())) < (1 << IOC_SIZEBITS()))
            ? ((N) * (SPI_IOC_TRANSFER_SIZE()))
            : 0);
    }

    static int SPI_IOC_TRANSFER_SIZE() {
        if (_SPI_IOC_TRANSFER_SIZE == -1) {
            _SPI_IOC_TRANSFER_SIZE = 44;
        }
        return _SPI_IOC_TRANSFER_SIZE;
    }

    static long SPI_IOC_MESSAGE(int N) {
        return _IOW(GET_SPI_IOC_MAGIC(), 0, SPI_MSGSIZE(N));
    }


    long _IOC_NRMASK() {
        return ((1L << IOC_NRBITS()) - 1);
    }

    long _IOC_TYPEMASK() {
        return ((1L << IOC_TYPEBITS()) - 1);
    }

    long _IOC_SIZEMASK() {
        return ((1L << IOC_SIZEBITS()) - 1);
    }

    long _IOC_DIRMASK() {
        return ((1L << IOC_DIRBITS()) - 1);
    }

    static long _IOC_TYPESHIFT() {
        return IOC_NRSHIFT() + IOC_NRBITS();
    }

    static long _IOC_SIZESHIFT() {
        return _IOC_TYPESHIFT() + IOC_TYPEBITS();
    }

    static long _IOC_DIRSHIFT() {
        return _IOC_SIZESHIFT() + IOC_SIZEBITS();
    }

    long _IO(long type, long nr) {
        return _IOC(IOC_NONE(), (type), (nr), 0);
    }

    static long _IOR(long type, long nr, long size) {
        return _IOC(IOC_READ(), type, nr, size);
    }

    static long _IOW(long type, long nr, long size) {
        return _IOC(IOC_WRITE(), type, nr, size);
    }

    static long _IOWR(long type, long nr, long size) {
        return _IOC(IOC_READ() | IOC_WRITE(), type, nr, size);
    }

    static long _IOC(long dir, long type, long nr, long size) {
        return (((dir) << _IOC_DIRSHIFT()) |
            ((type) << _IOC_TYPESHIFT()) |
            ((nr) << IOC_NRSHIFT()) |
            ((size) << _IOC_SIZESHIFT()));
    }

    static {
        SPI_IOC_RD_MODE = _IOR(GET_SPI_IOC_MAGIC(), 1, 1);
        SPI_IOC_WR_MODE = _IOW(GET_SPI_IOC_MAGIC(), 1, 1);
        SPI_IOC_RD_BITS_PER_WORD = _IOR(GET_SPI_IOC_MAGIC(), 3, 1);
        SPI_IOC_WR_BITS_PER_WORD = _IOW(GET_SPI_IOC_MAGIC(), 3, 1);
        SPI_IOC_RD_MAX_SPEED_HZ = _IOR(GET_SPI_IOC_MAGIC(), 4, 4);
        SPI_IOC_WR_MAX_SPEED_HZ = _IOW(GET_SPI_IOC_MAGIC(), 4, 4);
        SPI_IOC_RD_MODE32 = _IOR(GET_SPI_IOC_MAGIC(), 5, 4);
        SPI_IOC_WR_MODE32 = _IOW(GET_SPI_IOC_MAGIC(), 5, 4);
        SPI_IOC_RD_LSB_FIRST = _IOR(GET_SPI_IOC_MAGIC(), 2, 1);
        SPI_IOC_WR_LSB_FIRST = _IOW(GET_SPI_IOC_MAGIC(), 2, 1);

        GPIO_GET_CHIPINFO_IOCTL = _IOR(0xB4, 0x01, 68);


        GPIO_V2_GET_LINEINFO_IOCTL = _IOWR(0xb4, 0x05, LineInfo.LAYOUT.byteSize());
        GPIO_V2_GET_LINEINFO_WATCH_IOCTL = _IOWR(0xb4, 0x06, LineInfo.LAYOUT.byteSize());
        GPIO_V2_GET_LINE_IOCTL = _IOWR(0xb4, 0x07, LineRequest.LAYOUT.byteSize());
        GPIO_V2_LINE_SET_CONFIG_IOCTL = _IOWR(0xb4, 0x0d, LineConfig.LAYOUT.byteSize());
        GPIO_V2_LINE_GET_VALUES_IOCTL = _IOWR(0xb4, 0x0e, LineValues.LAYOUT.byteSize());
        GPIO_V2_LINE_SET_VALUES_IOCTL = _IOWR(0xb4, 0x0f, LineValues.LAYOUT.byteSize());

    }
}
