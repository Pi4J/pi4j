#!/bin/sh

/bin/bash  ../native/i2c/build.sh
insmod i2c-mock.ko
sleep 10
lsmod | grep i2c
dmesg | tail -n 10
journalctl -n 50