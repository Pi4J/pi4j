#!/bin/sh

# TODO: Guess the chip number, should be last available.
modprobe i2c-stub chip_addr=0x1c
# sleep is needed to make all driver processes establish the device
sleep 0.5
chmod 666 /dev/i2c-1