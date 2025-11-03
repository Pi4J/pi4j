#!/bin/sh

/bin/bash  ../native/i2c/build.sh
# This is a hack to load i2c-dev only if it is not built in kernel
modinfo i2c-dev >/dev/null 2>/dev/null && ! modprobe -n --first-time i2c-dev 2>/dev/null && echo "i2c-dev is loaded" || modprobe i2c-dev
insmod i2c-mock.ko