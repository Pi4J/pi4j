#!/bin/sh

/bin/bash  ../native/pwm/build.sh
insmod pwm-mock.ko
sleep 1