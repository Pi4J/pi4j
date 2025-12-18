#!/bin/sh


/bin/bash  "$(pwd)"/src/test/native/build.sh
modprobe spidev
insmod "$(pwd)"/src/test/resources/spi-mock.ko
#insmod spi-mock.ko
echo 'spidev' | sudo tee /sys/bus/spi/devices/spi0.0/driver_override
echo 'spi0.0' | sudo tee /sys/bus/spi/drivers/spidev/bind
chmod 0660 /dev/spidev0.0
chown root:dialout /dev/spidev0.0