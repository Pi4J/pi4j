package com.pi4j.plugin.ffm.common.serial;

public enum OutputMode {
    OPOST(0x0000001),
    OLCUC(0x0000002),
    ONLCR(0x0000004),
    OCRNL(0x0000010),
    ONOCR(0x0000020),
    ONLRET(0x0000040),
    OFILL(0x0000100),
    OFDEL(0x0000200),
    NLDLY(0x0000400),
    NL0(0x0000000),
    NL1(0x0000400),
    CRDLY(0x0003000),
    CR0(0x0000000),
    CR1(0x0001000),
    CR2(0x0002000),
    CR3(0x0003000),
    TABDLY(0x0014000),
    TAB0(0x0000000),
    TAB1(0x0004000),
    TAB2(0x0010000),
    TAB3(0x0014000),
    XTABS(0x0014000),
    BSDLY(0x0020000),
    BS0(0x0000000),
    BS1(0x0020000),
    VTDLY(0x0040000),
    VT0(0x0000000),
    VT1(0x0040000),
    FFDLY(0x0100000),
    FF0(0x0000000),
    FF1(0x0100000);

    private final int value;

    private OutputMode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
