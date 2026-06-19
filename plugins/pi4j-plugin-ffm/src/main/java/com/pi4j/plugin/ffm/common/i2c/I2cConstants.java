package com.pi4j.plugin.ffm.common.i2c;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

/**
 * Enumerates the integer constants from the Linux I2C kernel headers (include/uapi/linux/i2c.h and
 * include/uapi/linux/i2c-dev.h) used by this native backend: the {@code I2C_M_*} message flags, the
 * {@code I2C_FUNC_*} adapter capability bits, the {@code I2C_SMBUS_*} transaction types, and the
 * {@code I2C_RDWR} ioctl request. Each constant carries its numeric value via {@link #getValue()}, and the enum
 * also implements {@link Pi4JLayout} so a value can be read from or written to a {@link MemorySegment} as a
 * 32-bit integer.
 */
public enum I2cConstants implements Pi4JLayout {

    /**
     * {@code I2C_RDWR} ioctl request: performs a combined sequence of I2C messages in a single transfer.
     */
    I2C_RDWR(0x0707),

	/**
	 * {@code I2C_M_RD}: marks a message as a read (data flows from slave to master).
	 * Source: include/uapi/linux/i2c.h:76:9
	 */
	I2C_M_RD(1),

	/**
	 * {@code I2C_M_TEN}: the message uses a 10-bit slave address instead of the default 7-bit address.
	 * Source: include/uapi/linux/i2c.h:77:9
	 */
	I2C_M_TEN(16),

	/**
	 * {@code I2C_M_DMA_SAFE}: the message buffer is safe to use for DMA transfers.
	 * Source: include/uapi/linux/i2c.h:78:9
	 */
	I2C_M_DMA_SAFE(512),

	/**
	 * {@code I2C_M_RECV_LEN}: the first received byte gives the length of the remaining read (SMBus block read).
	 * Source: include/uapi/linux/i2c.h:79:9
	 */
	I2C_M_RECV_LEN(1024),

	/**
	 * {@code I2C_M_NO_RD_ACK}: do not send an acknowledge after each read byte (requires protocol mangling).
	 * Source: include/uapi/linux/i2c.h:80:9
	 */
	I2C_M_NO_RD_ACK(2048),

	/**
	 * {@code I2C_M_IGNORE_NAK}: treat a not-acknowledge from the slave as an acknowledge.
	 * Source: include/uapi/linux/i2c.h:81:9
	 */
	I2C_M_IGNORE_NAK(4096),

	/**
	 * {@code I2C_M_REV_DIR_ADDR}: invert the read/write direction bit of the address.
	 * Source: include/uapi/linux/i2c.h:82:9
	 */
	I2C_M_REV_DIR_ADDR(8192),

	/**
	 * {@code I2C_M_NOSTART}: omit the (repeated) start condition for this message.
	 * Source: include/uapi/linux/i2c.h:83:9
	 */
	I2C_M_NOSTART(16384),

	/**
	 * {@code I2C_M_STOP}: send a stop condition after this message even when more messages follow.
	 * Source: include/uapi/linux/i2c.h:84:9
	 */
	I2C_M_STOP(32768),

	/**
	 * {@code I2C_FUNC_I2C}: the adapter supports plain I2C-level transfers.
	 * Source: include/uapi/linux/i2c.h:91:9
	 */
	I2C_FUNC_I2C(1),

	/**
	 * {@code I2C_FUNC_10BIT_ADDR}: the adapter supports 10-bit slave addressing.
	 * Source: include/uapi/linux/i2c.h:92:9
	 */
	I2C_FUNC_10BIT_ADDR(2),

	/**
	 * {@code I2C_FUNC_PROTOCOL_MANGLING}: the adapter supports the {@code I2C_M_*} protocol-mangling flags.
	 * Source: include/uapi/linux/i2c.h:93:9
	 */
	I2C_FUNC_PROTOCOL_MANGLING(4),

	/**
	 * {@code I2C_FUNC_SMBUS_PEC}: the adapter supports SMBus packet error checking.
	 * Source: include/uapi/linux/i2c.h:94:9
	 */
	I2C_FUNC_SMBUS_PEC(8),

	/**
	 * {@code I2C_FUNC_NOSTART}: the adapter supports the {@code I2C_M_NOSTART} flag.
	 * Source: include/uapi/linux/i2c.h:95:9
	 */
	I2C_FUNC_NOSTART(16),

	/**
	 * {@code I2C_FUNC_SLAVE}: the adapter can operate as an I2C slave device.
	 * Source: include/uapi/linux/i2c.h:96:9
	 */
	I2C_FUNC_SLAVE(32),

	/**
	 * {@code I2C_FUNC_SMBUS_BLOCK_PROC_CALL}: the adapter supports the SMBus block process-call transaction.
	 * Source: include/uapi/linux/i2c.h:97:9
	 */
	I2C_FUNC_SMBUS_BLOCK_PROC_CALL(32768),

