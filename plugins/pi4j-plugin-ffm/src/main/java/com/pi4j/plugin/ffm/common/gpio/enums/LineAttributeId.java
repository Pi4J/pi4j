package com.pi4j.plugin.ffm.common.gpio.enums;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:108:6
 *
 * enum gpio_v2_line_attr_id - &struct gpio_v2_line_attribute.id values
 * identifying which field of the attribute union is in use.
 * @GPIO_V2_LINE_ATTR_ID_FLAGS: flags field is in use
 * @GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES: values field is in use
 * @GPIO_V2_LINE_ATTR_ID_DEBOUNCE: debounce_period_us field is in use
 */
public enum LineAttributeId implements Pi4JLayout {
	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:109:2
	 */
	GPIO_V2_LINE_ATTR_ID_FLAGS(1),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:110:2
	 */
	GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES(2),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/gpio.h:111:2
	 */
	GPIO_V2_LINE_ATTR_ID_DEBOUNCE(3);

	public static final MemoryLayout LAYOUT = ValueLayout.JAVA_INT;

	private final int value;

	LineAttributeId(int value) {
		this.value = value;
	}

	public static LineAttributeId create(int value) {
		return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElseThrow();
	}

	public static LineAttributeId createEmpty() {
		return null;
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

	public int getValue() {
		return value;
	}
}
