package com.pi4j.plugin.ffm.providers.spi;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBase;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.HexFormatter;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.common.spi.SpiMultipleTransferBuffer;
import com.pi4j.plugin.ffm.common.spi.SpiTransferBuffer;
import com.pi4j.plugin.ffm.detect.model.HWInterfaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * {@link Spi} implementation that performs full-duplex SPI transfers against a spidev character device
 * ({@code /dev/spidevB.C}) using the {@code SPI_IOC_MESSAGE} ioctl.
 * <p>
 * During initialization the SPI mode, bits-per-word, maximum clock speed and bit order are configured
 * through the {@code SPI_IOC_WR_*}/{@code SPI_IOC_RD_*} ioctls. Transfers larger than the driver's
 * {@code bufsiz} limit are automatically split into chunks, each issued as its own ioctl, to avoid the
 * kernel rejecting the request with {@code EMSGSIZE}.
 *
 * @see com.pi4j.io.spi.Spi
 * @see IoctlNative
 * @see SpiTransferBuffer
 * @see SpiMultipleTransferBuffer
 */
public class FFMSpi extends SpiBase implements Spi {
    private static final Logger logger = LoggerFactory.getLogger(FFMSpi.class);
    private static final String SPI_BUS = "/dev/spidev";

    /**
     * sysfs file exposing the maximum number of bytes the spidev driver accepts in a single
     * SPI_IOC_MESSAGE transfer. Transfers larger than this value are rejected by the kernel with
     * EMSGSIZE, so we read it once and split larger transfers into chunks of at most this size.
     */
    private static final String SPIDEV_BUFSIZ_PATH = "/sys/module/spidev/parameters/bufsiz";

    /**
     * Fallback transfer chunk size used when {@link #SPIDEV_BUFSIZ_PATH} cannot be read. This matches
     * the spidev driver's own compile-time default of 4096 bytes.
     */
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    /**
     * The bufsiz value is text ASCII (not binary). It is an unsigned int, so the largest decimal
     * representation is 10 bytes.
     */
    private static final int MAX_BUFSIZ_FILE_SIZE = 10;

    private final FileDescriptorNative FILE = new FileDescriptorNative();
    private final IoctlNative IOCTL = new IoctlNative();


    private int spiFileDescriptor;
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private final String path;

