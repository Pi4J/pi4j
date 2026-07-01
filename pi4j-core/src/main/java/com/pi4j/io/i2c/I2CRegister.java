package com.pi4j.io.i2c;

import com.pi4j.io.IODataReader;
import com.pi4j.io.IODataWriter;

/**
 * Encapsulates a single register of an I2C device, allowing data to be read from and written to that register
 * without repeating the register address on every call. Obtained from {@link I2C#getRegister(int)} and inherits
 * the full set of register read/write helpers from {@link I2CRegisterDataReader} and {@link I2CRegisterDataWriter}.
 */
public interface I2CRegister extends IODataWriter, IODataReader, I2CRegisterDataReader, I2CRegisterDataWriter {
    /**
     * Returns the register address this handle targets.
     *
     * @return the device register address
     */
    int getAddress();

    /**
     * Returns the register address this handle targets.
     *
     * @return the device register address
     */
    default int address(){
        return getAddress();
    }

    /**
     * Write a single word value (16-bit) to the I2C device register.
     *
     * @param word 16-bit word value to be written
     */
    void writeWord(int word);

    /**
     * Read a single word value (16-bit) from the I2C device register.
     *
     * @return If success, then returns 16-bit word value read from I2C register; else a negative error code.
     */
    int readWord();

    /**
     * Write a single word value (16-bit) to the I2C device register
     * and immediately reads back a 16-bit word value.
     *
     * @param word 16-bit word value to be written
     * @return The 16-bit word value read/returned; or a negative value if error
     */
    int writeReadWord(int word);
}
