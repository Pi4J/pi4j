package com.pi4j.plugin.mock.provider.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockI2C.java
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

import com.pi4j.io.i2c.*;
import com.pi4j.plugin.mock.Mock;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Objects;

/**
 * Mock, in-memory implementation of the pi4j-core {@link I2C} contract (also implementing
 * {@link I2CRegisterDataReader} and {@link I2CRegisterDataWriter}).
 * <p>
 * No real I2C transaction is performed. Bytes written to the device are appended to an
 * in-memory buffer and reads pop bytes back from that same buffer, so a test can write the
 * "device response" it expects and then read it back. Per-register operations are simulated
 * the same way, with a separate FIFO buffer per register index. This storage is intentionally
 * simplistic and meant only for unit testing the Pi4J I2C APIs.
 *
 * @see MockI2CBus
 */
public class MockI2C extends I2CBase<MockI2CBus> implements I2C, I2CRegisterDataReader, I2CRegisterDataWriter {

    private static final Logger logger = LoggerFactory.getLogger(MockI2C.class);

    /**
     * Per-register in-memory FIFO buffers indexed by register number (0-511), used to simulate
     * register reads and writes. A {@code null} entry means no data has been written to that
     * register yet.
     * <p>
     * ATTENTION:  The storage and management of the byte arrays
     *  are terribly inefficient and not intended for real-world
     *  usage.  These are only intended to unit testing the
     *  Pi4J I2C APIs.
     */
    // Supporting two byte registers values, requires larger Array.  Limit register value to 0x200
    protected ArrayDeque<Byte>[] registers = new ArrayDeque[512]; // 512 supported registers (0-511)
    /**
     * In-memory FIFO buffer backing the raw (non-register) device read/write methods. Writes
     * append to the tail and reads pop from the head.
     */
    protected ArrayDeque<Byte> raw = new ArrayDeque<>();

    /**
     * Creates a mock I2C device, wiring it to a {@link MockI2CBus} built from the same config.
     *
     * @param provider the {@link I2CProvider} that created this instance
     * @param config the {@link I2CConfig} identifying the simulated bus and device address
     */
    public MockI2C(I2CProvider provider, I2CConfig config){
        super(provider, config, new MockI2CBus(config));
        logger.debug("[{}::{}] :: CREATE(BUS={}; DEVICE={})",
            Mock.I2C_PROVIDER_NAME, this.id, config.bus(), config.device());
    }

    @Override
    public void close() {
        super.close();
        logger.debug("[{}::{}] :: CLOSE(BUS={}; DEVICE={})",
            Mock.I2C_PROVIDER_NAME, this.id, config.bus(), config.device());
    }

