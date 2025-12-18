package com.pi4j.plugin.ffm.common.serial;

public enum InputMode {
    IGNBRK(0x0000001),
    BRKINT(0x0000002),
    IGNPAR(0x0000004),
    PARMRK(0x0000010),
    INPCK(0x0000020),
    ISTRIP(0x0000040),
    INLCR(0x0000100),
    IGNCR(0x0000200),
    ICRNL(0x0000400),
    IUCLC(0x0001000),
    IXON(0x0002000),
    IXANY(0x0004000),
    IXOFF(0x0010000),
    IMAXBEL(0x0020000),
    IUTF8(0x0040000);

    private final int value;

    InputMode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
