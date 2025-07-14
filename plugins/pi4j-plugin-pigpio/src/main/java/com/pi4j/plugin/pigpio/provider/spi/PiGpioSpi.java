package com.pi4j.plugin.pigpio.provider.spi;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioSpi.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.spi.*;
import com.pi4j.library.pigpio.PiGpio;

/**
 * <p>PiGpioSpi class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioSpi extends SpiBase implements Spi {

    protected final PiGpio piGpio;
    protected final int handle;
    protected static int SPI_BUS_MASK = 0x0100;
    protected static int SPI_MODE_MASK = 0x0003;
    protected static int SPI_WRITE_LSB_FIRST_MASK = 0x4000;
    protected static int SPI_READ_LSB_FIRST_MASK = 0x8000;

    /**
     * <p>Constructor for PiGpioSpi.</p>
     *
     * @param piGpio a {@link com.pi4j.library.pigpio.PiGpio} object.
     * @param provider a {@link com.pi4j.io.spi.SpiProvider} object.
     * @param config a {@link com.pi4j.io.spi.SpiConfig} object.
     * <p>
     * ------------------------------------------------------------------
     * spiFlags consists of the least significant 22 bits.
     * ------------------------------------------------------------------
     * 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
     *  b  b  b  b  b  b  R  T  n  n  n  n  W  A u2 u1 u0 p2 p1 p0  m  m
     * <p>
     * [mm]     defines the SPI mode.
     *          Warning: modes 1 and 3 do not appear to work on the auxiliary SPI.
     * <p>
     *          Mode POL PHA
     *           0    0   0
     *           1    0   1
     *           2    1   0
     *           3    1   1
     * <p>
     * [px]     is 0 if CEx is active low (default) and 1 for active high.
     * [ux]     is 0 if the CEx GPIO is reserved for SPI (default) and 1 otherwise.
     * [A]      is 0 for the main SPI, 1 for the auxiliary SPI.
     * [W]      is 0 if the device is not 3-wire, 1 if the device is 3-wire. Main SPI only.
     * [nnnn]   defines the number of bytes (0-15) to write before switching the MOSI line to MISO to read data.
     *          This field is ignored if W is not set. Main SPI only.
     * [T]      is 1 if the least significant bit is transmitted on MOSI first, the default (0) shifts the
     *          most significant bit out first. Auxiliary SPI only.
     * [R]      is 1 if the least significant bit is received on MISO first, the default (0) receives the most
     *          significant bit first. Auxiliary SPI only.
     * [bbbbbb] defines the word size in bits (0-32). The default (0) sets 8 bits per word. Auxiliary SPI only.
     *
     */
    public PiGpioSpi(PiGpio piGpio, SpiProvider provider, SpiConfig config) {
        super(provider, config);

        // set local reference instance
        this.piGpio = piGpio;

        // get configured SPI bus
        SpiBus bus = config.bus();

        // get configured SPI mode
        SpiMode mode = config.mode();

        // the default value for 'flags' is zero
        int flags = 0;

        // if 'flags' were provided in the SPI config, then accept them
        if(config().flags() != null){
            flags = config().flags().intValue();
        }

        // only SPI BUS_0 and AUX SPI BUS_1 are supported by PiGPIO
        if(bus.getBus() > 1){
            throw new IOException("Unsupported BUS by PiGPIO SPI Provider: bus=" + bus.toString());
        }

        // channel/address (chip-select) #2 is not supported on SPI_BUS_0 by PiGPIO
        if(bus == SpiBus.BUS_0 && config.address() == 2) {
            throw new IOException("Unsupported Pigpio SPI channel (chip select) on SPI BUS_0 bus: address=" + config.address() );
        }
        if(config.address() > 2) {
            throw new IOException("Unsupported Pigpio SPI channel (chip select) address greater than 2" + config.address() );
        }
        // Comments on the PiGPIO web https://abyz.me.uk/rpi/pigpio/cif.html#spiOpen as follows:
        // "Warning: modes 1 and 3 do not appear to work on the auxiliary SPI."
        // SPI MODE_1 and MODE_3 are not supported on the AUX SPI BUS_1 by PiGPIO
        if(bus == SpiBus.BUS_1 && (mode == SpiMode.MODE_1 || mode == SpiMode.MODE_3)) {
            throw new IOException("Unsupported SPI mode on AUX SPI BUS_1: mode=" + mode.toString());
        }

        if(config.writeLsbFirstUserProvided()) {  // user provided, overwrite flags
         if (config().getWriteLsbFirst() == 0) {
                flags = (flags | (0xFFFFFFFF ^ SPI_WRITE_LSB_FIRST_MASK)); // clear bit
            }else {
                flags = (flags | (0xFFFFFFFF ^ SPI_WRITE_LSB_FIRST_MASK)) |SPI_WRITE_LSB_FIRST_MASK; // clear AUX bit
            }
        }

        if(config.readLsbFirstUserProvided()) {  // user provided, overwrite flags
            if (config().getReadLsbFirst() == 0) {
                flags = (flags | (0xFFFFFFFF ^ SPI_READ_LSB_FIRST_MASK)); // clear bit
            }else {
                flags = (flags | (0xFFFFFFFF ^ SPI_READ_LSB_FIRST_MASK)) |SPI_READ_LSB_FIRST_MASK; // clear AUX bit
            }
        }

        if(config.busUserProvided()) {  // user provided, overwrite flags
            // update flags value with BUS bit ('A' 0x0000=BUS0; 0x0100=BUS1)
            if (bus == SpiBus.BUS_0) {
                flags = (flags & (0xFFFFFFFF ^ SPI_BUS_MASK)); // clear AUX bit
            } else if (bus == SpiBus.BUS_1) {
                flags = (flags & (0xFFFFFFFF ^ SPI_BUS_MASK)) | SPI_BUS_MASK; // set AUX bit
            }
        }

        if(config.modeUserProvided()) {  // user provided, overwrite flags
            // update flags value with MODE bits ('mm' 0x03)
            flags = (flags & (0xFFFFFFFF ^ SPI_MODE_MASK)) | mode.getMode(); // set MODE bits
        }
            // create SPI instance of PiGPIO SPI
       this.handle = piGpio.spiOpen(
            config.address(),
            config.baud(),
            flags);

       // set open state flag
       this.isOpen = true;
    }

    /** {@inheritDoc} */
    @Override
    public Spi initialize(Context context) throws InitializeException {
        super.initialize(context);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        piGpio.spiClose(this.handle);
        super.close();
    }

    // -------------------------------------------------------------------
    // DEVICE TRANSFER FUNCTIONS
    // -------------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public int transfer(byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes) {
        return piGpio.spiXfer(this.handle, write, writeOffset, read, readOffset, numberOfBytes);
    }

    // -------------------------------------------------------------------
    // DEVICE WRITE FUNCTIONS
    // -------------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public int write(byte b) {
        return piGpio.spiWriteByte(this.handle, b);
    }

    /** {@inheritDoc} */
    @Override
    public int write(byte[] data, int offset, int length) {
        return piGpio.spiWrite(this.handle, data, offset, length);
    }


    // -------------------------------------------------------------------
    // RAW DEVICE READ FUNCTIONS
    // -------------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public int read() {
        return piGpio.spiReadByte(this.handle);
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        return piGpio.spiRead(this.handle, buffer, offset, length);
    }
}
