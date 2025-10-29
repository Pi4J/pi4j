#!/bin/sh

/bin/bash  ../native/spi/build.sh
insmod spi-mock.ko
sleep 1
lsmod
dmesg | tail -n 100