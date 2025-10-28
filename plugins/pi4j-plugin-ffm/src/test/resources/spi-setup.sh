#!/bin/sh

/bin/bash  ../native/spi/build.sh
insmod spi-mock.ko
sleep 1