	/**
	 * {@code I2C_FUNC_SMBUS_QUICK}: the adapter supports the SMBus quick command.
	 * Source: include/uapi/linux/i2c.h:98:9
	 */
	I2C_FUNC_SMBUS_QUICK(65536),

	/**
	 * {@code I2C_FUNC_SMBUS_READ_BYTE}: the adapter supports the SMBus receive-byte transaction.
	 * Source: include/uapi/linux/i2c.h:99:9
	 */
	I2C_FUNC_SMBUS_READ_BYTE(131072),

	/**
	 * {@code I2C_FUNC_SMBUS_WRITE_BYTE}: the adapter supports the SMBus send-byte transaction.
	 * Source: include/uapi/linux/i2c.h:100:9
	 */
	I2C_FUNC_SMBUS_WRITE_BYTE(262144),

	/**
	 * {@code I2C_FUNC_SMBUS_READ_BYTE_DATA}: the adapter supports the SMBus read-byte-data transaction.
	 * Source: include/uapi/linux/i2c.h:101:9
	 */
	I2C_FUNC_SMBUS_READ_BYTE_DATA(524288),

	/**
	 * {@code I2C_FUNC_SMBUS_WRITE_BYTE_DATA}: the adapter supports the SMBus write-byte-data transaction.
	 * Source: include/uapi/linux/i2c.h:102:9
	 */
	I2C_FUNC_SMBUS_WRITE_BYTE_DATA(1048576),

	/**
	 * {@code I2C_FUNC_SMBUS_READ_WORD_DATA}: the adapter supports the SMBus read-word-data transaction.
	 * Source: include/uapi/linux/i2c.h:103:9
	 */
	I2C_FUNC_SMBUS_READ_WORD_DATA(2097152),

	/**
	 * {@code I2C_FUNC_SMBUS_WRITE_WORD_DATA}: the adapter supports the SMBus write-word-data transaction.
	 * Source: include/uapi/linux/i2c.h:104:9
	 */
	I2C_FUNC_SMBUS_WRITE_WORD_DATA(4194304),

	/**
	 * {@code I2C_FUNC_SMBUS_PROC_CALL}: the adapter supports the SMBus process-call transaction.
	 * Source: include/uapi/linux/i2c.h:105:9
	 */
	I2C_FUNC_SMBUS_PROC_CALL(8388608),

	/**
	 * {@code I2C_FUNC_SMBUS_READ_BLOCK_DATA}: the adapter supports the SMBus read-block-data transaction.
	 * Source: include/uapi/linux/i2c.h:106:9
	 */
	I2C_FUNC_SMBUS_READ_BLOCK_DATA(16777216),

	/**
	 * {@code I2C_FUNC_SMBUS_WRITE_BLOCK_DATA}: the adapter supports the SMBus write-block-data transaction.
	 * Source: include/uapi/linux/i2c.h:107:9
	 */
	I2C_FUNC_SMBUS_WRITE_BLOCK_DATA(33554432),

	/**
	 * {@code I2C_FUNC_SMBUS_READ_I2C_BLOCK}: the adapter supports reading an I2C block (block read without a count byte).
	 * Source: include/uapi/linux/i2c.h:108:9
	 */
	I2C_FUNC_SMBUS_READ_I2C_BLOCK(67108864),

	/**
	 * {@code I2C_FUNC_SMBUS_WRITE_I2C_BLOCK}: the adapter supports writing an I2C block (block write without a count byte).
	 * Source: include/uapi/linux/i2c.h:109:9
	 */
	I2C_FUNC_SMBUS_WRITE_I2C_BLOCK(134217728),

	/**
	 * {@code I2C_FUNC_SMBUS_HOST_NOTIFY}: the adapter supports the SMBus host-notify protocol.
	 * Source: include/uapi/linux/i2c.h:110:9
	 */
	I2C_FUNC_SMBUS_HOST_NOTIFY(268435456),

	/**
	 * {@code I2C_SMBUS_BLOCK_MAX}: maximum number of data bytes in an SMBus block transfer.
	 * Source: include/uapi/linux/i2c.h:140:9
	 */
	I2C_SMBUS_BLOCK_MAX(32),

	/**
	 * {@code I2C_SMBUS_READ}: read direction for an SMBus transaction.
	 * Source: include/uapi/linux/i2c.h:149:9
	 */
	I2C_SMBUS_READ(1),

	/**
	 * {@code I2C_SMBUS_WRITE}: write direction for an SMBus transaction.
	 * Source: include/uapi/linux/i2c.h:150:9
	 */
	I2C_SMBUS_WRITE(0),

	/**
	 * {@code I2C_SMBUS_QUICK}: SMBus quick-command transaction size.
	 * Source: include/uapi/linux/i2c.h:154:9
	 */
	I2C_SMBUS_QUICK(0),

	/**
	 * {@code I2C_SMBUS_BYTE}: SMBus single-byte transaction size.
	 * Source: include/uapi/linux/i2c.h:155:9
	 */
	I2C_SMBUS_BYTE(1),

