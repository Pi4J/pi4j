#!/bin/bash

# Set to 1 to enable verbose debug logging from the mock driver (visible in dmesg)
DEBUG="${SPI_MOCK_DEBUG:-0}"

# Static SPI bus number; the spidev node is named spi<BUSNUM>.0 (default spi0.0)
BUSNUM="${SPI_MOCK_BUSNUM:-6}"

/bin/bash  ../native/spi/build.sh
modprobe spidev
insmod spi-mock.ko debug="$DEBUG" busnum="$BUSNUM"

# Sleep a second to let udev rules to be applied
sleep 0.5