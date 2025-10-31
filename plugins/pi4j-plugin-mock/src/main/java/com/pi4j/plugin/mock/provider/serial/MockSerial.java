package com.pi4j.plugin.mock.provider.serial;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockSerial.java
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

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialBase;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialProvider;
import com.pi4j.plugin.mock.Mock;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Objects;

/**
 * <p>MockSerial class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class MockSerial extends SerialBase implements Serial {

    private static final Logger logger = LoggerFactory.getLogger(MockSerial.class);

    /**
     * ATTENTION:  The storage and management of the byte arrays
     * are terribly inefficient and not intended for real-world
     * usage.  These are only intended to unit testing the
     * Pi4J SERIAL APIs.
     */
    protected ArrayDeque<Byte> raw = new ArrayDeque<>();

    /**
     * <p>Constructor for MockSerial.</p>
     *
     * @param provider a {@link com.pi4j.io.serial.SerialProvider} object.
     * @param config   a {@link com.pi4j.io.serial.SerialConfig} object.
     */
    public MockSerial(SerialProvider provider, SerialConfig config) {
        super(provider, config);
        logger.info(" [");
        logger.info(Mock.SERIAL_PROVIDER_NAME);
        logger.info("::");
        logger.info(this.id);
        logger.info("] :: OPEN(DEVICE={}; BAUD={})", config.device(), config.baud());
        logger.info("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        return raw.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        logger.info(" [");
        logger.info(Mock.SERIAL_PROVIDER_NAME);
        logger.info("::");
        logger.info(this.id);
        logger.info("] :: CLOSE(DEVICE={}; BAUD={})", config.device(), config.baud());
        logger.info("");
        super.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte b) {
        raw.add(b);
        logger.info(" [");
        logger.info(Mock.SERIAL_PROVIDER_NAME);
        logger.info("::");
        logger.info(this.id);
        logger.info("] :: WRITE(0x");
        logger.info(StringUtil.toHexString(b));
        logger.info(")");
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        for (int p = offset; p - offset < length; p++) {
            raw.add(data[p]); // add to internal buffer
        }
        logger.info(" [");
        logger.info(Mock.SERIAL_PROVIDER_NAME);
        logger.info("::");
        logger.info(this.id);
        logger.info("] :: WRITE(0x");
        logger.info(StringUtil.toHexString(data, offset, length));
        logger.info(")");
        return length;
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
        logger.info(" [");
        logger.info(Mock.SERIAL_PROVIDER_NAME);
        logger.info("::");
        logger.info(this.id);
        logger.info("] :: WRITE(\"");
        logger.info(data.toString());
        logger.info("\")");
        return data.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        if (raw.isEmpty()) return -1;
        byte b = raw.pop();
        logger.info(" [");
        logger.info(Mock.SERIAL_PROVIDER_NAME);
        logger.info("::");
        logger.info(this.id);
        logger.info("] :: READ (0x");
        logger.info(StringUtil.toHexString(b));
        logger.info(")");
        return b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);

        if (raw.isEmpty()) return -1;
        int counter = 0;
        for (int p = 0; p < length; p++) {
            if (p + offset > buffer.length) break;
            if (raw.isEmpty()) break;
            buffer[offset + p] = raw.pop();
            counter++;
        }

        logger.info(" [");
        logger.info(Mock.SERIAL_PROVIDER_NAME);
        logger.info("::");
        logger.info(this.id);
        logger.info("] :: READ (0x");
        logger.info(StringUtil.toHexString(buffer, offset, length));
        logger.info(")");

        return counter;
    }
}
