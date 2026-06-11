#!/bin/bash

/bin/bash  ../native/pwm/build.sh
insmod pwm-mock.ko

udevadm trigger

# Sleep a second to let udev rules to be applied
sleep 5