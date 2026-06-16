#!/bin/bash

# Set to 1 to enable verbose debug logging from the mock driver (visible in dmesg)
DEBUG="${I2C_MOCK_DEBUG:-0}"

/bin/bash  ../native/i2c/build.sh
# This is a hack to load i2c-dev only if it is not built in kernel
modinfo i2c-dev >/dev/null 2>/dev/null && ! modprobe -n --first-time i2c-dev 2>/dev/null && echo "i2c-dev is loaded" || modprobe i2c-dev
insmod i2c-mock.ko debug="$DEBUG"

# Sleep a second to let udev rules to be applied
sleep 0.5