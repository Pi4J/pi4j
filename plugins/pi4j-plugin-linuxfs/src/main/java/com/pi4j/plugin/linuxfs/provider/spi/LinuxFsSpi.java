/*
 *    * Copyright (C) 2012 - 2024 Pi4J
 *  * %%
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * -
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: EXTENSION
 *  FILENAME      :  LinuxFsSpi.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *  %%
 */


package com.pi4j.plugin.linuxfs.provider.spi;

import com.pi4j.io.exception.IOException;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBase;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.plugin.linuxfs.internal.LinuxLibC;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pi4j.plugin.linuxfs.internal.LinuxLibC.*;

/**
 * SPI implementation that uses JNA bindings to the Linux SPI device (i.e. /dev/spidev0.0). Only supports writing but
 * it works to drive an SSD1306 OLED display.
 *
 * @author mpilone
 * @see
 * <a href="https://github.com/sckulkarni246/ke-rpi-samples/blob/main/spi-c-ioctl/oled_functions.c#L157">spi-c-ioctl</a>
 * @see <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/spi/spidev.h">spidev.h</a>
 * @see <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/spi/spi.h">spi.h</a>
 * @see <a href="https://github.com/torvalds/linux/blob/master/tools/spi/spidev_fdx.c">spidev_fdx.c</a>
 * @see <a href="https://www.kernel.org/doc/html/latest/spi/spidev.html">SPI userspace API</a>
 * @since 10/3/24.
 */
public class LinuxFsSpi extends SpiBase implements Spi {

    private static final Logger LOG = LoggerFactory.getLogger(LinuxFsSpi.class);

    ///////////////////////////////////
    // From spidev.h
    private final static byte SPI_IOC_MAGIC = 'k';
    private final static byte SIZE_OF_BYTE = 1;
    private final static byte SIZE_OF_INT = 4;

    private static int SPI_IOC_MESSAGE(int n) {
        // Even though we will pass the structure to ioctl as a pointer, the command needs to know
        // the actual size of the structure (i.e. sizeof). Therefore, we use the ByValue interface
        // when getting the struct size.
        int structSize = Native.getNativeSize(spi_ioc_transfer.ByValue.class);
        int msgSize = ((((n)*(structSize)) < (1 << _IOC_SIZEBITS))
            ? ((n)*(structSize)) : 0);

        return _IOC(_IOC_WRITE, SPI_IOC_MAGIC, (byte)0, msgSize);
    }

    // These could be replaced with the specific values generated from the _IOC method (a macro in the native C),
    // but I think it is useful to see where the values come from.

    /* Read / Write of SPI mode (SPI_MODE_0..SPI_MODE_3) (limited to 8 bits) */
    private final static int SPI_IOC_RD_MODE = _IOC(_IOC_READ, SPI_IOC_MAGIC, (byte)1, SIZE_OF_BYTE);
    private final static int SPI_IOC_WR_MODE = _IOC(_IOC_WRITE, SPI_IOC_MAGIC, (byte)1, SIZE_OF_BYTE);

    /* Read / Write SPI bit justification */
    private final static int SPI_IOC_RD_LSB_FIRST = _IOC(_IOC_READ, SPI_IOC_MAGIC, (byte)2, SIZE_OF_BYTE);
    private final static int SPI_IOC_WR_LSB_FIRST = _IOC(_IOC_WRITE, SPI_IOC_MAGIC, (byte)2, SIZE_OF_BYTE);

    /* Read / Write SPI device word length (1..N) */
    private final static int SPI_IOC_RD_BITS_PER_WORD = _IOC(_IOC_READ, SPI_IOC_MAGIC, (byte)3, SIZE_OF_BYTE);
    private final static int SPI_IOC_WR_BITS_PER_WORD = _IOC(_IOC_WRITE, SPI_IOC_MAGIC, (byte)3, SIZE_OF_BYTE);

    /* Read / Write SPI device default max speed hz */
    private final static int SPI_IOC_RD_MAX_SPEED_HZ = _IOC(_IOC_READ, SPI_IOC_MAGIC, (byte)4, SIZE_OF_INT);
    private final static int SPI_IOC_WR_MAX_SPEED_HZ = _IOC(_IOC_WRITE, SPI_IOC_MAGIC, (byte)4, SIZE_OF_INT);

