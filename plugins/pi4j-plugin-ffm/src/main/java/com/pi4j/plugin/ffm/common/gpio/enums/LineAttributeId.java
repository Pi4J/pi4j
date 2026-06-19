package com.pi4j.plugin.ffm.common.gpio.enums;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

/**
 * Identifies which field of the kernel {@code struct gpio_v2_line_attribute} union is in use, mirroring the
 * {@code enum gpio_v2_line_attr_id} from {@code <uapi/linux/gpio.h>}. As a {@link Pi4JLayout} it is read from and
 * written to the {@code id} field of a {@link MemorySegment} as a 32-bit integer.
 */
public enum LineAttributeId implements Pi4JLayout {
    /**
     * {@code GPIO_V2_LINE_ATTR_ID_FLAGS}: the attribute's {@code flags} field is in use.
     */
    GPIO_V2_LINE_ATTR_ID_FLAGS(1),

    /**
     * {@code GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES}: the attribute's {@code values} field is in use.
     */
    GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES(2),

    /**
     * {@code GPIO_V2_LINE_ATTR_ID_DEBOUNCE}: the attribute's {@code debounce_period_us} field is in use.
     */
    GPIO_V2_LINE_ATTR_ID_DEBOUNCE(3);

    /** Native memory layout of this field: a single 32-bit integer ({@code __u32}). */
    public static final MemoryLayout LAYOUT = ValueLayout.JAVA_INT;

    private final int value;

    /**
     * Associates the constant with its kernel attribute-id value.
     *
     * @param value the {@code GPIO_V2_LINE_ATTR_ID_*} numeric value for this attribute
     */
    LineAttributeId(int value) {
        this.value = value;
    }

    /**
     * Resolves the constant whose {@link #getValue()} matches the given attribute id.
     *
     * @param value the kernel {@code GPIO_V2_LINE_ATTR_ID_*} value to look up
     * @return the matching {@link LineAttributeId} constant
     * @throws java.util.NoSuchElementException if no constant has the given value
     */
    public static LineAttributeId create(int value) {
        return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElseThrow();
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LineAttributeId from(MemorySegment buffer) throws Throwable {
        var value = buffer.get(ValueLayout.JAVA_INT, 0);
        return Arrays.stream(values()).filter(p -> p.getValue() == value).findFirst().orElseThrow();
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        buffer.set(ValueLayout.JAVA_INT, 0, getValue());
    }

    /**
     * Returns the kernel attribute-id value represented by this constant.
     *
     * @return the {@code GPIO_V2_LINE_ATTR_ID_*} numeric value
     */
    public int getValue() {
        return value;
    }
}
