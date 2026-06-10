#!/bin/bash

# Get the script to setup permissions
bash <(curl -sSL https://raw.githubusercontent.com/Pi4J/pi4j-os/main/script/setup-permissions.sh) <<< y

/bin/bash  ../native/pwm/build.sh
insmod pwm-mock.ko

# Sleep a second to let udev rules to be applied
sleep 1

echo "123"
ls -la /sys/class/pwm/pwmchip*