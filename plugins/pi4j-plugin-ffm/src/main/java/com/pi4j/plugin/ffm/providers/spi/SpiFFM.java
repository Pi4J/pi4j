package com.pi4j.plugin.ffm.providers.spi;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBase;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.common.spi.SpiTransferBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class SpiFFM extends SpiBase implements Spi {
    private static final Logger logger = LoggerFactory.getLogger(SpiFFM.class);
    private static final String SPI_BUS = "/dev/spidev";
    private final FileDescriptorNative FILE = new FileDescriptorNative();
    private final IoctlNative IOCTL = new IoctlNative();


    private int spiFileDescriptor;
    private final String path;

    public SpiFFM(SpiProvider provider, SpiConfig config) {
        super(provider, config);
        this.path = SPI_BUS + config.bus().getBus() + "." + config.address();
    }

    @Override
    public Spi initialize(Context context) throws InitializeException {
        super.initialize(context);

        logger.info("{} - setting up SPIBus...", path);
        logger.debug("{} - opening device file.", path);
        this.spiFileDescriptor = FILE.open(path, FileFlag.O_RDWR);

        logger.debug("{} - setting Write SPI Mode to {}.", path, config.mode());
        IOCTL.call(spiFileDescriptor, Command.getSpiIocWrMode(), config.mode().getMode());
        logger.debug("{} - setting Read SPI Mode to {}.", path, config.mode());
        IOCTL.call(spiFileDescriptor, Command.getSpiIocRdMode(), config.mode().getMode());
//        logger.debug("{} - setting Bit Ordering to {}.", path, config.?);
//        IOCTL.call(spiFileDescriptor, Command.getSpiIocWrLsbFirst(), bitOrdering);
        logger.debug("{} - setting Write Byte Length to {}.", path, 8);
        IOCTL.call(spiFileDescriptor, Command.getSpiIocWrBitsPerWord(), 8);
        logger.debug("{} - setting Read Byte Length to {}.", path, 8);
        IOCTL.call(spiFileDescriptor, Command.getSpiIocRdBitsPerWord(), 8);
        logger.debug("{} - setting Write Clock Frequency to {}.", path, config.baud());
        IOCTL.call(spiFileDescriptor, Command.getSpiIocWrMaxSpeedHz(), config.baud());
        logger.debug("{} - setting Read Clock Frequency to {}.", path, config.baud());
        IOCTL.call(spiFileDescriptor, Command.getSpiIocRdMaxSpeedHz(), config.baud());
        logger.debug("{} - setting writeLsbFirst to {}.", path, config.getWriteLsbFirst());
        IOCTL.call(spiFileDescriptor, Command.getSpiIocWrLsbFirst(), config.getWriteLsbFirst());
        logger.debug("{} - setting readLsbFirst to {}.", path, config.getReadLsbFirst());
        IOCTL.call(spiFileDescriptor, Command.getSpiIocRdLsbFirst(), config.getReadLsbFirst());

        this.isOpen = true;
        logger.info("{} - SPI Bus configured.", path);
        return this;
    }

    @Override
    public Spi shutdown(Context context) throws ShutdownException {
        FILE.close(spiFileDescriptor);
        return super.shutdown(context);
    }

    @Override
    public int transfer(byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes) {
        checkClosed();
        logger.trace("{} - Transferring data (length '{}')", path, numberOfBytes);
        logger.trace("{} - Write buffer: {}", path, write);
        logger.trace("{} - Read buffer: {}", path, read);
        var spiTransfer = new SpiTransferBuffer(write, read, numberOfBytes);
        spiTransfer = IOCTL.call(spiFileDescriptor, Command.getSpiIocMessage(1), spiTransfer);
        var readBytes = spiTransfer.getRxBuffer();
        if (read != null) {
            ByteBuffer.wrap(read).put(readBytes);
        }
        return readBytes.length;
    }

    @Override
    public int write(byte data) {
        return write(new byte[]{data}, 0, 1);
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        return transfer(data, offset, null, 0, length);
    }

    @Override
    public int read() {
        return read(new byte[1], 0, 1);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        return transfer(null, 0, buffer, offset, length);
    }

    /**
     * Checks if SPI Bus is closed.
     */
    private void checkClosed() {
        if (!isOpen) {
            throw new Pi4JException("SPI bus  '" + path + "' is closed");
        }
    }
}
