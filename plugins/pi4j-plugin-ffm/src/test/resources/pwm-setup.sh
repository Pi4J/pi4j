#!/bin/bash

/bin/bash  ../native/pwm/build.sh
insmod pwm-mock.ko

udevadm trigger --settle

# Sleep a second to let udev rules to be applied
sleep 0.5