    // -------------------------------------------------------------------
    // RAW DEVICE WRITE FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int write(byte b) {
        raw.add(b);
        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: WRITE(0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, StringUtil.toHexString(b));
        }
        return 0;
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        for(int p = offset; p-offset < length; p++){
            raw.add(data[p]); // add to internal buffer
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: WRITE(0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, StringUtil.toHexString(data, offset, length));
        }
        return length;
    }

    @Override
    public int write(Charset charset, CharSequence data) {
        byte[] buffer = data.toString().getBytes(charset);
        for (byte b : buffer) {
            raw.add(b); // add to internal buffer
        }
        logger.debug("[{}::{}] :: WRITE(0x{})", Mock.I2C_PROVIDER_NAME, this.id, data);
        return data.length();
    }

    // -------------------------------------------------------------------
    // RAW DEVICE READ FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * Pops and returns the next byte from the in-memory raw buffer, or {@code -1} when the
     * buffer is empty (simulating no data available).
     */
    @Override
    public int read() {
        if(raw.isEmpty()) return -1;
        byte b = raw.pop();
        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: READ(0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, StringUtil.toHexString(b));
        }
        return b;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Pops up to {@code length} bytes from the in-memory raw buffer into the supplied array,
     * returning the number actually copied, or {@code -1} when the buffer is empty.
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);

        if(raw.isEmpty()) return -1;
        int counter = 0;
        for(int p = 0; p < length; p++) {
            if(p+offset > buffer.length) break;
            if(raw.isEmpty()) break;
            buffer[offset + p] = raw.pop();
            counter++;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: READ(0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, StringUtil.toHexString(buffer, offset, length));
        }

        return counter;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Pops up to {@code length} bytes from the in-memory raw buffer and decodes them with the
     * given charset, or returns {@code null} when the buffer is empty.
     */
    @Override
    public String readString(Charset charset, int length) {
        if(raw.isEmpty()) return null;
        byte[] buffer = new byte[length];
        for(int p = 0; p < length; p++) {
            if(raw.isEmpty()) break;
            buffer[p] = raw.pop();
        }
        String result = new String(buffer, charset);
        logger.debug("[{}::{}] :: READ()", Mock.I2C_PROVIDER_NAME, this.id, result);
        return result;
    }

    // -------------------------------------------------------------------
    // DEVICE REGISTER WRITE FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int writeRegister(int register, byte b) {

        if (registers[register] == null) {
            registers[register] = new ArrayDeque<Byte>();
        }
        registers[register].add(b);

        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: WRITE(REG={}, 0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, register, StringUtil.toHexString(b));
        }

        return 0;
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        if (registers[register] == null) {
            registers[register] = new ArrayDeque<Byte>();
        }
        for(int p = offset; p-offset < length; p++){
            registers[register].add(data[p]);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: WRITE(REG={}, 0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, register, StringUtil.toHexString(data, offset, length));
        }

        return length;
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        int internalOffset = (register[0] & 0xff) + (register[1] << 8);

        if(registers[internalOffset] == null) registers[internalOffset] = new ArrayDeque<Byte>();
        for(int p = offset; p-offset < length; p++){
            registers[internalOffset].add(data[p]);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: WRITEREGISTER(REG=(two byte offset LSB first) {}, Chip register offset Decimal : {}  Hex : {}, offset = {}, User data: 0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, StringUtil.toHexString(register, 0, register.length),
                internalOffset, String.format("%02X", internalOffset), String.format("%02X", offset),
                StringUtil.toHexString(data, offset, length));
        }

        return length;
    }

    @Override
    public int writeRegister(int register, Charset charset, CharSequence data) {
        if (registers[register] == null) {
            registers[register] = new ArrayDeque<Byte>();
        }
        byte[] buffer = data.toString().getBytes(charset);
        for (int p = 0; p < buffer.length; p++) {
            registers[register].add(buffer[p]); // add to internal buffer
        }

        logger.debug("[{}::{}] :: WRITE(REG={}, 0x{})", Mock.I2C_PROVIDER_NAME, this.id, register, data);

        return data.length();
    }

    // -------------------------------------------------------------------
    // DEVICE REGISTER READ FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * Pops and returns the next byte previously written to the simulated register's in-memory
     * buffer.
     *
     * @throws IllegalStateException if the register has never been written to or its buffer is empty
     */
    @Override
    public int readRegister(int register) {
        if(registers[register] == null) throw new IllegalStateException("No available data to read");
        if(registers[register].isEmpty()) throw new IllegalStateException("No available data to read");
        byte b = registers[register].pop();

        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: WRITE(REG={}, 0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, register, StringUtil.toHexString(b));
        }

        return b;
    }


    /**
     * {@inheritDoc}
     * <p>
     * Resolves the two-byte {@code register} address (LSB first) to an internal index, then
     * pops up to {@code length} bytes from that register's simulated buffer into {@code buffer},
     * returning the number copied or {@code -1} when no data has been written there.
     */
    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        int internalOffset = (register[0] & 0xff) + (register[1] << 8);

        if (registers[internalOffset] == null) {
            return -1;
        }
        if (registers[internalOffset].isEmpty()) {
            return -1;
        }

        int counter = 0;
        for (int p = 0; p < length; p++) {
            if(p+offset > buffer.length) break;
            if(registers[internalOffset].isEmpty()) break;
            buffer[offset + p] = registers[internalOffset].pop();
            counter++;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: READREGISTER(REG= (two byte offset LSB first) {}, offset = {}, Chip register offset Decimal : {}  Hex : {}, 0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, StringUtil.toHexString(register, 0, register.length),
                String.format("%02X", offset), internalOffset, String.format("%02X", internalOffset),
                StringUtil.toHexString(buffer, offset, length));
        }

        return counter;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Pops up to {@code length} bytes from the simulated register's in-memory buffer into
     * {@code buffer}, returning the number copied or {@code -1} when no data has been written
     * to that register.
     */
    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        if(registers[register] == null) return -1;
        if(registers[register].isEmpty()) return -1;

        int counter = 0;
        for(int p = 0; p < length; p++) {
            if(p+offset > buffer.length) break;
            if(registers[register].isEmpty()) break;
            buffer[offset + p] = registers[register].pop();
            counter++;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[{}::{}] :: WRITE(REG={}, 0x{})",
                Mock.I2C_PROVIDER_NAME, this.id, register, StringUtil.toHexString(buffer, offset, length));
        }

        return counter;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Pops up to {@code length} bytes from the simulated register's in-memory buffer and
     * decodes them with the given charset, or returns {@code null} when no data has been
     * written to that register.
     */
    @Override
    public String readRegisterString(int register, Charset charset, int length) {
        if(registers[register] == null) return null;
        if(registers[register].isEmpty()) return null;

        byte[] buffer = new byte[length];
        for(int p = 0; p < length; p++) {
            if(registers[register].isEmpty()) break;
            buffer[p] = registers[register].pop();
        }
        String result = new String(buffer, charset);

        logger.debug("[{}::{}] :: WRITE(REG={}, 0x{})", Mock.I2C_PROVIDER_NAME, this.id, register, result);

        return result;
    }
}