    /**
     * Creates an SPI instance, resolving the spidev device path ({@code /dev/spidevB.C}) from the bus
     * and channel in the configuration and verifying that it is accessible with the required permissions.
     *
     * @param provider the {@link SpiProvider} that created this instance
     * @param config   the SPI configuration carrying the bus, channel, mode, baud rate and bit order
     */
    public FFMSpi(SpiProvider provider, SpiConfig config) {
        super(provider, config);
        this.path = SPI_BUS + config.bus().getBus() + "." + config.channel();
        FFMPermissionHelper.checkDevicePermissions(path, HWInterfaces.SPI, true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Opens the spidev device read-write and configures both the read and write SPI mode, 8-bit
     * word length, maximum clock frequency and LSB-first setting via the {@code SPI_IOC_WR_*} and
     * {@code SPI_IOC_RD_*} ioctls, then reads the driver's {@code bufsiz} to size the transfer chunks.
     */
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

        this.bufferSize = readBufferSize();
        logger.debug("{} - SPI transfer chunk size (bufsiz) is {} bytes.", path, bufferSize);

        this.isOpen = true;
        logger.info("{} - SPI Bus configured.", path);
        return this;
    }

    /**
     * Reads the spidev {@code bufsiz} module parameter, which is the maximum number of bytes accepted
     * by the kernel in a single SPI_IOC_MESSAGE transfer. Falls back to {@link #DEFAULT_BUFFER_SIZE}
     * when the sysfs file is missing, unreadable or holds an unexpected value.
     *
     * @return the maximum transfer chunk size in bytes
     */
    private int readBufferSize() {
        if (FILE.access(SPIDEV_BUFSIZ_PATH, FileFlag.R_OK) != 0) {
            logger.debug("{} - '{}' is not accessible, falling back to default chunk size {}.", path, SPIDEV_BUFSIZ_PATH, DEFAULT_BUFFER_SIZE);
            return DEFAULT_BUFFER_SIZE;
        }
        try {
            var bufsizFd = FILE.open(SPIDEV_BUFSIZ_PATH, FileFlag.O_RDONLY);
            var content = FILE.read(bufsizFd, new byte[MAX_BUFSIZ_FILE_SIZE], MAX_BUFSIZ_FILE_SIZE);
            FILE.close(bufsizFd);
            if (content == null || content.length == 0) {
                return DEFAULT_BUFFER_SIZE;
            }
            var size = Integer.parseInt(new String(content).trim());
            if (size <= 0) {
                logger.warn("{} - '{}' reported non-positive value {}, falling back to default chunk size {}.", path, SPIDEV_BUFSIZ_PATH, size, DEFAULT_BUFFER_SIZE);
                return DEFAULT_BUFFER_SIZE;
            }
            return size;
        } catch (RuntimeException e) {
            logger.warn("{} - could not read '{}', falling back to default chunk size {}: {}", path, SPIDEV_BUFSIZ_PATH, DEFAULT_BUFFER_SIZE, e.getMessage());
            return DEFAULT_BUFFER_SIZE;
        }
    }

    @Override
    public Spi shutdownInternal(Context context) throws ShutdownException {
        FILE.close(spiFileDescriptor);
        return super.shutdownInternal(context);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Performs a full-duplex exchange: clocks out {@code numberOfBytes} from {@code write} while
     * capturing the simultaneously clocked-in bytes into {@code read}. Transfers larger than the
     * driver's {@code bufsiz} are split into separate {@code SPI_IOC_MESSAGE(1)} ioctl calls (chunks
     * are not batched, since the limit applies to the cumulative total within one ioctl), which
     * releases chip-select between chunks.
     *
     * @throws Pi4JException if the SPI bus has been closed
     */
    @Override
    public int transfer(byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes) {
        checkClosed();
        Objects.checkFromIndexSize(readOffset, numberOfBytes, read.length);
        Objects.checkFromIndexSize(writeOffset, numberOfBytes, write.length);

        logger.trace("{} - Transferring data (length '{}') in chunks of at most {} bytes", path, numberOfBytes, bufferSize);
        logger.trace("{} - Write buffer: {}", path, HexFormatter.format(write));

        // Split larger transfers into bufsiz-sized chunks, each issued as its own SPI_IOC_MESSAGE(1)
        // ioctl call. These must NOT be batched into one SPI_IOC_MESSAGE(N): spidev's 'bufsiz' limit
        // applies to the cumulative tx (and rx) total across all transfers within a single ioctl, so
        // batching would sum the chunks back up and fail with EMSGSIZE - exactly what we avoid here.
        var totalRead = 0;
        for (var chunkOffset = 0; chunkOffset < numberOfBytes; chunkOffset += bufferSize) {
            var chunkSize = Math.min(bufferSize, numberOfBytes - chunkOffset);
            var writeData = Arrays.copyOfRange(write, writeOffset + chunkOffset, writeOffset + chunkOffset + chunkSize);
            var spiTransfer = new SpiTransferBuffer(writeData, new byte[chunkSize], chunkSize);
            spiTransfer = IOCTL.call(spiFileDescriptor, Command.getSpiIocMessage(1), spiTransfer);
            var readBytes = spiTransfer.getRxBuffer();
            System.arraycopy(readBytes, 0, read, readOffset + chunkOffset, chunkSize);
            totalRead += readBytes.length;
        }
        logger.trace("{} - Read buffer: {}", path, HexFormatter.format(read));
        return totalRead;
    }

    @Override
    public int write(byte data) {
        return write(new byte[]{data}, 0, 1);
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        return transfer(data, offset, new byte[data.length], 0, length);
    }

    @Override
    public void writeThenRead(byte[] write, byte[] read) {
        writeThenRead(write, 0, write.length, 0, read, 0, read.length);
    }

    @Override
    public void writeThenRead(byte[] write, int readDelayNanos, byte[] read) {
        writeThenRead(write, 0, write.length, readDelayNanos, read, 0, read.length);
    }

    /**
     * {@inheritDoc}
     * <p>
     * When both halves fit within the driver's {@code bufsiz}, the write phase, the optional read delay
     * and the read phase are issued as a single {@code SPI_IOC_MESSAGE(2)} ioctl under one chip-select
     * assertion. Otherwise the write and read phases are split into separate bufsiz-sized
     * {@code SPI_IOC_MESSAGE(1)} calls, which releases chip-select between chunks.
     *
     * @throws Pi4JException if the SPI bus has been closed
     */
    @Override
    public void writeThenRead(byte[] write, int writeOffset, int writeLength, int readDelayNanos, byte[] read, int readOffset, int readLength) {
        checkClosed();
        Objects.checkFromIndexSize(readOffset, readLength, read.length);
        Objects.checkFromIndexSize(writeOffset, writeLength, write.length);

        logger.trace("{} - Write-then-read (write '{}', read '{}') with chunk size {}", path, writeLength, readLength, bufferSize);
        logger.trace("{} - Write buffer: {}", path, HexFormatter.format(write));

        var delayUsecs = readDelayNanos / 1000;

        // Fast path: both halves fit within a single bufsiz, so the whole exchange runs as one
        // SPI_IOC_MESSAGE(2) under a single chip-select assertion (write, delay, then read). The
        // bufsiz limit is checked per direction (tx total vs rx total), so the write-only and
        // read-only transfers are bounded independently.
        if (writeLength <= bufferSize && readLength <= bufferSize) {
            var writeData = Arrays.copyOfRange(write, writeOffset, writeOffset + writeLength);
            // The unused direction must be null (not an empty array): a non-null pointer with a
            // non-zero len makes the kernel copy len bytes into/out of a zero-length buffer.
            var inputBuffer = new SpiTransferBuffer(writeData, null, writeLength, delayUsecs);
            var outputBuffer = new SpiTransferBuffer(null, read, readLength, delayUsecs);

            var transferBuffer = new SpiMultipleTransferBuffer(inputBuffer, outputBuffer);
            transferBuffer = IOCTL.call(spiFileDescriptor, Command.getSpiIocMessage(2), transferBuffer);
            var readBytes = transferBuffer.transferBuffer()[1].getRxBuffer();
            System.arraycopy(readBytes, 0, read, readOffset, readLength);

            logger.trace("{} - Read buffer: {}", path, HexFormatter.format(read));
            return;
        }

        // Chunked path: the write and/or read exceed spidev's bufsiz. They cannot share a single
        // ioctl (the limit is the cumulative tx/rx total per SPI_IOC_MESSAGE call), so the write
        // phase and then the read phase are issued as separate bufsiz-sized SPI_IOC_MESSAGE(1)
        // calls. This releases chip-select between chunks - unavoidable once a transfer is larger
        // than bufsiz; the only way to keep a single assertion is to raise the kernel's bufsiz.
        for (var chunkOffset = 0; chunkOffset < writeLength; chunkOffset += bufferSize) {
            var chunkSize = Math.min(bufferSize, writeLength - chunkOffset);
            var writeData = Arrays.copyOfRange(write, writeOffset + chunkOffset, writeOffset + chunkOffset + chunkSize);
            // apply the read delay only after the final write chunk, i.e. between the write and read phases
            var chunkDelay = chunkOffset + chunkSize >= writeLength ? delayUsecs : 0;
            // null rx (write-only): a non-null zero-length rx buffer would be overrun by the kernel
            var spiTransfer = new SpiTransferBuffer(writeData, null, chunkSize, chunkDelay);
            IOCTL.call(spiFileDescriptor, Command.getSpiIocMessage(1), spiTransfer);
        }

        for (var chunkOffset = 0; chunkOffset < readLength; chunkOffset += bufferSize) {
            var chunkSize = Math.min(bufferSize, readLength - chunkOffset);
            // null tx (read-only): clocks out zeros without reading from a zero-length tx buffer
            var spiTransfer = new SpiTransferBuffer(null, new byte[chunkSize], chunkSize);
            spiTransfer = IOCTL.call(spiFileDescriptor, Command.getSpiIocMessage(1), spiTransfer);
            System.arraycopy(spiTransfer.getRxBuffer(), 0, read, readOffset + chunkOffset, chunkSize);
        }

        logger.trace("{} - Read buffer: {}", path, HexFormatter.format(read));
    }

    @Override
    public int read() {
        return readByte();
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        return transfer(new byte[length], 0, buffer, offset, length);
    }

    @Override
    public byte readByte() {
        var buffer = new byte[1];
        transfer(new byte[1], 0, buffer, 0, 1);
        return buffer[0];
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
