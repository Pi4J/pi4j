package com.pi4j.plugin.ffm.providers.i2c.impl;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CBus;


abstract class I2CCommonIntf extends I2CBase<FFMI2CBus> {
    /**
     * Creates an I2C device bound to the given provider, configuration and bus, marking it as open.
     *
     * @param context  the context this device belongs to
     * @param config   the configuration describing the bus and device address
     * @param i2CBus   the bus instance used to serialize access for this device
     */
    protected I2CCommonIntf(Context context, I2CConfig config, FFMI2CBus i2CBus) {
        super(context, config, i2CBus);
    }

    /**
     * {@inheritDoc}
     * <p>
     * In addition to releasing the device, this closes the underlying {@link FFMI2CBus}, releasing its
     * I2C bus file descriptor.
     */
    @Override
      public void close() {
        if (closed) {
            return;
        }
        i2CBus.close();
        super.close();
    }


    @Override
    public int read() {
        return internalRead();
    }

    abstract int internalRead();


    @Override
    /**
     * Read a single raw byte (8-bit) value from the I/O device.
     *
     * @return The 8-bit byte value
     *
    */
    public byte readByte() {
        return  internalReadByte();
    }

    abstract public byte internalReadByte();

    @Override
    public int read(byte[] buffer, int offset, int length) {
        internalRead(buffer, offset, length);
        return length;
    }

    abstract  byte[] internalRead(byte[] buffer, int offset, int length) ;



    @Override
    public int write(byte b) {
        return internalWriteByte(b);
   }

   abstract  int internalWriteByte(byte b);

    /**
     * Write data to the I/O device.
     *
     * @param data the data to write
     * @return the number of bytes successfully written
     */
    abstract  int internalWrite(byte[] data);

    @Override
    public int write(byte[] data, int offset, int length) {
        return internalWrite(data, offset, length);
     }

     abstract int internalWrite(byte[] data, int offset, int length);

   @Override
    public int readRegister(int register) {
        return Byte.toUnsignedInt(internalRead( register, 1)[0]);
    }

    abstract   byte[] internalRead(int register, int size);




    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        internalRead(register, buffer, offset, length);
        return length;
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        internalRead( register, buffer, offset, length);
        return length;
    }


    abstract  byte[] internalRead(int register, byte[] buffer, int offset, int length) ;

    abstract  byte[] internalRead(byte[] register, byte[] buffer, int offset, int length) ;

    @Override
    public int writeRegister(int register, byte b) {
        return internalWrite(new byte[]{(byte) register}, new byte[]{b});
    }

    abstract int internalWrite(byte[] register, byte[] data);

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        return internalWrite(new byte[]{(byte) register}, data, offset, length);
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        return internalWrite(register, data, offset, length);
    }


    abstract  int internalWrite(byte[] register, byte[] data, int offset, int length);

    @Override
    public void writeThenRead(byte[] writeBuffer, int writeOffset, int writeSize, int readDelayNanos, byte[] readBuffer, int readOffset, int readSize) {
        internalWriteThenRead( writeBuffer,  writeOffset, writeSize,  readDelayNanos, readBuffer,  readOffset,  readSize);
    }

    abstract     void internalWriteThenRead(byte[] writeBuffer, int writeOffset, int writeSize, int readDelayNanos, byte[] readBuffer, int readOffset, int readSize);


    }
