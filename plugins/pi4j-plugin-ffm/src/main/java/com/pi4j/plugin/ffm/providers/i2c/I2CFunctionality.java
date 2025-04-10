package com.pi4j.plugin.ffm.providers.i2c;

/**
 * Functionalities available on the device.
 */
public enum I2CFunctionality {
    /**
     * Plain i2c-level commands
     * (Pure SMBus adapters typically can not do these)
     */
    I2C_FUNC_I2C(0x00000001),

    /**
     * Handles the 10-bit address extensions
     */
    I2C_FUNC_10BIT_ADDR(0x00000002),

    /**
     * Knows about the I2C_M_IGNORE_NAK,
     * I2C_M_REV_DIR_ADDR and I2C_M_NO_RD_ACK
     * flags (which modify the I2C protocol!)
     */
    I2C_FUNC_PROTOCOL_MANGLING(0x00000004),

    /**
     * Use of SMBus PEC
     */
    I2C_FUNC_SMBUS_PEC(0x00000008),

    /**
     * Can skip repeated start sequence
     * In kernel versions prior to 3.5 I2C_FUNC_NOSTART was implemented as
     * part of I2C_FUNC_PROTOCOL_MANGLING.
     */
    I2C_FUNC_NOSTART(0x00000010), /* I2C_M_NOSTART */
    /**
     * Set device as slave
     */
    I2C_FUNC_SLAVE(0x00000020),
    /**
     * SMBus 2.0
     */
    I2C_FUNC_SMBUS_BLOCK_PROC_CALL(0x00008000),

    /**
     * Handles the SMBus write_quick command
     */
    I2C_FUNC_SMBUS_QUICK(0x00010000),

    /**
     * Handles the SMBus read_byte command
     */
    I2C_FUNC_SMBUS_READ_BYTE(0x00020000),

    /**
     * Handles the SMBus write_byte command
     */
    I2C_FUNC_SMBUS_WRITE_BYTE(0x00040000),

    /**
     * Handles the SMBus read_byte_data command
     */
    I2C_FUNC_SMBUS_READ_BYTE_DATA(0x00080000),

    /**
     * Handles the SMBus write_byte_data command
     */
    I2C_FUNC_SMBUS_WRITE_BYTE_DATA(0x00100000),

    /**
     * Handles the SMBus read_word_data command
     */
    I2C_FUNC_SMBUS_READ_WORD_DATA(0x00200000),

    /**
     * Handles the SMBus write_byte_data command
     */
    I2C_FUNC_SMBUS_WRITE_WORD_DATA(0x00400000),

    /**
     * Handles the SMBus process_call command
     */
    I2C_FUNC_SMBUS_PROC_CALL(0x00800000),

    /**
     * Handles the SMBus read_block_data command
     */
    I2C_FUNC_SMBUS_READ_BLOCK_DATA(0x01000000),

    /**
     * Handles the SMBus write_block_data command
     */
    I2C_FUNC_SMBUS_WRITE_BLOCK_DATA(0x02000000),

    /**
     * Handles the SMBus read_i2c_block_data command
     */
    I2C_FUNC_SMBUS_READ_I2C_BLOCK(0x04000000), /* I2C-like block xfer */

    /**
     * Handles the SMBus write_i2c_block_data command
     */
    I2C_FUNC_SMBUS_WRITE_I2C_BLOCK(0x08000000), /* w/ 1-byte reg. addr. */
    /**
     * notify host
     */
    I2C_FUNC_SMBUS_HOST_NOTIFY(0x10000000),

    /**
     * Handles the SMBus read_byte and write_byte commands
     */
    I2C_FUNC_SMBUS_BYTE(I2C_FUNC_SMBUS_READ_BYTE,
            I2C_FUNC_SMBUS_WRITE_BYTE),

    /**
     * Handles the SMBus read_byte_data and write_byte_data commands
     */
    I2C_FUNC_SMBUS_BYTE_DATA(I2C_FUNC_SMBUS_READ_BYTE_DATA,
            I2C_FUNC_SMBUS_WRITE_BYTE_DATA),

    /**
     * Handles the SMBus read_word_data and write_word_data commands
     */
    I2C_FUNC_SMBUS_WORD_DATA(I2C_FUNC_SMBUS_READ_WORD_DATA,
            I2C_FUNC_SMBUS_WRITE_WORD_DATA),

    /**
     * Handles the SMBus read_block_data and write_block_data commands
     */
    I2C_FUNC_SMBUS_BLOCK_DATA(I2C_FUNC_SMBUS_READ_BLOCK_DATA,
            I2C_FUNC_SMBUS_WRITE_BLOCK_DATA),

    /**
     * Handles the SMBus read_i2c_block_data and write_i2c_block_data commands
     */
    I2C_FUNC_SMBUS_I2C_BLOCK(I2C_FUNC_SMBUS_READ_I2C_BLOCK,
            I2C_FUNC_SMBUS_WRITE_I2C_BLOCK),

    /**
     * Handles all SMBus commands that can be
     * emulated by a real I2C adapter (using
     * the transparent emulation layer)
     */
    I2C_FUNC_SMBUS_EMUL(I2C_FUNC_SMBUS_QUICK,
            I2C_FUNC_SMBUS_BYTE,
            I2C_FUNC_SMBUS_BYTE_DATA,
            I2C_FUNC_SMBUS_WORD_DATA,
            I2C_FUNC_SMBUS_PROC_CALL,
            I2C_FUNC_SMBUS_WRITE_BLOCK_DATA,
            I2C_FUNC_SMBUS_I2C_BLOCK,
            I2C_FUNC_SMBUS_PEC);

    int value;

    /**
     * Creates functionality.
     *
     * @param value byte value of functionality
     */
    I2CFunctionality(int value) {
        this.value = value;
    }

    /**
     * Creates functionality.
     *
     * @param func byte values of functionality
     */
    I2CFunctionality(I2CFunctionality... func) {
        for (I2CFunctionality item : func) {
            value = value | item.value;
        }
    }

    /**
     * Gets the functionality.
     *
     * @return byte value of functionality
     */
    public int getValue() {
        return value;
    }
}
