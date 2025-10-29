#!/bin/sh

/bin/bash  ../native/pwm/build.sh
insmod pwm-mock.ko
lsmod | grep pwm
dmesg | tail -n 10
ls -la /sys/class/pwm/pwmchip0/