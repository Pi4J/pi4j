package com.pi4j.plugin.ffm.common.permission.structs;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Class representing data returned by glibc 'getgrgid' native call.
 */
public record GroupData(byte[] grName) implements Pi4JLayout {
    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        MemoryLayout.sequenceLayout(1024, ValueLayout.JAVA_BYTE).withName("gr_name")
    );

    private static final MethodHandle MH_GR_NAME = LAYOUT.sliceHandle(groupElement("gr_name"));

    /**
     * Creates GroupData instance from MemorySegment provided.
     *
     * @param memorySegment buffer to construct GroupData from
     * @return GroupData instance
     * @throws Throwable if there is any exception while converting buffer to java object
     */
    public static GroupData create(MemorySegment memorySegment) throws Throwable {
        var groupDataInstance = GroupData.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            groupDataInstance = groupDataInstance.from(memorySegment);
        }
        return groupDataInstance;
    }

    /**
     * Creates empty GroupData object.
     *
     * @return empty GroupData object
     */
    public static GroupData createEmpty() {
        return new GroupData(new byte[0]);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GroupData from(MemorySegment buffer) throws Throwable {
        var grName = invokeExact(MH_GR_NAME, buffer).getString(0);
        return new GroupData(
            grName.getBytes()
        );
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        var grNameTmp = invokeExact(MH_GR_NAME, buffer);
        for (int i = 0; i < grName.length; i++) {
            grNameTmp.setAtIndex(ValueLayout.JAVA_BYTE, i, grName[i]);
        }
    }

    @Override
    public String toString() {
        return "GroupData{" +
            "grName=" + new String(grName) +
            '}';
    }
}
