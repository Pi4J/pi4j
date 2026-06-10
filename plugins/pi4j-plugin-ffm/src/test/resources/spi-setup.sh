#!/bin/bash

# Get the script to setup permissions
bash <(curl -sSL https://raw.githubusercontent.com/Pi4J/pi4j-os/main/script/setup-permissions.sh) <<< y

/bin/bash  ../native/spi/build.sh
modprobe spidev
insmod spi-mock.ko

# Sleep a second to let udev rules to be applied
sleep 1