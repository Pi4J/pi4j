#!/bin/bash

# Set to 1 to enable verbose debug logging from the mock driver (visible in dmesg)
DEBUG="${PWM_MOCK_DEBUG:-0}"

/bin/bash  ../native/pwm/build.sh
insmod pwm-mock.ko debug="$DEBUG"

udevadm trigger --settle

# Sleep a second to let udev rules to be applied
sleep 0.5