    /* Read / Write of the SPI mode field */
    private final static int SPI_IOC_RD_MODE32 = _IOC(_IOC_READ, SPI_IOC_MAGIC, (byte)5, SIZE_OF_INT);
    private final static int SPI_IOC_WR_MODE32 = _IOC(_IOC_WRITE, SPI_IOC_MAGIC, (byte)5, SIZE_OF_INT);

    @Structure.FieldOrder({"tx_buf", "rx_buf",
        "len", "speed_hz",
        "delay_usecs", "bits_per_word", "cs_change", "tx_nbits", "rx_nbits", "word_delay_usecs", "pad"})
    public static class spi_ioc_transfer extends Structure {
        public long tx_buf;
        public long rx_buf;

        public int		len;
        public int		speed_hz;

        public short	delay_usecs;
        public byte		bits_per_word;
        public byte		cs_change;
        public byte		tx_nbits;
        public byte		rx_nbits;
        public byte		word_delay_usecs;
        public byte		pad;

        public static class ByValue extends spi_ioc_transfer implements Structure.ByValue {}
    }

    ///////////////////////////////////
    // spi.h
    private final byte 	SPI_CPHA	=	1;	/* clock phase */
    private final byte 	SPI_CPOL	=	1 << 1;	/* clock polarity */

    private final byte 	SPI_MODE_0	=	(0|0);		/* (original MicroWire) */
    private final byte 	SPI_MODE_1	=	(0|SPI_CPHA);
    private final byte 	SPI_MODE_2	=	(SPI_CPOL|0);
    private final byte 	SPI_MODE_3	=	(SPI_CPOL|SPI_CPHA);


    private final byte  BITS8 = 8;

    private final static String SPI_DEVICE_BASE = "/dev/spidev";
    private final LinuxLibC libc = LinuxLibC.INSTANCE;
    private int fd;

    public LinuxFsSpi(LinuxFsSpiProviderImpl provider, SpiConfig config) {
        super(provider, config);
    }

    @Override
    public void open() {
        super.open();

        // From the docs (https://www.kernel.org/doc/html/v6.12/spi/spidev.html):
        // For a SPI device with chipselect C on bus B, you should see:
        //
        // /dev/spidevB.C ...
        //    character special device, major number 153 with a dynamically chosen minor device number.
        //    This is the node that userspace programs will open, created by “udev” or “mdev”.
        String spiDev = SPI_DEVICE_BASE + config().bus().getBus() + "." + config().address();
        LOG.info("Opening SPI bus {}, address {}", config().bus().getBus(), config().address());
        fd = libc.open(spiDev, LinuxLibC.O_RDWR);
        if (fd < 0) {
            throw new RuntimeException("Failed to open SPI device " + spiDev);
        }

        IntByReference intPtr = new IntByReference();
        int ret = libc.ioctl(fd, SPI_IOC_RD_MODE32, intPtr);
        if(ret != 0) {
            libc.close(fd);
            throw new RuntimeException("Could not read SPI mode.");
        }

        // if 'flags' were provided error
        if(config().flags() != null){
            throw new IOException("Unsupported SPI Pi5 parameter flags" );
        }
        switch (config().mode()) {
            case MODE_0:
                intPtr.setValue(intPtr.getValue() | SPI_MODE_0);
                break;
            case MODE_1:
                intPtr.setValue(intPtr.getValue() | SPI_MODE_1);
                break;
            case MODE_2:
                intPtr.setValue(intPtr.getValue() | SPI_MODE_2);
                break;
            case MODE_3:
                intPtr.setValue(intPtr.getValue() | SPI_MODE_3);
                break;
        }

        ret = libc.ioctl(fd, SPI_IOC_WR_MODE32, intPtr);
        if(ret != 0) {
            libc.close(fd);
            throw new RuntimeException("Could not write SPI mode..");
        }

        intPtr.setValue(config().baud());
        ret = libc.ioctl(fd, SPI_IOC_RD_MAX_SPEED_HZ, intPtr);
        if(ret != 0) {
            libc.close(fd);
            throw new RuntimeException("Could not read the SPI max speed.");
        }

        intPtr.setValue(config().baud());
        ret = libc.ioctl(fd, SPI_IOC_WR_MAX_SPEED_HZ, intPtr);
        if(ret != 0) {
            libc.close(fd);
            throw new RuntimeException("Could not write the SPI max speed.");
        }

        // Bits per word
        intPtr.setValue(BITS8);
        ret = libc.ioctl(fd, SPI_IOC_WR_BITS_PER_WORD, intPtr);
        if(ret != 0) {
            libc.close(fd);
            throw new RuntimeException("Could not write the SPI BITS per write.");
        }
        // Bits per word
        intPtr.setValue(BITS8);
        ret = libc.ioctl(fd, SPI_IOC_RD_BITS_PER_WORD, intPtr);
        if(ret != 0) {
            libc.close(fd);
            throw new RuntimeException("Could not write the SPI BITS per read.");
        }

        // BIT shift direction
        intPtr.setValue(config().readLsbFirst());
        ret = libc.ioctl(fd, SPI_IOC_RD_LSB_FIRST, intPtr);
        if(ret != 0) {
            libc.close(fd);
            throw new RuntimeException("Could not write the SPI SHIFT read.");
        }
        intPtr.setValue(config().writeLsbFirst());
        ret = libc.ioctl(fd, SPI_IOC_WR_LSB_FIRST, intPtr);
        if(ret != 0) {
            libc.close(fd);
            throw new RuntimeException("Could not write the SPI SHIFT write.");
        }
   }

