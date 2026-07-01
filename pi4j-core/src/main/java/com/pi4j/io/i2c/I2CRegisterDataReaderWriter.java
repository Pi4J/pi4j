package com.pi4j.io.i2c;

/**
 * Combines the I2C register read and write capabilities of {@link I2CRegisterDataReader} and
 * {@link I2CRegisterDataWriter} into a single interface, and adds combined write-then-read register helpers.
 * Implemented by {@link I2C} so a device exposes the full register data API.
 */
public interface I2CRegisterDataReaderWriter extends I2CRegisterDataReader, I2CRegisterDataWriter {
    /**
     * Write a single word value (16-bit) to the I2C device register
     * and immediately reads back a 16-bit word value.
     *
     * @param register the register address to write to
     * @param word 16-bit word value to be written
     * @return The 16-bit word value read/returned; or a negative value if error
     */
    default int writeReadRegisterWord(int register, int word) {
        writeRegisterWord(register, word);
        return readRegisterWord(register);
    }
}
