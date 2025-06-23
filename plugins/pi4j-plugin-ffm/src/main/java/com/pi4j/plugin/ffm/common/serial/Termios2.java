package com.pi4j.plugin.ffm.common.serial;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

public record Termios2(int c_iflag, int c_oflag, int c_cflag, int c_lflag, byte c_line, byte[] c_cc, int c_ispeed, int c_ospeed) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("c_iflag"),
        ValueLayout.JAVA_INT.withName("c_oflag"),
        ValueLayout.JAVA_INT.withName("c_cflag"),
        ValueLayout.JAVA_INT.withName("c_lflag"),
        ValueLayout.JAVA_BYTE.withName("c_line"),
        MemoryLayout.sequenceLayout(19, ValueLayout.JAVA_BYTE).withName("c_cc"),
        ValueLayout.JAVA_INT.withName("c_ispeed"),
        ValueLayout.JAVA_INT.withName("c_ospeed")
    );

    private static final VarHandle VH_IFLAG = LAYOUT.varHandle(groupElement("c_iflag"));
    private static final VarHandle VH_OFLAG = LAYOUT.varHandle(groupElement("c_oflag"));
    private static final VarHandle VH_CFLAG = LAYOUT.varHandle(groupElement("c_cflag"));
    private static final VarHandle VH_LFLAG = LAYOUT.varHandle(groupElement("c_lflag"));
    private static final VarHandle VH_CLINE = LAYOUT.varHandle(groupElement("c_line"));
    private static final MethodHandle MH_CC = LAYOUT.sliceHandle(MemoryLayout.PathElement.groupElement("c_cc"));
    private static final VarHandle VH_ISPEED = LAYOUT.varHandle(groupElement("c_ispeed"));
    private static final VarHandle VH_OSPEED = LAYOUT.varHandle(groupElement("c_ospeed"));

    public static Termios2 create(MemorySegment memorySegment) throws Throwable {
        var termiosInstance = Termios2.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            termiosInstance = termiosInstance.from(memorySegment);
        }
        return termiosInstance;
    }

    public static Termios2 createEmpty() {
        return new Termios2(0,0,0,0, (byte) 0, new byte[0], 0, 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Termios2 from(MemorySegment buffer) throws Throwable {
        return new Termios2(
            (int) VH_IFLAG.get(buffer, 0L),
            (int) VH_OFLAG.get(buffer, 0L),
            (int) VH_CFLAG.get(buffer, 0L),
            (int) VH_LFLAG.get(buffer, 0L),
            (byte) VH_CLINE.get(buffer, 0L),
            invokeExact(MH_CC, buffer).toArray(ValueLayout.JAVA_BYTE),
            (int) VH_ISPEED.get(buffer, 0L),
            (int) VH_OSPEED.get(buffer, 0L)
        );
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_IFLAG.set(buffer, 0L, c_iflag);
        VH_OFLAG.set(buffer, 0L, c_oflag);
        VH_CFLAG.set(buffer, 0L, c_cflag);
        VH_LFLAG.set(buffer, 0L, c_lflag);
        VH_CLINE.set(buffer, 0L, c_line);

        var bufTmp = invokeExact(MH_CC, buffer);
        for (int i = 0; i < c_cc.length; i++) {
            bufTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, c_cc[i]);
        }

        VH_ISPEED.set(buffer, 0L, c_ispeed);
        VH_OSPEED.set(buffer, 0L, c_ospeed);
    }

    @Override
    public String toString() {
        return "Termios2{" +
            "c_iflag=" + c_iflag +
            ", c_oflag=" + c_oflag +
            ", c_cflag=" + c_cflag +
            ", c_lflag=" + c_lflag +
            ", c_line=" + c_line +
            ", c_cc=" + Arrays.toString(c_cc) +
            ", c_ispeed=" + c_ispeed +
            ", c_ospeed=" + c_ospeed +
            '}';
    }
}
