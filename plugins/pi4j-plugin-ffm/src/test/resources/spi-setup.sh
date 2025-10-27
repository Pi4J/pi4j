#!/bin/sh

/bin/bash  ../native/spi/build.sh
modprobe spidev
insmod spi-mock.ko