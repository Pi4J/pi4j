package com.pi4j.plugin.ffm.common.i2c;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

/**
 * Source: NO_POSITION
 */
public enum I2cConstants implements Pi4JLayout {
	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:76:9
	 */
	I2C_M_RD(1),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:77:9
	 */
	I2C_M_TEN(16),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:78:9
	 */
	I2C_M_DMA_SAFE(512),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:79:9
	 */
	I2C_M_RECV_LEN(1024),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:80:9
	 */
	I2C_M_NO_RD_ACK(2048),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:81:9
	 */
	I2C_M_IGNORE_NAK(4096),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:82:9
	 */
	I2C_M_REV_DIR_ADDR(8192),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:83:9
	 */
	I2C_M_NOSTART(16384),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:84:9
	 */
	I2C_M_STOP(32768),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:91:9
	 */
	I2C_FUNC_I2C(1),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:92:9
	 */
	I2C_FUNC_10BIT_ADDR(2),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:93:9
	 */
	I2C_FUNC_PROTOCOL_MANGLING(4),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:94:9
	 */
	I2C_FUNC_SMBUS_PEC(8),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:95:9
	 */
	I2C_FUNC_NOSTART(16),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:96:9
	 */
	I2C_FUNC_SLAVE(32),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:97:9
	 */
	I2C_FUNC_SMBUS_BLOCK_PROC_CALL(32768),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:98:9
	 */
	I2C_FUNC_SMBUS_QUICK(65536),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:99:9
	 */
	I2C_FUNC_SMBUS_READ_BYTE(131072),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:100:9
	 */
	I2C_FUNC_SMBUS_WRITE_BYTE(262144),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:101:9
	 */
	I2C_FUNC_SMBUS_READ_BYTE_DATA(524288),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:102:9
	 */
	I2C_FUNC_SMBUS_WRITE_BYTE_DATA(1048576),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:103:9
	 */
	I2C_FUNC_SMBUS_READ_WORD_DATA(2097152),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:104:9
	 */
	I2C_FUNC_SMBUS_WRITE_WORD_DATA(4194304),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:105:9
	 */
	I2C_FUNC_SMBUS_PROC_CALL(8388608),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:106:9
	 */
	I2C_FUNC_SMBUS_READ_BLOCK_DATA(16777216),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:107:9
	 */
	I2C_FUNC_SMBUS_WRITE_BLOCK_DATA(33554432),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:108:9
	 */
	I2C_FUNC_SMBUS_READ_I2C_BLOCK(67108864),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:109:9
	 */
	I2C_FUNC_SMBUS_WRITE_I2C_BLOCK(134217728),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:110:9
	 */
	I2C_FUNC_SMBUS_HOST_NOTIFY(268435456),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:140:9
	 */
	I2C_SMBUS_BLOCK_MAX(32),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:149:9
	 */
	I2C_SMBUS_READ(1),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:150:9
	 */
	I2C_SMBUS_WRITE(0),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:154:9
	 */
	I2C_SMBUS_QUICK(0),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:155:9
	 */
	I2C_SMBUS_BYTE(1),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:156:9
	 */
	I2C_SMBUS_BYTE_DATA(2),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:157:9
	 */
	I2C_SMBUS_WORD_DATA(3),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:158:9
	 */
	I2C_SMBUS_PROC_CALL(4),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:159:9
	 */
	I2C_SMBUS_BLOCK_DATA(5),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:160:9
	 */
	I2C_SMBUS_I2C_BLOCK_BROKEN(6),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:161:9
	 */
	I2C_SMBUS_BLOCK_PROC_CALL(7),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:162:9
	 */
	I2C_SMBUS_I2C_BLOCK_DATA(8),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:112:9
	 */
	I2C_FUNC_SMBUS_BYTE(393216),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:114:9
	 */
	I2C_FUNC_SMBUS_BYTE_DATA(1572864),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:116:9
	 */
	I2C_FUNC_SMBUS_WORD_DATA(6291456),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:118:9
	 */
	I2C_FUNC_SMBUS_BLOCK_DATA(50331648),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:120:9
	 */
	I2C_FUNC_SMBUS_I2C_BLOCK(201326592),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:123:9
	 */
	I2C_FUNC_SMBUS_EMUL(251592712),

	/**
	 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/uapi/linux/i2c.h:133:9
	 */
	I2C_FUNC_SMBUS_EMUL_ALL(268402696);

	public static final MemoryLayout LAYOUT = ValueLayout.JAVA_INT;

	private final int value;

	I2cConstants(int value) {
		this.value = value;
	}

	public static I2cConstants create(int value) {
		return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElseThrow();
	}

	public static I2cConstants createEmpty() {
		return null;
	}

	@Override
	public MemoryLayout getMemoryLayout() {
		return LAYOUT;
	}

	@Override
	@SuppressWarnings("unchecked")
	public I2cConstants from(MemorySegment buffer) throws Throwable {
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
