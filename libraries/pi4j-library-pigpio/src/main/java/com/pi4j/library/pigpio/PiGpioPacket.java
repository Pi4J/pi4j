package com.pi4j.library.pigpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PiGpioPacket.java
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

import com.pi4j.library.pigpio.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * typedef struct
 * {
 * uint32_t cmd;
 * uint32_t p1;
 * uint32_t p2;
 * union
 * {
 * uint32_t p3;
 * uint32_t ext_len;
 * uint32_t res;
 * };
 * } cmdCmd_t;
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioPacket {

    private static final Logger logger = LoggerFactory.getLogger(PiGpioPacket.class);

    private PiGpioCmd cmd;
    private int p1 = 0;
    private int p2 = 0;
    private int p3 = 0;
    private byte[] data = new byte[0];

    /**
     * <p>Constructor for PiGpioPacket.</p>
     */
    public PiGpioPacket() {
    }

    /**
     * <p>Constructor for PiGpioPacket.</p>
     *
     * @param cmd a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     */
    public PiGpioPacket(PiGpioCmd cmd) {
        this.cmd(cmd);
    }

    /**
     * <p>Constructor for PiGpioPacket.</p>
     *
     * @param cmd a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     * @param p1  a int.
     */
    public PiGpioPacket(PiGpioCmd cmd, int p1) {
        this.cmd(cmd).p1(p1);
    }

    /**
     * <p>Constructor for PiGpioPacket.</p>
     *
     * @param cmd a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     * @param p1  a int.
     * @param p2  a int.
     */
    public PiGpioPacket(PiGpioCmd cmd, int p1, int p2) {
        this.cmd(cmd).p1(p1).p2(p2);
    }

    /**
     * <p>Constructor for PiGpioPacket.</p>
     *
     * @param cmd  a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     * @param p1   a int.
     * @param p2   a int.
     * @param data an array of {@link Byte} objects.
     */
    public PiGpioPacket(PiGpioCmd cmd, int p1, int p2, byte[] data) {
        this.cmd(cmd).p1(p1).p2(p2).data(data);
    }

    /**
     * <p>cmd.</p>
     *
     * @return a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     */
    public PiGpioCmd cmd() {
        return this.cmd;
    }

    /**
     * <p>cmd.</p>
     *
     * @param cmd a {@link com.pi4j.library.pigpio.PiGpioCmd} object.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket cmd(PiGpioCmd cmd) {
        this.cmd = cmd;
        return this;
    }

    /**
     * <p>p1.</p>
     *
     * @return a int.
     */
    public int p1() {
        return this.p1;
    }

    /**
     * <p>p1.</p>
     *
     * @param p1 a int.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket p1(int p1) {
        this.p1 = p1;
        return this;
    }

    /**
     * <p>p2.</p>
     *
     * @return a int.
     */
    public int p2() {
        return this.p2;
    }

    /**
     * <p>p2.</p>
     *
     * @param p2 a int.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket p2(int p2) {
        this.p2 = p2;
        return this;
    }

    /**
     * <p>p3.</p>
     *
     * @return a int.
     */
    public int p3() {
        return this.p3;
    }

    /**
     * <p>p3.</p>
     *
     * @param p3 a int.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    protected PiGpioPacket p3(int p3) {
        this.p3 = p3;
        return this;
    }

    // the following methods are actaully returning the "P3" value as P3
    // is a C union and used for multiple purposes depending on context

    /**
     * <p>result.</p>
     *
     * @return a int.
     */
    public int result() {
        return p3();
    }

    /**
     * <p>success.</p>
     *
     * @return a boolean.
     */
    public boolean success() {
        return p3() >= 0;
    }

    /**
     * <p>hasData.</p>
     *
     * @return a boolean.
     */
    public boolean hasData() {
        if (this.data == null) return false;
        return this.data.length > 0;
    }

    /**
     * <p>dataLength.</p>
     *
     * @return a int.
     */
    public int dataLength() {
        if (this.data == null) return 0;
        return this.data.length;
    }

    /**
     * <p>data.</p>
     *
     * @return an array of {@link Byte} objects.
     */
    public byte[] data() {
        return this.data;
    }

    /**
     * <p>data.</p>
     *
     * @param data a {@link java.lang.CharSequence} object.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket data(CharSequence data) {
        return this.data(data.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * <p>data.</p>
     *
     * @param data an array of {@link Byte} objects.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket data(byte[] data) {
        return data(data, data.length);
    }

    /**
     * <p>data.</p>
     *
     * @param data   an array of {@link Byte} objects.
     * @param length a int.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket data(byte[] data, int length) {
        return data(data, 0, data.length);
    }

    /**
     * <p>data.</p>
     *
     * @param data   an array of {@link Byte} objects.
     * @param offset a int.
     * @param length a int.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket data(byte[] data, int offset, int length) {
        // check for valid data
        if (data != null && data.length > 0 && length > 0) {
            this.p3 = length;
            this.data = Arrays.copyOfRange(data, offset, offset + length);
        } else {
            this.p3 = 0;
            this.data = new byte[0];
        }
        return this;
    }

    /**
     * <p>data.</p>
     *
     * @param value a int.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket data(int value) {
        // check for valid value
        if (value > 0) {
            this.p3 = 4; // 4 bytes length
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(value);
            this.data = buffer.array();
        } else {
            this.p3 = 0;
            this.data = new byte[0];
        }
        return this;
    }


    /**
     * <p>data.</p>
     *
     * @param value a byte.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     */
    public PiGpioPacket data(byte value) {
        // check for valid value
        if (value > 0) {
            this.p3 = 4; // 4 bytes length
            this.data = new byte[]{value, 0, 0, 0}; // little endian
        } else {
            this.p3 = 0;
            this.data = new byte[0];
        }
        return this;
    }

    /**
     * <p>dataToString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String dataToString() {
        if (data == null) return null;
        if (data.length == 0) return "";
        return new String(data, StandardCharsets.US_ASCII);
    }

    /**
     * <p>decode.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     * @return a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     * @throws IOException if an error occurs accessing {@code stream}.
     */
    public static PiGpioPacket decode(InputStream stream) throws IOException {
        // read only header bytes
        byte[] header = stream.readNBytes(16);

        ByteBuffer rx = ByteBuffer.wrap(header);
        rx.order(ByteOrder.LITTLE_ENDIAN);

        // parse packet parameters from raw received bytes
        PiGpioCmd cmd = PiGpioCmd.from(rx.getInt()); // CMD <4 bytes :: 0-3>
        int p1 = rx.getInt();                        // P1  <4 bytes :: 4-7>
        int p2 = rx.getInt();                        // P2  <4 bytes :: 8-11>
        int p3 = rx.getInt();                        // P3  <4 bytes :: 12-15>

        // create new packet
        PiGpioPacket packet = new PiGpioPacket(cmd, p1, p2)
            .p3(p3); // set RAW P3 value

        int remaining = bytesToRead(packet, stream);

        if (remaining > 0) {
            var temp = stream.readNBytes(remaining);
            packet.data(temp);
        }
        return packet;
    }

    /**
     * The packet may indicate the number of bytes to expect from the stream.
     * <p>
     * E.g. I2C packets provide this value via {@link PiGpioPacket#p3}
     *
     * @param packet the packet being read
     * @param stream incoming data
     * @return the number of bytes to expect to read from the input stream
     * @throws IOException
     */
    static int bytesToRead(PiGpioPacket packet, InputStream stream) throws IOException {
        switch (packet.cmd) {
            case I2CRI:
            case I2CRD:
                return packet.p3;
            default:
                return stream.available();
        }
    }

    /**
     * <p>encode.</p>
     *
     * @param packet a {@link com.pi4j.library.pigpio.PiGpioPacket} object.
     * @return an array of {@link Byte} objects.
     */
    public static byte[] encode(PiGpioPacket packet) {
        // create byte array and byte buffer using LITTLE ENDIAN for the ARM platform
        byte[] bytes = new byte[16 + packet.dataLength()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // place packet values into structured data bytes
        buffer.putInt(packet.cmd().value());  // CMD
        buffer.putInt((packet.p1()));         // <P1>
        buffer.putInt((packet.p2()));         // <P2>
        buffer.putInt((packet.p3()));         // <P3>
        if (packet.data != null && packet.data.length > 0) {
            buffer.put(packet.data());        // <DATA>
        }

        // return byte array
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        //if(this.data != null && this.data.length > 0)
        if (p3() > 0)
            return String.format("CMD=%s(%d); P1=%d; P2=%d; P3=%d; PAYLOAD=[0x%s]", cmd().name(), cmd().value(), p1(), p2(), p3(), StringUtil.toHexString(data()));
        else
            return String.format("CMD=%s(%d); P1=%d; P2=%d; P3=%d", cmd().name(), cmd().value(), p1(), p2(), p3());
    }
}