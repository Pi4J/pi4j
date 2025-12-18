package com.pi4j.plugin.ffm.common.serial;

public enum ControlMode {
    CRTSCTS (020000000000),
    CBAUD(0x0010017),
    CSIZE(0x0000060),
    CS5(0x0000000),
    CS6(0x0000020),
    CS7(0x0000040),
    CS8(0x0000060),
    CSTOPB(0x0000100),
    CREAD(0x0000200),
    PARENB(0x0000400),
    PARODD(0x0001000),
    HUPCL(0x0002000),
    CLOCAL(0x0004000),
    CBAUDEX(0x0010000),
    BOTHER(0x0010000),
    IBSHIFT	   (16);
    private final int value;

    ControlMode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
