package com.pi4j.plugin.ffm.common.serial;

public enum LocalMode {
    ISIG(0x0000001),
    ICANON(0x0000002),
    XCASE(0x0000004),
    ECHO(0x0000010),
    ECHOE(0x0000020),
    ECHOK(0x0000040),
    ECHONL(0x0000100),
    NOFLSH(0x0000200),
    TOSTOP(0x0000400),
    ECHOCTL(0x0001000),
    ECHOPRT(0x0002000),
    ECHOKE(0x0004000),
    FLUSHO(0x0010000),
    PENDIN(0x0040000),
    IEXTEN(0x0100000),
    EXTPROC(0x0200000);
    private final int value;

    LocalMode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

}
