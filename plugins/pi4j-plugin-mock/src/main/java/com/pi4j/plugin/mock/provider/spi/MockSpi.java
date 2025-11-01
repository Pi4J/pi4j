package com.pi4j.plugin.mock.provider.spi;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockSpi.java
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
 * <p>MockSpi class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class MockSpi extends SpiBase implements Spi {

    private static final Logger logger = LoggerFactory.getLogger(MockSpi.class);
    private final String logPreamble;

    protected ArrayDeque<Byte> raw = new ArrayDeque<>();

    /**
     * <p>Constructor for MockSpi.</p>
     *
     * @param provider a {@link com.pi4j.io.spi.SpiProvider} object.
     * @param config   a {@link com.pi4j.io.spi.SpiConfig} object.
     */
    public MockSpi(SpiProvider provider, SpiConfig config) {
        super(provider, config);
        logPreamble = "[" + Mock.SPI_PROVIDER_NAME + "::" + this.id + "] ::";
        logger.info("{} OPEN(CHANNEL={}; BAUD={})", logPreamble, config.channel(), config.baud());
    }

    /**
     * <p>Lets the tester read all the data in this mocks raw buffer.</p>
     * <p>
     * It returns all the data that has been accumulated by write() or transfer() calls
     * and not yet been consumed by read() or transfer() calls.
     *
     * @return the bytes in the buffer
     */
    public byte[] readEntireMockBuffer() {
        var bytes = new byte[raw.size()];
        for (int i = 0; !raw.isEmpty(); ++i) {
            bytes[i] = raw.pop();
        }
        logger.info("{} READALL (0x{})", logPreamble, StringUtil.toHexString(bytes));
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        logger.info("{} CLOSE(CHANNEL={}; BAUD={})", logPreamble, config.channel(), config.baud());
        super.close();
    }

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte b) {
        raw.add(b);
        logger.info("{} WRITE(0x{})", logPreamble, StringUtil.toHexString(b));
        return 0;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(Charset charset, CharSequence data) {
        byte[] buffer = data.toString().getBytes(charset);
        for (int p = 0; p < buffer.length; p++) {
            raw.add(buffer[p]); // add to internal buffer
        }
        logger.info("{} WRITE(\"{}\")", logPreamble, data);
        return data.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        if (raw.isEmpty()) return -1;
        byte b = raw.pop();
        logger.info("{} READ (0x{})", logPreamble, StringUtil.toHexString(b));
        return b;
    }

    /**
     * {@inheritDoc}
     */
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
