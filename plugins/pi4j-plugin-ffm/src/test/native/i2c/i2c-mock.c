/*
 * Mock I2C driver, that holds it's data in memory
 *
 * Copyright (C) 2025 Nick Gritsenko
 */

#include <linux/i2c.h>
#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/slab.h>
#include <linux/version.h>

#define MODULE_NAME "i2c-mock"

/*
* I2C Mock driver provides basic functionality to integration test of Pi4j project and regression.
* It simulates working I2C interface with any possible functionailities (for SMBus, ioctl xfers and file write/read) and echoes data was sent.
* Storage model:
* - a value written without a register is kept in a single 'registerless' buffer
* - a value written to a register is kept in a per-register buffer (register -> buffer map)
* - on a read the matching buffer is returned back, so 'write then read' echoes the same data
* Limitations:
* - there are no devices within the driver. It can work with any device address. For simplicity, it is refferred to device '1C' within pi4j tests
* - up to REGISTER_COUNT distinct registers are tracked, each buffer is BUFFER_SIZE bytes long (should be enough for tests)
*/

#define REGISTER_COUNT 10
#define BUFFER_SIZE 1024

/* Optional verbose debug logging, toggled by the 'debug' module parameter (set from i2c-setup.sh) */
static int debug;
module_param(debug, int, 0644);
MODULE_PARM_DESC(debug, "Enable verbose debug logging (default 0)");