	/**
	 * {@code I2C_SMBUS_BYTE_DATA}: SMBus byte-data (command plus one data byte) transaction size.
	 * Source: include/uapi/linux/i2c.h:156:9
	 */
	I2C_SMBUS_BYTE_DATA(2),

	/**
	 * {@code I2C_SMBUS_WORD_DATA}: SMBus word-data (command plus two data bytes) transaction size.
	 * Source: include/uapi/linux/i2c.h:157:9
	 */
	I2C_SMBUS_WORD_DATA(3),

	/**
	 * {@code I2C_SMBUS_PROC_CALL}: SMBus process-call transaction size.
	 * Source: include/uapi/linux/i2c.h:158:9
	 */
	I2C_SMBUS_PROC_CALL(4),

	/**
	 * {@code I2C_SMBUS_BLOCK_DATA}: SMBus block-data transaction size.
	 * Source: include/uapi/linux/i2c.h:159:9
	 */
	I2C_SMBUS_BLOCK_DATA(5),

	/**
	 * {@code I2C_SMBUS_I2C_BLOCK_BROKEN}: legacy I2C block transaction size (kept for backward compatibility).
	 * Source: include/uapi/linux/i2c.h:160:9
	 */
	I2C_SMBUS_I2C_BLOCK_BROKEN(6),

	/**
	 * {@code I2C_SMBUS_BLOCK_PROC_CALL}: SMBus block process-call transaction size.
	 * Source: include/uapi/linux/i2c.h:161:9
	 */
	I2C_SMBUS_BLOCK_PROC_CALL(7),

	/**
	 * {@code I2C_SMBUS_I2C_BLOCK_DATA}: I2C block transaction size (block read/write without a count byte).
	 * Source: include/uapi/linux/i2c.h:162:9
	 */
	I2C_SMBUS_I2C_BLOCK_DATA(8),

	/**
	 * {@code I2C_FUNC_SMBUS_BYTE}: combined capability bit for both SMBus read-byte and write-byte transactions.
	 * Source: include/uapi/linux/i2c.h:112:9
	 */
	I2C_FUNC_SMBUS_BYTE(393216),

	/**
	 * {@code I2C_FUNC_SMBUS_BYTE_DATA}: combined capability bit for both SMBus read- and write-byte-data transactions.
	 * Source: include/uapi/linux/i2c.h:114:9
	 */
	I2C_FUNC_SMBUS_BYTE_DATA(1572864),

	/**
	 * {@code I2C_FUNC_SMBUS_WORD_DATA}: combined capability bit for both SMBus read- and write-word-data transactions.
	 * Source: include/uapi/linux/i2c.h:116:9
	 */
	I2C_FUNC_SMBUS_WORD_DATA(6291456),

	/**
	 * {@code I2C_FUNC_SMBUS_BLOCK_DATA}: combined capability bit for both SMBus read- and write-block-data transactions.
	 * Source: include/uapi/linux/i2c.h:118:9
	 */
	I2C_FUNC_SMBUS_BLOCK_DATA(50331648),

	/**
	 * {@code I2C_FUNC_SMBUS_I2C_BLOCK}: combined capability bit for both I2C block read and write transactions.
	 * Source: include/uapi/linux/i2c.h:120:9
	 */
	I2C_FUNC_SMBUS_I2C_BLOCK(201326592),

	/**
	 * {@code I2C_FUNC_SMBUS_EMUL}: aggregate of the SMBus transactions that can be emulated on top of plain I2C.
	 * Source: include/uapi/linux/i2c.h:123:9
	 */
	I2C_FUNC_SMBUS_EMUL(251592712),

	/**
	 * {@code I2C_FUNC_SMBUS_EMUL_ALL}: aggregate of all emulatable SMBus transactions, including block and host-notify.
	 * Source: include/uapi/linux/i2c.h:133:9
	 */
	I2C_FUNC_SMBUS_EMUL_ALL(268402696);

	public static final MemoryLayout LAYOUT = ValueLayout.JAVA_INT;

	private final int value;

	I2cConstants(int value) {
		this.value = value;
	}

	/**
	 * Returns the constant whose numeric value matches the supplied integer.
	 *
	 * @param value the kernel integer value to look up
	 * @return the matching {@link I2cConstants}
	 * @throws java.util.NoSuchElementException if no constant has the given value
	 */
	public static I2cConstants create(int value) {
		return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElseThrow();
	}

	/**
	 * Returns {@code null}, as this enum has no meaningful "empty" constant; provided to satisfy the
	 * factory convention used by the other {@link Pi4JLayout} types.
	 *
	 * @return always {@code null}
	 */
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

	/**
	 * Returns the underlying kernel integer value of this constant.
	 *
	 * @return the numeric value as defined in the Linux I2C headers
	 */
	public int getValue() {
		return value;
	}
}
