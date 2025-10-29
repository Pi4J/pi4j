#!/bin/sh

/bin/bash  ../native/spi/build.sh
insmod spi-mock.ko
sleep 10
lsmod | grep spi
dmesg | tail -n 10
ls -la /dev/spidev0.0