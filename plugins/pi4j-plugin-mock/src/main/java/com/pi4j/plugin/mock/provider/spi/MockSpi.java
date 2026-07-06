package com.pi4j.plugin.mock.provider.spi;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBase;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.plugin.mock.Mock;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Objects;

/**
 * Mock, in-memory implementation of the pi4j-core {@link Spi} contract, extending {@link SpiBase}.
 * Instead of communicating over a real SPI bus it keeps a single in-memory FIFO buffer: bytes
 * written by {@code write(...)} (or supplied to {@code transfer(...)}) are appended to the buffer,
 * and {@code read(...)} / {@code transfer(...)} consume bytes from the front of that same buffer.
 * This lets tests prime mock responses and later verify what was written.
 */
public class MockSpi extends SpiBase implements Spi {

    private static final Logger logger = LoggerFactory.getLogger(MockSpi.class);
    private final String logPreamble;

    /** In-memory FIFO buffer holding bytes written to (and read from) this mock SPI device. */
    protected ArrayDeque<Byte> raw = new ArrayDeque<>();

    /**
     * Creates a mock SPI instance for the given provider and configuration, logging the simulated
     * open with the configured channel and baud rate.
     *
     * @param provider the {@link SpiProvider} that created this instance
     * @param config   the {@link SpiConfig} describing the SPI channel and baud rate
     */
    public MockSpi(SpiProvider provider, SpiConfig config) {
        super(provider, config);
        logPreamble = "[" + Mock.SPI_PROVIDER_NAME + "::" + this.id + "] ::";
        logger.info("{} OPEN(CHANNEL={}; BAUD={})", logPreamble, config.channel(), config.baud());
    }

    /**
     * Test helper that drains and returns the entire contents of this mock's in-memory buffer.
     * <p>
     * It returns all bytes accumulated by {@code write(...)} or {@code transfer(...)} calls that
     * have not yet been consumed by {@code read(...)} or {@code transfer(...)} calls. The buffer is
     * emptied as a side effect, allowing a test to assert exactly what was written to the device.
     *
     * @return the remaining buffered bytes, in the order they were written (oldest first); an empty
     *         array if the buffer is empty
     */
    public byte[] readEntireMockBuffer() {
        var bytes = new byte[raw.size()];
        for (int i = 0; !raw.isEmpty(); ++i) {
            bytes[i] = raw.pop();
        }
        logger.info("{} READALL (0x{})", logPreamble, StringUtil.toHexString(bytes));
        return bytes;
    }

    @Override
    public void close() {
        logger.info("{} CLOSE(CHANNEL={}; BAUD={})", logPreamble, config.channel(), config.baud());
        super.close();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Simulates a full-duplex SPI transfer against the in-memory buffer: it first pops up to
     * {@code numberOfBytes} previously buffered ("prepared") bytes into the {@code read} buffer,
     * then appends the supplied {@code write} bytes to the buffer for later verification. Always
     * reports success.
     */
    @Override
    public int transfer(byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes) {
        byte[] prepared = new byte[numberOfBytes];
        // read the (potentially) prepared mock data
        readNoLogging(prepared, 0, numberOfBytes);
        //write the provided data for later verification
        writeNoLogging(write, writeOffset, numberOfBytes);

        // for every byte of the 'write' buffer, transfer a byte
        // from the prepared data to the 'read' buffer.
        int offsetIndex = readOffset;
        for (byte preparedByte : prepared) {
            read[offsetIndex++] = preparedByte;
        }
        logger.info("{} TRANSFER(READ(0x{}), WRITE(0x{})", logPreamble, StringUtil.toHexString(prepared), StringUtil.toHexString(write, writeOffset, numberOfBytes));
        // code for 'OK'
        return 0;
    }

    @Override
    public int write(byte b) {
        raw.add(b);
        logger.info("{} WRITE(0x{})", logPreamble, StringUtil.toHexString(b));
        return 0;
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        writeNoLogging(data, offset, length);
        logger.info("{} WRITE(0x{})", logPreamble, StringUtil.toHexString(data, offset, length));
        return length;
    }

    private void writeNoLogging(byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        for (int p = offset; p - offset < length; p++) {
            raw.add(data[p]); // add to internal buffer
        }
    }

    @Override
    public int write(Charset charset, CharSequence data) {
        byte[] buffer = data.toString().getBytes(charset);
        for (int p = 0; p < buffer.length; p++) {
            raw.add(buffer[p]); // add to internal buffer
        }
        logger.info("{} WRITE(\"{}\")", logPreamble, data);
        return data.length();
    }

    @Override
    public int read() {
        if (raw.isEmpty()) return -1;
        byte b = raw.pop();
        logger.info("{} READ (0x{})", logPreamble, StringUtil.toHexString(b));
        return b;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        Integer counter = readNoLogging(buffer, offset, length);
        if (counter == null) return -1;

        logger.info("{} READ (0x{})", logPreamble, StringUtil.toHexString(buffer, offset, length));

        return counter;
    }

    private Integer readNoLogging(byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);

        if (raw.isEmpty()) return null;
        int counter = 0;
        for (int p = 0; p < length; p++) {
            if (p + offset > buffer.length) break;
            if (raw.isEmpty()) break;
            buffer[offset + p] = raw.pop();
            counter++;
        }
        return counter;
    }
}
