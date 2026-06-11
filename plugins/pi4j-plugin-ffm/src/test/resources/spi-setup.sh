#!/bin/bash

/bin/bash  ../native/spi/build.sh
modprobe spidev
insmod spi-mock.ko

udevadm trigger --settle

# Sleep a second to let udev rules to be applied
sleep 1