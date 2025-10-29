#!/bin/sh

/bin/bash  ../native/pwm/build.sh
insmod pwm-mock.ko
sleep 10
lsmod | grep pwm
dmesg | tail -n 10