    @Override
    public void close() {
        libc.close(fd);

        super.close();
    }

    @Override
    public int transfer(byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes) {
        PeerAccessibleMemory buf = new PeerAccessibleMemory(numberOfBytes);
        buf.write(0, write, writeOffset, numberOfBytes);

        // According to the docs you can use the same buffer for tx/rx.
        spi_ioc_transfer transfer = new spi_ioc_transfer();
        transfer.tx_buf = buf.getPeer();
        transfer.rx_buf = buf.getPeer();
        transfer.bits_per_word = BITS8;
        transfer.speed_hz = config.baud();
        transfer.delay_usecs = 0;
        transfer.len = numberOfBytes;

        int ret = libc.ioctl(fd, SPI_IOC_MESSAGE(1), transfer);
        if (ret < 0) {
            LOG.error("Could not write SPI message. ret {}, error: {}", ret, Native.getLastError());
            numberOfBytes = -1;
        }
        else {
            buf.read(0, read, readOffset, numberOfBytes);
        }

        buf.close();

        return numberOfBytes;
    }

    @Override
    public int read() {
        byte[] buf = new byte[1];
        if (read(buf, 0, 1) == 1) {
            return buf[0];
        }
        else {
            return -1;
        }
    }

    @Override
    public int read(byte[] read, int offset, int length) {
        PeerAccessibleMemory buf = new PeerAccessibleMemory(length);

        spi_ioc_transfer transfer = new spi_ioc_transfer();
        transfer.tx_buf = 0;
        transfer.rx_buf = buf.getPeer();
        transfer.bits_per_word = BITS8;
        transfer.speed_hz = config.baud();
        transfer.delay_usecs = 0;
        transfer.len = length;

        int ret = libc.ioctl(fd, SPI_IOC_MESSAGE(1), transfer);
        if (ret < 0) {
            LOG.error("Could not write SPIIOC  message. ret {}, error: {}", ret, Native.getLastError());
            length = -1;
        }else{
            buf.read(0, read, offset, length);
        }

        buf.close();

        return length;
    }

    @Override
    public int write(byte b) {
        return write(new byte[] {b}, 0, 1);
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        // We could reuse this buffer for all requests as long as it is large enough.
        // For now we'll alloc/free a new one on each request.
        PeerAccessibleMemory buf = new PeerAccessibleMemory(length);
        buf.write(0, data, offset, length);

        spi_ioc_transfer transfer = new spi_ioc_transfer();
        transfer.tx_buf = buf.getPeer();
        transfer.rx_buf = 0;
        transfer.bits_per_word = BITS8;
        transfer.speed_hz = config.baud();
        transfer.delay_usecs = 0;
        transfer.len = length;

        int ret = libc.ioctl(fd, SPI_IOC_MESSAGE(1), transfer);
        if (ret < 0) {
            LOG.error("Could not write SPI message. ret {}, error: {}", ret, Native.getLastError());
            length = 0;
        }

        buf.close();

        return length;
    }

    /**
     * Extension of Memory to allow direct access to the native peer pointer. This is required because
     * the {@link spi_ioc_transfer} structure uses a long for the tx_buf and rx_buf fields but a
     * native pointer is normally only 32 bitsso we can't let JNA do the mapping for us. There may be a better
     * way to do this with JNA. We fetch the peer and put it in a long in the struct to maintain proper struct
     * alignment.
     */
    private static class PeerAccessibleMemory extends Memory {
        public PeerAccessibleMemory(long size) {
            super(size);
        }

        long getPeer() {
            return peer;
        }
    }
}