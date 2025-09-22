package com.pi4j.plugin.ffm.providers.serial;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialProviderBase;

public class SerialFFMProviderImpl extends SerialProviderBase {

    /**
     * <p>Constructor for PiGpioSerialProviderImpl.</p>
     */
    public SerialFFMProviderImpl() {
        this.id = "SerialFFMProviderImpl";
        this.name = "SerialFFMProviderImpl";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serial create(SerialConfig config) {
        // create new I/O instance based on I/O config
        var serial = new SerialFFM(this, config);
        this.context.registry().add(serial);
        return serial;
    }
}
