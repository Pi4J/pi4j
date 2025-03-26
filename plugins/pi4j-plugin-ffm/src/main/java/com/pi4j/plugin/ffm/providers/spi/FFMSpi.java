package com.pi4j.plugin.ffm.providers.spi;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.spi.*;

public class FFMSpi extends SpiBase implements Spi {
    protected static int SPI_BUS_MASK = 0x0100;
    protected static int SPI_MODE_MASK = 0x0003;
    protected static int SPI_WRITE_LSB_FIRST_MASK = 0x4000;
    protected static int SPI_READ_LSB_FIRST_MASK = 0x8000;

    public FFMSpi( SpiProvider provider, SpiConfig config) {
        super(provider, config);

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
            throw new IOException("Unsupported SPI channel (chip select) on SPI BUS_0 bus: address=" + config.address() );
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
//        this.handle = piGpio.spiOpen(
//            config.address(),
//            config.baud(),
//            flags);

        // set open state flag
        this.isOpen = true;
    }

    @Override
    public Spi initialize(Context context) throws InitializeException {
        super.initialize(context);
        return this;
    }

    @Override
    public void close() {
        super.close();
    }

    // -------------------------------------------------------------------
    // DEVICE TRANSFER FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int transfer(byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes) {
        return 0;
    }

    // -------------------------------------------------------------------
    // DEVICE WRITE FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int write(byte b) {
        return 0;
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        return 0;
    }


    // -------------------------------------------------------------------
    // RAW DEVICE READ FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int read() {
        return 0;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        return 0;
    }
}
