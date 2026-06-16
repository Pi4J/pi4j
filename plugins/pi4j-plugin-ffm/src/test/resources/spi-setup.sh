#!/bin/bash

# Set to 1 to enable verbose debug logging from the mock driver (visible in dmesg)
DEBUG="${SPI_MOCK_DEBUG:-0}"

/bin/bash  ../native/spi/build.sh
modprobe spidev
insmod spi-mock.ko debug="$DEBUG"

# Sleep a second to let udev rules to be applied
sleep 0.5