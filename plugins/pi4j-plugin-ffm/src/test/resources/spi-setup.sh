#!/bin/sh

pwd
modprobe spidev
#insmod "$(pwd)"/src/test/resources/spi-mock.ko
insmod spi-mock.ko
echo 'spidev' | sudo tee /sys/bus/spi/devices/spi0.0/driver_override
echo 'spi0.0' | sudo tee /sys/bus/spi/drivers/spidev/bind
chmod 0666 /dev/spidev0.0