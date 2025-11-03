/*
 * Mock I2C driver, that holds it's data in memory
 *
 * Copyright (C) 2025 Nick Gritsenko
 */

#include <linux/i2c.h>
#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/module.h>

#define MODULE_NAME "i2c-mock"

/*
* I2C Mock driver provides basic functionality to integration test of Pi4j project and regression.
* It simulates working I2C interface with any possible functionailities (for SMBus, ioctl xfers and file write/read) and echoes data was sent.
* Limitations:
* - there are no devices within the driver. It can work with any device address. For simplicity, it is refferred to device '1C' within pi4j tests
* - data buffers are organized in two pieces: registerless buffer and map<register, buffer>
* - all buffers are 1024 chars long (should be enough for tests)
*/

static unsigned char * internal_buf;
struct internal_registers {
	unsigned short device_register;
	unsigned char *data_buf;
};
struct internal_registers registers[10];

// all possible functionalities
static u32 i2c_mock_funcions(struct i2c_adapter *adapter)
{
	return I2C_FUNC_I2C | I2C_FUNC_SMBUS_BYTE | I2C_FUNC_SMBUS_BYTE_DATA | I2C_FUNC_SMBUS_WORD_DATA
			 | I2C_FUNC_SMBUS_BLOCK_DATA | I2C_FUNC_SMBUS_I2C_BLOCK;
}

// helper method to display information in kernel logs in format 'OA:0B:0C:FF'
static unsigned char * format_hex(const unsigned char *bin, unsigned int binsz, unsigned char **result)
{
  unsigned char     hex_str[]= "0123456789ABCDEF";
  unsigned int      i;

  if (!(*result = (unsigned char *)kmalloc(binsz * 3 + 1, GFP_KERNEL)))
    return (NULL);

  (*result)[binsz * 2] = 0;

  if (!binsz)
    return (NULL);

  for (i = 0; i < binsz; i++) {
		(*result)[i * 3 + 0] = hex_str[(bin[i] >> 4) & 0x0F];
		(*result)[i * 3 + 1] = hex_str[(bin[i]     ) & 0x0F];
		(*result)[i * 3 + 2] = (i + 1 < binsz) && bin[i + 1] ? ':' : '\0';
    }
  return (*result);
}

// parsing i2c message and writing/reading into buffer
static void i2c_parse_msg(struct i2c_adapter *adap, struct i2c_msg *msgs, unsigned char *data_buf)
{
	int j, start_index = 1;
	unsigned char * message;
	if (!data_buf) {
		data_buf = internal_buf;
		start_index = 0;
	}
	if (msgs->flags & I2C_M_RD) {
		for (j = 0; j < msgs->len; j++)
			msgs->buf[j] = data_buf[j];
		dev_info(&adap->dev, "    Read data: %s", format_hex(msgs->buf, msgs->len, &message));
	} else {
		// default is to save incoming buffer
		for (j = start_index; j < msgs->len; j++)
			data_buf[j - start_index] = msgs->buf[j];
		dev_info(&adap->dev, "    Write data: %s", format_hex(data_buf, sizeof(data_buf), &message));
	}

	kfree(message);
}

// helper method to find buffer from map by provided register
static unsigned char * find_register(const ushort device_register, unsigned char **register_buf)
{
	int i;
	for (i = 0; i < 10; i++) {
		if (registers[i].device_register == device_register) {
			if (!registers[i].data_buf) {
				registers[i].data_buf = (char *)kmalloc(1024 * sizeof(char), GFP_KERNEL);
			}
			register_buf = &registers[i].data_buf;
			return *register_buf;
		}
	}

	for (i = 0; i < 10; i++) {
		if (!registers[i].device_register) {
			registers[i].device_register = device_register;
			registers[i].data_buf = (char *)kmalloc(1024 * sizeof(char), GFP_KERNEL);
			register_buf = &registers[i].data_buf;
			return *register_buf;
		}
	}
	return NULL;
}

// special flag for handling non-register access
static ushort last_reg;

// main method to work with i2c within ioctl interface and file write/read
static int i2c_mock_xfer(struct i2c_adapter *adap, struct i2c_msg *msgs, int num)
{
	if (num == 1) {
		// we have only one message
		struct i2c_msg * msg = &msgs[0];
		ushort reg;
		unsigned char *register_buf;
		if (msgs->flags & I2C_M_RD && last_reg) {
			// first, check if we want to read without register, grab last one
			register_buf = find_register(last_reg, &register_buf);
			dev_info(&adap->dev, "Accessing I2C deivce '%02X' with last register '%02X'", msg->addr, last_reg);
		} else if (*msg->buf && msg->len > 1) {
			// second, check if we want to access register with data, save current register as last one
			reg = msg->buf[0];
			last_reg = reg;
			register_buf = find_register(reg, &register_buf);
			dev_info(&adap->dev, "Accessing I2C deivce '%02X' with register '%02X'", msg->addr, reg);
		} else {
			// last, check if we want to access data with no register, drop last register
			last_reg = 0;
			dev_info(&adap->dev, "Accessing I2C device '%02X' without register", msg->addr);
		}
		i2c_parse_msg(adap, &msgs[0], register_buf);
	} else if (num > 1) {
		// we have a register with the first message and others are just data
		struct i2c_msg * register_msg = &msgs[0];
		
		ushort reg = register_msg->buf[0];
		unsigned char *register_buf = find_register(reg, &register_buf);
		dev_info(&adap->dev, "Accessing I2C deivce '%02X' with register '%02X'", msgs->addr, reg);
		if (register_buf == NULL) {
			return -1;
		}
		int i;
		for (i = 1; i < num; i++) {
			i2c_parse_msg(adap, &msgs[i], register_buf);
		}
	} else {
		dev_err(&adap->dev, "Unsupported type");
		return -1;
	}
    return num;
}

