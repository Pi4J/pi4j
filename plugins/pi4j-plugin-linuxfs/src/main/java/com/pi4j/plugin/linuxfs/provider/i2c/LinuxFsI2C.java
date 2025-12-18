package com.pi4j.plugin.linuxfs.provider.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  PiGpioI2C.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.*
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.linuxfs.util.SystemUtil;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Objects;

/**
 * <p>PiGpioI2C class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsI2C extends I2CBase<LinuxFsI2CBus> implements I2C {

    private final LinuxFsI2CBus i2CBus;

    /**
     * <p>Constructor for PiGpioI2C.</p>
     *
     * @param i2CBus
     * @param provider
     *     a {@link I2CProvider} object.
     * @param config
     *     a {@link I2CConfig} object.
     */
    public LinuxFsI2C(LinuxFsI2CBus i2CBus, I2CProvider provider, I2CConfig config) {
        super(provider, config, i2CBus);
        this.i2CBus = i2CBus;
    }

    // -------------------------------------------------------------------
    // RAW DEVICE WRITE FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte b) {
        return this.i2CBus.execute(this, file -> {
            file.write(b);
            return 1;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        return this.i2CBus.execute(this, file -> {
            file.write(data, offset, length);
            return length;
        });
    }

    // -------------------------------------------------------------------
    // RAW DEVICE READ FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        return this.i2CBus.execute(this, RandomAccessFile::read);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        return this.i2CBus.execute(this, file -> file.read(buffer, offset, length));
    }

    // -------------------------------------------------------------------
    // DEVICE REGISTER WRITE FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(int register, byte b) {
        return write((byte) register, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        byte[] tmp = new byte[length + 1];
        tmp[0] = (byte) register;
        System.arraycopy(data, offset, tmp, 1, length);
        return write(tmp);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        byte[] tmp = new byte[length + register.length];
        System.arraycopy(register,0, tmp, 0, register.length);
        System.arraycopy(data, offset, tmp, register.length, length);
        int rc = write(tmp);
        return (rc - register.length);  // do not include the register bytes as what was written...
    }

    // -------------------------------------------------------------------
    // DEVICE REGISTER READ FUNCTIONS
    // -------------------------------------------------------------------



    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register) {
        byte reg[] = new byte[1];
        reg[0] = (byte) (register & 0xff);
        byte buffer[] = new byte[1];
        this.readRegister(reg, buffer,0,buffer.length);
        int rVal =  buffer[0] & 0xff;
        return rVal;
    }

    /**
     * {@inheritDoc}
     */
     @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
         byte reg[] = new byte[1];
         reg[0] = (byte) (register & 0xff);
         int rCode = this.readRegister(reg, buffer, offset, length);
         return rCode;
    }


     /**
     * {@inheritDoc}
      *
      * <p>  This function uses the IOCTL interface to the LinuxFS. This
      * is required so the I2C transaction uses an I2C RESTART.
      * The following details explain creating the ByteBuffers used
      * to create the 'C" structures for the IOCTL. </p>
      * <p>i2c_msg: The space *buf will be used to set a pointer to the data buffer.
      * This pointer must be aligned to the machines 4 or 8 byte alignment.
      * </p>
      * <p>
      * This alignment is accomplished when the pointer is 'put' into the
      * ByteBuffer
      * </p>
      *
      *<ul>
      *<li> struct i2c_msg {</li>
      * <li>        __u16 addr;</li>
      * <li>        __u16 flags;</li>
      * <li>        __u16 len;</li>
      * <li>        __u8 *buf;    wordSize</li>
      * <li>        u8 buffer</li>
      * <li>};</li>
      *</ul>
      * <p>
      *     The ioctl command is  I2CConstants.I2C_RDWR, this command requires
      *     two i2c_msg entries.
      *</p>
      * <p> The following describes the ByteBuffer, ioctlData,  contents:  </p>
      *
      * <ul>
      * <li>     two byte address entry one</li>
      * <li>     two byte len entry one</li>
      * <li>     Possible padding for alignment</li>
      * <li>     word size area for pointer to write data buffer</li>
      * <li>     two byte address entry two</li>
      * <li>     two byte flags entry two</li>
      * <li>     two byte len entry two</li>
      * <li>     Possible padding for alignment</li>
      * <li>     word size area for pointer to read data buffer</li>
      * <li>     Bytes required to contain write buffer contents</li>
      * <li>     Bytes required to contain read data buffer</li>
      *</ul>
      *
      * <p> There is a second byte buffer, offsets, this specifies the
      *     ByteBuffer offset of any pointer paired with the ByteBuffer offset
      *     of the data pointed to. </p>
      *
      *     This ByteBuffer contents
      *<ul>
      *<li>      ByteBuffer position start of pointer to write data buffer</li>
      *<li>      ByteBuffer position start of write buffer contents</li>
      *<li>      ByteBuffer position start of pointer to read data buffer</li>
      * <li>     ByteBuffer position start of read buffer contents</li>
      *</ul>
      *
      */
    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        // command I2C_RDWR
        long command = I2CConstants.I2C_RDWR;
        // create byte buffer containing the i2c messages
        // address,flags, number of bytes data, pointer
        // First message to write device register
        short deviceAddr = (short) (this.config.device()& 0xff);
        short writeFlags = (short) (I2CConstants.I2C_SMBUS_WRITE & 0xff);
        short writeLength = (short) register.length;
        // two pointers will be used so 2 pairs of offset entries
        IntBuffer offsets = IntBuffer.allocate(4);
         // create ByteBuffer, load with the write details Matches i2c provider
        ByteBuffer ioctlData = ByteBuffer.allocate(2048);
        // Ensures Pi BCM little_endian
        ioctlData.order(ByteOrder.nativeOrder());
        ioctlData.putShort(deviceAddr);
        ioctlData.putShort(writeFlags);
        ioctlData.putShort(writeLength);
        // Before creating the pointer entry space, see if we are properly aligned
        int wrtAlignValue = ioctlData.position()%SystemUtil.getWordSize();
        // test if current position is less than word size, if less move position to word boundary
        //  If greater than word size set position to next word boundary
        if(ioctlData.position() <SystemUtil.getWordSize()) {
            ioctlData.position( (SystemUtil.getWordSize() - ioctlData.position()) +  ioctlData.position());
        }else if(wrtAlignValue != 0){
            ioctlData.position(ioctlData.position()+ (SystemUtil.getWordSize() - wrtAlignValue) );
        }
        // first pair of offset info
        offsets.put(ioctlData.position());
        byte[] wrtPtrData = new byte[SystemUtil.getWordSize()];
        ioctlData.put(wrtPtrData);

        // second message to read back from device
        short readFlags = (short) (I2CConstants.I2C_M_RD & 0xff);
        short readLength = (short) length;
        byte[] readBuff =  new byte[length];
        //  load ByteBuffer with read details
        ioctlData.putShort(deviceAddr);
        ioctlData.putShort(readFlags);
        ioctlData.putShort(readLength);
        // Before creating the pointer entry space, see if we are properly aligned
        // If not aligned, the pointer entry will be moved right the required number
        // of byte to be properly aligned
        // For consistency, test if current position is less than word size, if less move position to word boundary
        //  If greater than word size set position to next word boundary
        int rdAlignValue = ioctlData.position()%SystemUtil.getWordSize();
        if(ioctlData.position() <SystemUtil.getWordSize()) {
            ioctlData.position( (SystemUtil.getWordSize() - ioctlData.position()) +  ioctlData.position());
        }else if(rdAlignValue != 0){
            ioctlData.position(ioctlData.position()+(SystemUtil.getWordSize() - rdAlignValue));
        }

        byte[] rdPtrData = new byte[SystemUtil.getWordSize()];
        // second pair of offset info, position 2
        offsets.put(2,ioctlData.position());
        ioctlData.put(rdPtrData);



        int regBuffPosition = ioctlData.position();
        // part of first pair, position 1
        offsets.put(1, regBuffPosition);
        ioctlData.put(register);
        int readBuffPosition = ioctlData.position();
        // last section of second pair, position 3
        offsets.put(3,readBuffPosition);
        ioctlData.put(readBuff);
        // restore offset position to expected value, It was not at 4 as we put data into offset
        // out of order
        offsets.position(4);
        // set to start of your data
        ioctlData.flip();
        offsets.flip();

       this.i2CBus.executeIOCTL(this, command, ioctlData, offsets);

        // move results back into user buffer. Location determined by user supplied offset
        for (int i = 0; i < length; i++) {
            buffer[i + offset] = ioctlData.get(readBuffPosition + i);
        }

        return readLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeReadRegisterWord(int register, int word) {
        byte reg[] = new byte[3];
        reg[0] = (byte) (register & 0xff);
        reg[1] = (byte) (word & 0xff);
        reg[2] = (byte) ((word >> 8) & 0xff);
        byte buff[] = new byte[2];
        word = (buff[1] << 8)  | buff[0];
        return word;

    }
}
