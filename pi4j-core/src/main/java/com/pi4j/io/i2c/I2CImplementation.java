package com.pi4j.io.i2c;

/**
 * Selects the low-level mechanism a provider uses to communicate with an I2C device. Configured on an
 * {@link I2CConfig} (via {@link I2CConfigBuilder#i2cImplementation(I2CImplementation)}) to choose the access
 * strategy when the device is created.
 */
public enum I2CImplementation {
    /** Access the device through the Linux SMBus protocol layer. */
    SMBUS,
    /** Access the device through direct, raw I2C transactions rather than the SMBus protocol. */
    DIRECT
}