// main method to work with SMBus protocol
static int i2c_mock_smbus_xfer(struct i2c_adapter *adap, u16 addr,
			  unsigned short flags, char read_write,
			  u8 command, int size, union i2c_smbus_data *data)
{
	dev_info(&adap->dev, "Accessing SMBus device '%02X' with register '%02X'", addr, command);
	unsigned char *register_buf = find_register(command, &register_buf);
	if (!register_buf) {
		dev_err(&adap->dev, "Cannot get buffer for register %d", command);
		return -1;
	}
	unsigned char *message;
	switch (size) {
		case I2C_SMBUS_QUICK:
		case I2C_SMBUS_BYTE:
			if (read_write == I2C_SMBUS_READ) {
				data->byte = internal_buf[0];
				dev_info(&adap->dev, "    Read data (I2C_SMBUS_QUICK|I2C_SMBUS_BYTE): %s", format_hex(internal_buf, sizeof(internal_buf), &message));
			} else {
				internal_buf[0] = command;
				dev_info(&adap->dev, "    Write data (I2C_SMBUS_QUICK|I2C_SMBUS_BYTE): %s", format_hex(internal_buf, sizeof(internal_buf), &message));
			}
			break;
		case I2C_SMBUS_BYTE_DATA:
			if (read_write == I2C_SMBUS_READ) {
				data->byte = register_buf[0];
				dev_info(&adap->dev, "    Read data (I2C_SMBUS_BYTE_DATA): %s", format_hex(internal_buf, sizeof(register_buf), &message));
			} else {
				register_buf[0] = data->byte;
				dev_info(&adap->dev, "   Write data (I2C_SMBUS_BYTE_DATA): %s", format_hex(register_buf, sizeof(register_buf), &message));
			}
			break;
		case I2C_SMBUS_WORD_DATA:
			if (read_write == I2C_SMBUS_READ) {
				data->word = ((register_buf[0] & 0xFF) << 8) | (register_buf[1] & 0xFF);
				dev_info(&adap->dev, "    Read data (I2C_SMBUS_WORD_DATA): %s", format_hex(register_buf, sizeof(register_buf), &message));
			} else {
				register_buf[0] = data->word & 0XFF;
				register_buf[1] = (data->word >> 8) & 0xFF;
				dev_info(&adap->dev, "   Write data (I2C_SMBUS_WORD_DATA): %s", format_hex(register_buf, sizeof(register_buf), &message));
			}
			break;
		case I2C_SMBUS_BLOCK_DATA:
			if (read_write == I2C_SMBUS_READ) {
				int i;
				data->block[0] = I2C_SMBUS_BLOCK_MAX + 1;
				for (i = 1; i < I2C_SMBUS_BLOCK_MAX + 1; i++) {
					data->block[i] = register_buf[i - 1];
				}
				dev_info(&adap->dev, "    Read data (I2C_SMBUS_BLOCK_DATA): %s", format_hex(register_buf, sizeof(register_buf), &message));
			} else {
				int i;
				int size = data->block[0];
				for (i = 1; i < size + 1; i++) {
					register_buf[i - 1] = data->block[i];
				}
				dev_info(&adap->dev, "   Write data (I2C_SMBUS_BLOCK_DATA): %s", format_hex(register_buf, sizeof(register_buf), &message));
			}
			break;
		default:
			kfree(message);
			return -EOPNOTSUPP;
	}
	kfree(message);
	return 0;
}

static const struct i2c_algorithm i2c_algorithm = {
	.functionality	= i2c_mock_funcions,
	.xfer			= i2c_mock_xfer,
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
	ret = i2c_add_numbered_adapter(&i2c_mock_adapter);
	if (ret) {
        pr_err("i2c-mock: Failed to add new adapter");
        return ret;
    }

	dev_info(&i2c_mock_adapter.dev, "Setup: mock adapter at '/dev/i2c-%d' with deivce at address '1C'", i2c_mock_adapter.nr);
	internal_buf = (char *)kmalloc(1024 * sizeof(char), GFP_KERNEL);

	return 0;
}

static void __exit i2c_mock_exit(void)
{
	dev_info(&i2c_mock_adapter.dev, "Removing i2c mock adapter");
	i2c_del_adapter(&i2c_mock_adapter);
	kfree(internal_buf);
	kfree(registers);
}

module_init(i2c_mock_init);
module_exit(i2c_mock_exit);

MODULE_LICENSE("Dual MIT/GPL");
MODULE_DESCRIPTION("I2C Mock Device");
MODULE_AUTHOR("Nick Gritsenko");