package com.pi4j.plugin.ffm.common.gpio.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Java mapping of the kernel {@code struct gpiochip_info} from {@code <uapi/linux/gpio.h>}, returned by the
 * {@link com.pi4j.plugin.ffm.common.gpio.GpioConstants#GPIO_GET_CHIPINFO_IOCTL} {@code ioctl} on a
 * {@code /dev/gpiochipN} file descriptor. As a {@link Pi4JLayout} it can be marshalled to and from a
 * {@link MemorySegment}.
 *
 * @param name  the Linux kernel name of this GPIO chip, as raw NUL-padded bytes (up to 32 bytes)
 * @param label a functional name for this GPIO chip such as a product number, as raw NUL-padded bytes (may be empty)
 * @param lines the number of GPIO lines exposed by this chip
 */
public record ChipInfo(byte[] name, byte[] label, int lines) implements Pi4JLayout {
    /** Native memory layout of {@code struct gpiochip_info}: two 32-byte name buffers followed by a 32-bit line count. */
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("name"),
        MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("label"),
        ValueLayout.JAVA_INT.withName("lines")
    );

    private static final MethodHandle MH_NAME = LAYOUT.sliceHandle(groupElement("name"));

    private static final MethodHandle MH_LABEL = LAYOUT.sliceHandle(groupElement("label"));

    private static final VarHandle VH_LINES = LAYOUT.varHandle(groupElement("lines"));

    /**
     * Decodes a {@code struct gpiochip_info} from the given native buffer. A {@link MemorySegment#NULL} buffer
     * yields an empty instance (see {@link #createEmpty()}) rather than reading memory.
     *
     * @param memorySegment the native buffer holding a {@code struct gpiochip_info}, or {@link MemorySegment#NULL}
     * @return a {@link ChipInfo} populated from the buffer, or an empty instance if the buffer is NULL
     * @throws Throwable if reading or converting the buffer fails
     */
    public static ChipInfo create(MemorySegment memorySegment) throws Throwable {
        var chipinfoInstance = ChipInfo.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            chipinfoInstance = chipinfoInstance.from(memorySegment);
        }
        return chipinfoInstance;
    }

    /**
     * Creates an empty {@link ChipInfo} with zero-length name and label and a line count of zero, used as a
     * placeholder before a buffer is read.
     *
     * @return an empty {@link ChipInfo} instance
     */
    public static ChipInfo createEmpty() {
        return new ChipInfo(new byte[]{}, new byte[]{}, 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChipInfo from(MemorySegment buffer) throws Throwable {
        // we know that this two fields are strings, so when converting from byte array we trim the zeroes left in byte array for padding
        var name = new String(invokeExact(MH_NAME, buffer).toArray(ValueLayout.JAVA_BYTE)).trim();
        var label = new String(invokeExact(MH_LABEL, buffer).toArray(ValueLayout.JAVA_BYTE)).trim();
        return new ChipInfo(
            name.getBytes(),
            label.getBytes(),
            (int) VH_LINES.get(buffer, 0L));
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        var nameTmp = invokeExact(MH_NAME, buffer);
        for (int i = 0; i < name.length; i++) {
            nameTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, name[i]);
        }
        var labelTmp = invokeExact(MH_LABEL, buffer);
        for (int i = 0; i < label.length; i++) {
            labelTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, label[i]);
        }
        VH_LINES.set(buffer, 0L, lines);
    }

    @Override
    public String toString() {
        return "ChipInfo{" +
            "name=(" + new String(name) + ")" + Arrays.toString(name) +
            ", label=(" + new String(label) + ")" + Arrays.toString(label) +
            ", lines=" + lines +
            '}';
    }
}
