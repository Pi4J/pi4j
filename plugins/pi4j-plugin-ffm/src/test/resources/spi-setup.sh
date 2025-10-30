#!/bin/sh

/bin/bash  ../native/spi/build.sh
modprobe spidev
insmod spi-mock.ko
lsmod | grep spi
dmesg | tail -n 10
journalctl -n 50