#define mock_dbg(dev, fmt, ...) \
	do { if (debug) dev_info(dev, fmt, ##__VA_ARGS__); } while (0)

/* value written/read without addressing a register */
static unsigned char *internal_buf;

/* one entry of the register -> buffer map */
struct internal_register {
	bool used;			/* slot is taken (distinguishes a real register 0x00 from an empty slot) */
	unsigned short device_register;	/* register number this buffer belongs to */
	unsigned char *data_buf;	/* stored value */
	unsigned int len;		/* number of valid bytes (used by SMBus block transfers) */
};
static struct internal_register registers[REGISTER_COUNT];

/* last register addressed, used by file-access reads that come in a separate transfer */
static unsigned short last_reg;

// all possible functionalities
static u32 i2c_mock_funcions(struct i2c_adapter *adapter)
{
	return I2C_FUNC_I2C | I2C_FUNC_SMBUS_BYTE | I2C_FUNC_SMBUS_BYTE_DATA | I2C_FUNC_SMBUS_WORD_DATA
			 | I2C_FUNC_SMBUS_BLOCK_DATA | I2C_FUNC_SMBUS_I2C_BLOCK;
}

// helper method to display information in kernel logs in format '0A:0B:0C:FF'
static unsigned char *format_hex(const unsigned char *bin, unsigned int binsz, unsigned char **result)
{
	static const char hex_str[] = "0123456789ABCDEF";
	unsigned int i;

	*result = NULL;
	if (!binsz)
		return NULL;

	/* two hex chars plus a separator per byte; the last separator slot holds the NUL terminator */
	*result = kmalloc(binsz * 3, GFP_KERNEL);
	if (!*result)
		return NULL;

	for (i = 0; i < binsz; i++) {
		(*result)[i * 3 + 0] = hex_str[(bin[i] >> 4) & 0x0F];
		(*result)[i * 3 + 1] = hex_str[(bin[i]     ) & 0x0F];
		(*result)[i * 3 + 2] = (i + 1 < binsz) ? ':' : '\0';
	}
	return *result;
}

// helper method to find (or lazily create) the buffer mapped to a register
static struct internal_register *find_register(unsigned short device_register)
{
	int i;

	/* return an existing entry */
	for (i = 0; i < REGISTER_COUNT; i++) {
		if (registers[i].used && registers[i].device_register == device_register)
			return &registers[i];
	}

	/* otherwise claim a free slot */
	for (i = 0; i < REGISTER_COUNT; i++) {
		if (!registers[i].used) {
			registers[i].data_buf = kzalloc(BUFFER_SIZE, GFP_KERNEL);
			if (!registers[i].data_buf)
				return NULL;
			registers[i].used = true;
			registers[i].device_register = device_register;
			registers[i].len = 0;
			return &registers[i];
		}
	}
	return NULL;
}

/*
 * Parse a single i2c message: a read copies the stored buffer back into the
 * message, a write stores the message into the buffer. 'write_offset' tells how
 * many leading register bytes of a write message to skip before the payload
 * (1 when the register is the first byte of the same message, 0 otherwise).
 * A NULL data_buf selects the registerless buffer.
 */
static void i2c_parse_msg(struct i2c_adapter *adap, struct i2c_msg *msg,
			  unsigned char *data_buf, int write_offset)
{
	unsigned char *message = NULL;
	int j;

	if (!data_buf)
		data_buf = internal_buf;

	if (msg->flags & I2C_M_RD) {
		for (j = 0; j < msg->len; j++)
			msg->buf[j] = data_buf[j];
		mock_dbg(&adap->dev, "    Read data: %s", format_hex(msg->buf, msg->len, &message));
	} else {
		for (j = write_offset; j < msg->len; j++)
			data_buf[j - write_offset] = msg->buf[j];
		mock_dbg(&adap->dev, "    Write data: %s",
			 format_hex(data_buf, msg->len - write_offset, &message));
	}

	kfree(message);
}

// main method to work with i2c within ioctl interface and file write/read
static int i2c_mock_xfer(struct i2c_adapter *adap, struct i2c_msg *msgs, int num)
{
	if (num == 1) {
		// a single message: either a (registerless) read/write or a register write carrying [reg, data...]
		struct i2c_msg *msg = &msgs[0];
		struct internal_register *entry;

		if (msg->flags & I2C_M_RD) {
			// read: use the last addressed register, fall back to the registerless buffer
			if (last_reg) {
				entry = find_register(last_reg);
				if (!entry)
					return -ENOMEM;
				mock_dbg(&adap->dev, "Reading I2C device '%02X' from last register '%02X'",
					 msg->addr, last_reg);
				i2c_parse_msg(adap, msg, entry->data_buf, 0);
			} else {
				mock_dbg(&adap->dev, "Reading I2C device '%02X' without register", msg->addr);
				i2c_parse_msg(adap, msg, NULL, 0);
			}
		} else if (msg->len > 1) {
			// write with a register: first byte is the register, the rest is the payload
			unsigned short reg = msg->buf[0];

			last_reg = reg;
			entry = find_register(reg);
			if (!entry)
				return -ENOMEM;
			mock_dbg(&adap->dev, "Writing I2C device '%02X' to register '%02X'", msg->addr, reg);
			i2c_parse_msg(adap, msg, entry->data_buf, 1);
		} else {
			// write without a register
			last_reg = 0;
			mock_dbg(&adap->dev, "Writing I2C device '%02X' without register", msg->addr);
			i2c_parse_msg(adap, msg, NULL, 0);
		}
	} else if (num > 1) {
		// the first message addresses a register, the following ones carry the data (typically a read)
		struct i2c_msg *register_msg = &msgs[0];
		unsigned short reg = register_msg->buf[0];
		struct internal_register *entry = find_register(reg);
		int i;

		if (!entry)
			return -ENOMEM;
		last_reg = reg;
		mock_dbg(&adap->dev, "Accessing I2C device '%02X' with register '%02X'", msgs->addr, reg);
		for (i = 1; i < num; i++)
			i2c_parse_msg(adap, &msgs[i], entry->data_buf, 0);
	} else {
		dev_err(&adap->dev, "Unsupported type");
		return -EINVAL;
	}
	return num;
}

// main method to work with SMBus protocol
static int i2c_mock_smbus_xfer(struct i2c_adapter *adap, u16 addr,
			  unsigned short flags, char read_write,
			  u8 command, int size, union i2c_smbus_data *data)
{
	struct internal_register *entry;
	unsigned char *message = NULL;
	int i, len;

	// quick and byte transfers carry no register, echo them through the registerless buffer
	if (size == I2C_SMBUS_QUICK) {
		mock_dbg(&adap->dev, "Accessing SMBus device '%02X' (I2C_SMBUS_QUICK)", addr);
		return 0;
	}
	if (size == I2C_SMBUS_BYTE) {
		if (read_write == I2C_SMBUS_READ) {
			// Receive Byte: read a single byte without sending a register.
			// A real device returns the byte at its current cursor, so echo
			// back the last addressed register, or the registerless buffer
			// when no register has been addressed yet.
			if (last_reg) {
				entry = find_register(last_reg);
				if (!entry) {
					dev_err(&adap->dev, "Cannot get buffer for register %02X", last_reg);
					return -ENOMEM;
				}
				data->byte = entry->data_buf[0];
				mock_dbg(&adap->dev, "    Read data (I2C_SMBUS_BYTE) from last register '%02X': %02X",
					 last_reg, data->byte);
			} else {
				data->byte = internal_buf[0];
				mock_dbg(&adap->dev, "    Read data (I2C_SMBUS_BYTE): %02X", data->byte);
			}
		} else {
			// Send Byte: write a single byte without a register
			internal_buf[0] = command;
			last_reg = 0;
			mock_dbg(&adap->dev, "    Write data (I2C_SMBUS_BYTE): %02X", command);
		}
		return 0;
	}

	// everything else is register based
	mock_dbg(&adap->dev, "Accessing SMBus device '%02X' with register '%02X'", addr, command);
	entry = find_register(command);
	if (!entry) {
		dev_err(&adap->dev, "Cannot get buffer for register %02X", command);
		return -ENOMEM;
	}
	// move the device cursor so a following Receive Byte reads this register
	last_reg = command;

	switch (size) {
	case I2C_SMBUS_BYTE_DATA:
		if (read_write == I2C_SMBUS_READ) {
			data->byte = entry->data_buf[0];
			mock_dbg(&adap->dev, "    Read data (I2C_SMBUS_BYTE_DATA): %s",
				 format_hex(entry->data_buf, 1, &message));
		} else {
			entry->data_buf[0] = data->byte;
			entry->len = 1;
			mock_dbg(&adap->dev, "    Write data (I2C_SMBUS_BYTE_DATA): %s",
				 format_hex(entry->data_buf, 1, &message));
		}
		break;
	case I2C_SMBUS_WORD_DATA:
		if (read_write == I2C_SMBUS_READ) {
			// SMBus words are little endian: low byte first
			data->word = (entry->data_buf[1] << 8) | entry->data_buf[0];
			mock_dbg(&adap->dev, "    Read data (I2C_SMBUS_WORD_DATA): %s",
				 format_hex(entry->data_buf, 2, &message));
		} else {
			entry->data_buf[0] = data->word & 0xFF;
			entry->data_buf[1] = (data->word >> 8) & 0xFF;
			entry->len = 2;
			mock_dbg(&adap->dev, "    Write data (I2C_SMBUS_WORD_DATA): %s",
				 format_hex(entry->data_buf, 2, &message));
		}
		break;
	case I2C_SMBUS_BLOCK_DATA:
		if (read_write == I2C_SMBUS_READ) {
			len = min_t(int, entry->len, I2C_SMBUS_BLOCK_MAX);
			data->block[0] = len;
			for (i = 0; i < len; i++)
				data->block[i + 1] = entry->data_buf[i];
			mock_dbg(&adap->dev, "    Read data (I2C_SMBUS_BLOCK_DATA): %s",
				 format_hex(entry->data_buf, len, &message));
		} else {
			len = min_t(int, data->block[0], I2C_SMBUS_BLOCK_MAX);
			for (i = 0; i < len; i++)
				entry->data_buf[i] = data->block[i + 1];
			entry->len = len;
			mock_dbg(&adap->dev, "    Write data (I2C_SMBUS_BLOCK_DATA): %s",
				 format_hex(entry->data_buf, len, &message));
		}
		break;
	default:
		return -EOPNOTSUPP;
	}

	kfree(message);
	return 0;
}

static const struct i2c_algorithm i2c_algorithm = {
	.functionality	= i2c_mock_funcions,
#if LINUX_VERSION_CODE >= KERNEL_VERSION(6, 11, 0)
	.xfer			= i2c_mock_xfer,
#else
	.master_xfer	= i2c_mock_xfer,
#endif
	.smbus_xfer 	= i2c_mock_smbus_xfer,
};

static struct i2c_adapter i2c_mock_adapter = {
	.owner		= THIS_MODULE,
	.class		= I2C_CLASS_HWMON,
	.algo		= &i2c_algorithm,
	.name		= MODULE_NAME,
	.nr			= 99
};

static int __init i2c_mock_init(void)
{
	int ret;

	internal_buf = kzalloc(BUFFER_SIZE, GFP_KERNEL);
	if (!internal_buf)
		return -ENOMEM;

	ret = i2c_add_numbered_adapter(&i2c_mock_adapter);
	if (ret) {
		pr_err("i2c-mock: Failed to add new adapter");
		kfree(internal_buf);
		return ret;
	}

	mock_dbg(&i2c_mock_adapter.dev, "Setup: mock adapter at '/dev/i2c-%d' with deivce at address '1C'", i2c_mock_adapter.nr);

	return 0;
}

static void __exit i2c_mock_exit(void)
{
	int i;

	mock_dbg(&i2c_mock_adapter.dev, "Removing i2c mock adapter");
	i2c_del_adapter(&i2c_mock_adapter);
	kfree(internal_buf);
	for (i = 0; i < REGISTER_COUNT; i++)
		kfree(registers[i].data_buf);
}

module_init(i2c_mock_init);
module_exit(i2c_mock_exit);

MODULE_LICENSE("Dual MIT/GPL");
MODULE_DESCRIPTION("I2C Mock Device");
MODULE_AUTHOR("Nick Gritsenko");
